package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Card;
import at.htlleonding.flashcards.model.StudyRecord;
import at.htlleonding.flashcards.model.ThemeProvider;
import at.htlleonding.flashcards.model.TranslationProvider;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class StudyView {
    private final Stage stage;
    private final List<Card> studyCards;
    private Card currentCard;
    private int studyIndex = 0;

    private final BorderPane root;
    private final Button flipBtn;
    private final Button nextBtn;
    private final HBox assessmentBox;
    private final Button beendenBtn;
    private final List<Button> assessmentBtns = new ArrayList<>();

    private final List<MediaPlayer> activeMediaPlayers = new ArrayList<>();

    private final Map<String, Integer> sessionRatings = new HashMap<>();

    private Consumer<String> onAssessment;
    private Runnable onFinish;
    private Runnable onSessionEnd;

    public StudyView(List<Card> cards) {
        this.studyCards = new ArrayList<>(cards);
        Collections.shuffle(this.studyCards);
        this.currentCard = studyCards.isEmpty() ? null : studyCards.get(0);

        stage = new Stage();
        stage.setTitle("Study Mode");
        stage.setWidth(900);
        stage.setHeight(700);

        root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + ThemeProvider.get("bg-primary") + ";");

        // ── TOP: Beenden ───────────────────────────────────────────────────
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);
        beendenBtn = new Button();
        beendenBtn.textProperty().bind(TranslationProvider.createStringBinding("study.finish_btn"));
        beendenBtn.setStyle(
            "-fx-background-color: " + ThemeProvider.get("accent-green") + "; -fx-text-fill: " + ThemeProvider.get("text-on-primary") + "; -fx-font-weight: bold; " +
            "-fx-font-size: 14px; -fx-padding: 10 24; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        beendenBtn.setOnAction(e -> {
            if (onFinish != null) onFinish.run();
            fireSessionEnd();
            stage.close();
        });
        topBar.getChildren().add(beendenBtn);
        root.setTop(topBar);

        // ── BOTTOM: Buttons ────────────────────────────────────────────────
        VBox bottomArea = new VBox(12);
        bottomArea.setAlignment(Pos.CENTER);

        assessmentBox = new HBox(10);
        assessmentBox.setAlignment(Pos.CENTER);
        String[][] assessments = {
            {TranslationProvider.get("study.wrong"),     "accent-red",    "FALSCH"},
            {TranslationProvider.get("study.difficult"),  "accent-orange", "SCHWIERIG"},
            {TranslationProvider.get("study.ok"),          "accent-blue",   "OK"},
            {TranslationProvider.get("study.easy"),        "accent-green",  "LEICHT"}
        };
        for (String[] ass : assessments) {
            Button btn = new Button(ass[0]);
            btn.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-font-weight: bold; " +
                "-fx-font-size: 13px; -fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand;",
                ThemeProvider.get(ass[1]), ThemeProvider.get("text-on-primary")
            ));
            btn.setOnAction(e -> handleAssessment(ass[2]));
            btn.setUserData(ass[1]);
            assessmentBtns.add(btn);
            assessmentBox.getChildren().add(btn);
        }
        assessmentBox.setVisible(false);
        assessmentBox.setManaged(false);

        HBox navBox = new HBox(20);
        navBox.setAlignment(Pos.CENTER);

        flipBtn = new Button();
        flipBtn.textProperty().bind(TranslationProvider.createStringBinding("study.reveal_btn"));
        flipBtn.setStyle(
            "-fx-background-color: " + ThemeProvider.get("neutral-gray") + "; -fx-text-fill: " + ThemeProvider.get("text-on-primary") + "; -fx-font-weight: bold; " +
            "-fx-font-size: 14px; -fx-padding: 10 24; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        flipBtn.setOnAction(e -> flipCard());

        nextBtn = new Button("Nächste Karte");
        nextBtn.setStyle(
            "-fx-background-color: " + ThemeProvider.get("neutral-gray") + "; -fx-text-fill: " + ThemeProvider.get("text-on-primary") + "; -fx-font-weight: bold; " +
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

        Runnable themeListener = this::applyTheme;
        ThemeProvider.addThemeListener(themeListener);
        stage.setOnHidden(e -> {
            stopAllAudio();
            ThemeProvider.removeThemeListener(themeListener);
        });

        showCardFront();
    }

    private void handleAssessment(String label) {
        if (currentCard == null) return;
        currentCard.getStudyHistory().add(new StudyRecord(label));
        sessionRatings.merge(label, 1, Integer::sum);
        if (onAssessment != null) onAssessment.accept(label);
        nextCard();
    }

    private Node buildSidePanel(String text, String textStyle,
                                String imageData, String imageName,
                                String audioData, String audioName) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMinHeight(Region.USE_PREF_SIZE);

        boolean isCode = isMonospaceOrCode(text);
        boolean isMultiLine = text != null && text.contains("\n");

        String style = textStyle;
        if (isCode) {
            style = style.replace("-fx-font-size: 36px;", "-fx-font-size: 20px;")
                         .replace("-fx-font-size: 26px;", "-fx-font-size: 18px;")
                         .replace("-fx-font-weight: bold;", "");
            style += " -fx-font-family: monospace;";
        }
        label.setStyle(style);

        if (isCode || isMultiLine) {
            label.setTextAlignment(TextAlignment.LEFT);
            label.setAlignment(Pos.CENTER_LEFT);
        } else {
            label.setTextAlignment(TextAlignment.CENTER);
            label.setAlignment(Pos.CENTER);
        }
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
                FlashcardsView.buildImageUI(imageData, imageName, 500, 350));
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

    private static boolean isMonospaceOrCode(String text) {
        if (text == null) return false;
        String trimmed = text.trim();
        if (trimmed.startsWith("git ") ||
            trimmed.startsWith("docker ") ||
            trimmed.startsWith("$ ") ||
            trimmed.startsWith("# ") ||
            trimmed.startsWith("FROM ") ||
            trimmed.startsWith("RUN ") ||
            trimmed.startsWith("CMD ") ||
            trimmed.startsWith("WORKDIR ") ||
            trimmed.startsWith("COPY ") ||
            trimmed.startsWith("netstat ") ||
            trimmed.startsWith("lsof ") ||
            trimmed.startsWith("kubectl ") ||
            trimmed.startsWith("mvn ") ||
            trimmed.startsWith("java ") ||
            trimmed.startsWith("javac ") ||
            trimmed.startsWith("chmod ") ||
            trimmed.startsWith("cat ") ||
            trimmed.startsWith("echo ")) {
            return true;
        }
        for (String line : text.split("\n")) {
            String tl = line.trim();
            if (tl.startsWith("$ ") || tl.startsWith("git ") || tl.startsWith("docker ") || tl.startsWith("FROM ") || tl.startsWith("RUN ")) {
                return true;
            }
        }
        return false;
    }

    private void showCardFront() {
        stopAllAudio();
        if (currentCard == null) {
            Label empty = new Label();
            empty.textProperty().bind(TranslationProvider.createStringBinding("study.empty_deck"));
            empty.setStyle("-fx-font-size: 18px; -fx-text-fill: " + ThemeProvider.get("text-disabled") + ";");
            root.setCenter(new StackPane(empty));
            flipBtn.setVisible(false);
            flipBtn.setManaged(false);
            nextBtn.setVisible(false);
            nextBtn.setManaged(false);
            assessmentBox.setVisible(false);
            assessmentBox.setManaged(false);
            return;
        }

        String style = "-fx-font-size: 36px; -fx-text-fill: " + ThemeProvider.get("accent-blue-strong") + "; -fx-font-weight: bold;";
        setCenterScrollable(buildSidePanel(
            currentCard.getQuestion(), style,
            currentCard.getFrontImageData(), currentCard.getFrontImageName(),
            currentCard.getFrontAudioData(), currentCard.getFrontAudioName()
        ));

        flipBtn.setVisible(true);
        flipBtn.setManaged(true);
        nextBtn.setVisible(false);
        nextBtn.setManaged(false);
        assessmentBox.setVisible(false);
        assessmentBox.setManaged(false);
    }

    private void flipCard() {
        if (currentCard == null) return;

        String frontStyle = "-fx-font-size: 26px; -fx-text-fill: " + ThemeProvider.get("accent-blue-strong") + "; -fx-font-weight: bold;";
        String backStyle  = "-fx-font-size: 26px; -fx-text-fill: " + ThemeProvider.get("accent-green-dark") + "; -fx-font-weight: bold;";

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

        setCenterScrollable(split);

        flipBtn.setVisible(false);
        flipBtn.setManaged(false);
        nextBtn.setVisible(true);
        nextBtn.setManaged(true);
        assessmentBox.setVisible(true);
        assessmentBox.setManaged(true);
    }

    private void setCenterScrollable(Node node) {
        if (node instanceof Region r) {
            r.setMinHeight(Region.USE_PREF_SIZE);
        }
        ScrollPane scrollPane = new ScrollPane(node);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        root.setCenter(scrollPane);
    }

    private void showStats() {
        stopAllAudio();

        VBox statsBox = new VBox(20);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(40));
        statsBox.setStyle("-fx-background-color: " + ThemeProvider.get("bg-card") + "; -fx-background-radius: 16;");

        Label title = new Label(TranslationProvider.get("study.stats_title"));
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("fg-dark") + ";");

        Label subtitle = new Label(TranslationProvider.get("study.stats_subtitle", studyCards.size()));
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: " + ThemeProvider.get("text-subtle") + ";");

        VBox ratingsBox = new VBox(10);
        ratingsBox.setAlignment(Pos.CENTER);
        String[][] ratings = {
            {TranslationProvider.get("study.easy"),      "accent-green",  "LEICHT"},
            {TranslationProvider.get("study.ok"),         "accent-blue",   "OK"},
            {TranslationProvider.get("study.difficult"),  "accent-orange", "SCHWIERIG"},
            {TranslationProvider.get("study.wrong"),      "accent-red",    "FALSCH"}
        };
        for (String[] r : ratings) {
            int count = sessionRatings.getOrDefault(r[2], 0);
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER);
            Label dot = new Label("\u25CF");
            dot.setStyle("-fx-font-size: 18px; -fx-text-fill: " + ThemeProvider.get(r[1]) + ";");
            Label lbl = new Label(r[0] + ": " + count);
            lbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("fg-dark") + "; -fx-min-width: 160;");
            row.getChildren().addAll(dot, lbl);
            ratingsBox.getChildren().add(row);
        }

        HBox buttons = new HBox(16);
        buttons.setAlignment(Pos.CENTER);

        Button retryBtn = new Button(TranslationProvider.get("study.retry_btn"));
        retryBtn.setStyle(
            "-fx-background-color: " + ThemeProvider.get("accent-blue") + "; -fx-text-fill: " + ThemeProvider.get("text-on-primary") + "; -fx-font-weight: bold; " +
            "-fx-font-size: 14px; -fx-padding: 10 28; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        retryBtn.setOnAction(e -> {
            sessionRatings.clear();
            studyIndex = 0;
            Collections.shuffle(studyCards);
            currentCard = studyCards.isEmpty() ? null : studyCards.get(0);
            beendenBtn.setVisible(true);
            beendenBtn.setManaged(true);
            showCardFront();
        });

        Button doneBtn = new Button(TranslationProvider.get("study.done_btn"));
        doneBtn.setStyle(
            "-fx-background-color: " + ThemeProvider.get("accent-green") + "; -fx-text-fill: " + ThemeProvider.get("text-on-primary") + "; -fx-font-weight: bold; " +
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

        beendenBtn.setVisible(false);
        beendenBtn.setManaged(false);
        flipBtn.setVisible(false);
        flipBtn.setManaged(false);
        nextBtn.setVisible(false);
        nextBtn.setManaged(false);
        assessmentBox.setVisible(false);
        assessmentBox.setManaged(false);
    }

    public void applyTheme() {
        root.setStyle("-fx-background-color: " + ThemeProvider.get("bg-primary") + ";");
        beendenBtn.setStyle(
            "-fx-background-color: " + ThemeProvider.get("accent-green") + "; -fx-text-fill: " + ThemeProvider.get("text-on-primary") + "; -fx-font-weight: bold; " +
            "-fx-font-size: 14px; -fx-padding: 10 24; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        for (Button btn : assessmentBtns) {
            String token = (String) btn.getUserData();
            btn.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-font-weight: bold; " +
                "-fx-font-size: 13px; -fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand;",
                ThemeProvider.get(token), ThemeProvider.get("text-on-primary")
            ));
        }
        flipBtn.setStyle(
            "-fx-background-color: " + ThemeProvider.get("neutral-gray") + "; -fx-text-fill: " + ThemeProvider.get("text-on-primary") + "; -fx-font-weight: bold; " +
            "-fx-font-size: 14px; -fx-padding: 10 24; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        nextBtn.setStyle(
            "-fx-background-color: " + ThemeProvider.get("neutral-gray") + "; -fx-text-fill: " + ThemeProvider.get("text-on-primary") + "; -fx-font-weight: bold; " +
            "-fx-font-size: 14px; -fx-padding: 10 24; -fx-background-radius: 8; -fx-cursor: hand;"
        );
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
        studyIndex++;
        if (studyIndex >= studyCards.size()) {
            showStats();
            return;
        }
        currentCard = studyCards.get(studyIndex);
        showCardFront();
    }

    public void show() {
        stage.show();
    }

    public void setOnAssessment(Consumer<String> cb) { this.onAssessment = cb; }
    public void setOnFinish(Runnable cb) { this.onFinish = cb; }
    public void setOnSessionEnd(Runnable cb) { this.onSessionEnd = cb; }
}
