package at.htlleonding.flashcards.presenter;

import at.htlleonding.flashcards.model.*;
import at.htlleonding.flashcards.view.*;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
        ImportFormatDialog dialog = new ImportFormatDialog((Stage) flashcardsView.getScene().getWindow(), "Import");
        dialog.showAndWait().ifPresent(format -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(format + " files", "*." + format.toLowerCase()));
            File file = fc.showOpenDialog(flashcardsView.getScene().getWindow());
            if (file != null) performImport(file, format, flashcardsView);
        });
    }

    private void performImport(File file, String format, FlashcardsView flashcardsView) {
        var decks = model.getDecks();
        if (decks.isEmpty()) return;
        var deck = decks.get(0);

        try {
            List<Card> importedCards = new ArrayList<>();
            if (format.equals("JSON")) {
                importedCards = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(file, new com.fasterxml.jackson.core.type.TypeReference<List<Card>>() {});
            } else {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 2) {
                            importedCards.add(new Card(parts[0], parts[1]));
                        }
                    }
                }
            }

            DuplicateActionDialog duplicateDialog = new DuplicateActionDialog((Stage) flashcardsView.getScene().getWindow());
            DuplicateActionDialog.Action action = duplicateDialog.showAndWait().orElse(DuplicateActionDialog.Action.CANCEL);
            
            if (action == DuplicateActionDialog.Action.CANCEL) return;
            
            boolean allowDuplicates = (action == DuplicateActionDialog.Action.ALLOW_ALL);

            for (Card c : importedCards) {
                if (!allowDuplicates && deck.getCards().stream().anyMatch(existing -> existing.getQuestion().equalsIgnoreCase(c.getQuestion().trim()))) continue;
                deck.addCard(c);
            }
            model.updateDeck(deck);
            flashcardsView.renderCards(deck.getCards());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleExportRequested(FlashcardsView flashcardsView) {
        ImportFormatDialog dialog = new ImportFormatDialog((Stage) flashcardsView.getScene().getWindow(), "Export");
        dialog.showAndWait().ifPresent(format -> {
            FileChooser fc = new FileChooser();
            fc.setInitialFileName("export." + format.toLowerCase());
            File file = fc.showSaveDialog(flashcardsView.getScene().getWindow());
            if (file != null) performExport(file, format, flashcardsView);
        });
    }

    private void performExport(File file, String format, FlashcardsView flashcardsView) {
        var deck = model.getDecks().get(0);
        try {
            if (format.equals("JSON")) {
                new com.fasterxml.jackson.databind.ObjectMapper().writeValue(file, deck.getCards());
            } else {
                try (PrintWriter pw = new PrintWriter(file)) {
                    for (Card c : deck.getCards()) pw.println(c.getQuestion() + "," + c.getAnswer());
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
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
