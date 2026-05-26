package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Card;
import at.htlleonding.flashcards.model.CardSelector;
import at.htlleonding.flashcards.model.StudyRecord;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class StudyView {
    private final Stage stage;
    private final List<Card> studyCards;
    private Card currentCard;
    private final CardSelector cardSelector = CardSelector.of(CardSelector.AlgorithmType.WEIGHTED);

    private final BorderPane root;
    private final Button flipBtn;
    private final Button nextBtn;
    private final HBox assessmentBox;

    private final List<MediaPlayer> activeMediaPlayers = new ArrayList<>();

    private final Map<String, Integer> sessionRatings = new HashMap<>();
    private final Set<String> ratedCardIds = new HashSet<>();

    private Consumer<String> onAssessment;
    private Runnable onFinish;
    private Runnable onSessionEnd;

    public StudyView(List<Card> cards) {
        this.studyCards = new ArrayList<>(cards);
        this.currentCard = studyCards.isEmpty() ? null : cardSelector.selectNext(studyCards);

        stage = new Stage();
        stage.setTitle("Study Mode");
        stage.setWidth(900);
        stage.setHeight(700);
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
            fireSessionEnd();
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
            {"Falsch",    "#dc3545"},
            {"Schwierig", "#FF9800"},
            {"Ok",        "#2196F3"},
            {"Leicht",    "#4CAF50"}
        };
        for (String[] ass : assessments) {
            Button btn = new Button(ass[0]);
            btn.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-font-size: 13px; -fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand;",
                ass[1]
            ));
            btn.setOnAction(e -> handleAssessment(ass[0]));
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

        nextBtn = new Button("Nächste Karte");
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

    private void handleAssessment(String label) {
        if (currentCard == null) return;
        String ratingKey = label.toUpperCase();
        cardSelector.recordRating(currentCard, ratingKey);
        currentCard.getStudyHistory().add(new StudyRecord(ratingKey));
        sessionRatings.merge(label, 1, Integer::sum);
        ratedCardIds.add(currentCard.getId());
        if (onAssessment != null) onAssessment.accept(label);

        if (ratedCardIds.size() >= studyCards.size()) {
            showStats();
        } else {
            nextCard();
        }
    }

    private Node buildSidePanel(String text, String textStyle,
                                String imageData, String imageName,
                                String audioData, String audioName) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setStyle(textStyle);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);

        boolean hasMedia = (imageData != null && !imageData.isEmpty()) ||
                           (audioData != null && !audioData.isEmpty());

        if (!hasMedia) {
            VBox wrapper = new VBox(label);
            wrapper.setAlignment(Pos.CENTER);
            VBox.setVgrow(label, Priority.ALWAYS);
            return wrapper;
        }

        VBox content = new VBox(16);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(Double.MAX_VALUE);
        content.setMaxHeight(Double.MAX_VALUE);

        if (imageData != null && !imageData.isEmpty()) {
            content.getChildren().add(
                FlashcardsView.buildImageUI(imageData, imageName, 300, 200));
        }
        if (audioData != null && !audioData.isEmpty()) {
            content.getChildren().add(
                FlashcardsView.buildAudioPlayerUI(audioData, audioName, activeMediaPlayers));
        }
        content.getChildren().add(label);

        VBox wrapper = new VBox(content);
        wrapper.setAlignment(Pos.CENTER);
        VBox.setVgrow(content, Priority.ALWAYS);
        return wrapper;
    }

    private void showCardFront() {
        stopAllAudio();
        if (currentCard == null) {
            Label empty = new Label("Keine Karten zum Lernen.");
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

        String frontStyle = "-fx-font-size: 26px; -fx-text-fill: #0D47A1; -fx-font-weight: bold;";
        String backStyle  = "-fx-font-size: 26px; -fx-text-fill: #1B5E20; -fx-font-weight: bold;";

        Node top = buildSidePanel(
            currentCard.getQuestion(), frontStyle,
            currentCard.getFrontImageData(), currentCard.getFrontImageName(),
            currentCard.getFrontAudioData(), currentCard.getFrontAudioName()
        );
        Node bottom = buildSidePanel(
            currentCard.getAnswer(), backStyle,
            currentCard.getBackImageData(), currentCard.getBackImageName(),
            currentCard.getBackAudioData(), currentCard.getBackAudioName()
        );

        if (top instanceof Region tr) { tr.setMaxWidth(Double.MAX_VALUE); VBox.setVgrow(tr, Priority.ALWAYS); }
        if (bottom instanceof Region br) { br.setMaxWidth(Double.MAX_VALUE); VBox.setVgrow(br, Priority.ALWAYS); }

        Separator divider = new Separator(Orientation.HORIZONTAL);
        divider.setPadding(new Insets(4, 0, 4, 0));

        VBox split = new VBox(10, top, divider, bottom);
        split.setAlignment(Pos.CENTER);
        split.setFillWidth(true);

        root.setCenter(split);

        flipBtn.setVisible(false);
        flipBtn.setManaged(false);
        nextBtn.setVisible(true);
        nextBtn.setManaged(true);
        assessmentBox.setVisible(true);
        assessmentBox.setManaged(true);
    }

    private void showStats() {
        stopAllAudio();

        VBox statsBox = new VBox(20);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(40));
        statsBox.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 16;");

        Label title = new Label("Session abgeschlossen!");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #212121;");

        Label subtitle = new Label(studyCards.size() + " Karten bewertet");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #757575;");

        VBox ratingsBox = new VBox(10);
        ratingsBox.setAlignment(Pos.CENTER);
        String[][] ratings = {
            {"Leicht",    "#4CAF50"},
            {"Ok",        "#2196F3"},
            {"Schwierig", "#FF9800"},
            {"Falsch",    "#dc3545"}
        };
        for (String[] r : ratings) {
            int count = sessionRatings.getOrDefault(r[0], 0);
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER);
            Label dot = new Label("●");
            dot.setStyle("-fx-font-size: 18px; -fx-text-fill: " + r[1] + ";");
            Label lbl = new Label(r[0] + ": " + count);
            lbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #212121; -fx-min-width: 160;");
            row.getChildren().addAll(dot, lbl);
            ratingsBox.getChildren().add(row);
        }

        HBox buttons = new HBox(16);
        buttons.setAlignment(Pos.CENTER);

        Button retryBtn = new Button("Wiederholen");
        retryBtn.setStyle(
            "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-font-size: 14px; -fx-padding: 10 28; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        retryBtn.setOnAction(e -> {
            sessionRatings.clear();
            ratedCardIds.clear();
            cardSelector.reset();
            currentCard = studyCards.isEmpty() ? null : cardSelector.selectNext(studyCards);
            flipBtn.setVisible(true);
            flipBtn.setManaged(true);
            nextBtn.setVisible(false);
            nextBtn.setManaged(false);
            assessmentBox.setVisible(false);
            assessmentBox.setManaged(false);
            showCardFront();
        });

        Button doneBtn = new Button("Fertig");
        doneBtn.setStyle(
            "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-font-size: 14px; -fx-padding: 10 28; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        doneBtn.setOnAction(e -> {
            fireSessionEnd();
            stage.close();
        });

        buttons.getChildren().addAll(retryBtn, doneBtn);
        statsBox.getChildren().addAll(title, subtitle, ratingsBox, buttons);

        StackPane centerWrapper = new StackPane(statsBox);
        centerWrapper.setAlignment(Pos.CENTER);
        root.setCenter(centerWrapper);

        flipBtn.setVisible(false);
        flipBtn.setManaged(false);
        nextBtn.setVisible(false);
        nextBtn.setManaged(false);
        assessmentBox.setVisible(false);
        assessmentBox.setManaged(false);
    }

    private void fireSessionEnd() {
        if (onSessionEnd != null) onSessionEnd.run();
    }

    private void stopAllAudio() {
        for (MediaPlayer mp : activeMediaPlayers) {
            mp.stop();
            mp.dispose();
        }
        activeMediaPlayers.clear();
    }

    private void nextCard() {
        currentCard = cardSelector.selectNext(studyCards);
        showCardFront();
    }

    public void show() {
        stage.show();
    }

    public void setOnAssessment(Consumer<String> cb) { this.onAssessment = cb; }
    public void setOnFinish(Runnable cb) { this.onFinish = cb; }
    public void setOnSessionEnd(Runnable cb) { this.onSessionEnd = cb; }
}
