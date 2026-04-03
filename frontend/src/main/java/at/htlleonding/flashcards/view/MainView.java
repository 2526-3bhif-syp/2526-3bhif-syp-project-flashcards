package at.htlleonding.flashcards.view;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;

public class MainView extends BorderPane {
    public MainView() {
        // Außen eckig: Kein Padding, weißer Hintergrund
        this.setStyle("-fx-background-color: white;");

        // Sidebar links
        SidebarView sidebar = new SidebarView();
        this.setLeft(sidebar);

        // Rechter Bereich: Navbar oben, abgerundeter Content darunter
        VBox rightArea = new VBox();
        NavbarView navbar = new NavbarView();
        
        // Der eigentliche Inhaltsbereich (Content) wird abgerundet
        StackPane mainContent = new StackPane();
        VBox.setVgrow(mainContent, Priority.ALWAYS);
        
        // Styling für den "inneren" abgerundeten Bereich
        // Erzeugt den Effekt, dass die Linien von Navbar und Sidebar rund zusammenfließen
        mainContent.setStyle(
            "-fx-background-color: #f8f9fa; " + // Leichter Kontrast zum Weiß
            "-fx-background-radius: 20 0 0 0; " + // Nur oben-links abgerundet
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1 0 0 1; " + // Obere und linke Linie
            "-fx-border-radius: 20 0 0 0;"
        );
        
        rightArea.getChildren().addAll(navbar, mainContent);
        this.setCenter(rightArea);
    }
}