package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Deck;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.List;
import java.util.function.Consumer;

public class HomeView extends VBox {

    // ── ui references ──────────────────────────────────────────────────────
    private FlowPane deckGrid;

    // ── callbacks ──────────────────────────────────────────────────────────
    private Consumer<Deck> onDeckSelected;
    private Consumer<Deck> onEditDeckRequested;
    private Consumer<Deck> onDeleteDeckRequested;
    private Runnable onCreateDeckRequested;
    private Runnable onImportDeckRequested;

    public HomeView() {
        this.setPadding(new Insets(20));
        this.setSpacing(16);

        this.getChildren().addAll(buildHeader(), buildDeckGrid());
    }

    // ── layout builders ────────────────────────────────────────────────────

    private HBox buildHeader() {
        Label title = new Label("My Decks");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button importBtn = createActionButton("Import Deck", "#2196F3", "#1565C0");
        importBtn.setOnAction(e -> { if (onImportDeckRequested != null) onImportDeckRequested.run(); });

        HBox header = new HBox(10, title, spacer, importBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private ScrollPane buildDeckGrid() {
        deckGrid = new FlowPane();
        deckGrid.setHgap(20);
        deckGrid.setVgap(20);
        deckGrid.setPadding(new Insets(4));

        ScrollPane sp = new ScrollPane(deckGrid);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(sp, Priority.ALWAYS);
        return sp;
    }

    // ── public API ─────────────────────────────────────────────────────────

    public void renderDecks(List<Deck> decks) {
        deckGrid.getChildren().clear();
        addPlusTile();
        for (Deck deck : decks) {
            addDeckTile(deck);
        }
    }

    // ── tile builders ──────────────────────────────────────────────────────

    private void addPlusTile() {
        StackPane tile = new StackPane();
        tile.setPrefSize(150, 200);
        tile.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1; " +
                      "-fx-border-radius: 15; -fx-background-radius: 15; -fx-cursor: hand;");

        Label plusLabel = new Label("+");
        plusLabel.setStyle("-fx-font-size: 60px; -fx-text-fill: #999999;");

        tile.getChildren().add(plusLabel);
        tile.setOnMouseClicked(e -> {
            if (onCreateDeckRequested != null) onCreateDeckRequested.run();
        });

        deckGrid.getChildren().add(tile);
    }

    private void addDeckTile(Deck deck) {
        StackPane tile = new StackPane();
        tile.setPrefSize(150, 200);
        tile.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1; " +
                      "-fx-border-radius: 15; -fx-background-radius: 15; -fx-cursor: hand;");

        // ── Centered content (icon + name) ─────────────────────────────────
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);

        Image iconImage = IconManager.getIcon(deck.getIconId());
        ImageView iconView = new ImageView(iconImage);
        iconView.setFitWidth(80);
        iconView.setFitHeight(80);
        iconView.setPreserveRatio(true);

        Label deckLabel = new Label(deck.getName() != null ? deck.getName() : "");
        deckLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        deckLabel.setWrapText(true);
        deckLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        deckLabel.setAlignment(Pos.CENTER);
        deckLabel.setMaxWidth(130);

        content.getChildren().addAll(iconView, deckLabel);

        // ── Overlay row for edit / delete buttons ───────────────────────────
        HBox topRow = new HBox();
        topRow.setPadding(new Insets(8));
        topRow.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        // pickOnBounds=false means transparent areas in topRow don't swallow
        // mouse events intended for the tile click handler
        topRow.setPickOnBounds(false);
        StackPane.setAlignment(topRow, Pos.TOP_LEFT);

        Button editBtn = new Button("✎");
        editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;");
        editBtn.setOnMouseEntered(ev -> editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #007bff; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;"));
        editBtn.setOnMouseExited(ev -> editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;"));
        editBtn.setOnAction(e -> {
            e.consume();
            if (onEditDeckRequested != null) onEditDeckRequested.accept(deck);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button deleteBtn = new Button("✖");
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;");
        deleteBtn.setOnMouseEntered(ev -> deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #dc3545; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;"));
        deleteBtn.setOnMouseExited(ev -> deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;"));
        deleteBtn.setOnAction(e -> {
            e.consume();
            if (onDeleteDeckRequested != null) onDeleteDeckRequested.accept(deck);
        });

        topRow.getChildren().addAll(editBtn, spacer, deleteBtn);

        tile.getChildren().addAll(content, topRow);

        tile.setOnMouseClicked(e -> {
            if (onDeckSelected != null) onDeckSelected.accept(deck);
        });

        deckGrid.getChildren().add(tile);
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private Button createActionButton(String text, String color, String hoverColor) {
        Button btn = new Button(text);
        String def  = String.format("-fx-background-color: %s; -fx-border-color: %s; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 14; -fx-cursor: hand; -fx-font-size: 13px; -fx-text-fill: white; -fx-font-weight: bold;", color, hoverColor);
        String hover = String.format("-fx-background-color: %s; -fx-border-color: %s; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 14; -fx-cursor: hand; -fx-font-size: 13px; -fx-text-fill: white; -fx-font-weight: bold;", hoverColor, hoverColor);
        btn.setStyle(def);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(def));
        return btn;
    }

    // ── callback setters ───────────────────────────────────────────────────

    public void setOnDeckSelected(Consumer<Deck> cb) { this.onDeckSelected = cb; }
    public void setOnEditDeckRequested(Consumer<Deck> cb) { this.onEditDeckRequested = cb; }
    public void setOnDeleteDeckRequested(Consumer<Deck> cb) { this.onDeleteDeckRequested = cb; }
    public void setOnCreateDeckRequested(Runnable cb) { this.onCreateDeckRequested = cb; }
    public void setOnImportDeckRequested(Runnable cb) { this.onImportDeckRequested = cb; }
}
