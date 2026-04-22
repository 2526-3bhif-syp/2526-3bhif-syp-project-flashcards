package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Deck;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private DeckResult result;

    public static record DeckResult(String name, String description) {}

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
        }

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setPrefWidth(350);

        Label nameLabel = new Label("Deck Name:");
        nameLabel.setStyle("-fx-font-weight: bold;");
        nameField = new TextField();
        nameField.setPromptText("e.g. Mathematics, History...");

        Label descLabel = new Label("Description:");
        descLabel.setStyle("-fx-font-weight: bold;");
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("What is this deck about?");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setWrapText(true);

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
            result = new DeckResult(nameField.getText().trim(), descriptionArea.getText().trim());
            stage.close();
        });

        root.getChildren().addAll(nameLabel, nameField, descLabel, descriptionArea, buttonBox);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    private void validate() {
        saveButton.setDisable(nameField.getText().trim().isEmpty());
    }

    public Optional<DeckResult> showAndWait() {
        stage.showAndWait();
        return Optional.ofNullable(result);
    }
}
