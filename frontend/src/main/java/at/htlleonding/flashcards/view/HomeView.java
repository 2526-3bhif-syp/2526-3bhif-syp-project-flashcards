package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Deck;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.function.Consumer;

public class HomeView extends VBox {
    private FlowPane deckGrid;
    private Consumer<Deck> onDeckSelected;
    private Consumer<Deck> onEditDeckRequested;
    private Consumer<Deck> onDeleteDeckRequested;
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

    public void setOnDeckSelected(Consumer<Deck> callback) {
        this.onDeckSelected = callback;
    }

    public void setOnEditDeckRequested(Consumer<Deck> callback) {
        this.onEditDeckRequested = callback;
    }

    public void setOnDeleteDeckRequested(Consumer<Deck> callback) {
        this.onDeleteDeckRequested = callback;
    }

    public void setOnCreateDeckRequested(Runnable callback) {
        this.onCreateDeckRequested = callback;
    }

    public void renderDecks(List<Deck> decks) {
        deckGrid.getChildren().clear();
        
        // "+" Kachel hinzufügen
        addPlusTile();
        
        for (Deck deck : decks) {
            addDeckTile(deck);
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

    private void addDeckTile(Deck deck) {
        VBox tile = new VBox();
        tile.setPrefSize(150, 200);
        tile.setPadding(new Insets(5));
        tile.setAlignment(Pos.TOP_CENTER);
        tile.setSpacing(10);
        
        // Styling der Kachel
        tile.setStyle("-fx-background-color: white; " +
                     "-fx-border-color: #cccccc; " +
                     "-fx-border-radius: 15; " +
                     "-fx-background-radius: 15; " +
                     "-fx-cursor: hand;");

        // Action Buttons oben
        HBox topBox = new HBox();
        topBox.setAlignment(Pos.TOP_CENTER);
        
        Button editBtn = new Button("✎");
        editBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 2 6; -fx-background-radius: 5;");
        editBtn.setOnAction(e -> {
            e.consume();
            if (onEditDeckRequested != null) {
                onEditDeckRequested.accept(deck);
            }
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button deleteBtn = new Button("✖");
        deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 2 6; -fx-background-radius: 5;");
        deleteBtn.setOnAction(e -> {
            e.consume();
            if (onDeleteDeckRequested != null) {
                onDeleteDeckRequested.accept(deck);
            }
        });
        
        topBox.getChildren().addAll(editBtn, spacer, deleteBtn);

        // Icon Platzhalter
        Rectangle iconPlaceholder = new Rectangle(60, 40);
        iconPlaceholder.setFill(Color.TRANSPARENT);
        iconPlaceholder.setStroke(Color.GRAY);
        iconPlaceholder.setStrokeWidth(1);

        Label deckLabel = new Label(deck.getName());
        deckLabel.setStyle("-fx-font-weight: bold;");
        deckLabel.setWrapText(true);
        deckLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        tile.getChildren().addAll(topBox, iconPlaceholder, deckLabel);
        
        tile.setOnMouseClicked(e -> {
            if (onDeckSelected != null) {
                onDeckSelected.accept(deck);
            }
        });

        deckGrid.getChildren().add(tile);
    }
}
