package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Deck;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class CreateDeckDialog {
    private final Stage stage;
    private final TextField nameField;
    private final TextArea descriptionArea;
    private final Button saveButton;
    private String selectedIconId = "default";
    private DeckResult result;
    private Button selectedButton;

    public static record DeckResult(String name, String description, String iconId) {}

    public CreateDeckDialog(Stage owner) {
        this(owner, null);
    }

    public CreateDeckDialog(Stage owner, Deck deckToEdit) {
        stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        
        if (deckToEdit == null) {
            stage.setTitle("Create New Deck");
        } else {
            stage.setTitle("Edit Deck");
            selectedIconId = deckToEdit.getIconId();
        }

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setPrefWidth(550);

        Label nameLabel = new Label("Deck Name:");
        nameLabel.setStyle("-fx-font-weight: bold;");
        nameField = new TextField();
        nameField.setPromptText("e.g. Mathematics, History...");

        Label descLabel = new Label("Description:");
        descLabel.setStyle("-fx-font-weight: bold;");
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("What is this deck about?");
        descriptionArea.setPrefRowCount(2);
        descriptionArea.setWrapText(true);

        Label iconLabel = new Label("Choose Icon:");
        iconLabel.setStyle("-fx-font-weight: bold;");
        
        FlowPane iconPicker = new FlowPane(10, 10);
        iconPicker.setAlignment(Pos.CENTER_LEFT);
        
        // Create icon buttons with proper selection tracking
        for (String iconId : IconManager.getAvailableIconIds()) {
            Image iconImage = IconManager.getIcon(iconId);
            ImageView iconView = new ImageView(iconImage);
            iconView.setFitWidth(64);
            iconView.setFitHeight(64);
            iconView.setPreserveRatio(true);
            iconView.setSmooth(true);
            
            Button iconBtn = new Button();
            iconBtn.setGraphic(iconView);
            iconBtn.setPrefSize(74, 74);
            iconBtn.setUserData(iconId); // Store the icon ID as user data
            
            boolean isSelected = iconId.equals(selectedIconId);
            updateIconButtonStyle(iconBtn, isSelected);
            
            if (isSelected) {
                selectedButton = iconBtn;
            }
            
            iconBtn.setOnAction(e -> {
                selectedIconId = iconId;
                
                // Deselect previous button
                if (selectedButton != null) {
                    updateIconButtonStyle(selectedButton, false);
                }
                
                // Select new button
                selectedButton = iconBtn;
                updateIconButtonStyle(selectedButton, true);
            });
            
            iconPicker.getChildren().add(iconBtn);
        }

        if (deckToEdit != null) {
            nameField.setText(deckToEdit.getName());
            descriptionArea.setText(deckToEdit.getDescription());
        }

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        saveButton = new Button(deckToEdit == null ? "Create Deck" : "Save Changes");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        
        if (deckToEdit == null) {
            saveButton.setDisable(true);
        }
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> stage.close());

        buttonBox.getChildren().addAll(cancelButton, saveButton);

        // Validation
        nameField.textProperty().addListener((obs, oldVal, newVal) -> validate());

        saveButton.setOnAction(e -> {
            result = new DeckResult(nameField.getText().trim(), descriptionArea.getText().trim(), selectedIconId);
            stage.close();
        });

        root.getChildren().addAll(nameLabel, nameField, descLabel, descriptionArea, iconLabel, iconPicker, buttonBox);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    private void updateIconButtonStyle(Button btn, boolean selected) {
        if (selected) {
            btn.setStyle("-fx-background-color: #007bff; -fx-border-color: #007bff; -fx-border-radius: 5; -fx-padding: 0;");
        } else {
            btn.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 0;");
        }
    }

    private void validate() {
        saveButton.setDisable(nameField.getText().trim().isEmpty());
    }

    public Optional<DeckResult> showAndWait() {
        stage.showAndWait();
        return Optional.ofNullable(result);
    }
}
