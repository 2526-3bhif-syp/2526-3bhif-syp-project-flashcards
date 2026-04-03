package at.htlleonding.flashcards.presenter;

import at.htlleonding.flashcards.view.*;
import javafx.scene.Node;

import java.util.HashMap;
import java.util.Map;

public class MainPresenter {
    private final MainView view;
    private final Map<String, Node> views = new HashMap<>();

    public MainPresenter(MainView view) {
        this.view = view;
        
        // Views vorab erstellen
        views.put("Home", new HomeView());
        views.put("Flashcards", new FlashcardsView());
        views.put("Statistic", new StatisticView());
        views.put("Settings", new SettingsView());

        // Standard-View setzen
        navigateTo("Home");

        // Navigation in der Sidebar registrieren
        view.getSidebar().setOnNavigationAction(this::navigateTo);
    }

    private void navigateTo(String destination) {
        Node targetView = views.get(destination);
        if (targetView != null) {
            view.setView(targetView);
        } else {
            System.out.println("Navigation to " + destination + " not yet implemented.");
        }
    }

    public MainView getView() {
        return view;
    }
}