package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Card;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
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

    private final VBox cardArea;
    private final Label frontLabel;
    private final Label backLabel;
    private final Button flipBtn;
    private final Button nextBtn;
    private final HBox assessmentBox;

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

        BorderPane root = new BorderPane();
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

        // ── CENTER: Card ───────────────────────────────────────────────────
        cardArea = new VBox(20);
        cardArea.setAlignment(Pos.CENTER);

        frontLabel = new Label();
        frontLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #0D47A1; -fx-font-weight: bold;");
        frontLabel.setWrapText(true);
        frontLabel.setAlignment(Pos.CENTER);
        frontLabel.setMaxWidth(600);

        backLabel = new Label();
        backLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #1B5E20; -fx-font-weight: bold;");
        backLabel.setWrapText(true);
        backLabel.setAlignment(Pos.CENTER);
        backLabel.setMaxWidth(600);

        cardArea.getChildren().add(frontLabel);
        root.setCenter(cardArea);

        // ── BOTTOM: Buttons ────────────────────────────────────────────────
        VBox bottomArea = new VBox(12);
        bottomArea.setAlignment(Pos.CENTER);

        // Assessment buttons (hidden until flipped)
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

        // Navigation: flip (left) + next (right)
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

    private void showCardFront() {
        if (currentCard == null) {
            frontLabel.setText("No cards to study.");
            flipBtn.setVisible(false);
            flipBtn.setManaged(false);
            nextBtn.setVisible(false);
            nextBtn.setManaged(false);
            assessmentBox.setVisible(false);
            assessmentBox.setManaged(false);
            return;
        }
        cardArea.getChildren().clear();
        frontLabel.setText(currentCard.getQuestion());
        cardArea.getChildren().add(frontLabel);

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
        cardArea.getChildren().clear();
        frontLabel.setText(currentCard.getQuestion());
        backLabel.setText(currentCard.getAnswer());
        cardArea.getChildren().addAll(frontLabel, backLabel);

        flipBtn.setVisible(false);
        flipBtn.setManaged(false);
        nextBtn.setVisible(true);
        nextBtn.setManaged(true);
        assessmentBox.setVisible(true);
        assessmentBox.setManaged(true);
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
