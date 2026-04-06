package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Card;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class CreateCardDialog {
    private final Stage stage;
    private final TextArea questionArea;
    private final TextArea answerArea;
    private final Button saveButton;
    private Card result;
    
    // Default constructor for creating a new card
    public CreateCardDialog(Stage owner) {
        this(owner, null);
    }

    public CreateCardDialog(Stage owner, Card cardToEdit) {
        stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        
        boolean isEditMode = cardToEdit != null;
        stage.setTitle(isEditMode ? "Edit Flashcard" : "Create New Flashcard");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setPrefWidth(400);

        Label qLabel = new Label("Question (Front):");
        qLabel.setStyle("-fx-font-weight: bold;");
        questionArea = new TextArea();
        questionArea.setPromptText("Enter the question here...");
        questionArea.setPrefRowCount(3);
        questionArea.setWrapText(true);
        if (isEditMode) questionArea.setText(cardToEdit.getQuestion());

        Label aLabel = new Label("Answer (Back):");
        aLabel.setStyle("-fx-font-weight: bold;");
        answerArea = new TextArea();
        answerArea.setPromptText("Enter the answer here...");
        answerArea.setPrefRowCount(3);
        answerArea.setWrapText(true);
        if (isEditMode) answerArea.setText(cardToEdit.getAnswer());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        saveButton = new Button(isEditMode ? "Save Changes" : "Save Card");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        saveButton.setDisable(!isEditMode);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> stage.close());

        buttonBox.getChildren().addAll(cancelButton, saveButton);

        // Validation: Enable save button only if both fields are not empty
        questionArea.textProperty().addListener((obs, oldVal, newVal) -> validate());
        answerArea.textProperty().addListener((obs, oldVal, newVal) -> validate());

        saveButton.setOnAction(e -> {
            if (isEditMode) {
                cardToEdit.setQuestion(questionArea.getText().trim());
                cardToEdit.setAnswer(answerArea.getText().trim());
                result = cardToEdit;
            } else {
                result = new Card(questionArea.getText().trim(), answerArea.getText().trim());
            }
            stage.close();
        });

        root.getChildren().addAll(qLabel, questionArea, aLabel, answerArea, buttonBox);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    private void validate() {
        boolean isValid = !questionArea.getText().trim().isEmpty() && 
                          !answerArea.getText().trim().isEmpty();
        saveButton.setDisable(!isValid);
    }

    public Optional<Card> showAndWait() {
        stage.showAndWait();
        return Optional.ofNullable(result);
    }
}
