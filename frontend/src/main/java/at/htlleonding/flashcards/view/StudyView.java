package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Card;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class StudyView {
    private final Stage stage;
    private final List<Card> studyCards;
    private int currentIndex;
    private Card currentCard;

    private final BorderPane root;
    private final Button flipBtn;
    private final Button nextBtn;
    private final HBox assessmentBox;

    private final List<MediaPlayer> activeMediaPlayers = new ArrayList<>();

    private Consumer<String> onAssessment;
    private Runnable onFinish;

    public StudyView(List<Card> cards) {
        this.studyCards = new ArrayList<>(cards);
        Collections.shuffle(this.studyCards);
        this.currentIndex = 0;
        this.currentCard = this.studyCards.isEmpty() ? null : this.studyCards.get(0);

        stage = new Stage();
        stage.setTitle("Study Mode");
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.setOnHidden(e -> stopAllAudio());

        root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f8f9fa;");

        // ── TOP: Finish ────────────────────────────────────────────────────
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);
        Button finishBtn = new Button("Finish");
        finishBtn.setStyle(
            "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-font-size: 14px; -fx-padding: 10 24; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        finishBtn.setOnAction(e -> {
            if (onFinish != null) onFinish.run();
            stage.close();
        });
        topBar.getChildren().add(finishBtn);
        root.setTop(topBar);

        // ── BOTTOM: Buttons ────────────────────────────────────────────────
        VBox bottomArea = new VBox(12);
        bottomArea.setAlignment(Pos.CENTER);

        assessmentBox = new HBox(10);
        assessmentBox.setAlignment(Pos.CENTER);
        String[][] assessments = {
            {"Falsch", "#dc3545"},
            {"Schwierig", "#FF9800"},
            {"Ok", "#2196F3"},
            {"Leicht", "#4CAF50"}
        };
        for (String[] ass : assessments) {
            Button btn = new Button(ass[0]);
            btn.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-font-size: 13px; -fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand;",
                ass[1]
            ));
            btn.setOnAction(e -> {
                if (onAssessment != null) onAssessment.accept(ass[0]);
            });
            assessmentBox.getChildren().add(btn);
        }
        assessmentBox.setVisible(false);
        assessmentBox.setManaged(false);

        HBox navBox = new HBox(20);
        navBox.setAlignment(Pos.CENTER);

        flipBtn = new Button("Aufdecken");
        flipBtn.setStyle(
            "-fx-background-color: #607D8B; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-font-size: 14px; -fx-padding: 10 24; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        flipBtn.setOnAction(e -> flipCard());

        nextBtn = new Button("Next Card");
        nextBtn.setStyle(
            "-fx-background-color: #607D8B; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-font-size: 14px; -fx-padding: 10 24; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        nextBtn.setOnAction(e -> nextCard());
        nextBtn.setVisible(false);
        nextBtn.setManaged(false);

        navBox.getChildren().addAll(flipBtn, nextBtn);
        bottomArea.getChildren().addAll(assessmentBox, navBox);
        root.setBottom(bottomArea);

        Scene scene = new Scene(root);
        stage.setScene(scene);

        showCardFront();
    }

    private Node buildSidePanel(String text, String textStyle,
                                String imageData, String imageName,
                                String audioData, String audioName) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle(textStyle);
        label.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);

        boolean hasMedia = (imageData != null && !imageData.isEmpty()) ||
                           (audioData != null && !audioData.isEmpty());

        if (!hasMedia) {
            StackPane pane = new StackPane(label);
            pane.setAlignment(Pos.CENTER);
            return pane;
        }

        VBox content = new VBox(12);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(Double.MAX_VALUE);

        if (imageData != null && !imageData.isEmpty()) {
            content.getChildren().add(
                FlashcardsView.buildImageUI(imageData, imageName, 300, 250));
        }
        if (audioData != null && !audioData.isEmpty()) {
            content.getChildren().add(
                FlashcardsView.buildAudioPlayerUI(audioData, audioName, activeMediaPlayers));
        }
        content.getChildren().add(label);

        StackPane pane = new StackPane(content);
        pane.setAlignment(Pos.CENTER);
        return pane;
    }

    private void showCardFront() {
        stopAllAudio();
        if (currentCard == null) {
            Label empty = new Label("No cards to study.");
            empty.setStyle("-fx-font-size: 18px; -fx-text-fill: #999999;");
            root.setCenter(new StackPane(empty));
            flipBtn.setVisible(false);
            flipBtn.setManaged(false);
            nextBtn.setVisible(false);
            nextBtn.setManaged(false);
            assessmentBox.setVisible(false);
            assessmentBox.setManaged(false);
            return;
        }

        String style = "-fx-font-size: 36px; -fx-text-fill: #0D47A1; -fx-font-weight: bold;";
        root.setCenter(buildSidePanel(
            currentCard.getQuestion(), style,
            currentCard.getFrontImageData(), currentCard.getFrontImageName(),
            currentCard.getFrontAudioData(), currentCard.getFrontAudioName()
        ));

        flipBtn.setText("Aufdecken");
        flipBtn.setVisible(true);
        flipBtn.setManaged(true);
        nextBtn.setVisible(false);
        nextBtn.setManaged(false);
        assessmentBox.setVisible(false);
        assessmentBox.setManaged(false);
    }

    private void flipCard() {
        if (currentCard == null) return;

        String frontStyle = "-fx-font-size: 28px; -fx-text-fill: #0D47A1; -fx-font-weight: bold;";
        String backStyle  = "-fx-font-size: 28px; -fx-text-fill: #1B5E20; -fx-font-weight: bold;";

        Node left = buildSidePanel(
            currentCard.getQuestion(), frontStyle,
            currentCard.getFrontImageData(), currentCard.getFrontImageName(),
            currentCard.getFrontAudioData(), currentCard.getFrontAudioName()
        );

        Node right = buildSidePanel(
            currentCard.getAnswer(), backStyle,
            currentCard.getBackImageData(), currentCard.getBackImageName(),
            currentCard.getBackAudioData(), currentCard.getBackAudioName()
        );

        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        if (left instanceof Region lr) lr.setMaxWidth(Double.MAX_VALUE);
        if (right instanceof Region rr) rr.setMaxWidth(Double.MAX_VALUE);

        Separator divider = new Separator(Orientation.VERTICAL);

        HBox split = new HBox(10, left, divider, right);
        split.setAlignment(Pos.CENTER);
        VBox.setVgrow(split, Priority.ALWAYS);

        root.setCenter(split);

        flipBtn.setVisible(false);
        flipBtn.setManaged(false);
        nextBtn.setVisible(true);
        nextBtn.setManaged(true);
        assessmentBox.setVisible(true);
        assessmentBox.setManaged(true);
    }

    private void stopAllAudio() {
        for (MediaPlayer mp : activeMediaPlayers) {
            mp.stop();
            mp.dispose();
        }
        activeMediaPlayers.clear();
    }

    private void nextCard() {
        currentIndex++;
        if (currentIndex >= studyCards.size()) {
            currentIndex = 0;
        }
        currentCard = studyCards.get(currentIndex);
        showCardFront();
    }

    public void show() {
        stage.show();
    }

    public void setOnAssessment(Consumer<String> cb) {
        this.onAssessment = cb;
    }

    public void setOnFinish(Runnable cb) {
        this.onFinish = cb;
    }
}
