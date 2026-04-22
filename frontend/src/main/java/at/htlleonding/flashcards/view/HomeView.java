package at.htlleonding.flashcards.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class HomeView extends VBox {

    // ── state ──────────────────────────────────────────────────────────────
    private List<String> currentDeckNames = new ArrayList<>();
    private boolean selectMode = false;
    private final Set<String> selectedDeckNames = new LinkedHashSet<>();

    // ── ui references ──────────────────────────────────────────────────────
    private FlowPane deckGrid;
    private Button selectToggleBtn;
    private Button exportSelectedBtn;

    // ── callbacks ──────────────────────────────────────────────────────────
    private Consumer<String> onDeckSelected;
    private Runnable onImportDeckRequested;
    private Consumer<String> onExportDeckRequested;
    private Consumer<List<String>> onExportSelectedDecksRequested;

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

        selectToggleBtn = createActionButton("Select", "#607D8B", "#455A64");
        selectToggleBtn.setOnAction(e -> toggleSelectMode());

        exportSelectedBtn = createActionButton("Export Selected (0)", "#FF9800", "#E65100");
        exportSelectedBtn.setVisible(false);
        exportSelectedBtn.setManaged(false);
        exportSelectedBtn.setDisable(true);
        exportSelectedBtn.setOnAction(e -> {
            if (onExportSelectedDecksRequested != null && !selectedDeckNames.isEmpty()) {
                onExportSelectedDecksRequested.accept(new ArrayList<>(selectedDeckNames));
            }
        });

        HBox header = new HBox(10, title, spacer, importBtn, selectToggleBtn, exportSelectedBtn);
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

    public void renderDecks(List<String> deckNames) {
        currentDeckNames = deckNames;
        deckGrid.getChildren().clear();
        for (String name : deckNames) {
            deckGrid.getChildren().add(buildDeckTile(name));
        }
    }

    // ── select mode ────────────────────────────────────────────────────────

    private void toggleSelectMode() {
        selectMode = !selectMode;
        selectedDeckNames.clear();
        if (selectMode) {
            selectToggleBtn.setText("Cancel");
            exportSelectedBtn.setVisible(true);
            exportSelectedBtn.setManaged(true);
            updateExportSelectedLabel();
        } else {
            selectToggleBtn.setText("Select");
            exportSelectedBtn.setVisible(false);
            exportSelectedBtn.setManaged(false);
        }
        renderDecks(currentDeckNames);
    }

    private void updateExportSelectedLabel() {
        exportSelectedBtn.setText("Export Selected (" + selectedDeckNames.size() + ")");
        exportSelectedBtn.setDisable(selectedDeckNames.isEmpty());
    }

    // ── tile builder ───────────────────────────────────────────────────────

    private VBox buildDeckTile(String name) {
        boolean isSelected = selectedDeckNames.contains(name);
        String borderColor = isSelected ? "#2196F3" : "#cccccc";
        String bgColor = isSelected ? "#E3F2FD" : "white";
        String borderWidth = isSelected ? "2" : "1";

        VBox tile = new VBox();
        tile.setPrefSize(150, 200);
        tile.setAlignment(Pos.TOP_CENTER);
        tile.setSpacing(0);
        tile.setStyle(String.format(
            "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: %s; -fx-border-radius: 15; -fx-background-radius: 15; -fx-cursor: hand;",
            bgColor, borderColor, borderWidth
        ));

        // Top row: checkmark (select mode) or export button (normal mode)
        HBox topRow = new HBox();
        topRow.setPadding(new Insets(8, 8, 0, 8));
        if (selectMode) {
            Label check = new Label(isSelected ? "✔" : "○");
            check.setStyle("-fx-font-size: 14px; -fx-text-fill: " + (isSelected ? "#2196F3" : "#bbbbbb") + ";");
            topRow.getChildren().add(check);
        } else {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Button exportBtn = new Button("↑");
            exportBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;");
            exportBtn.setOnMouseEntered(ev -> exportBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2196F3; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;"));
            exportBtn.setOnMouseExited(ev -> exportBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;"));
            exportBtn.setOnAction(e -> {
                e.consume();
                if (onExportDeckRequested != null) onExportDeckRequested.accept(name);
            });
            topRow.getChildren().addAll(spacer, exportBtn);
        }

        // Center icon + label
        VBox center = new VBox(10);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(10, 0, 10, 0));
        VBox.setVgrow(center, Priority.ALWAYS);

        Rectangle iconPlaceholder = new Rectangle(60, 40);
        iconPlaceholder.setFill(Color.TRANSPARENT);
        iconPlaceholder.setStroke(Color.GRAY);
        iconPlaceholder.setStrokeWidth(1);

        Label deckLabel = new Label(name);
        deckLabel.setStyle("-fx-font-weight: bold;");

        center.getChildren().addAll(iconPlaceholder, deckLabel);

        tile.getChildren().addAll(topRow, center);

        tile.setOnMouseClicked(e -> {
            if (selectMode) {
                if (selectedDeckNames.contains(name)) selectedDeckNames.remove(name);
                else selectedDeckNames.add(name);
                updateExportSelectedLabel();
                renderDecks(currentDeckNames);
            } else {
                if (onDeckSelected != null) onDeckSelected.accept(name);
            }
        });

        return tile;
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

    public void setOnDeckSelected(Consumer<String> cb) { this.onDeckSelected = cb; }
    public void setOnImportDeckRequested(Runnable cb) { this.onImportDeckRequested = cb; }
    public void setOnExportDeckRequested(Consumer<String> cb) { this.onExportDeckRequested = cb; }
    public void setOnExportSelectedDecksRequested(Consumer<List<String>> cb) { this.onExportSelectedDecksRequested = cb; }
}
