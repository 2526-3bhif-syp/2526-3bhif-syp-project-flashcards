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
        homeView.setOnExportDeckRequested(deckName -> handleDeckExportRequested(deckName, homeView));
        homeView.setOnExportSelectedDecksRequested(names -> handleDecksExportRequested(names, homeView));

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

    // ── Home: deck import / export ─────────────────────────────────────────

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

    private void handleDeckExportRequested(String deckName, HomeView homeView) {
        Deck deck = findDeck(deckName);
        if (deck == null) return;
        File file = saveDialog(homeView, deckName + ".json");
        if (file == null) return;
        try {
            model.getPersistence().exportToJSON(deck, file);
        } catch (IOException e) {
            alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage());
        }
    }

    private void handleDecksExportRequested(List<String> deckNames, HomeView homeView) {
        List<Deck> decks = deckNames.stream().map(this::findDeck).filter(Objects::nonNull).collect(Collectors.toList());
        if (decks.isEmpty()) return;
        File file = saveDialog(homeView, "decks_export.json");
        if (file == null) return;
        try {
            model.getPersistence().exportDecksToJSON(decks, file);
        } catch (IOException e) {
            alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage());
        }
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
        var decks = model.getDecks();
        if (decks.isEmpty()) return;
        var deck = decks.get(0);

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
            var decks = model.getDecks();
            if (!decks.isEmpty()) {
                var deck = decks.get(0);
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
            var decks = model.getDecks();
            if (!decks.isEmpty()) {
                var deck = decks.get(0);
                deck.updateCard(updatedCard);
                model.updateDeck(deck);
                flashcardsView.renderCards(deck.getCards());
            }
        });
    }

    private void handleDeleteCardRequested(FlashcardsView flashcardsView, Card cardToDelete) {
        var decks = model.getDecks();
        if (!decks.isEmpty()) {
            var deck = decks.get(0);
            deck.removeCard(cardToDelete);
            model.updateDeck(deck);
            flashcardsView.renderCards(deck.getCards());
        }
    }

    // ── navigation ─────────────────────────────────────────────────────────

    private void handleDeckSelected(String deckName) {
        Deck deck = findDeck(deckName);
        if (deck == null) return;
        FlashcardsView fView = (FlashcardsView) views.get("Flashcards");
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
            var decks = model.getDecks();
            if (!decks.isEmpty()) {
                FlashcardsView fView = (FlashcardsView) views.get("Flashcards");
                fView.renderCards(decks.get(0).getCards());
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
        List<String> names = model.getDecks().stream().map(Deck::getName).toList();
        hv.renderDecks(names);
    }

    private Deck findDeck(String name) {
        return model.getDecks().stream().filter(d -> d.getName().equals(name)).findFirst().orElse(null);
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
