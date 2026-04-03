package at.htlleonding.flashcards.presenter;

import at.htlleonding.flashcards.view.*;
import javafx.scene.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class MainPresenter {
    private final MainView view;
    private final Map<String, Node> views = new HashMap<>();
    
    private final Stack<String> backStack = new Stack<>();
    private final Stack<String> forwardStack = new Stack<>();
    private String currentViewName;

    public MainPresenter(MainView view) {
        this.view = view;
        
        // Views vorab erstellen
        views.put("Home", new HomeView());
        views.put("Flashcards", new FlashcardsView());
        views.put("Statistic", new StatisticView());
        views.put("Settings", new SettingsView());

        // Navigation in der Sidebar registrieren
        view.getSidebar().setOnNavigationAction(this::handleNavigation);
        
        // Initialer Zustand
        navigateTo("Home", false);
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

    private void navigateTo(String destination, boolean addToHistory) {
        if (destination.equals(currentViewName)) return;

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