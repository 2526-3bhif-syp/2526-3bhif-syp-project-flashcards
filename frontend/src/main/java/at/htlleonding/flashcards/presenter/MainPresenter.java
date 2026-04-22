package at.htlleonding.flashcards.presenter;

import at.htlleonding.flashcards.model.*;
import at.htlleonding.flashcards.view.*;
import javafx.scene.Node;
import javafx.scene.control.Alert;
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

    public MainPresenter(MainView view) {
        this.view = view;
        this.model = new Model();

        HomeView homeView = new HomeView();
        homeView.setOnDeckSelected(this::handleDeckSelected);
        homeView.setOnImportDeckRequested(() -> handleDeckImportRequested(homeView));
        homeView.setOnCreateDeckRequested(() -> handleCreateDeckRequested(homeView));
        homeView.setOnEditDeckRequested(deck -> handleEditDeckRequested(homeView, deck));
        homeView.setOnDeleteDeckRequested(deck -> handleDeleteDeckRequested(homeView, deck));
        homeView.renderDecks(model.getDecks());

        FlashcardsView flashcardsView = new FlashcardsView();
        flashcardsView.setOnAddCardRequested(() -> handleAddCardRequested(flashcardsView));
        flashcardsView.setOnEditCardRequested(card -> handleEditCardRequested(flashcardsView, card));
        flashcardsView.setOnDeleteCardRequested(card -> handleDeleteCardRequested(flashcardsView, card));
        flashcardsView.setOnImportRequested(() -> handleImportRequested(flashcardsView));
        flashcardsView.setOnExportCardRequested(card -> handleCardExportRequested(card, flashcardsView));
        flashcardsView.setOnExportSelectedCardsRequested(cards -> handleCardsExportRequested(cards, flashcardsView));

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
            for (Deck deck : imported) {
                model.addOrMergeDeck(deck);
            }
            refreshHomeView();
            alert(Alert.AlertType.INFORMATION, imported.size() + " deck(s) imported successfully.");
        } catch (Exception e) {
            alert(Alert.AlertType.ERROR, "Import failed: " + e.getMessage());
        }
    }

    // ── Home: deck CRUD ────────────────────────────────────────────────────

    private void handleCreateDeckRequested(HomeView homeView) {
        Stage owner = (Stage) homeView.getScene().getWindow();
        CreateDeckDialog dialog = new CreateDeckDialog(owner);
        dialog.showAndWait().ifPresent(deckResult -> {
            Deck newDeck = new Deck(deckResult.name(), deckResult.description(), deckResult.iconId());
            model.addDeck(newDeck);
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

    private void handleDeleteDeckRequested(HomeView homeView, Deck deck) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Deck");
        alert.setHeaderText("Are you sure you want to delete the deck \"" + deck.getName() + "\"?");
        alert.setContentText("This deck contains " + deck.getCardCount() + " cards. This action cannot be undone.");

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == javafx.scene.control.ButtonType.OK) {
                model.removeDeck(deck);
                homeView.renderDecks(model.getDecks());
            }
        });
    }

    // ── Flashcards: card import / export ───────────────────────────────────

    private void handleImportRequested(FlashcardsView flashcardsView) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import JSON");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        File file = fc.showOpenDialog(flashcardsView.getScene().getWindow());
        if (file != null) performImport(file, flashcardsView);
    }

    private void performImport(File file, FlashcardsView flashcardsView) {
        String currentDeckName = flashcardsView.getDeckTitle();
        Deck deck = model.getDecks().stream()
                .filter(d -> d.getName().equals(currentDeckName))
                .findFirst()
                .orElse(null);
        if (deck == null) return;

        try {
            Deck importedDeck = model.getPersistence().importFromJSON(file);

            if (importedDeck == null || importedDeck.getCards().isEmpty()) {
                alert(Alert.AlertType.INFORMATION, "No cards found in the imported file.");
                return;
            }

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

            boolean allowDuplicates = (action == DuplicateActionDialog.Action.ALLOW_ALL);
            int count = 0;
            for (Card c : validImportedCards) {
                boolean isDuplicate = deck.getCards().stream().anyMatch(ex ->
                    ex.getQuestion() != null &&
                    ex.getQuestion().trim().equalsIgnoreCase(c.getQuestion().trim())
                );
                if (!allowDuplicates && isDuplicate) continue;
                deck.addCard(c);
                count++;
            }

            model.updateDeck(deck);
            flashcardsView.renderCards(deck.getCards());
            alert(Alert.AlertType.INFORMATION, count + " cards imported successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Import failed: " + e.getMessage());
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
        } catch (IOException e) {
            alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage());
        }
    }

    // ── Flashcards: card CRUD ──────────────────────────────────────────────

    private void handleAddCardRequested(FlashcardsView flashcardsView) {
        Stage owner = (Stage) flashcardsView.getScene().getWindow();
        CreateCardDialog dialog = new CreateCardDialog(owner);
        dialog.showAndWait().ifPresent(newCard -> {
            // Find the currently displayed deck
            String currentDeckName = ((FlashcardsView) views.get("Flashcards")).getDeckTitle();
            Deck deck = model.getDecks().stream()
                    .filter(d -> d.getName().equals(currentDeckName))
                    .findFirst()
                    .orElse(null);

            if (deck != null) {
                deck.addCard(newCard);
                model.updateDeck(deck);
                flashcardsView.renderCards(deck.getCards());
            }
        });
    }

    private void handleEditCardRequested(FlashcardsView flashcardsView, Card cardToEdit) {
        Stage owner = (Stage) flashcardsView.getScene().getWindow();
        CreateCardDialog dialog = new CreateCardDialog(owner, cardToEdit);
        dialog.showAndWait().ifPresent(updatedCard -> {
            String currentDeckName = flashcardsView.getDeckTitle();
            Deck deck = model.getDecks().stream()
                    .filter(d -> d.getName().equals(currentDeckName))
                    .findFirst()
                    .orElse(null);

            if (deck != null) {
                deck.updateCard(updatedCard);
                model.updateDeck(deck);
                flashcardsView.renderCards(deck.getCards());
            }
        });
    }

    private void handleDeleteCardRequested(FlashcardsView flashcardsView, Card cardToDelete) {
        String currentDeckName = flashcardsView.getDeckTitle();
        Deck deck = model.getDecks().stream()
                .filter(d -> d.getName().equals(currentDeckName))
                .findFirst()
                .orElse(null);

        if (deck != null) {
            deck.removeCard(cardToDelete);
            model.updateDeck(deck);
            flashcardsView.renderCards(deck.getCards());
        }
    }

    // ── navigation ─────────────────────────────────────────────────────────

    private void handleDeckSelected(Deck deck) {
        FlashcardsView fView = (FlashcardsView) views.get("Flashcards");
        fView.setDeckInfo(deck.getName(), deck.getDescription() != null ? deck.getDescription() : "", deck.getIconId());
        fView.renderCards(deck.getCards());
        navigateTo("Flashcards", true);
    }

    private void handleNavigation(String destination) {
        if (destination.equals("Back")) goBack();
        else if (destination.equals("Forward")) goForward();
        else navigateTo(destination, true);
    }

    private void navigateTo(String destination, boolean addToHistory) {
        if (destination.equals(currentViewName)) return;

        if (destination.equals("Home")) {
            refreshHomeView();
        } else if (destination.equals("Flashcards")) {
            FlashcardsView fView = (FlashcardsView) views.get("Flashcards");
            if (fView.getDeckTitle().isEmpty()) {
                List<Deck> decks = model.getDecks();
                if (!decks.isEmpty()) {
                    Deck first = decks.get(0);
                    fView.setDeckInfo(first.getName(), first.getDescription() != null ? first.getDescription() : "", first.getIconId());
                    fView.renderCards(first.getCards());
                }
            }
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

    public MainView getView() { return view; }
}
