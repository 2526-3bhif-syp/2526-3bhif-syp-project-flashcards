package at.htlleonding.flashcards.view;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;

public class MainView extends BorderPane {
    private final StackPane mainContent;
    private final SidebarView sidebar;
    private final NavbarView navbar;

    public MainView() {
        this.setStyle("-fx-background-color: white;");

        sidebar = new SidebarView();
        this.setLeft(sidebar);

        VBox rightArea = new VBox();
        navbar = new NavbarView();
        
        mainContent = new StackPane();
        VBox.setVgrow(mainContent, Priority.ALWAYS);
        
        mainContent.setStyle(
            "-fx-background-color: #f8f9fa; " + 
            "-fx-background-radius: 20 0 0 0; " + 
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1 0 0 1; " + 
            "-fx-border-radius: 20 0 0 0;"
        );
        
        rightArea.getChildren().addAll(navbar, mainContent);
        this.setCenter(rightArea);
    }

    public void setView(Node view) {
        mainContent.getChildren().clear();
        mainContent.getChildren().add(view);
    }

    public SidebarView getSidebar() {
        return sidebar;
    }

    public NavbarView getNavbar() {
        return navbar;
    }
}