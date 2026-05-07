package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Card;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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

    // Audio state fields
    private String fAudioData, fAudioName, fAudioDuration;
    private String bAudioData, bAudioName, bAudioDuration;
    private VBox fAudioInfoBox, bAudioInfoBox;
    
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

        // Initialize state if editing
        if (isEditMode) {
            fAudioData = cardToEdit.getFrontAudioData();
            fAudioName = cardToEdit.getFrontAudioName();
            fAudioDuration = cardToEdit.getFrontAudioDuration();
            bAudioData = cardToEdit.getBackAudioData();
            bAudioName = cardToEdit.getBackAudioName();
            bAudioDuration = cardToEdit.getBackAudioDuration();
        }

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setPrefWidth(450);

        // --- Question Section Header ---
        HBox qHeader = new HBox(10);
        qHeader.setAlignment(Pos.CENTER_LEFT);
        Label qLabel = new Label("Question (Front):");
        qLabel.setStyle("-fx-font-weight: bold;");
        Region qSpacer = new Region();
        HBox.setHgrow(qSpacer, Priority.ALWAYS);
        Button qAddExtra = new Button("Add Extra");
        qHeader.getChildren().addAll(qLabel, qSpacer, qAddExtra);

        questionArea = new TextArea();
        questionArea.setPromptText("Enter the question here...");
        questionArea.setPrefRowCount(3);
        questionArea.setWrapText(true);
        if (isEditMode) questionArea.setText(cardToEdit.getQuestion());

        fAudioInfoBox = new VBox(5);

        // --- Answer Section Header ---
        HBox aHeader = new HBox(10);
        aHeader.setAlignment(Pos.CENTER_LEFT);
        Label aLabel = new Label("Answer (Back):");
        aLabel.setStyle("-fx-font-weight: bold;");
        Region aSpacer = new Region();
        HBox.setHgrow(aSpacer, Priority.ALWAYS);
        Button aAddExtra = new Button("Add Extra");
        aHeader.getChildren().addAll(aLabel, aSpacer, aAddExtra);

        answerArea = new TextArea();
        answerArea.setPromptText("Enter the answer here...");
        answerArea.setPrefRowCount(3);
        answerArea.setWrapText(true);
        if (isEditMode) answerArea.setText(cardToEdit.getAnswer());

        bAudioInfoBox = new VBox(5);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        saveButton = new Button(isEditMode ? "Save Changes" : "Save Card");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        saveButton.setDisable(!isEditMode);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> stage.close());

        buttonBox.getChildren().addAll(cancelButton, saveButton);

        // Validation
        questionArea.textProperty().addListener((obs, oldVal, newVal) -> validate());
        answerArea.textProperty().addListener((obs, oldVal, newVal) -> validate());

        saveButton.setOnAction(e -> {
            Card card = isEditMode ? cardToEdit : new Card();
            card.setQuestion(questionArea.getText().trim());
            card.setAnswer(answerArea.getText().trim());
            // Audio data saving will be added in Step 3
            result = card;
            stage.close();
        });

        root.getChildren().addAll(qHeader, questionArea, fAudioInfoBox, aHeader, answerArea, bAudioInfoBox, buttonBox);
        
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
