package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Deck;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.*;
import java.util.function.Consumer;

public class HomeView extends VBox {

    // ── state ──────────────────────────────────────────────────────────────
    private List<Deck> currentDecks = new ArrayList<>();
    private boolean selectMode = false;
    private final Set<Deck> selectedDecks = new LinkedHashSet<>();

    // ── ui references ──────────────────────────────────────────────────────
    private FlowPane deckGrid;
    private Button selectToggleBtn;
    private Button exportSelectedBtn;
    private Button deleteSelectedBtn;

    // ── callbacks ──────────────────────────────────────────────────────────
    private Consumer<Deck> onDeckSelected;
    private Consumer<Deck> onEditDeckRequested;
    private Consumer<Deck> onDeleteDeckRequested;
    private Consumer<Deck> onExportDeckRequested;
    private Consumer<List<Deck>> onExportSelectedDecksRequested;
    private Consumer<List<Deck>> onDeleteSelectedDecksRequested;
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

        Button importBtn = createBtn("Import Deck", "#2196F3", "#1565C0");
        importBtn.setOnAction(e -> { if (onImportDeckRequested != null) onImportDeckRequested.run(); });

        selectToggleBtn = createBtn("Select", "#607D8B", "#455A64");
        selectToggleBtn.setOnAction(e -> toggleSelectMode());

        exportSelectedBtn = createBtn("Export (0)", "#FF9800", "#E65100");
        exportSelectedBtn.setVisible(false);
        exportSelectedBtn.setManaged(false);
        exportSelectedBtn.setDisable(true);
        exportSelectedBtn.setOnAction(e -> {
            if (onExportSelectedDecksRequested != null && !selectedDecks.isEmpty())
                onExportSelectedDecksRequested.accept(new ArrayList<>(selectedDecks));
        });

        deleteSelectedBtn = createBtn("Delete (0)", "#dc3545", "#b02a37");
        deleteSelectedBtn.setVisible(false);
        deleteSelectedBtn.setManaged(false);
        deleteSelectedBtn.setDisable(true);
        deleteSelectedBtn.setOnAction(e -> {
            if (onDeleteSelectedDecksRequested != null && !selectedDecks.isEmpty())
                onDeleteSelectedDecksRequested.accept(new ArrayList<>(selectedDecks));
        });

        HBox right = new HBox(8, importBtn, selectToggleBtn, deleteSelectedBtn, exportSelectedBtn);
        right.setAlignment(Pos.CENTER_RIGHT);

        HBox header = new HBox(10, title, spacer, right);
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
        currentDecks = decks;
        deckGrid.getChildren().clear();
        if (!selectMode) addPlusTile();
        for (Deck deck : decks) addDeckTile(deck);
    }

    // ── select mode ────────────────────────────────────────────────────────

    private void toggleSelectMode() {
        selectMode = !selectMode;
        selectedDecks.clear();
        if (selectMode) {
            selectToggleBtn.setText("Cancel");
            exportSelectedBtn.setVisible(true);
            exportSelectedBtn.setManaged(true);
            deleteSelectedBtn.setVisible(true);
            deleteSelectedBtn.setManaged(true);
        } else {
            selectToggleBtn.setText("Select");
            exportSelectedBtn.setVisible(false);
            exportSelectedBtn.setManaged(false);
            deleteSelectedBtn.setVisible(false);
            deleteSelectedBtn.setManaged(false);
        }
        updateSelectModeButtons();
        renderDecks(currentDecks);
    }

    private void updateSelectModeButtons() {
        int n = selectedDecks.size();
        exportSelectedBtn.setText("Export (" + n + ")");
        exportSelectedBtn.setDisable(n == 0);
        deleteSelectedBtn.setText("Delete (" + n + ")");
        deleteSelectedBtn.setDisable(n == 0);
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
        tile.setOnMouseClicked(e -> { if (onCreateDeckRequested != null) onCreateDeckRequested.run(); });
        deckGrid.getChildren().add(tile);
    }

    private void addDeckTile(Deck deck) {
        boolean isSelected = selectedDecks.contains(deck);
        String borderColor = isSelected ? "#2196F3" : "#cccccc";
        String bgColor     = isSelected ? "#E3F2FD" : "white";
        String borderWidth = isSelected ? "2" : "1";

        StackPane tile = new StackPane();
        tile.setPrefSize(150, 200);
        tile.setStyle(String.format(
            "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: %s; " +
            "-fx-border-radius: 15; -fx-background-radius: 15; -fx-cursor: hand;",
            bgColor, borderColor, borderWidth));

        // ── Centered content ───────────────────────────────────────────────
        VBox content = new VBox(8);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(10));

        if (selectMode) {
            Label check = new Label(isSelected ? "✔" : "○");
            check.setStyle("-fx-font-size: 18px; -fx-text-fill: " + (isSelected ? "#2196F3" : "#bbbbbb") + ";");
            content.getChildren().add(check);
        }

        Image iconImage = IconManager.getIcon(deck.getIconId());
        ImageView iconView = new ImageView(iconImage);
        iconView.setFitWidth(selectMode ? 55 : 75);
        iconView.setFitHeight(selectMode ? 55 : 75);
        iconView.setPreserveRatio(true);

        Label nameLabel = new Label(deck.getName() != null ? deck.getName() : "");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setMaxWidth(130);

        Label countLabel = new Label(deck.getCardCount() + " cards");
        countLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888;");

        content.getChildren().addAll(iconView, nameLabel, countLabel);
        tile.getChildren().add(content);

        if (selectMode) {
            tile.setOnMouseClicked(e -> {
                if (selectedDecks.contains(deck)) selectedDecks.remove(deck);
                else selectedDecks.add(deck);
                updateSelectModeButtons();
                renderDecks(currentDecks);
            });
        } else {
            // ── Overlay: edit / export / delete icons ──────────────────────
            HBox topRow = new HBox(2);
            topRow.setPadding(new Insets(6));
            topRow.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            topRow.setPickOnBounds(false);
            StackPane.setAlignment(topRow, Pos.TOP_LEFT);

            Button editBtn = iconBtn("✎", "#aaaaaa", "#007bff");
            editBtn.setOnAction(e -> { e.consume(); if (onEditDeckRequested != null) onEditDeckRequested.accept(deck); });

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button exportBtn = iconBtn("⬆", "#aaaaaa", "#FF9800");
            exportBtn.setOnAction(e -> { e.consume(); if (onExportDeckRequested != null) onExportDeckRequested.accept(deck); });

            Button deleteBtn = iconBtn("✖", "#aaaaaa", "#dc3545");
            deleteBtn.setOnAction(e -> { e.consume(); if (onDeleteDeckRequested != null) onDeleteDeckRequested.accept(deck); });

            topRow.getChildren().addAll(editBtn, spacer, exportBtn, deleteBtn);
            tile.getChildren().add(topRow);

            tile.setOnMouseClicked(e -> { if (onDeckSelected != null) onDeckSelected.accept(deck); });
        }

        deckGrid.getChildren().add(tile);
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private Button iconBtn(String icon, String normal, String hover) {
        Button btn = new Button(icon);
        String s1 = "-fx-background-color: transparent; -fx-text-fill: " + normal + "; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 3;";
        String s2 = "-fx-background-color: transparent; -fx-text-fill: " + hover  + "; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 3;";
        btn.setStyle(s1);
        btn.setOnMouseEntered(ev -> btn.setStyle(s2));
        btn.setOnMouseExited(ev -> btn.setStyle(s1));
        return btn;
    }

    private Button createBtn(String text, String color, String hoverColor) {
        Button btn = new Button(text);
        String s1 = String.format("-fx-background-color: %s; -fx-border-color: %s; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 14; -fx-cursor: hand; -fx-font-size: 13px; -fx-text-fill: white; -fx-font-weight: bold;", color, hoverColor);
        String s2 = String.format("-fx-background-color: %s; -fx-border-color: %s; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 14; -fx-cursor: hand; -fx-font-size: 13px; -fx-text-fill: white; -fx-font-weight: bold;", hoverColor, hoverColor);
        btn.setStyle(s1);
        btn.setOnMouseEntered(e -> btn.setStyle(s2));
        btn.setOnMouseExited(e -> btn.setStyle(s1));
        return btn;
    }

    // ── callback setters ───────────────────────────────────────────────────

    public void setOnDeckSelected(Consumer<Deck> cb) { this.onDeckSelected = cb; }
    public void setOnEditDeckRequested(Consumer<Deck> cb) { this.onEditDeckRequested = cb; }
    public void setOnDeleteDeckRequested(Consumer<Deck> cb) { this.onDeleteDeckRequested = cb; }
    public void setOnExportDeckRequested(Consumer<Deck> cb) { this.onExportDeckRequested = cb; }
    public void setOnExportSelectedDecksRequested(Consumer<List<Deck>> cb) { this.onExportSelectedDecksRequested = cb; }
    public void setOnDeleteSelectedDecksRequested(Consumer<List<Deck>> cb) { this.onDeleteSelectedDecksRequested = cb; }
    public void setOnCreateDeckRequested(Runnable cb) { this.onCreateDeckRequested = cb; }
    public void setOnImportDeckRequested(Runnable cb) { this.onImportDeckRequested = cb; }
}
