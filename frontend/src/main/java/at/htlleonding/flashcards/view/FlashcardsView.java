package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Card;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class FlashcardsView extends HBox {

    // ── state ──────────────────────────────────────────────────────────────
    private List<Card> currentCards = new ArrayList<>();
    private boolean selectMode = false;
    private final Set<Card> selectedCards = new LinkedHashSet<>();
    private Card selectedDetailCard = null;

    // ── ui references ──────────────────────────────────────────────────────
    private FlowPane cardsGrid;
    private VBox detailPanel;
    private Button selectToggleBtn;
    private Button exportSelectedBtn;

    // ── callbacks ──────────────────────────────────────────────────────────
    private Runnable onAddCardRequested;
    private Consumer<Card> onEditCardRequested;
    private Consumer<Card> onDeleteCardRequested;
    private Runnable onImportRequested;
    private Consumer<Card> onExportCardRequested; // export single card
    private Consumer<List<Card>> onExportSelectedCardsRequested;

    public FlashcardsView() {
        this.setPadding(new Insets(20));
        this.setSpacing(20);

        VBox leftSide = buildLeftSide();
        HBox.setHgrow(leftSide, Priority.ALWAYS);

        detailPanel = buildDetailPanel();

        this.getChildren().addAll(leftSide, detailPanel);
    }

    // ── layout builders ────────────────────────────────────────────────────

    private VBox buildLeftSide() {
        HBox actionBar = new HBox(10);
        actionBar.setAlignment(Pos.CENTER_LEFT);

        Button importBtn = createActionButton("Import", "#2196F3", "#1565C0");
        importBtn.setOnAction(e -> { if (onImportRequested != null) onImportRequested.run(); });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        selectToggleBtn = createActionButton("Select", "#607D8B", "#455A64");
        selectToggleBtn.setOnAction(e -> toggleSelectMode());

        exportSelectedBtn = createActionButton("Export Selected (0)", "#FF9800", "#E65100");
        exportSelectedBtn.setVisible(false);
        exportSelectedBtn.setManaged(false);
        exportSelectedBtn.setOnAction(e -> {
            if (onExportSelectedCardsRequested != null && !selectedCards.isEmpty()) {
                onExportSelectedCardsRequested.accept(new ArrayList<>(selectedCards));
            }
        });

        actionBar.getChildren().addAll(importBtn, spacer, selectToggleBtn, exportSelectedBtn);

        cardsGrid = new FlowPane();
        cardsGrid.setHgap(15);
        cardsGrid.setVgap(15);
        cardsGrid.setPadding(new Insets(10));
        cardsGrid.setPrefWrapLength(600);

        ScrollPane scrollPane = new ScrollPane(cardsGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        addPlusCard();

        VBox left = new VBox(15, actionBar, scrollPane);
        return left;
    }

    private VBox buildDetailPanel() {
        VBox panel = new VBox();
        panel.setPrefWidth(230);
        panel.setMinWidth(230);
        panel.setPadding(new Insets(16));
        panel.setSpacing(0);
        panel.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #e0e0e0; -fx-border-radius: 12; -fx-background-radius: 12;");

        Label placeholder = new Label("Select a card\nto see details");
        placeholder.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 14px;");
        placeholder.setAlignment(Pos.CENTER);
        placeholder.setMaxWidth(Double.MAX_VALUE);
        placeholder.setMaxHeight(Double.MAX_VALUE);
        placeholder.setTextAlignment(TextAlignment.CENTER);
        VBox.setVgrow(placeholder, Priority.ALWAYS);

        panel.getChildren().add(placeholder);
        return panel;
    }

    private void showCardDetail(Card card) {
        selectedDetailCard = card;
        detailPanel.getChildren().clear();
        detailPanel.setSpacing(10);

        // ── Question box ──────────────────────────────────────────────────
        VBox questionBox = new VBox(6);
        questionBox.setPadding(new Insets(12));
        questionBox.setStyle("-fx-background-color: #E3F2FD; -fx-border-color: #90CAF9; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label qHeader = new Label("QUESTION");
        qHeader.setStyle("-fx-font-size: 10px; -fx-text-fill: #1565C0; -fx-font-weight: bold;");

        Label qText = new Label(card.getQuestion());
        qText.setWrapText(true);
        qText.setStyle("-fx-font-size: 14px; -fx-text-fill: #0D47A1;");

        questionBox.getChildren().addAll(qHeader, qText);

        // ── Answer box ────────────────────────────────────────────────────
        VBox answerBox = new VBox(6);
        answerBox.setPadding(new Insets(12));
        answerBox.setStyle("-fx-background-color: #E8F5E9; -fx-border-color: #A5D6A7; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label aHeader = new Label("ANSWER");
        aHeader.setStyle("-fx-font-size: 10px; -fx-text-fill: #2E7D32; -fx-font-weight: bold;");

        Label aText = new Label(card.getAnswer());
        aText.setWrapText(true);
        aText.setStyle("-fx-font-size: 14px; -fx-text-fill: #1B5E20;");

        answerBox.getChildren().addAll(aHeader, aText);

        detailPanel.getChildren().addAll(questionBox, answerBox);

        // ── Tags ──────────────────────────────────────────────────────────
        if (card.getTags() != null && !card.getTags().isEmpty()) {
            FlowPane tagsPane = new FlowPane(6, 6);
            tagsPane.setPadding(new Insets(4, 0, 0, 0));
            for (String tag : card.getTags()) {
                Label chip = new Label(tag);
                chip.setPadding(new Insets(3, 8, 3, 8));
                chip.setStyle("-fx-background-color: #EEEEEE; -fx-border-color: #BDBDBD; -fx-border-radius: 12; -fx-background-radius: 12; -fx-font-size: 11px; -fx-text-fill: #555555;");
                tagsPane.getChildren().add(chip);
            }
            detailPanel.getChildren().add(tagsPane);
        }

        // ── Spacer ────────────────────────────────────────────────────────
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        detailPanel.getChildren().add(spacer);

        // ── Buttons ───────────────────────────────────────────────────────
        Button editBtn = createActionButton("Edit", "#607D8B", "#455A64");
        editBtn.setMaxWidth(Double.MAX_VALUE);
        editBtn.setOnAction(e -> { if (onEditCardRequested != null) onEditCardRequested.accept(card); });

        Button exportCardBtn = createActionButton("Export Card", "#FF9800", "#E65100");
        exportCardBtn.setMaxWidth(Double.MAX_VALUE);
        exportCardBtn.setOnAction(e -> { if (onExportCardRequested != null) onExportCardRequested.accept(card); });

        VBox btnBox = new VBox(8, editBtn, exportCardBtn);
        detailPanel.getChildren().add(btnBox);
    }

    // ── select mode ────────────────────────────────────────────────────────

    private void toggleSelectMode() {
        selectMode = !selectMode;
        selectedCards.clear();
        if (selectMode) {
            selectToggleBtn.setText("Cancel");
            exportSelectedBtn.setVisible(true);
            exportSelectedBtn.setManaged(true);
            updateExportSelectedLabel();
            // Reset detail panel to placeholder while in select mode
            buildDetailPanel();
            detailPanel.getChildren().clear();
            Label hint = new Label("Select cards\nto export");
            hint.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 14px;");
            hint.setAlignment(Pos.CENTER);
            hint.setMaxWidth(Double.MAX_VALUE);
            hint.setMaxHeight(Double.MAX_VALUE);
            hint.setTextAlignment(TextAlignment.CENTER);
            VBox.setVgrow(hint, Priority.ALWAYS);
            detailPanel.getChildren().add(hint);
        } else {
            selectToggleBtn.setText("Select");
            exportSelectedBtn.setVisible(false);
            exportSelectedBtn.setManaged(false);
        }
        renderCards(currentCards);
    }

    private void updateExportSelectedLabel() {
        exportSelectedBtn.setText("Export Selected (" + selectedCards.size() + ")");
        exportSelectedBtn.setDisable(selectedCards.isEmpty());
    }

    // ── card rendering ─────────────────────────────────────────────────────

    private void addPlusCard() {
        VBox plusCard = new VBox();
        plusCard.setPrefSize(120, 160);
        plusCard.setAlignment(Pos.CENTER);
        plusCard.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;");
        Label plusLabel = new Label("+");
        plusLabel.setStyle("-fx-font-size: 40px; -fx-text-fill: #999999;");
        plusCard.getChildren().add(plusLabel);
        plusCard.setOnMouseClicked(e -> { if (onAddCardRequested != null) onAddCardRequested.run(); });
        cardsGrid.getChildren().add(plusCard);
    }

    public void renderCards(List<Card> cards) {
        currentCards = cards;
        cardsGrid.getChildren().clear();
        addPlusCard();

        for (Card card : cards) {
            boolean isSelected = selectedCards.contains(card);
            String borderColor = isSelected ? "#2196F3" : "#cccccc";
            String bgColor = isSelected ? "#E3F2FD" : "white";

            VBox cardTile = new VBox();
            cardTile.setPrefSize(120, 160);
            cardTile.setPadding(new Insets(5));
            cardTile.setAlignment(Pos.TOP_CENTER);
            cardTile.setStyle(String.format(
                "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: %s; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;",
                bgColor, borderColor, isSelected ? "2" : "1"
            ));

            HBox topBox = new HBox();

            if (selectMode) {
                Label checkmark = new Label(isSelected ? "✔" : "○");
                checkmark.setStyle("-fx-font-size: 14px; -fx-text-fill: " + (isSelected ? "#2196F3" : "#bbbbbb") + ";");
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                topBox.getChildren().addAll(checkmark, spacer);
            } else {
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Button deleteBtn = new Button("✖");
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;");
                deleteBtn.setOnMouseEntered(ev -> deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #dc3545; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;"));
                deleteBtn.setOnMouseExited(ev -> deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;"));
                deleteBtn.setOnAction(e -> {
                    e.consume();
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Card");
                    alert.setHeaderText("Delete Flashcard");
                    alert.setContentText("Are you sure you want to delete this card?");
                    alert.showAndWait().ifPresent(r -> {
                        if (r == ButtonType.OK && onDeleteCardRequested != null) onDeleteCardRequested.accept(card);
                    });
                });
                topBox.getChildren().addAll(spacer, deleteBtn);
            }

            Label qLabel = new Label(card.getQuestion());
            qLabel.setWrapText(true);
            qLabel.setTextAlignment(TextAlignment.CENTER);
            qLabel.setAlignment(Pos.CENTER);
            qLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            qLabel.setStyle("-fx-text-fill: black;");
            VBox.setVgrow(qLabel, Priority.ALWAYS);

            cardTile.getChildren().addAll(topBox, qLabel);

            cardTile.setOnMouseClicked(e -> {
                if (selectMode) {
                    if (selectedCards.contains(card)) selectedCards.remove(card);
                    else selectedCards.add(card);
                    updateExportSelectedLabel();
                    renderCards(currentCards);
                } else {
                    showCardDetail(card);
                }
            });

            cardsGrid.getChildren().add(cardTile);
        }
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

    public void setOnAddCardRequested(Runnable cb) { this.onAddCardRequested = cb; }
    public void setOnEditCardRequested(Consumer<Card> cb) { this.onEditCardRequested = cb; }
    public void setOnDeleteCardRequested(Consumer<Card> cb) { this.onDeleteCardRequested = cb; }
    public void setOnImportRequested(Runnable cb) { this.onImportRequested = cb; }
    public void setOnExportCardRequested(Consumer<Card> cb) { this.onExportCardRequested = cb; }
    public void setOnExportSelectedCardsRequested(Consumer<List<Card>> cb) { this.onExportSelectedCardsRequested = cb; }
}
