package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Card;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class FlashcardsView extends HBox {

    // ── state ──────────────────────────────────────────────────────────────
    private List<Card> currentCards = new ArrayList<>();
    private boolean selectMode = false;
    private final Set<Card> selectedCards = new LinkedHashSet<>();
    private Card selectedDetailCard = null;
    private final List<MediaPlayer> activeMediaPlayers = new ArrayList<>();

    // ── ui references ──────────────────────────────────────────────────────
    private FlowPane cardsGrid;
    private VBox detailPanel;
    private VBox contentArea;
    private VBox deckInfoRow;
    private Button selectToggleBtn;
    private Button exportSelectedBtn;
    private Button deleteSelectedBtn;
    private Label deckTitleLabel;
    private ImageView iconView;

    // ── callbacks ──────────────────────────────────────────────────────────
    private Runnable onAddCardRequested;
    private Consumer<Card> onEditCardRequested;
    private Consumer<Card> onDeleteCardRequested;
    private Runnable onImportRequested;
    private Consumer<Card> onExportCardRequested;
    private Consumer<List<Card>> onExportSelectedCardsRequested;
    private Consumer<List<Card>> onDeleteSelectedCardsRequested;
    private Function<Card, String> deckNameResolver;

    public FlashcardsView() {
        this.setPadding(new Insets(20));
        this.setSpacing(20);

        VBox leftSide = buildLeftSide();
        HBox.setHgrow(leftSide, Priority.ALWAYS);

        detailPanel = buildDetailPanel();

        this.getChildren().addAll(leftSide, detailPanel);
    }

    // ── layout builders ────────────────────────────────────────────────────

    private VBox buildLeftSide() {
        // ── action bar ────────────────────────────────────────────────────
        Button importBtn = createSubtleBtn("⬇ Import Cards", "#2196F3");
        importBtn.setOnAction(e -> { if (onImportRequested != null) onImportRequested.run(); });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        selectToggleBtn = createBtn("Select", "#607D8B", "#455A64");
        selectToggleBtn.setOnAction(e -> toggleSelectMode());

        // shown only in select mode
        deleteSelectedBtn = createBtn("Delete (0)", "#dc3545", "#b02a37");
        deleteSelectedBtn.setVisible(false);
        deleteSelectedBtn.setManaged(false);
        deleteSelectedBtn.setDisable(true);
        deleteSelectedBtn.setOnAction(e -> {
            if (onDeleteSelectedCardsRequested != null && !selectedCards.isEmpty())
                onDeleteSelectedCardsRequested.accept(new ArrayList<>(selectedCards));
        });

        exportSelectedBtn = createSubtleBtn("⬆ Export (0)", "#4CAF50");
        exportSelectedBtn.setVisible(false);
        exportSelectedBtn.setManaged(false);
        exportSelectedBtn.setDisable(true);
        exportSelectedBtn.setOnAction(e -> {
            if (onExportSelectedCardsRequested != null && !selectedCards.isEmpty())
                onExportSelectedCardsRequested.accept(new ArrayList<>(selectedCards));
        });

        HBox actionBar = new HBox(8, importBtn, spacer, selectToggleBtn, deleteSelectedBtn, exportSelectedBtn);
        actionBar.setAlignment(Pos.CENTER_LEFT);

        // ── cards grid ────────────────────────────────────────────────────
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

        return new VBox(10, actionBar, scrollPane);
    }

    private VBox buildDetailPanel() {
        VBox panel = new VBox();
        panel.setPrefWidth(230);
        panel.setMinWidth(230);
        panel.setPadding(new Insets(16));
        panel.setSpacing(0);
        panel.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #e0e0e0; " +
                       "-fx-border-radius: 12; -fx-background-radius: 12;");

        // ── deck info row ─────────────────────────────────────────────────
        deckTitleLabel = new Label("");
        deckTitleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        deckTitleLabel.setWrapText(true);
        deckTitleLabel.setAlignment(Pos.CENTER);
        deckTitleLabel.setTextAlignment(TextAlignment.CENTER);
        deckTitleLabel.setMaxWidth(Double.MAX_VALUE);

        Image defaultIcon = IconManager.getIcon("default");
        iconView = new ImageView(defaultIcon);
        iconView.setFitWidth(100);
        iconView.setFitHeight(100);
        iconView.setPreserveRatio(true);
        iconView.setSmooth(true);

        deckInfoRow = new VBox(8, iconView, deckTitleLabel);
        deckInfoRow.setAlignment(Pos.CENTER);
        deckInfoRow.setVisible(false);
        deckInfoRow.setManaged(false);

        Separator separator = new Separator();
        separator.setPadding(new Insets(6, 0, 6, 0));
        separator.visibleProperty().bind(deckInfoRow.visibleProperty());
        separator.managedProperty().bind(deckInfoRow.managedProperty());

        // ── content area ──────────────────────────────────────────────────
        contentArea = new VBox();
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        showPlaceholder();

        panel.getChildren().addAll(deckInfoRow, separator, contentArea);
        return panel;
    }

    // ── deck info ──────────────────────────────────────────────────────────

    public String getDeckTitle() {
        return deckTitleLabel.getText();
    }

    public void setDeckInfo(String title, String description, String iconId) {
        deckTitleLabel.setText(title != null ? title : "");
        Image iconImage = IconManager.getIcon(iconId);
        if (iconImage != null) iconView.setImage(iconImage);
        deckInfoRow.setVisible(true);
        deckInfoRow.setManaged(true);
    }

    public void clearDeckInfo() {
        stopAllAudio();
        deckInfoRow.setVisible(false);
        deckInfoRow.setManaged(false);
        deckTitleLabel.setText("");
        selectedDetailCard = null;
        showPlaceholder();
    }

    public void clearSelectedCard() {
        stopAllAudio();
        selectedDetailCard = null;
        showPlaceholder();
    }

    // ── detail panel content ───────────────────────────────────────────────

    private void showPlaceholder() {
        stopAllAudio();
        contentArea.getChildren().clear();
        Label placeholder = new Label("Select a card\nto see details");
        placeholder.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 14px;");
        placeholder.setAlignment(Pos.CENTER);
        placeholder.setMaxWidth(Double.MAX_VALUE);
        placeholder.setMaxHeight(Double.MAX_VALUE);
        placeholder.setTextAlignment(TextAlignment.CENTER);
        VBox.setVgrow(placeholder, Priority.ALWAYS);
        contentArea.getChildren().add(placeholder);
    }

    private void showCardDetail(Card card) {
        stopAllAudio();
        selectedDetailCard = card;
        contentArea.getChildren().clear();
        contentArea.setSpacing(10);

        // Show deck badge only in "all cards" mode (deck info row is hidden)
        if (!deckInfoRow.isVisible() && deckNameResolver != null) {
            String deckName = deckNameResolver.apply(card);
            if (deckName != null) {
                Label badge = new Label("Deck: " + deckName);
                badge.setPadding(new Insets(4, 10, 4, 10));
                badge.setStyle("-fx-background-color: #EEEEEE; -fx-border-color: #BDBDBD; " +
                               "-fx-border-radius: 12; -fx-background-radius: 12; " +
                               "-fx-font-size: 11px; -fx-text-fill: #555555;");
                contentArea.getChildren().add(badge);
            }
        }

        VBox questionBox = new VBox(6);
        questionBox.setPadding(new Insets(12));
        questionBox.setStyle("-fx-background-color: #E3F2FD; -fx-border-color: #90CAF9; -fx-border-radius: 8; -fx-background-radius: 8;");
        Label qHeader = new Label("QUESTION");
        qHeader.setStyle("-fx-font-size: 10px; -fx-text-fill: #1565C0; -fx-font-weight: bold;");
        Label qText = new Label(card.getQuestion());
        qText.setWrapText(true);
        qText.setStyle("-fx-font-size: 14px; -fx-text-fill: #0D47A1;");
        questionBox.getChildren().addAll(qHeader, qText);
        
        if (card.getFrontAudioData() != null) {
            questionBox.getChildren().add(buildAudioPlayerUI(card.getFrontAudioData(), card.getFrontAudioName()));
        }
        if (card.getFrontImageData() != null) {
            questionBox.getChildren().add(buildImageUI(card.getFrontImageData(), card.getFrontImageName()));
        }

        VBox answerBox = new VBox(6);
        answerBox.setPadding(new Insets(12));
        answerBox.setStyle("-fx-background-color: #E8F5E9; -fx-border-color: #A5D6A7; -fx-border-radius: 8; -fx-background-radius: 8;");
        Label aHeader = new Label("ANSWER");
        aHeader.setStyle("-fx-font-size: 10px; -fx-text-fill: #2E7D32; -fx-font-weight: bold;");
        Label aText = new Label(card.getAnswer());
        aText.setWrapText(true);
        aText.setStyle("-fx-font-size: 14px; -fx-text-fill: #1B5E20;");
        answerBox.getChildren().addAll(aHeader, aText);

        if (card.getBackAudioData() != null) {
            answerBox.getChildren().add(buildAudioPlayerUI(card.getBackAudioData(), card.getBackAudioName()));
        }
        if (card.getBackImageData() != null) {
            answerBox.getChildren().add(buildImageUI(card.getBackImageData(), card.getBackImageName()));
        }

        contentArea.getChildren().addAll(questionBox, answerBox);

        if (card.getTags() != null && !card.getTags().isEmpty()) {
            FlowPane tagsPane = new FlowPane(6, 6);
            tagsPane.setPadding(new Insets(4, 0, 0, 0));
            for (String tag : card.getTags()) {
                Label chip = new Label(tag);
                chip.setPadding(new Insets(3, 8, 3, 8));
                chip.setStyle("-fx-background-color: #EEEEEE; -fx-border-color: #BDBDBD; -fx-border-radius: 12; -fx-background-radius: 12; -fx-font-size: 11px; -fx-text-fill: #555555;");
                tagsPane.getChildren().add(chip);
            }
            contentArea.getChildren().add(tagsPane);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        contentArea.getChildren().add(spacer);

        Button editBtn = createBtn("Edit", "#607D8B", "#455A64");
        editBtn.setMaxWidth(Double.MAX_VALUE);
        editBtn.setOnAction(e -> { if (onEditCardRequested != null) onEditCardRequested.accept(card); });

        Button exportCardBtn = createSubtleBtn("⬆ Export Card", "#4CAF50");
        exportCardBtn.setMaxWidth(Double.MAX_VALUE);
        exportCardBtn.setOnAction(e -> { if (onExportCardRequested != null) onExportCardRequested.accept(card); });

        contentArea.getChildren().add(new VBox(8, editBtn, exportCardBtn));
    }

    private void stopAllAudio() {
        for (MediaPlayer mp : activeMediaPlayers) {
            mp.stop();
            mp.dispose();
        }
        activeMediaPlayers.clear();
    }

    private String formatTime(Duration duration) {
        if (duration == null) return "00:00";
        int seconds = (int) duration.toSeconds();
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    private VBox buildAudioPlayerUI(String base64Data, String fileName) {
        File tempFile = AudioHelper.saveTempAudio(base64Data);
        if (tempFile == null) return new VBox(new Label("Error loading audio"));

        MediaPlayer mediaPlayer;
        try {
            Media media = new Media(tempFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            activeMediaPlayers.add(mediaPlayer);
        } catch (Exception e) {
            return new VBox(new Label("Audio player error: Codecs missing?"));
        }

        VBox player = new VBox(4);
        player.setPadding(new Insets(8, 0, 0, 0));

        Label nameLabel = new Label("🎵 " + fileName);
        nameLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");

        HBox controls = new HBox(8);
        controls.setAlignment(Pos.CENTER_LEFT);

        Button playBtn = new Button("▶");
        playBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-background-radius: 50; -fx-min-width: 30; -fx-min-height: 30; -fx-cursor: hand;");

        Slider progressSlider = new Slider(0, 100, 0);
        HBox.setHgrow(progressSlider, Priority.ALWAYS);

        Label timeLabel = new Label("00:00 / 00:00");
        timeLabel.setStyle("-fx-font-size: 10px;");

        playBtn.setOnAction(e -> {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playBtn.setText("▶");
            } else {
                mediaPlayer.play();
                playBtn.setText("⏸");
            }
        });

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!progressSlider.isValueChanging()) {
                double total = mediaPlayer.getTotalDuration().toSeconds();
                if (total > 0) {
                    progressSlider.setValue(newTime.toSeconds() / total * 100);
                }
            }
            timeLabel.setText(formatTime(newTime) + " / " + formatTime(mediaPlayer.getTotalDuration()));
        });

        progressSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (progressSlider.isValueChanging()) {
                mediaPlayer.seek(mediaPlayer.getTotalDuration().multiply(newVal.doubleValue() / 100.0));
            }
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.stop();
            mediaPlayer.seek(Duration.ZERO);
            playBtn.setText("▶");
            progressSlider.setValue(0);
        });

        controls.getChildren().addAll(playBtn, progressSlider, timeLabel);

        HBox volumeBox = new HBox(5);
        volumeBox.setAlignment(Pos.CENTER_LEFT);
        Label volIcon = new Label("🔊");
        Slider volumeSlider = new Slider(0, 1, 0.5);
        volumeSlider.setPrefWidth(80);
        mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty());
        volumeBox.getChildren().addAll(volIcon, volumeSlider);

        player.getChildren().addAll(nameLabel, controls, volumeBox);
        return player;
    }

    private VBox buildImageUI(String base64Data, String fileName) {
        byte[] bytes = Base64.getDecoder().decode(base64Data);
        ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(bytes)));
        imageView.setFitWidth(190);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        Label nameLabel = new Label("🖼 " + (fileName != null ? fileName : ""));
        nameLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");

        VBox container = new VBox(4, imageView, nameLabel);
        container.setPadding(new Insets(8, 0, 0, 0));
        return container;
    }

    // ── select mode ────────────────────────────────────────────────────────

    private void toggleSelectMode() {
        selectMode = !selectMode;
        selectedCards.clear();
        if (selectMode) {
            selectToggleBtn.setText("Cancel");
            deleteSelectedBtn.setVisible(true);
            deleteSelectedBtn.setManaged(true);
            exportSelectedBtn.setVisible(true);
            exportSelectedBtn.setManaged(true);
            updateSelectModeButtons();

            contentArea.getChildren().clear();
            Label hint = new Label("Select cards\nto export or delete");
            hint.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 14px;");
            hint.setAlignment(Pos.CENTER);
            hint.setMaxWidth(Double.MAX_VALUE);
            hint.setMaxHeight(Double.MAX_VALUE);
            hint.setTextAlignment(TextAlignment.CENTER);
            VBox.setVgrow(hint, Priority.ALWAYS);
            contentArea.getChildren().add(hint);
        } else {
            selectToggleBtn.setText("Select");
            deleteSelectedBtn.setVisible(false);
            deleteSelectedBtn.setManaged(false);
            exportSelectedBtn.setVisible(false);
            exportSelectedBtn.setManaged(false);
            showPlaceholder();
        }
        renderCards(currentCards);
    }

    private void updateSelectModeButtons() {
        int n = selectedCards.size();
        exportSelectedBtn.setText("Export (" + n + ")");
        exportSelectedBtn.setDisable(n == 0);
        deleteSelectedBtn.setText("Delete (" + n + ")");
        deleteSelectedBtn.setDisable(n == 0);
    }

    // ── card rendering ─────────────────────────────────────────────────────

    private void addPlusCard() {
        VBox plusCard = new VBox();
        plusCard.setPrefSize(120, 160);
        plusCard.setAlignment(Pos.CENTER);
        plusCard.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;");
        Label plusLabel = new Label("+");
        plusLabel.setStyle("-fx-font-size: 40px; -fx-text-fill: #999999;");
        plusCard.getChildren().add(plusLabel);
        plusCard.setOnMouseClicked(e -> { if (onAddCardRequested != null) onAddCardRequested.run(); });
        cardsGrid.getChildren().add(plusCard);
    }

    public void exitSelectMode() {
        if (!selectMode) return;
        selectMode = false;
        selectedCards.clear();
        selectToggleBtn.setText("Select");
        deleteSelectedBtn.setVisible(false);
        deleteSelectedBtn.setManaged(false);
        exportSelectedBtn.setVisible(false);
        exportSelectedBtn.setManaged(false);
        showPlaceholder();
    }

    public void renderCards(List<Card> cards) {
        currentCards = cards;
        cardsGrid.getChildren().clear();
        addPlusCard();

        for (Card card : cards) {
            boolean isSelected = selectedCards.contains(card);
            String borderColor = isSelected ? "#2196F3" : "#cccccc";
            String bgColor     = isSelected ? "#E3F2FD" : "white";

            VBox cardTile = new VBox();
            cardTile.setPrefSize(120, 160);
            cardTile.setPadding(new Insets(5));
            cardTile.setAlignment(Pos.TOP_CENTER);
            cardTile.setStyle(String.format(
                "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: %s; " +
                "-fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;",
                bgColor, borderColor, isSelected ? "2" : "1"
            ));

            HBox topBox = new HBox();

            if (selectMode) {
                Label checkmark = new Label(isSelected ? "✔" : "○");
                checkmark.setStyle("-fx-font-size: 14px; -fx-text-fill: " + (isSelected ? "#2196F3" : "#bbbbbb") + ";");
                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);
                topBox.getChildren().addAll(checkmark, sp);
            } else {
                Button editBtn = new Button("✎");
                editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;");
                editBtn.setOnMouseEntered(ev -> editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #007bff; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;"));
                editBtn.setOnMouseExited(ev -> editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;"));
                editBtn.setOnAction(e -> {
                    e.consume();
                    if (onEditCardRequested != null) onEditCardRequested.accept(card);
                });

                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);

                Button deleteBtn = new Button("✖");
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;");
                deleteBtn.setOnMouseEntered(ev -> deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #dc3545; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;"));
                deleteBtn.setOnMouseExited(ev -> deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 4;"));
                deleteBtn.setOnAction(e -> {
                    e.consume();
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Card");
                    alert.setHeaderText("Delete Flashcard");
                    alert.setContentText("Are you sure you want to delete this card?");
                    alert.showAndWait().ifPresent(r -> {
                        if (r == ButtonType.OK && onDeleteCardRequested != null) onDeleteCardRequested.accept(card);
                    });
                });
                topBox.getChildren().addAll(editBtn, sp, deleteBtn);
            }

            Label qLabel = new Label(card.getQuestion());
            qLabel.setWrapText(true);
            qLabel.setTextAlignment(TextAlignment.CENTER);
            qLabel.setAlignment(Pos.CENTER);
            qLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            qLabel.setStyle("-fx-text-fill: black;");
            VBox.setVgrow(qLabel, Priority.ALWAYS);

            cardTile.getChildren().addAll(topBox, qLabel);

            if (card.getTags() != null && !card.getTags().isEmpty()) {
                FlowPane tagsPane = new FlowPane(4, 4);
                tagsPane.setAlignment(Pos.CENTER);
                tagsPane.setPadding(new Insets(0, 0, 5, 0));
                int count = 0;
                for (String tag : card.getTags()) {
                    if (count >= 3) {
                        Label more = new Label("...");
                        more.setStyle("-fx-font-size: 9px; -fx-text-fill: #888888;");
                        tagsPane.getChildren().add(more);
                        break;
                    }
                    Label tagLabel = new Label(tag);
                    tagLabel.setPadding(new Insets(1, 6, 1, 6));
                    tagLabel.setStyle("-fx-background-color: #E3F2FD; -fx-border-color: #2196F3; " +
                                       "-fx-border-radius: 10; -fx-background-radius: 10; " +
                                       "-fx-font-size: 9px; -fx-text-fill: #1976D2; -fx-border-width: 0.5; -fx-font-weight: bold;");
                    tagsPane.getChildren().add(tagLabel);
                    count++;
                }
                cardTile.getChildren().add(tagsPane);
            }

            cardTile.setOnMouseClicked(e -> {
                if (selectMode) {
                    if (selectedCards.contains(card)) selectedCards.remove(card);
                    else selectedCards.add(card);
                    updateSelectModeButtons();
                    renderCards(currentCards);
                } else {
                    showCardDetail(card);
                }
            });

            cardsGrid.getChildren().add(cardTile);
        }
    }

    // ── helpers ────────────────────────────────────────────────────────────

     private Button createBtn(String text, String color, String hoverColor) {
         Button btn = new Button(text);
         String s1 = String.format("-fx-background-color: %s; -fx-border-color: %s; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 14; -fx-cursor: hand; -fx-font-size: 13px; -fx-text-fill: white; -fx-font-weight: bold;", color, hoverColor);
         String s2 = String.format("-fx-background-color: %s; -fx-border-color: %s; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 14; -fx-cursor: hand; -fx-font-size: 13px; -fx-text-fill: white; -fx-font-weight: bold;", hoverColor, hoverColor);
         btn.setStyle(s1);
         btn.setOnMouseEntered(e -> btn.setStyle(s2));
         btn.setOnMouseExited(e -> btn.setStyle(s1));
         return btn;
     }

     private Button createSubtleBtn(String text, String accentColor) {
         Button btn = new Button(text);
         String s1 = String.format(
             "-fx-background-color: white; -fx-border-color: %s; -fx-border-width: 1.5; -fx-border-radius: 8; " +
             "-fx-background-radius: 8; -fx-padding: 10 16; -fx-cursor: hand; -fx-font-size: 13px; " +
             "-fx-text-fill: #000000; -fx-font-weight: bold;",
             accentColor
         );
         String s2 = String.format(
             "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 1.5; -fx-border-radius: 8; " +
             "-fx-background-radius: 8; -fx-padding: 10 16; -fx-cursor: hand; -fx-font-size: 13px; " +
             "-fx-text-fill: white; -fx-font-weight: bold;",
             accentColor, accentColor
         );
         btn.setStyle(s1);
         btn.setOnMouseEntered(e -> btn.setStyle(s2));
         btn.setOnMouseExited(e -> btn.setStyle(s1));
         return btn;
     }

    // ── callback setters ───────────────────────────────────────────────────

    public void setOnAddCardRequested(Runnable cb) { this.onAddCardRequested = cb; }
    public void setOnEditCardRequested(Consumer<Card> cb) { this.onEditCardRequested = cb; }
    public void setOnDeleteCardRequested(Consumer<Card> cb) { this.onDeleteCardRequested = cb; }
    public void setOnImportRequested(Runnable cb) { this.onImportRequested = cb; }
    public void setOnExportCardRequested(Consumer<Card> cb) { this.onExportCardRequested = cb; }
    public void setOnExportSelectedCardsRequested(Consumer<List<Card>> cb) { this.onExportSelectedCardsRequested = cb; }
    public void setOnDeleteSelectedCardsRequested(Consumer<List<Card>> cb) { this.onDeleteSelectedCardsRequested = cb; }
    public void setDeckNameResolver(Function<Card, String> resolver) { this.deckNameResolver = resolver; }
}
