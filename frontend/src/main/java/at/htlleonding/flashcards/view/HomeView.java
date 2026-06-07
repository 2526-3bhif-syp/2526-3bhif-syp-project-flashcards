package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Deck;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import at.htlleonding.flashcards.model.ThemeProvider;
import at.htlleonding.flashcards.model.TranslationProvider;
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
    private Button importBtn;

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
        TranslationProvider.localeProperty().addListener((obs, oldLocale, newLocale) -> {
            updateTextOnLocaleChange();
        });
    }

    public void applyTheme() {
        renderDecks(currentDecks);
        rebuildHeaderButtons();
    }

    private void rebuildHeaderButtons() {
        String importColor = ThemeProvider.get("accent-blue");
        String exportColor = ThemeProvider.get("accent-green");
        String deleteColor = ThemeProvider.get("accent-red");
        String deleteHover = ThemeProvider.get("accent-red-hover");
        String neutral = ThemeProvider.get("neutral-gray");
        String neutralDark = ThemeProvider.get("neutral-gray-dark");
        importBtn.setStyle(buildSubtleBtnStyle(importColor));
        selectToggleBtn.setStyle(buildBtnStyle(neutral, neutralDark));
        deleteSelectedBtn.setStyle(buildBtnStyle(deleteColor, deleteHover));
        exportSelectedBtn.setStyle(buildSubtleBtnStyle(exportColor));
    }

    // ── layout builders ────────────────────────────────────────────────────

    private HBox buildHeader() {
        Label title = new Label();
        title.textProperty().bind(TranslationProvider.createStringBinding("home.title"));
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        importBtn = createSubtleBtn(TranslationProvider.get("home.import_deck"), ThemeProvider.get("accent-blue"));
        importBtn.textProperty().bind(TranslationProvider.createStringBinding("home.import_deck"));
        importBtn.setOnAction(e -> { if (onImportDeckRequested != null) onImportDeckRequested.run(); });

        selectToggleBtn = createBtn(TranslationProvider.get("home.select"), ThemeProvider.get("neutral-gray"), ThemeProvider.get("neutral-gray-dark"));
        selectToggleBtn.setOnAction(e -> toggleSelectMode());

        exportSelectedBtn = createSubtleBtn(TranslationProvider.get("home.export_selected", 0), ThemeProvider.get("accent-green"));
        exportSelectedBtn.setVisible(false);
        exportSelectedBtn.setManaged(false);
        exportSelectedBtn.setDisable(true);
        exportSelectedBtn.setOnAction(e -> {
            if (onExportSelectedDecksRequested != null && !selectedDecks.isEmpty())
                onExportSelectedDecksRequested.accept(new ArrayList<>(selectedDecks));
        });

        deleteSelectedBtn = createBtn(TranslationProvider.get("home.delete_selected", 0), ThemeProvider.get("accent-red"), ThemeProvider.get("accent-red-hover"));
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

    public void exitSelectMode() {
        if (!selectMode) return;
        selectMode = false;
        selectedDecks.clear();
        selectToggleBtn.setText(TranslationProvider.get("home.select"));
        exportSelectedBtn.setVisible(false);
        exportSelectedBtn.setManaged(false);
        deleteSelectedBtn.setVisible(false);
        deleteSelectedBtn.setManaged(false);
    }

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
            selectToggleBtn.setText(TranslationProvider.get("home.cancel"));
            exportSelectedBtn.setVisible(true);
            exportSelectedBtn.setManaged(true);
            deleteSelectedBtn.setVisible(true);
            deleteSelectedBtn.setManaged(true);
        } else {
            selectToggleBtn.setText(TranslationProvider.get("home.select"));
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
        exportSelectedBtn.setText(TranslationProvider.get("home.export_selected", n));
        exportSelectedBtn.setDisable(n == 0);
        deleteSelectedBtn.setText(TranslationProvider.get("home.delete_selected", n));
        deleteSelectedBtn.setDisable(n == 0);
    }

    // ── tile builders ──────────────────────────────────────────────────────

    private void addPlusTile() {
        StackPane tile = new StackPane();
        tile.setPrefSize(150, 200);
        tile.setStyle("-fx-background-color: " + ThemeProvider.get("bg-card") + "; -fx-border-color: " + ThemeProvider.get("border-default") + "; -fx-border-width: 1; " +
                      "-fx-border-radius: 15; -fx-background-radius: 15; -fx-cursor: hand;");
        Label plusLabel = new Label("+");
        plusLabel.setStyle("-fx-font-size: 60px; -fx-text-fill: " + ThemeProvider.get("text-disabled") + ";");
        tile.getChildren().add(plusLabel);
        tile.setOnMouseClicked(e -> { if (onCreateDeckRequested != null) onCreateDeckRequested.run(); });
        deckGrid.getChildren().add(tile);
    }

    private void addDeckTile(Deck deck) {
        boolean isSelected = selectedDecks.contains(deck);
        String borderColor = isSelected ? ThemeProvider.get("accent-blue") : ThemeProvider.get("border-default");
        String bgColor     = isSelected ? ThemeProvider.get("accent-blue-bg") : ThemeProvider.get("bg-card");
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
            check.setStyle("-fx-font-size: 18px; -fx-text-fill: " + (isSelected ? ThemeProvider.get("accent-blue") : ThemeProvider.get("text-hint")) + ";");
            content.getChildren().add(check);
        }

        Image iconImage = IconManager.getIcon(deck.getIconId());
        ImageView iconView = new ImageView(iconImage);
        iconView.setFitWidth(selectMode ? 55 : 75);
        iconView.setFitHeight(selectMode ? 55 : 75);
        iconView.setPreserveRatio(true);
        iconView.setSmooth(true);

        Label nameLabel = new Label(deck.getName() != null ? deck.getName() : "");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-primary") + ";");
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setMaxWidth(130);

        Label countLabel = new Label();
        countLabel.textProperty().bind(TranslationProvider.createStringBinding("home.cards", deck.getCardCount()));
        countLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + ThemeProvider.get("text-muted") + ";");

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

            Button editBtn = iconBtn("✎", ThemeProvider.get("text-placeholder"), ThemeProvider.get("accent-link"));
            editBtn.setOnAction(e -> { e.consume(); if (onEditDeckRequested != null) onEditDeckRequested.accept(deck); });

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button exportBtn = iconBtn("⬆", ThemeProvider.get("text-placeholder"), ThemeProvider.get("accent-orange"));
            exportBtn.setOnAction(e -> { e.consume(); if (onExportDeckRequested != null) onExportDeckRequested.accept(deck); });

            Button deleteBtn = iconBtn("✖", ThemeProvider.get("text-placeholder"), ThemeProvider.get("accent-red"));
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

    private static String buildBtnStyle(String color, String hoverColor) {
        return String.format("-fx-background-color: %s; -fx-border-color: %s; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 14; -fx-cursor: hand; -fx-font-size: 13px; -fx-text-fill: white; -fx-font-weight: bold;", color, hoverColor);
    }

    private Button createBtn(String text, String color, String hoverColor) {
        Button btn = new Button(text);
        String s1 = buildBtnStyle(color, hoverColor);
        String s2 = buildBtnStyle(hoverColor, hoverColor);
        btn.setStyle(s1);
        btn.setOnMouseEntered(e -> btn.setStyle(s2));
        btn.setOnMouseExited(e -> btn.setStyle(s1));
        return btn;
    }

    private static String buildSubtleBtnStyle(String accentColor) {
        return String.format(
            "-fx-background-color: " + ThemeProvider.get("bg-card") + "; -fx-border-color: %s; -fx-border-width: 1.5; -fx-border-radius: 8; " +
            "-fx-background-radius: 8; -fx-padding: 10 16; -fx-cursor: hand; -fx-font-size: 13px; " +
            "-fx-text-fill: " + ThemeProvider.get("fg-black") + "; -fx-font-weight: bold;",
            accentColor
        );
    }

    private Button createSubtleBtn(String text, String accentColor) {
        Button btn = new Button(text);
        String s1 = buildSubtleBtnStyle(accentColor);
        String s2 = String.format(
            "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 1.5; -fx-border-radius: 8; " +
            "-fx-background-radius: 8; -fx-padding: 10 16; -fx-cursor: hand; -fx-font-size: 13px; " +
            "-fx-text-fill: white; -fx-font-weight: bold;",
            accentColor, accentColor
        );
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

    private void updateTextOnLocaleChange() {
        selectToggleBtn.setText(TranslationProvider.get(selectMode ? "home.cancel" : "home.select"));
        updateSelectModeButtons();
    }
}

