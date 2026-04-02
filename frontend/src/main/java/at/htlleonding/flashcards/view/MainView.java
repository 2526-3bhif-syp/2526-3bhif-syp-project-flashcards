package at.htlleonding.flashcards.view;

import javafx.scene.layout.BorderPane;

public class MainView extends BorderPane {
    public MainView() {
        this.setStyle("-fx-background-color: white;");
        
        NavbarView navbar = new NavbarView();
        this.setTop(navbar);

        SidebarView sidebar = new SidebarView();
        this.setLeft(sidebar);
    }
}
