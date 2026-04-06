package at.htlleonding.flashcards.presenter;

import at.htlleonding.flashcards.model.*;
import at.htlleonding.flashcards.view.*;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

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

        // Views vorab erstellen
        views.put("Home", homeView);
        views.put("Flashcards", flashcardsView);
        views.put("Statistic", new StatisticView());
        views.put("Settings", new SettingsView());

        // Navigation in der Sidebar registrieren
        view.getSidebar().setOnNavigationAction(this::handleNavigation);
        
        // Initialer Zustand
        navigateTo("Home", false);
    }

    private void handleAddCardRequested(FlashcardsView flashcardsView) {
        Stage owner = (Stage) flashcardsView.getScene().getWindow();
        CreateCardDialog dialog = new CreateCardDialog(owner);
        dialog.showAndWait().ifPresent(newCard -> {
            // Dem aktuellen Deck im Modell hinzufgen
            var decks = model.getDecks();
            if (!decks.isEmpty()) {
                var deck = decks.get(0); 
                deck.addCard(newCard);
                model.updateDeck(deck); // Explizit das Model triggern zum Speichern
                
                // View aktualisieren
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
        // Dummy: Wir nehmen immer das erste Deck aus dem Modell
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
                forwardStack.clear(); // Neue Navigation löscht den Vorwärts-Verlauf
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
