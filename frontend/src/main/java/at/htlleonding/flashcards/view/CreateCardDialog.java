package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Card;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
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
    private MediaPlayer previewPlayer;

    // Image state fields
    private String fImageData, fImageName;
    private String bImageData, bImageName;
    private VBox fImageInfoBox, bImageInfoBox;
    
    // Default constructor for creating a new card
    public CreateCardDialog(Stage owner) {
        this(owner, null);
    }

    public CreateCardDialog(Stage owner, Card cardToEdit) {
        stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        
        stage.setOnCloseRequest(e -> stopPreview());

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
            fImageData = cardToEdit.getFrontImageData();
            fImageName = cardToEdit.getFrontImageName();
            bImageData = cardToEdit.getBackImageData();
            bImageName = cardToEdit.getBackImageName();
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
        styleExtraButton(qAddExtra);
        qAddExtra.setOnAction(e -> showExtraMenu(qAddExtra, true));
        qHeader.getChildren().addAll(qLabel, qSpacer, qAddExtra);

        questionArea = new TextArea();
        questionArea.setPromptText("Enter the question here...");
        questionArea.setPrefRowCount(3);
        questionArea.setWrapText(true);
        if (isEditMode) questionArea.setText(cardToEdit.getQuestion());

        fAudioInfoBox = new VBox(5);
        fImageInfoBox = new VBox(5);

        // --- Answer Section Header ---
        HBox aHeader = new HBox(10);
        aHeader.setAlignment(Pos.CENTER_LEFT);
        Label aLabel = new Label("Answer (Back):");
        aLabel.setStyle("-fx-font-weight: bold;");
        Region aSpacer = new Region();
        HBox.setHgrow(aSpacer, Priority.ALWAYS);
        Button aAddExtra = new Button("Add Extra");
        styleExtraButton(aAddExtra);
        aAddExtra.setOnAction(e -> showExtraMenu(aAddExtra, false));
        aHeader.getChildren().addAll(aLabel, aSpacer, aAddExtra);

        answerArea = new TextArea();
        answerArea.setPromptText("Enter the answer here...");
        answerArea.setPrefRowCount(3);
        answerArea.setWrapText(true);
        if (isEditMode) answerArea.setText(cardToEdit.getAnswer());

        bAudioInfoBox = new VBox(5);
        bImageInfoBox = new VBox(5);
        updateAudioInfo(true);
        updateAudioInfo(false);
        updateImageInfo(true);
        updateImageInfo(false);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        saveButton = new Button(isEditMode ? "Save Changes" : "Save Card");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        saveButton.setDisable(!isEditMode);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> {
            stopPreview();
            stage.close();
        });

        buttonBox.getChildren().addAll(cancelButton, saveButton);

        // Validation
        questionArea.textProperty().addListener((obs, oldVal, newVal) -> validate());
        answerArea.textProperty().addListener((obs, oldVal, newVal) -> validate());

        saveButton.setOnAction(e -> {
            stopPreview();
            Card card = isEditMode ? cardToEdit : new Card();
            card.setQuestion(questionArea.getText().trim());
            card.setAnswer(answerArea.getText().trim());
            card.setFrontAudioData(fAudioData);
            card.setFrontAudioName(fAudioName);
            card.setFrontAudioDuration(fAudioDuration);
            card.setBackAudioData(bAudioData);
            card.setBackAudioName(bAudioName);
            card.setBackAudioDuration(bAudioDuration);
            card.setFrontImageData(fImageData);
            card.setFrontImageName(fImageName);
            card.setBackImageData(bImageData);
            card.setBackImageName(bImageName);
            result = card;
            stage.close();
        });

        root.getChildren().addAll(qHeader, questionArea, fAudioInfoBox, fImageInfoBox, aHeader, answerArea, bAudioInfoBox, bImageInfoBox, buttonBox);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    private void showExtraMenu(Button anchor, boolean isFront) {
        ContextMenu menu = new ContextMenu();
        menu.setStyle("-fx-background-color: white; " +
                     "-fx-background-radius: 8; " +
                     "-fx-border-color: #eeeeee; " +
                     "-fx-border-radius: 8; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        // Audio Item
        SVGPath musicIcon = new SVGPath();
        musicIcon.setContent("M12 3v10.55c-.59-.34-1.27-.55-2-.55-2.21 0-4 1.79-4 4s1.79 4 4 4 4-1.79 4-4V7h4V3h-6z");
        musicIcon.setFill(Color.web("#555555"));
        musicIcon.setScaleX(0.7);
        musicIcon.setScaleY(0.7);
        
        MenuItem audioItem = new MenuItem("Add Audio File");
        audioItem.setGraphic(musicIcon);
        audioItem.setOnAction(e -> handleAddAudio(isFront));

        // Image Item
        SVGPath imageIcon = new SVGPath();
        imageIcon.setContent("M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z");
        imageIcon.setFill(Color.web("#555555"));
        imageIcon.setScaleX(0.7);
        imageIcon.setScaleY(0.7);

        MenuItem imageItem = new MenuItem("Add Image");
        imageItem.setGraphic(imageIcon);
        imageItem.setOnAction(e -> handleAddImage(isFront));
        
        menu.getItems().addAll(audioItem, imageItem);
        
        menu.show(anchor, javafx.geometry.Side.BOTTOM, 0, 0);
    }

    private void styleExtraButton(Button btn) {
        SVGPath plusIcon = new SVGPath();
        plusIcon.setContent("M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z");
        plusIcon.setFill(Color.web("#2196F3"));
        plusIcon.setScaleX(0.45);
        plusIcon.setScaleY(0.45);
        
        btn.setGraphic(plusIcon);
        btn.setContentDisplay(ContentDisplay.RIGHT);
        btn.setGraphicTextGap(-2); // Adjusted for scaled icon

        btn.setStyle("-fx-background-color: transparent; " +
                     "-fx-border-color: #2196F3; " +
                     "-fx-border-radius: 5; " +
                     "-fx-text-fill: #2196F3; " +
                     "-fx-cursor: hand; " +
                     "-fx-font-weight: bold; " +
                     "-fx-font-size: 11px; " +
                     "-fx-padding: 3 4 3 8;"); // Adjusted right padding for icon

        ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
        
        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: #2196F3; " +
                         "-fx-border-color: #2196F3; " +
                         "-fx-border-radius: 5; " +
                         "-fx-text-fill: white; " +
                         "-fx-cursor: hand; " +
                         "-fx-font-weight: bold; " +
                         "-fx-font-size: 11px; " +
                         "-fx-padding: 3 4 3 8;");
            plusIcon.setFill(Color.WHITE);
            st.setToX(1.1);
            st.setToY(1.1);
            st.playFromStart();
        });
        
        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: transparent; " +
                         "-fx-border-color: #2196F3; " +
                         "-fx-border-radius: 5; " +
                         "-fx-text-fill: #2196F3; " +
                         "-fx-cursor: hand; " +
                         "-fx-font-weight: bold; " +
                         "-fx-font-size: 11px; " +
                         "-fx-padding: 3 4 3 8;");
            plusIcon.setFill(Color.web("#2196F3"));
            st.setToX(1.0);
            st.setToY(1.0);
            st.playFromStart();
        });
    }

    private void handleAddAudio(boolean isFront) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Audio File");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));
        File file = fc.showOpenDialog(stage);
        if (file != null) {
            // Check file size (5MB limit)
            if (file.length() > 5 * 1024 * 1024) {
                new Alert(Alert.AlertType.ERROR, "The file is too large. Maximum size is 5MB.").show();
                return;
            }

            AudioHelper.getDurationInSeconds(file, seconds -> {
                if (seconds > 60) {
                    new Alert(Alert.AlertType.ERROR, "The audio file is too long. Please select a file under 1 minute.").show();
                    return;
                }

                try {
                    byte[] bytes = Files.readAllBytes(file.toPath());
                    String base64 = Base64.getEncoder().encodeToString(bytes);
                    String name = file.getName();

                    String durationStr;
                    if (seconds > 0) {
                        int mins = (int) (seconds / 60);
                        int secs = (int) (seconds % 60);
                        durationStr = String.format("%02d:%02d", mins, secs);
                    } else {
                        durationStr = "Unknown";
                    }

                    if (isFront) {
                        fAudioData = base64;
                        fAudioName = name;
                        fAudioDuration = durationStr;
                    } else {
                        bAudioData = base64;
                        bAudioName = name;
                        bAudioDuration = durationStr;
                    }
                    updateAudioInfo(isFront);
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Could not load audio file.").show();
                }
            });        }
    }

    private void handleAddImage(boolean isFront) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Image");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
        File file = fc.showOpenDialog(stage);
        if (file == null) return;

        if (file.length() > 5 * 1024 * 1024) {
            new Alert(Alert.AlertType.ERROR, "The file is too large. Maximum size is 5MB.").show();
            return;
        }
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String base64 = Base64.getEncoder().encodeToString(bytes);
            if (isFront) {
                fImageData = base64;
                fImageName = file.getName();
            } else {
                bImageData = base64;
                bImageName = file.getName();
            }
            updateImageInfo(isFront);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Could not load image file.").show();
        }
    }

    private void updateImageInfo(boolean isFront) {
        VBox box = isFront ? fImageInfoBox : bImageInfoBox;
        String data = isFront ? fImageData : bImageData;
        String name = isFront ? fImageName : bImageName;

        box.getChildren().clear();
        if (data != null) {
            byte[] bytes = Base64.getDecoder().decode(data);
            ImageView thumb = new ImageView(new Image(new ByteArrayInputStream(bytes)));
            thumb.setFitWidth(60);
            thumb.setFitHeight(60);
            thumb.setPreserveRatio(true);
            thumb.setSmooth(true);

            Label label = new Label("🖼 " + name);
            label.setStyle("-fx-font-size: 11px;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button delBtn = new Button("🗑");
            delBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-cursor: hand;");
            delBtn.setOnAction(e -> {
                if (isFront) { fImageData = null; fImageName = null; }
                else { bImageData = null; bImageName = null; }
                updateImageInfo(isFront);
            });

            HBox info = new HBox(10);
            info.setAlignment(Pos.CENTER_LEFT);
            info.setPadding(new Insets(5));
            info.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");
            info.getChildren().addAll(thumb, label, spacer, delBtn);
            box.getChildren().add(info);
        }
    }

    private void updateAudioInfo(boolean isFront) {
        VBox box = isFront ? fAudioInfoBox : bAudioInfoBox;
        String data = isFront ? fAudioData : bAudioData;
        String name = isFront ? fAudioName : bAudioName;
        String dur = isFront ? fAudioDuration : bAudioDuration;

        box.getChildren().clear();
        if (data != null) {
            HBox info = new HBox(10);
            info.setAlignment(Pos.CENTER_LEFT);
            info.setPadding(new Insets(5));
            info.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");
            
            Label label = new Label(String.format("🎵 %s (%s)", name, dur));
            label.setStyle("-fx-font-size: 11px;");
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Button playBtn = new Button("▶");
            playBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            playBtn.setOnAction(e -> togglePreview(data, playBtn));

            Button delBtn = new Button("🗑");
            delBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-cursor: hand;");
            delBtn.setOnAction(e -> {
                stopPreview();
                if (isFront) {
                    fAudioData = fAudioName = fAudioDuration = null;
                } else {
                    bAudioData = bAudioName = bAudioDuration = null;
                }
                updateAudioInfo(isFront);
            });
            
            info.getChildren().addAll(label, spacer, playBtn, delBtn);
            box.getChildren().add(info);
        }
    }

    private void togglePreview(String base64Data, Button btn) {
        if (previewPlayer != null && previewPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            stopPreview();
            btn.setText("▶");
        } else {
            stopPreview();
            File temp = AudioHelper.saveTempAudio(base64Data);
            if (temp != null) {
                try {
                    Media media = new Media(temp.toURI().toString());
                    previewPlayer = new MediaPlayer(media);
                    previewPlayer.setOnEndOfMedia(() -> {
                        stopPreview();
                        btn.setText("▶");
                    });
                    previewPlayer.play();
                    btn.setText("⏸");
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.WARNING, "Playback failed: Your system might be missing MP3 codecs or JavaFX Media is not configured correctly.").show();
                    btn.setText("▶");
                }
            }
        }
    }

    private void stopPreview() {
        if (previewPlayer != null) {
            previewPlayer.stop();
            previewPlayer.dispose();
            previewPlayer = null;
        }
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
