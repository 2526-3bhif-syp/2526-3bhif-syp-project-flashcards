package at.htlleonding.flashcards.presenter;

import at.htlleonding.flashcards.model.*;
import at.htlleonding.flashcards.view.*;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MainPresenter {
    private final MainView view;
    private final Model model;
    private final Map<String, Node> views = new HashMap<>();

    private final Stack<String> backStack = new Stack<>();
    private final Stack<String> forwardStack = new Stack<>();
    private String currentViewName;
    private Deck currentDeck; // null = "all cards" mode

    public MainPresenter(MainView view) {
        this.view = view;
        this.model = new Model();

        HomeView homeView = new HomeView();
        homeView.setOnDeckSelected(this::handleDeckSelected);
        homeView.setOnImportDeckRequested(() -> handleDeckImportRequested(homeView));
        homeView.setOnCreateDeckRequested(() -> handleCreateDeckRequested(homeView));
        homeView.setOnEditDeckRequested(deck -> handleEditDeckRequested(homeView, deck));
        homeView.setOnDeleteDeckRequested(deck -> handleDeleteDeckRequested(homeView, deck));
        homeView.setOnExportDeckRequested(deck -> handleSingleDeckExportRequested(deck, homeView));
        homeView.setOnExportSelectedDecksRequested(decks -> handleDecksExportRequested(decks, homeView));
        homeView.setOnDeleteSelectedDecksRequested(decks -> handleDeleteSelectedDecksRequested(decks, homeView));
        homeView.renderDecks(model.getDecks());

        FlashcardsView flashcardsView = new FlashcardsView();
        flashcardsView.setOnAddCardRequested(() -> handleAddCardRequested(flashcardsView));
        flashcardsView.setOnEditCardRequested(card -> handleEditCardRequested(flashcardsView, card));
        flashcardsView.setOnDeleteCardRequested(card -> handleDeleteCardRequested(flashcardsView, card));
        flashcardsView.setOnImportRequested(() -> handleImportRequested(flashcardsView));
        flashcardsView.setOnExportCardRequested(card -> handleCardExportRequested(card, flashcardsView));
        flashcardsView.setOnExportSelectedCardsRequested(cards -> handleCardsExportRequested(cards, flashcardsView));
        flashcardsView.setOnDeleteSelectedCardsRequested(cards -> handleDeleteSelectedCardsRequested(cards, flashcardsView));
        flashcardsView.setDeckNameResolver(card -> { Deck d = findDeckForCard(card); return d != null ? d.getName() : null; });

        views.put("Home", homeView);
        views.put("Flashcards", flashcardsView);
        views.put("Statistic", new StatisticView());
        views.put("Settings", new SettingsView());

        view.getSidebar().setOnNavigationAction(this::handleNavigation);

        navigateTo("Home", false);
    }

    // ── Home: deck import ──────────────────────────────────────────────────

    private void handleDeckImportRequested(HomeView homeView) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import Deck");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        File file = fc.showOpenDialog(homeView.getScene().getWindow());
        if (file == null) return;

        try {
            List<Deck> imported = model.getPersistence().importDecksFromJSON(file);

            if (imported == null || imported.isEmpty()) {
                alert(Alert.AlertType.INFORMATION, "No decks found in the imported file.");
                return;
            }

            List<Deck> existingDecks = model.getDecks();
            boolean hasDuplicates = imported.stream().anyMatch(imp ->
                existingDecks.stream().anyMatch(ex ->
                    ex.getName() != null &&
                    ex.getName().trim().equalsIgnoreCase(imp.getName() != null ? imp.getName().trim() : "")
                )
            );

            DuplicateDeckActionDialog.Action action = DuplicateDeckActionDialog.Action.ALLOW_ALL;
            if (hasDuplicates) {
                DuplicateDeckActionDialog dialog = new DuplicateDeckActionDialog((Stage) homeView.getScene().getWindow());
                action = dialog.showAndWait().orElse(DuplicateDeckActionDialog.Action.CANCEL);
            }

            if (action == DuplicateDeckActionDialog.Action.CANCEL) return;

            int count = 0;
            for (Deck deck : imported) {
                Deck existing = existingDecks.stream()
                    .filter(ex -> ex.getName() != null &&
                                  ex.getName().trim().equalsIgnoreCase(deck.getName() != null ? deck.getName().trim() : ""))
                    .findFirst().orElse(null);
                boolean isDuplicate = existing != null;

                if (isDuplicate) {
                    if (action == DuplicateDeckActionDialog.Action.SKIP) continue;
                    if (action == DuplicateDeckActionDialog.Action.REPLACE) model.removeDeck(existing);
                    // ALLOW_ALL: regenerate UUID so both decks stay independent
                    if (action == DuplicateDeckActionDialog.Action.ALLOW_ALL) {
                        deck.setId(java.util.UUID.randomUUID().toString());
                    }
                }
                // Also guard against UUID collision with any existing deck (e.g. non-duplicate name but same UUID from export)
                boolean uuidCollision = existingDecks.stream()
                    .anyMatch(ex -> ex.getId() != null && ex.getId().equals(deck.getId()));
                if (uuidCollision) {
                    deck.setId(java.util.UUID.randomUUID().toString());
                }
                model.addDeck(deck);
                count++;
            }

            refreshHomeView();
            alert(Alert.AlertType.INFORMATION, count + " deck(s) imported successfully.");
        } catch (Exception e) {
            alert(Alert.AlertType.ERROR, "Deck could not be loaded. Please check the JSON format.\n\nDetails: " + e.getMessage());
        }
    }

    // ── Home: deck CRUD ────────────────────────────────────────────────────

    private void handleCreateDeckRequested(HomeView homeView) {
        Stage owner = (Stage) homeView.getScene().getWindow();
        CreateDeckDialog dialog = new CreateDeckDialog(owner);
        dialog.showAndWait().ifPresent(deckResult -> {
            Deck newDeck = new Deck(deckResult.name(), deckResult.description(), deckResult.iconId());
            model.addOrMergeDeck(newDeck);
            homeView.renderDecks(model.getDecks());
        });
    }

    private void handleEditDeckRequested(HomeView homeView, Deck deck) {
        Stage owner = (Stage) homeView.getScene().getWindow();
        CreateDeckDialog dialog = new CreateDeckDialog(owner, deck);
        dialog.showAndWait().ifPresent(deckResult -> {
            deck.setName(deckResult.name());
            deck.setDescription(deckResult.description());
            deck.setIconId(deckResult.iconId());
            model.updateDeck(deck);
            homeView.renderDecks(model.getDecks());
        });
    }

    private void handleSingleDeckExportRequested(Deck deck, HomeView homeView) {
        File file = saveDialog(homeView, deck.getName().replaceAll("\\s+", "_") + ".json");
        if (file == null) return;
        try {
            model.getPersistence().exportToJSON(deck, file);
        } catch (IOException e) {
            alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage());
        }
    }

    private void handleDecksExportRequested(List<Deck> decks, HomeView homeView) {
        File file = saveDialog(homeView, "decks_export.json");
        if (file == null) return;
        try {
            model.getPersistence().exportDecksToJSON(decks, file);
            homeView.exitSelectMode();
            homeView.renderDecks(model.getDecks());
            alert(Alert.AlertType.INFORMATION, decks.size() + " deck(s) exported successfully.");
        } catch (IOException e) {
            alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage());
        }
    }

    private void handleDeleteSelectedDecksRequested(List<Deck> decks, HomeView homeView) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Decks");
        confirm.setHeaderText("Delete " + decks.size() + " deck(s)?");
        confirm.setContentText("This action cannot be undone.");
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == javafx.scene.control.ButtonType.OK) {
                decks.forEach(model::removeDeck);
                if (decks.contains(currentDeck)) {
                    currentDeck = null;
                    refreshFlashcardsView();
                }
                homeView.exitSelectMode();
                homeView.renderDecks(model.getDecks());
            }
        });
    }

    private void handleDeleteDeckRequested(HomeView homeView, Deck deck) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Deck");
        alert.setHeaderText("Are you sure you want to delete the deck \"" + deck.getName() + "\"?");
        alert.setContentText("This deck contains " + deck.getCardCount() + " cards. This action cannot be undone.");

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == javafx.scene.control.ButtonType.OK) {
                model.removeDeck(deck);
                if (deck == currentDeck) {
                    currentDeck = null;
                    refreshFlashcardsView();
                }
                homeView.renderDecks(model.getDecks());
            }
        });
    }

    // ── Flashcards: card import / export ───────────────────────────────────

    private void handleImportRequested(FlashcardsView flashcardsView) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import Cards");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        File file = fc.showOpenDialog(flashcardsView.getScene().getWindow());
        if (file != null) performImport(file, flashcardsView);
    }

    private void performImport(File file, FlashcardsView flashcardsView) {
        try {
            Deck importedDeck = model.getPersistence().importCardsFromJSON(file);

            if (importedDeck == null || importedDeck.getCards().isEmpty()) {
                alert(Alert.AlertType.INFORMATION, "No cards found in the imported file.");
                return;
            }

            Deck resolved = currentDeck;
            if (resolved == null) {
                resolved = pickDeck((Stage) flashcardsView.getScene().getWindow());
                if (resolved == null) return;
            }
            final Deck deck = resolved;

            List<Card> validImportedCards = importedDeck.getCards().stream()
                .filter(c -> c != null &&
                             c.getQuestion() != null && !c.getQuestion().isBlank() &&
                             c.getAnswer() != null && !c.getAnswer().isBlank())
                .toList();

            if (validImportedCards.isEmpty()) {
                alert(Alert.AlertType.WARNING, "No valid cards (missing question or answer) found.");
                return;
            }

            boolean hasDuplicates = validImportedCards.stream().anyMatch(ic ->
                deck.getCards().stream().anyMatch(ex ->
                    ex.getQuestion() != null &&
                    ex.getQuestion().trim().equalsIgnoreCase(ic.getQuestion().trim())
                )
            );

            DuplicateActionDialog.Action action = DuplicateActionDialog.Action.ALLOW_ALL;
            if (hasDuplicates) {
                DuplicateActionDialog dialog = new DuplicateActionDialog((Stage) flashcardsView.getScene().getWindow());
                action = dialog.showAndWait().orElse(DuplicateActionDialog.Action.CANCEL);
            }

            if (action == DuplicateActionDialog.Action.CANCEL) return;

            int count = 0;
            for (Card c : validImportedCards) {
                Card existing = deck.getCards().stream()
                    .filter(ex -> ex.getQuestion() != null &&
                                  ex.getQuestion().trim().equalsIgnoreCase(c.getQuestion().trim()))
                    .findFirst().orElse(null);
                boolean isDuplicate = existing != null;

                if (isDuplicate) {
                    if (action == DuplicateActionDialog.Action.SKIP) continue;
                    if (action == DuplicateActionDialog.Action.REPLACE) deck.removeCard(existing);
                    // ALLOW_ALL: regenerate UUID so both cards stay independent
                    if (action == DuplicateActionDialog.Action.ALLOW_ALL) {
                        c.setId(java.util.UUID.randomUUID().toString());
                    }
                }
                // Guard against UUID collision with any card already in the deck
                boolean uuidCollision = deck.getCards().stream()
                    .anyMatch(ex -> ex.getId() != null && ex.getId().equals(c.getId()));
                if (uuidCollision) {
                    c.setId(java.util.UUID.randomUUID().toString());
                }
                deck.addCard(c);
                count++;
            }

            model.updateDeck(deck);
            refreshFlashcardsView();
            alert(Alert.AlertType.INFORMATION, count + " cards imported successfully.");

        } catch (Exception e) {
            alert(Alert.AlertType.ERROR, "Cards could not be imported. Please check the JSON format.\n\nDetails: " + e.getMessage());
        }
    }

    private void handleCardExportRequested(Card card, FlashcardsView flashcardsView) {
        File file = saveDialog(flashcardsView, "card_export.json");
        if (file == null) return;
        try {
            model.getPersistence().exportCardToJSON(card, file);
        } catch (IOException e) {
            alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage());
        }
    }

    private void handleCardsExportRequested(List<Card> cards, FlashcardsView flashcardsView) {
        File file = saveDialog(flashcardsView, "cards_export.json");
        if (file == null) return;
        try {
            model.getPersistence().exportCardsToJSON(cards, file);
            flashcardsView.exitSelectMode();
            refreshFlashcardsView();
        } catch (IOException e) {
            alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage());
        }
    }

    // ── Flashcards: card CRUD ──────────────────────────────────────────────

    private void handleAddCardRequested(FlashcardsView flashcardsView) {
        Stage owner = (Stage) flashcardsView.getScene().getWindow();
        Deck deck = currentDeck != null ? currentDeck : pickDeck(owner);
        if (deck == null) return;
        CreateCardDialog dialog = new CreateCardDialog(owner);
        dialog.showAndWait().ifPresent(newCard -> {
            deck.addCard(newCard);
            model.updateDeck(deck);
            refreshFlashcardsView();
        });
    }

    private void handleEditCardRequested(FlashcardsView flashcardsView, Card cardToEdit) {
        Stage owner = (Stage) flashcardsView.getScene().getWindow();
        Deck deck = currentDeck != null ? currentDeck : findDeckForCard(cardToEdit);
        if (deck == null) return;
        CreateCardDialog dialog = new CreateCardDialog(owner, cardToEdit);
        dialog.showAndWait().ifPresent(updatedCard -> {
            deck.updateCard(updatedCard);
            model.updateDeck(deck);
            refreshFlashcardsView();
        });
    }

    private void handleDeleteCardRequested(FlashcardsView flashcardsView, Card cardToDelete) {
        Deck deck = currentDeck != null ? currentDeck : findDeckForCard(cardToDelete);
        if (deck == null) return;
        deck.removeCard(cardToDelete);
        model.updateDeck(deck);
        flashcardsView.clearSelectedCard();
        refreshFlashcardsView();
    }

    private void handleDeleteSelectedCardsRequested(List<Card> cards, FlashcardsView flashcardsView) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Cards");
        confirm.setHeaderText("Delete " + cards.size() + " card(s)?");
        confirm.setContentText("This action cannot be undone.");
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == javafx.scene.control.ButtonType.OK) {
                for (Card card : cards) {
                    Deck deck = currentDeck != null ? currentDeck : findDeckForCard(card);
                    if (deck != null) {
                        deck.removeCard(card);
                        model.updateDeck(deck);
                    }
                }
                flashcardsView.clearSelectedCard();
                flashcardsView.exitSelectMode();
                refreshFlashcardsView();
            }
        });
    }

    // ── navigation ─────────────────────────────────────────────────────────

    private void handleDeckSelected(Deck deck) {
        currentDeck = deck;
        FlashcardsView fView = (FlashcardsView) views.get("Flashcards");
        fView.setDeckInfo(deck.getName(), deck.getDescription() != null ? deck.getDescription() : "", deck.getIconId());
        fView.renderCards(deck.getCards());
        navigateTo("Flashcards", true);
    }

    private void handleNavigation(String destination) {
        if (destination.equals("Back")) goBack();
        else if (destination.equals("Forward")) goForward();
        else {
            if (destination.equals("Flashcards")) currentDeck = null;
            navigateTo(destination, true);
        }
    }

    private void navigateTo(String destination, boolean addToHistory) {
        if (destination.equals(currentViewName)) return;

        if (destination.equals("Home")) {
            refreshHomeView();
        } else if (destination.equals("Flashcards") && currentDeck == null) {
            FlashcardsView fView = (FlashcardsView) views.get("Flashcards");
            fView.clearDeckInfo();
            List<Card> allCards = model.getDecks().stream()
                    .flatMap(d -> d.getCards().stream())
                    .collect(Collectors.toList());
            fView.renderCards(allCards);
        }

        Node targetView = views.get(destination);
        if (targetView != null) {
            if (addToHistory && currentViewName != null) {
                backStack.push(currentViewName);
                forwardStack.clear();
            }
            currentViewName = destination;
            view.setView(targetView);
            updateArrowStates();
        }
    }

    private void goBack() {
        if (!backStack.isEmpty()) {
            forwardStack.push(currentViewName);
            currentViewName = backStack.pop();
            view.setView(views.get(currentViewName));
            updateArrowStates();
        }
    }

    private void goForward() {
        if (!forwardStack.isEmpty()) {
            backStack.push(currentViewName);
            currentViewName = forwardStack.pop();
            view.setView(views.get(currentViewName));
            updateArrowStates();
        }
    }

    private void updateArrowStates() {
        view.getSidebar().setBackEnabled(!backStack.isEmpty());
        view.getSidebar().setForwardEnabled(!forwardStack.isEmpty());
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private void refreshHomeView() {
        HomeView hv = (HomeView) views.get("Home");
        hv.renderDecks(model.getDecks());
    }

    private File saveDialog(Node anchor, String initialName) {
        FileChooser fc = new FileChooser();
        fc.setInitialFileName(initialName);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        return fc.showSaveDialog(anchor.getScene().getWindow());
    }

    private void alert(Alert.AlertType type, String message) {
        new Alert(type, message).showAndWait();
    }

    private Deck findDeckForCard(Card card) {
        return model.getDecks().stream()
                .filter(d -> d.getCards().stream().anyMatch(c -> c.getId().equals(card.getId())))
                .findFirst().orElse(null);
    }

    private Deck pickDeck(Stage owner) {
        List<Deck> decks = model.getDecks();
        if (decks.isEmpty()) {
            alert(Alert.AlertType.WARNING, "No decks available. Create a deck first.");
            return null;
        }
        if (decks.size() == 1) return decks.get(0);
        List<String> names = decks.stream().map(Deck::getName).collect(Collectors.toList());
        ChoiceDialog<String> dialog = new ChoiceDialog<>(names.get(0), names);
        dialog.initOwner(owner);
        dialog.setTitle("Select Deck");
        dialog.setHeaderText("Choose the deck:");
        dialog.setContentText("Deck:");
        return dialog.showAndWait()
                .flatMap(name -> decks.stream().filter(d -> d.getName().equals(name)).findFirst())
                .orElse(null);
    }

    private void refreshFlashcardsView() {
        FlashcardsView fView = (FlashcardsView) views.get("Flashcards");
        if (currentDeck != null) {
            fView.renderCards(currentDeck.getCards());
        } else {
            List<Card> allCards = model.getDecks().stream()
                    .flatMap(d -> d.getCards().stream())
                    .collect(Collectors.toList());
            fView.renderCards(allCards);
        }
    }

    public MainView getView() { return view; }
}
