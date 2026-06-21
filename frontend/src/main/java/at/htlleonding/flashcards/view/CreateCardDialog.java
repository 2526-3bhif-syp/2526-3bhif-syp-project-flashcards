package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Card;
import at.htlleonding.flashcards.model.ThemeProvider;
import at.htlleonding.flashcards.model.TranslationProvider;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class CreateCardDialog {
    private final Stage stage;
    private final TextArea questionArea;
    private final TextArea answerArea;
    private final TextField tagField;
    private final FlowPane tagsPane;
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

    // Tags state
    private final List<String> tags;
    
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
        stage.setTitle(isEditMode ? TranslationProvider.get("card.edit_title") : TranslationProvider.get("card.create_title"));

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
            tags = new ArrayList<>(cardToEdit.getTags());
        } else {
            tags = new ArrayList<>();
        }

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setPrefWidth(450);
        root.setStyle("-fx-background-color: " + ThemeProvider.get("bg-primary") + "; -fx-border-color: " + ThemeProvider.get("border-muted") + "; -fx-border-width: 1.5; -fx-border-radius: 12; -fx-background-radius: 12;");

        String inputBg = ThemeProvider.getTheme().equals("dark") ? ThemeProvider.get("bg-card") : "#ffffff";
        String inputText = ThemeProvider.getTheme().equals("dark") ? ThemeProvider.get("text-primary") : "#333333";
        String inputPrompt = ThemeProvider.getTheme().equals("dark") ? ThemeProvider.get("text-placeholder") : "#888888";

        // --- Question Section Header ---
        HBox qHeader = new HBox(10);
        qHeader.setAlignment(Pos.CENTER_LEFT);
        Label qLabel = new Label(TranslationProvider.get("card.front_label"));
        qLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-primary") + ";");
        Region qSpacer = new Region();
        HBox.setHgrow(qSpacer, Priority.ALWAYS);
        Button qAddExtra = new Button(TranslationProvider.get("card.add_extra"));
        styleExtraButton(qAddExtra);
        qAddExtra.setOnAction(e -> showExtraMenu(qAddExtra, true));
        qHeader.getChildren().addAll(qLabel, qSpacer, qAddExtra);

        questionArea = new TextArea();
        questionArea.setPromptText(TranslationProvider.get("card.front_prompt"));
        questionArea.setPrefRowCount(3);
        questionArea.setWrapText(true);
        setupInputField(questionArea, inputBg, inputText, inputPrompt);
        if (isEditMode) questionArea.setText(cardToEdit.getQuestion());

        fAudioInfoBox = new VBox(5);
        fImageInfoBox = new VBox(5);

        // --- Answer Section Header ---
        HBox aHeader = new HBox(10);
        aHeader.setAlignment(Pos.CENTER_LEFT);
        Label aLabel = new Label(TranslationProvider.get("card.back_label"));
        aLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-primary") + ";");
        Region aSpacer = new Region();
        HBox.setHgrow(aSpacer, Priority.ALWAYS);
        Button aAddExtra = new Button(TranslationProvider.get("card.add_extra"));
        styleExtraButton(aAddExtra);
        aAddExtra.setOnAction(e -> showExtraMenu(aAddExtra, false));
        aHeader.getChildren().addAll(aLabel, aSpacer, aAddExtra);

        answerArea = new TextArea();
        answerArea.setPromptText(TranslationProvider.get("card.back_prompt"));
        answerArea.setPrefRowCount(3);
        answerArea.setWrapText(true);
        setupInputField(answerArea, inputBg, inputText, inputPrompt);
        if (isEditMode) answerArea.setText(cardToEdit.getAnswer());

        bAudioInfoBox = new VBox(5);
        bImageInfoBox = new VBox(5);

        // --- Tags Section ---
        Label tagsLabel = new Label(TranslationProvider.get("card.tags_label"));
        tagsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-primary") + ";");
        
        tagField = new TextField();
        tagField.setPromptText(TranslationProvider.get("card.tags_prompt"));
        setupInputField(tagField, inputBg, inputText, inputPrompt);
        
        Button addTagBtn = new Button(TranslationProvider.get("card.add_btn"));
        styleAddTagButton(addTagBtn);
        addTagBtn.setOnAction(e -> addTagFromField());
        
        tagField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                addTagFromField();
                e.consume();
            }
        });

        HBox tagInputBox = new HBox(10, tagField, addTagBtn);
        HBox.setHgrow(tagField, Priority.ALWAYS);

        tagsPane = new FlowPane(6, 6);
        tagsPane.setPadding(new Insets(5, 0, 5, 0));

        updateAudioInfo(true);
        updateAudioInfo(false);
        updateImageInfo(true);
        updateImageInfo(false);
        updateTagsUI();

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        saveButton = new Button(isEditMode ? TranslationProvider.get("card.save_btn") : TranslationProvider.get("card.create_btn"));
        String saveNormal = "-fx-background-color: " + ThemeProvider.get("accent-green") + "; -fx-text-fill: " + ThemeProvider.get("text-on-primary") + "; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14;";
        String saveHover  = "-fx-background-color: " + ThemeProvider.get("accent-green-strong") + "; -fx-text-fill: " + ThemeProvider.get("text-on-primary") + "; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14;";
        saveButton.setStyle(saveNormal);
        saveButton.setOnMouseEntered(e -> saveButton.setStyle(saveHover));
        saveButton.setOnMouseExited(e -> saveButton.setStyle(saveNormal));
        saveButton.setDefaultButton(true);
        saveButton.setDisable(!isEditMode);
        
        Button cancelButton = new Button(TranslationProvider.get("card.cancel_btn"));
        cancelButton.setStyle("-fx-background-color: " + ThemeProvider.get("bg-card") + "; -fx-text-fill: " + ThemeProvider.get("text-secondary") + "; -fx-border-color: " + ThemeProvider.get("border-muted") + "; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-weight: bold;");
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: " + ThemeProvider.get("bg-hover") + "; -fx-text-fill: " + ThemeProvider.get("text-primary") + "; -fx-border-color: " + ThemeProvider.get("border-muted") + "; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-weight: bold;"));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: " + ThemeProvider.get("bg-card") + "; -fx-text-fill: " + ThemeProvider.get("text-secondary") + "; -fx-border-color: " + ThemeProvider.get("border-muted") + "; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-weight: bold;"));
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
            card.setTags(new ArrayList<>(tags));
            result = card;
            stage.close();
        });

        root.getChildren().addAll(qHeader, questionArea, fAudioInfoBox, fImageInfoBox, aHeader, answerArea, bAudioInfoBox, bImageInfoBox, tagsLabel, tagInputBox, tagsPane, buttonBox);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    private void addTagFromField() {
        String text = tagField.getText().trim();
        if (!text.isEmpty() && !tags.contains(text)) {
            tags.add(text);
            tagField.clear();
            updateTagsUI();
        }
    }

    private void updateTagsUI() {
        tagsPane.getChildren().clear();
        for (String tag : tags) {
            HBox chip = new HBox(5);
            chip.setAlignment(Pos.CENTER_LEFT);
            chip.setPadding(new Insets(3, 8, 3, 8));
            chip.setStyle("-fx-background-color: " + ThemeProvider.get("accent-blue-bg") + "; -fx-border-color: " + ThemeProvider.get("accent-blue") + "; " +
                           "-fx-border-radius: 15; -fx-background-radius: 15; -fx-border-width: 0.5;");

            Label label = new Label(tag);
            label.setStyle("-fx-font-size: 11px; -fx-text-fill: " + ThemeProvider.get("accent-blue-hover") + "; -fx-font-weight: bold;");

            Button removeBtn = new Button("\u00D7");
            removeBtn.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-text-fill: " + ThemeProvider.get("accent-blue-hover") + "; -fx-cursor: hand; -fx-font-weight: bold;");
            removeBtn.setOnMouseEntered(e -> removeBtn.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-text-fill: " + ThemeProvider.get("accent-red-strong") + "; -fx-cursor: hand; -fx-font-weight: bold;"));
            removeBtn.setOnMouseExited(e -> removeBtn.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-text-fill: " + ThemeProvider.get("accent-blue-hover") + "; -fx-cursor: hand; -fx-font-weight: bold;"));
            removeBtn.setOnAction(e -> {
                tags.remove(tag);
                updateTagsUI();
            });
            
            chip.getChildren().addAll(label, removeBtn);
            tagsPane.getChildren().add(chip);
        }
    }

    private void showExtraMenu(Button anchor, boolean isFront) {
        ContextMenu menu = new ContextMenu();
        menu.setStyle("-fx-background-color: " + ThemeProvider.get("bg-card") + "; " +
                     "-fx-background-radius: 8; " +
                     "-fx-border-color: " + ThemeProvider.get("border-muted") + "; " +
                     "-fx-border-radius: 8; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        // Audio Item
        SVGPath musicIcon = new SVGPath();
        musicIcon.setContent("M12 3v10.55c-.59-.34-1.27-.55-2-.55-2.21 0-4 1.79-4 4s1.79 4 4 4 4-1.79 4-4V7h4V3h-6z");
        musicIcon.setFill(Color.web(ThemeProvider.get("text-secondary")));
        musicIcon.setScaleX(0.7);
        musicIcon.setScaleY(0.7);
        
        Label audioLabel = new Label(TranslationProvider.get("card.add_audio"));
        audioLabel.setStyle("-fx-text-fill: " + ThemeProvider.get("text-primary") + "; -fx-font-weight: bold;");
        HBox audioContent = new HBox(8, musicIcon, audioLabel);
        audioContent.setAlignment(Pos.CENTER_LEFT);

        MenuItem audioItem = new MenuItem();
        audioItem.setGraphic(audioContent);
        audioItem.setOnAction(e -> handleAddAudio(isFront));

        // Image Item
        SVGPath imageIcon = new SVGPath();
        imageIcon.setContent("M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z");
        imageIcon.setFill(Color.web(ThemeProvider.get("text-secondary")));
        imageIcon.setScaleX(0.7);
        imageIcon.setScaleY(0.7);

        Label imageLabel = new Label(TranslationProvider.get("card.add_image"));
        imageLabel.setStyle("-fx-text-fill: " + ThemeProvider.get("text-primary") + "; -fx-font-weight: bold;");
        HBox imageContent = new HBox(8, imageIcon, imageLabel);
        imageContent.setAlignment(Pos.CENTER_LEFT);

        MenuItem imageItem = new MenuItem();
        imageItem.setGraphic(imageContent);
        imageItem.setOnAction(e -> handleAddImage(isFront));
        
        menu.getItems().addAll(audioItem, imageItem);
        
        menu.show(anchor, javafx.geometry.Side.BOTTOM, 0, 0);
    }

    private void styleExtraButton(Button btn) {
        SVGPath plusIcon = new SVGPath();
        plusIcon.setContent("M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z");
        plusIcon.setFill(Color.web(ThemeProvider.get("accent-blue")));
        plusIcon.setScaleX(0.45);
        plusIcon.setScaleY(0.45);

        btn.setGraphic(plusIcon);
        btn.setContentDisplay(ContentDisplay.RIGHT);
        btn.setGraphicTextGap(-2);

        String normalStyle = "-fx-background-color: transparent; " +
                     "-fx-border-color: " + ThemeProvider.get("accent-blue") + "; " +
                     "-fx-border-radius: 5; " +
                     "-fx-text-fill: " + ThemeProvider.get("accent-blue") + "; " +
                     "-fx-cursor: hand; " +
                     "-fx-font-weight: bold; " +
                     "-fx-font-size: 11px; " +
                     "-fx-padding: 3 4 3 8;";

        String hoverStyle = "-fx-background-color: " + ThemeProvider.get("accent-blue") + "; " +
                     "-fx-border-color: " + ThemeProvider.get("accent-blue") + "; " +
                     "-fx-border-radius: 5; " +
                     "-fx-text-fill: " + ThemeProvider.get("text-on-primary") + "; " +
                     "-fx-cursor: hand; " +
                     "-fx-font-weight: bold; " +
                     "-fx-font-size: 11px; " +
                     "-fx-padding: 3 4 3 8;";

        btn.setStyle(normalStyle);

        ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);

        btn.setOnMouseEntered(e -> {
            btn.setStyle(hoverStyle);
            plusIcon.setFill(Color.web(ThemeProvider.get("text-on-primary")));
            st.setToX(1.1);
            st.setToY(1.1);
            st.playFromStart();
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(normalStyle);
            plusIcon.setFill(Color.web(ThemeProvider.get("accent-blue")));
            st.setToX(1.0);
            st.setToY(1.0);
            st.playFromStart();
        });
    }

    private void styleAddTagButton(Button btn) {
        String baseStyle = "-fx-background-color: " + ThemeProvider.get("bg-card") + "; " +
                           "-fx-border-color: " + ThemeProvider.get("accent-blue") + "; " +
                           "-fx-border-width: 1.5; " +
                           "-fx-border-radius: 8; " +
                           "-fx-background-radius: 8; " +
                           "-fx-text-fill: " + ThemeProvider.get("accent-blue") + "; " +
                           "-fx-cursor: hand; " +
                           "-fx-font-weight: bold; " +
                           "-fx-padding: 4 16;";

        String hoverStyle = "-fx-background-color: " + ThemeProvider.get("accent-blue") + "; " +
                            "-fx-border-color: " + ThemeProvider.get("accent-blue") + "; " +
                            "-fx-border-width: 1.5; " +
                            "-fx-border-radius: 8; " +
                            "-fx-background-radius: 8; " +
                            "-fx-text-fill: " + ThemeProvider.get("text-on-primary") + "; " +
                            "-fx-cursor: hand; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 4 16;";

        btn.setStyle(baseStyle);

        ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
        
        btn.setOnMouseEntered(e -> {
            btn.setStyle(hoverStyle);
            st.setToX(1.05);
            st.setToY(1.05);
            st.playFromStart();
        });
        
        btn.setOnMouseExited(e -> {
            btn.setStyle(baseStyle);
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
                new Alert(Alert.AlertType.ERROR, TranslationProvider.get("card.err_file_too_large")).show();
                return;
            }

            AudioHelper.getDurationInSeconds(file, seconds -> {
                if (seconds > 60) {
                    new Alert(Alert.AlertType.ERROR, TranslationProvider.get("card.err_audio_too_long")).show();
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
                    new Alert(Alert.AlertType.ERROR, TranslationProvider.get("card.err_load_audio_failed")).show();
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
            new Alert(Alert.AlertType.ERROR, TranslationProvider.get("card.err_file_too_large")).show();
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
            new Alert(Alert.AlertType.ERROR, TranslationProvider.get("card.err_load_image_failed")).show();
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
            label.setStyle("-fx-font-size: 11px; -fx-text-fill: " + ThemeProvider.get("text-primary") + ";");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button delBtn = new Button("\uD83D\uDDD1");
            delBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + ThemeProvider.get("accent-red") + "; -fx-cursor: hand;");
            delBtn.setOnAction(e -> {
                if (isFront) { fImageData = null; fImageName = null; }
                else { bImageData = null; bImageName = null; }
                updateImageInfo(isFront);
            });

            HBox info = new HBox(10);
            info.setAlignment(Pos.CENTER_LEFT);
            info.setPadding(new Insets(5));
            info.setStyle("-fx-background-color: " + ThemeProvider.get("bg-hover") + "; -fx-background-radius: 5;");
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
            info.setStyle("-fx-background-color: " + ThemeProvider.get("bg-hover") + "; -fx-background-radius: 5;");

            Label label = new Label(String.format("\uD83C\uDFB5 %s (%s)", name, dur));
            label.setStyle("-fx-font-size: 11px; -fx-text-fill: " + ThemeProvider.get("text-primary") + ";");
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Button playBtn = new Button("▶");
            playBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + ThemeProvider.get("text-primary") + "; -fx-cursor: hand;");
            playBtn.setOnAction(e -> togglePreview(data, playBtn));

            Button delBtn = new Button("\uD83D\uDDD1");
            delBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + ThemeProvider.get("accent-red") + "; -fx-cursor: hand;");
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
                    new Alert(Alert.AlertType.WARNING, TranslationProvider.get("card.err_playback_failed")).show();
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

    private void setupInputField(javafx.scene.control.TextInputControl input, String inputBg, String inputText, String inputPrompt) {
        String baseStyle = String.format(
            "-fx-control-inner-background: %s; " +
            "-fx-background-color: %s; " +
            "-fx-text-fill: %s; " +
            "-fx-text-inner-color: %s; " +
            "-fx-prompt-text-fill: %s; " +
            "-fx-border-color: %s; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 5; " +
            "-fx-background-radius: 5; " +
            "-fx-focus-color: transparent; " +
            "-fx-faint-focus-color: transparent;",
            inputBg, inputBg, inputText, inputText, inputPrompt, ThemeProvider.get("border-muted")
        );
        input.setStyle(baseStyle);

        input.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                input.setStyle(baseStyle + " -fx-border-color: " + ThemeProvider.get("accent-blue") + ";");
            } else {
                input.setStyle(baseStyle);
            }
        });
    }
}
