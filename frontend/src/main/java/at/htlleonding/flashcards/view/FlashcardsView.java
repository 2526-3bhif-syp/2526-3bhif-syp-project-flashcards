package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Card;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;

import java.util.List;
import java.util.function.Consumer;

public class FlashcardsView extends HBox {
    private FlowPane cardsGrid;
    private VBox detailPanel;

    private Runnable onAddCardRequested;
    private Consumer<Card> onEditCardRequested;
    private Consumer<Card> onDeleteCardRequested;
    private Runnable onImportRequested;
    private Runnable onExportRequested;

    public FlashcardsView() {
        this.setPadding(new Insets(20));
        this.setSpacing(20);

        VBox leftSide = buildLeftSide();
        HBox.setHgrow(leftSide, Priority.ALWAYS);

        detailPanel = buildDetailPanel();

        this.getChildren().addAll(leftSide, detailPanel);
    }

    private VBox buildLeftSide() {
        HBox actionBar = new HBox(10);
        actionBar.setAlignment(Pos.CENTER_LEFT);

        Button importBtn = createActionButton("Import");
        Button exportBtn = createActionButton("Export");
        importBtn.setOnAction(e -> { if (onImportRequested != null) onImportRequested.run(); });
        exportBtn.setOnAction(e -> { if (onExportRequested != null) onExportRequested.run(); });

        actionBar.getChildren().addAll(importBtn, exportBtn);

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
        panel.setPrefWidth(220);
        panel.setMinWidth(220);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label placeholder = new Label("Select a card\nto see details");
        placeholder.setStyle("-fx-text-fill: #999999; -fx-font-size: 14px;");
        placeholder.setAlignment(Pos.CENTER);
        placeholder.setMaxWidth(Double.MAX_VALUE);
        placeholder.setTextAlignment(TextAlignment.CENTER);
        VBox.setVgrow(placeholder, Priority.ALWAYS);

        panel.getChildren().add(placeholder);
        return panel;
    }

    private void showCardDetail(Card card) {
        detailPanel.getChildren().clear();

        Label qHeader = new Label("Question");
        qHeader.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888; -fx-font-weight: bold;");

        Label qText = new Label(card.getQuestion());
        qText.setWrapText(true);
        qText.setStyle("-fx-font-size: 14px; -fx-text-fill: #222222;");

        Separator sep1 = new Separator();
        VBox.setMargin(sep1, new Insets(8, 0, 8, 0));

        Label aHeader = new Label("Answer");
        aHeader.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888; -fx-font-weight: bold;");

        Label aText = new Label(card.getAnswer());
        aText.setWrapText(true);
        aText.setStyle("-fx-font-size: 14px; -fx-text-fill: #222222;");

        detailPanel.getChildren().addAll(qHeader, qText, sep1, aHeader, aText);

        if (card.getTags() != null && !card.getTags().isEmpty()) {
            Separator sep2 = new Separator();
            VBox.setMargin(sep2, new Insets(8, 0, 8, 0));

            Label tHeader = new Label("Tags");
            tHeader.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888; -fx-font-weight: bold;");

            Label tText = new Label(String.join(", ", card.getTags()));
            tText.setWrapText(true);
            tText.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

            detailPanel.getChildren().addAll(sep2, tHeader, tText);
        }
    }

    private Button createActionButton(String text) {
        Button btn = new Button(text);
        String defaultStyle = "-fx-background-color: #2196F3; -fx-border-color: #1976D2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand; -fx-font-size: 13px; -fx-text-fill: white; -fx-font-weight: bold;";
        String hoverStyle   = "-fx-background-color: #1565C0; -fx-border-color: #0D47A1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand; -fx-font-size: 13px; -fx-text-fill: white; -fx-font-weight: bold;";
        btn.setStyle(defaultStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(defaultStyle));
        return btn;
    }

    private void addPlusCard() {
        VBox plusCard = new VBox();
        plusCard.setPrefSize(120, 160);
        plusCard.setAlignment(Pos.CENTER);
        plusCard.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;");

        Label plusLabel = new Label("+");
        plusLabel.setStyle("-fx-font-size: 40px; -fx-text-fill: #999999;");
        plusCard.getChildren().add(plusLabel);

        plusCard.setOnMouseClicked(e -> {
            if (onAddCardRequested != null) onAddCardRequested.run();
        });

        cardsGrid.getChildren().add(plusCard);
    }

    public void renderCards(List<Card> cards) {
        cardsGrid.getChildren().clear();
        addPlusCard();

        for (Card card : cards) {
            VBox cardTile = new VBox();
            cardTile.setPrefSize(120, 160);
            cardTile.setPadding(new Insets(5));
            cardTile.setAlignment(Pos.TOP_CENTER);
            cardTile.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;");

            HBox topBox = new HBox();

            Button editBtn = new Button("✎");
            editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;");
            editBtn.setOnMouseEntered(ev -> editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #007bff; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;"));
            editBtn.setOnMouseExited(ev -> editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;"));
            editBtn.setOnAction(e -> {
                e.consume();
                if (onEditCardRequested != null) onEditCardRequested.accept(card);
            });

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
                alert.setContentText("Are you sure you want to delete this card permanently?");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK && onDeleteCardRequested != null) {
                        onDeleteCardRequested.accept(card);
                    }
                });
            });

            topBox.getChildren().addAll(editBtn, spacer, deleteBtn);

            Label qLabel = new Label(card.getQuestion());
            qLabel.setWrapText(true);
            qLabel.setTextAlignment(TextAlignment.CENTER);
            qLabel.setAlignment(Pos.CENTER);
            qLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            qLabel.setStyle("-fx-text-fill: black;");
            VBox.setVgrow(qLabel, Priority.ALWAYS);

            cardTile.getChildren().addAll(topBox, qLabel);

            // Click tile → show details in right panel
            cardTile.setOnMouseClicked(e -> showCardDetail(card));

            cardsGrid.getChildren().add(cardTile);
        }
    }

    public void setOnAddCardRequested(Runnable callback) { this.onAddCardRequested = callback; }
    public void setOnEditCardRequested(Consumer<Card> callback) { this.onEditCardRequested = callback; }
    public void setOnDeleteCardRequested(Consumer<Card> callback) { this.onDeleteCardRequested = callback; }
    public void setOnImportRequested(Runnable callback) { this.onImportRequested = callback; }
    public void setOnExportRequested(Runnable callback) { this.onExportRequested = callback; }
}
