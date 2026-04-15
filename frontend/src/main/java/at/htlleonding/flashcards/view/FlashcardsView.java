package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Card;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.function.Consumer;

public class FlashcardsView extends HBox {
    private VBox deckInfoSidebar;
    private FlowPane cardsGrid;
    private Label deckTitleLabel;
    private Label deckDescriptionLabel;
    private Runnable onAddCardRequested;
    private Consumer<Card> onEditCardRequested;
    private Consumer<Card> onDeleteCardRequested;
    private Runnable onImportRequested;
    private Runnable onExportRequested;

    public FlashcardsView() {
        this.setPadding(new Insets(20));
        this.setSpacing(30);

        initSidebar();
        initCardsGrid();

        this.getChildren().addAll(deckInfoSidebar, cardsGrid);
    }

    private void initSidebar() {
        deckInfoSidebar = new VBox();
        deckInfoSidebar.setPrefWidth(200);
        deckInfoSidebar.setSpacing(15);
        deckInfoSidebar.setPadding(new Insets(10));

        // Icon Platzhalter
        Rectangle iconPlaceholder = new Rectangle(80, 60);
        iconPlaceholder.setFill(Color.TRANSPARENT);
        iconPlaceholder.setStroke(Color.GRAY);
        iconPlaceholder.setStrokeWidth(1);

        deckTitleLabel = new Label("Deck Title");
        deckTitleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        deckDescriptionLabel = new Label("No description available.");
        deckDescriptionLabel.setWrapText(true);
        deckDescriptionLabel.setStyle("-fx-text-fill: #666666;");

        VBox buttonBox = new VBox(10);
        Button studyBtn = createSidebarButton("Study");
        Button exportBtn = createSidebarButton("Export");
        Button importBtn = createSidebarButton("Import");

        importBtn.setOnAction(e -> {
            if (onImportRequested != null) onImportRequested.run();
        });
        exportBtn.setOnAction(e -> {
            if (onExportRequested != null) onExportRequested.run();
        });

        buttonBox.getChildren().addAll(studyBtn, exportBtn, importBtn);

        deckInfoSidebar.getChildren().addAll(iconPlaceholder, deckTitleLabel, deckDescriptionLabel, new Region(), buttonBox);
        VBox.setVgrow(deckInfoSidebar.getChildren().get(3), Priority.ALWAYS); // Spacer
    }

    private void initCardsGrid() {
        cardsGrid = new FlowPane();
        cardsGrid.setHgap(15);
        cardsGrid.setVgap(15);
        cardsGrid.setPadding(new Insets(10));
        cardsGrid.setPrefWrapLength(600); // Breite setzen, damit FlowPane korrekt umbricht

        ScrollPane scrollPane = new ScrollPane(cardsGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        this.getChildren().add(scrollPane);
        
        // Initialer "+" Button
        addPlusCard();
    }

    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 5;");
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
            if (onAddCardRequested != null) {
                onAddCardRequested.run();
            }
        });

        cardsGrid.getChildren().add(plusCard);
    }

    public void setDeckInfo(String title, String description) {
        deckTitleLabel.setText(title);
        deckDescriptionLabel.setText(description);
    }

    public void setOnAddCardRequested(Runnable callback) {
        this.onAddCardRequested = callback;
    }

    public void setOnEditCardRequested(Consumer<Card> callback) {
        this.onEditCardRequested = callback;
    }

    public void setOnDeleteCardRequested(Consumer<Card> callback) {
        this.onDeleteCardRequested = callback;
    }

    public void setOnImportRequested(Runnable callback) {
        this.onImportRequested = callback;
    }

    public void setOnExportRequested(Runnable callback) {
        this.onExportRequested = callback;
    }

    public void renderCards(List<Card> cards) {
        // Erst alles außer dem "+" Button entfernen
        cardsGrid.getChildren().clear();
        addPlusCard();

        for (Card card : cards) {
            VBox cardTile = new VBox();
            cardTile.setPrefSize(120, 160);
            cardTile.setPadding(new Insets(5)); // Reduced padding to move closer to corners
            cardTile.setAlignment(Pos.TOP_CENTER);
            cardTile.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;");
            
            HBox topBox = new HBox();
            
            Button editBtn = new Button("✎");
            editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;");
            editBtn.setOnMouseEntered(ev -> editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #007bff; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;"));
            editBtn.setOnMouseExited(ev -> editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;"));
            
            editBtn.setOnAction(e -> {
                e.consume();
                if (onEditCardRequested != null) {
                    onEditCardRequested.accept(card);
                }
            });
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Button deleteBtn = new Button("✖");
            deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;");
            deleteBtn.setOnMouseEntered(ev -> deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #dc3545; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;"));
            deleteBtn.setOnMouseExited(ev -> deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;"));
            
            deleteBtn.setOnAction(e -> {
                e.consume(); // Prevent launching edit mode
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
            qLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            qLabel.setAlignment(Pos.CENTER);
            qLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            qLabel.setStyle("-fx-text-fill: black;");
            
            VBox.setVgrow(qLabel, Priority.ALWAYS);
            
            cardTile.getChildren().addAll(topBox, qLabel);
            
            // Editable by clicking the card
            cardTile.setOnMouseClicked(e -> {
                if (onEditCardRequested != null) {
                    onEditCardRequested.accept(card);
                }
            });
            
            cardsGrid.getChildren().add(cardTile);
        }
    }
}
