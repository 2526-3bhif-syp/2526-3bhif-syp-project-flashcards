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

    public CreateCardDialog(Stage owner) {
        stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Create New Flashcard");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setPrefWidth(400);

        Label qLabel = new Label("Question (Front):");
        qLabel.setStyle("-fx-font-weight: bold;");
        questionArea = new TextArea();
        questionArea.setPromptText("Enter the question here...");
        questionArea.setPrefRowCount(3);
        questionArea.setWrapText(true);

        Label aLabel = new Label("Answer (Back):");
        aLabel.setStyle("-fx-font-weight: bold;");
        answerArea = new TextArea();
        answerArea.setPromptText("Enter the answer here...");
        answerArea.setPrefRowCount(3);
        answerArea.setWrapText(true);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        saveButton = new Button("Save Card");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        saveButton.setDisable(true);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> stage.close());

        buttonBox.getChildren().addAll(cancelButton, saveButton);

        // Validation: Enable save button only if both fields are not empty
        questionArea.textProperty().addListener((obs, oldVal, newVal) -> validate());
        answerArea.textProperty().addListener((obs, oldVal, newVal) -> validate());

        saveButton.setOnAction(e -> {
            result = new Card(questionArea.getText().trim(), answerArea.getText().trim());
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
