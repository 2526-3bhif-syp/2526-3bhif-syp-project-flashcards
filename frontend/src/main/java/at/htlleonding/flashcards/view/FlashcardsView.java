package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Card;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private ImageView iconView;
    private Runnable onAddCardRequested;
    private Consumer<Card> onEditCardRequested;
    private Consumer<Card> onDeleteCardRequested;

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

        // Icon Anzeige
        Image defaultIconImage = IconManager.getIcon("default");
        iconView = new ImageView(defaultIconImage);
        iconView.setFitWidth(80);
        iconView.setFitHeight(80);
        iconView.setPreserveRatio(true);
        
        VBox iconContainer = new VBox();
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.setMinWidth(80);
        iconContainer.setMinHeight(80);
        iconContainer.getChildren().add(iconView);

        deckTitleLabel = new Label("Deck Title");
        deckTitleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        deckDescriptionLabel = new Label("No description available.");
        deckDescriptionLabel.setWrapText(true);
        deckDescriptionLabel.setStyle("-fx-text-fill: #666666;");

        VBox buttonBox = new VBox(10);
        buttonBox.getChildren().addAll(
                createSidebarButton("Study"),
                createSidebarButton("Export"),
                createSidebarButton("Import")
        );

        deckInfoSidebar.getChildren().addAll(iconContainer, deckTitleLabel, deckDescriptionLabel, new Region(), buttonBox);
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

    public String getDeckTitle() {
        return deckTitleLabel.getText();
    }

    public void setDeckInfo(String title, String description, String iconId) {
        deckTitleLabel.setText(title);
        deckDescriptionLabel.setText(description);
        
        Image iconImage = IconManager.getIcon(iconId);
        iconView.setImage(iconImage);
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
