package at.htlleonding.flashcards.presenter;

import at.htlleonding.flashcards.model.*;
import at.htlleonding.flashcards.view.*;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

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
        
        FlashcardsView flashcardsView = new FlashcardsView();
        flashcardsView.setOnAddCardRequested(() -> handleAddCardRequested(flashcardsView));
        flashcardsView.setOnEditCardRequested(card -> handleEditCardRequested(flashcardsView, card));
        flashcardsView.setOnDeleteCardRequested(card -> handleDeleteCardRequested(flashcardsView, card));
        flashcardsView.setOnImportRequested(() -> handleImportRequested(flashcardsView));
        flashcardsView.setOnExportRequested(() -> handleExportRequested(flashcardsView));

        views.put("Home", homeView);
        views.put("Flashcards", flashcardsView);
        views.put("Statistic", new StatisticView());
        views.put("Settings", new SettingsView());

        view.getSidebar().setOnNavigationAction(this::handleNavigation);
        
        navigateTo("Home", false);
    }

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
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No cards found in the imported file.");
                alert.showAndWait();
                return;
            }

            // Clean imported cards: remove those with null/empty question or answer
            List<Card> validImportedCards = importedDeck.getCards().stream()
                .filter(c -> c != null && 
                             c.getQuestion() != null && !c.getQuestion().isBlank() &&
                             c.getAnswer() != null && !c.getAnswer().isBlank())
                .toList();

            if (validImportedCards.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Import failed: No valid cards (missing question or answer) found.");
                alert.showAndWait();
                return;
            }

            // Check if duplicates exist
            boolean hasDuplicates = validImportedCards.stream().anyMatch(importedCard -> 
                deck.getCards().stream().anyMatch(existing -> 
                    existing.getQuestion() != null && 
                    existing.getQuestion().trim().equalsIgnoreCase(importedCard.getQuestion().trim())
                )
            );

            DuplicateActionDialog.Action action = DuplicateActionDialog.Action.ALLOW_ALL;
            if (hasDuplicates) {
                DuplicateActionDialog duplicateDialog = new DuplicateActionDialog((Stage) flashcardsView.getScene().getWindow());
                action = duplicateDialog.showAndWait().orElse(DuplicateActionDialog.Action.CANCEL);
            }
            
            if (action == DuplicateActionDialog.Action.CANCEL) return;
            
            boolean allowDuplicates = (action == DuplicateActionDialog.Action.ALLOW_ALL);
            int importedCount = 0;

            for (Card c : validImportedCards) {
                boolean isDuplicate = deck.getCards().stream().anyMatch(existing -> 
                    existing.getQuestion() != null && 
                    existing.getQuestion().trim().equalsIgnoreCase(c.getQuestion().trim())
                );

                if (!allowDuplicates && isDuplicate) continue;
                
                deck.addCard(c);
                importedCount++;
            }
            
            model.updateDeck(deck);
            flashcardsView.renderCards(deck.getCards());
            
            Alert success = new Alert(Alert.AlertType.INFORMATION, importedCount + " cards successfully imported.");
            success.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace(); // For debugging in console
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fehler beim Importieren: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleExportRequested(FlashcardsView flashcardsView) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export JSON");
        fc.setInitialFileName("export.json");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        File file = fc.showSaveDialog(flashcardsView.getScene().getWindow());
        if (file != null) performExport(file);
    }

    private void performExport(File file) {
        var decks = model.getDecks();
        if (decks.isEmpty()) return;
        var deck = decks.get(0);

        try {
            model.getPersistence().exportToJSON(deck, file);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fehler beim Exportieren: " + e.getMessage());
            alert.showAndWait();
        }
    }

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

    private void handleNavigation(String destination) {
        if (destination.equals("Back")) {
            goBack();
        } else if (destination.equals("Forward")) {
            goForward();
        } else {
            navigateTo(destination, true);
        }
    }

    private void handleDeckSelected(String deckName) {
        var deck = model.getDecks().get(0); 
        FlashcardsView fView = (FlashcardsView) views.get("Flashcards");
        fView.setDeckInfo(deck.getName(), "This deck is about basic " + deck.getName().toLowerCase() + " phrases.");
        fView.renderCards(deck.getCards());
        navigateTo("Flashcards", true);
    }

    private void navigateTo(String destination, boolean addToHistory) {
        if (destination.equals(currentViewName)) return;
        if (destination.equals("Flashcards")) {
            var decks = model.getDecks();
            if (!decks.isEmpty()) {
                var deck = decks.get(0);
                FlashcardsView fView = (FlashcardsView) views.get("Flashcards");
                fView.setDeckInfo(deck.getName(), "This deck is about basic " + deck.getName().toLowerCase() + " phrases.");
                fView.renderCards(deck.getCards());
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

    public MainView getView() {
        return view;
    }
}
