package at.htlleonding.flashcards.view;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class MainView extends BorderPane {
    public MainView() {
        this.setStyle("-fx-background-color: white;");
        
        SidebarView sidebar = new SidebarView();
        this.setLeft(sidebar);

        BorderPane contentArea = new BorderPane();
        NavbarView navbar = new NavbarView();
        contentArea.setTop(navbar);
        
        // Platzhalter für den Hauptinhalt
        StackPane mainContent = new StackPane();
        contentArea.setCenter(mainContent);
        
        this.setCenter(contentArea);
    }
}
