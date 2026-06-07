package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.ThemeProvider;
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
        sidebar = new SidebarView();
        this.setLeft(sidebar);

        VBox rightArea = new VBox();
        navbar = new NavbarView();
        
        mainContent = new StackPane();
        VBox.setVgrow(mainContent, Priority.ALWAYS);
        
        rightArea.getChildren().addAll(navbar, mainContent);
        this.setCenter(rightArea);

        applyTheme();
    }

    public void applyTheme() {
        this.setStyle("-fx-background-color: " + ThemeProvider.get("bg-card") + ";");
        mainContent.setStyle(
            "-fx-background-color: " + ThemeProvider.get("bg-primary") + "; " + 
            "-fx-background-radius: 20 0 0 0; " + 
            "-fx-border-color: " + ThemeProvider.get("border-light") + "; " +
            "-fx-border-width: 1 0 0 1; " + 
            "-fx-border-radius: 20 0 0 0;"
        );
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