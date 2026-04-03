package at.htlleonding.flashcards.view;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class MainView extends BorderPane {
    public MainView() {
        this.setStyle("-fx-background-color: white;");
        
        NavbarView navbar = new NavbarView();
        this.setTop(navbar);

        SidebarView sidebar = new SidebarView();
        this.setLeft(sidebar);

        // Platzhalter für den Hauptinhalt
        StackPane mainContent = new StackPane();
        this.setCenter(mainContent);
    }
}
