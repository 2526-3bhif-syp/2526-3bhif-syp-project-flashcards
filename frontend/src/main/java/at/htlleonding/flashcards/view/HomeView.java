package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Deck;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.function.Consumer;

public class HomeView extends VBox {
    private FlowPane deckGrid;
    private Consumer<String> onDeckSelected;
    private Runnable onCreateDeckRequested;

    public HomeView() {
        this.setPadding(new Insets(20));
        this.setSpacing(20);

        Label title = new Label("My Decks");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        deckGrid = new FlowPane();
        deckGrid.setHgap(20);
        deckGrid.setVgap(20);

        this.getChildren().addAll(title, deckGrid);
    }

    public void setOnDeckSelected(Consumer<String> callback) {
        this.onDeckSelected = callback;
    }

    public void setOnCreateDeckRequested(Runnable callback) {
        this.onCreateDeckRequested = callback;
    }

    public void renderDecks(List<Deck> decks) {
        deckGrid.getChildren().clear();
        
        // "+" Kachel hinzufügen
        addPlusTile();
        
        for (Deck deck : decks) {
            addDeckTile(deck.getName());
        }
    }

    private void addPlusTile() {
        VBox tile = new VBox();
        tile.setPrefSize(150, 200);
        tile.setAlignment(Pos.CENTER);
        tile.setStyle("-fx-background-color: white; " +
                     "-fx-border-color: #cccccc; " +
                     "-fx-border-radius: 15; " +
                     "-fx-background-radius: 15; " +
                     "-fx-cursor: hand;");

        Label plusLabel = new Label("+");
        plusLabel.setStyle("-fx-font-size: 60px; -fx-text-fill: #999999;");

        tile.getChildren().add(plusLabel);
        tile.setOnMouseClicked(e -> {
            if (onCreateDeckRequested != null) {
                onCreateDeckRequested.run();
            }
        });

        deckGrid.getChildren().add(tile);
    }

    private void addDeckTile(String name) {
        VBox tile = new VBox();
        tile.setPrefSize(150, 200);
        tile.setAlignment(Pos.CENTER);
        tile.setSpacing(10);
        
        // Styling der Kachel (basierend auf gui.jpeg)
        tile.setStyle("-fx-background-color: white; " +
                     "-fx-border-color: #cccccc; " +
                     "-fx-border-radius: 15; " +
                     "-fx-background-radius: 15; " +
                     "-fx-cursor: hand;");

        // Icon Platzhalter (ähnlich wie im Sketch)
        Rectangle iconPlaceholder = new Rectangle(60, 40);
        iconPlaceholder.setFill(Color.TRANSPARENT);
        iconPlaceholder.setStroke(Color.GRAY);
        iconPlaceholder.setStrokeWidth(1);

        Label deckLabel = new Label(name);
        deckLabel.setStyle("-fx-font-weight: bold;");

        tile.getChildren().addAll(iconPlaceholder, deckLabel);
        
        tile.setOnMouseClicked(e -> {
            if (onDeckSelected != null) {
                onDeckSelected.accept(name);
            }
        });

        deckGrid.getChildren().add(tile);
    }
}
