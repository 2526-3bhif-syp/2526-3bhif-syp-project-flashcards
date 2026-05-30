package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Card;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.function.Consumer;

public class StudyModeView extends VBox {

    private Card currentCard;

    // ── ui references ──────────────────────────────────────────────────────
    private Label deckLabel;
    private VBox cardArea;
    private Button revealBtn;
    private HBox ratingBar;

    // ── callbacks ──────────────────────────────────────────────────────────
    private Consumer<String> onRatingSelected;
    private Runnable onStopRequested;

    public StudyModeView() {
        this.setPadding(new Insets(24));
        this.setSpacing(20);
        this.setAlignment(Pos.TOP_CENTER);

        HBox header = buildHeader();

        cardArea = new VBox(16);
        cardArea.setAlignment(Pos.CENTER);
        VBox.setVgrow(cardArea, Priority.ALWAYS);

        revealBtn = createBtn("Aufdecken", "#2196F3", "#1565C0");
        revealBtn.setPrefWidth(180);
        revealBtn.setOnAction(e -> reveal());

        ratingBar = buildRatingBar();
        ratingBar.setVisible(false);
        ratingBar.setManaged(false);

        VBox bottom = new VBox(12, revealBtn, ratingBar);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(8, 0, 0, 0));

        this.getChildren().addAll(header, cardArea, bottom);
    }

    // ── layout builders ────────────────────────────────────────────────────

    private HBox buildHeader() {
        deckLabel = new Label("");
        deckLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button stopBtn = createSubtleBtn("Beenden", "#dc3545");
        stopBtn.setOnAction(e -> { if (onStopRequested != null) onStopRequested.run(); });

        HBox header = new HBox(12, deckLabel, spacer, stopBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private HBox buildRatingBar() {
        Button falschBtn  = createRatingBtn("Falsch",    "#dc3545", "#b02a37");
        Button schwierigBtn = createRatingBtn("Schwierig", "#FF9800", "#e65100");
        Button okBtn      = createRatingBtn("Ok",        "#2196F3", "#1565C0");
        Button leichtBtn  = createRatingBtn("Leicht",    "#4CAF50", "#2E7D32");

        falschBtn.setOnAction(e   -> rate("FALSCH"));
        schwierigBtn.setOnAction(e -> rate("SCHWIERIG"));
        okBtn.setOnAction(e       -> rate("OK"));
        leichtBtn.setOnAction(e   -> rate("LEICHT"));

        HBox bar = new HBox(12, falschBtn, schwierigBtn, okBtn, leichtBtn);
        bar.setAlignment(Pos.CENTER);
        return bar;
    }

    // ── public API ─────────────────────────────────────────────────────────

    public void setDeckName(String name) {
        deckLabel.setText(name != null ? name : "");
    }

    public void showCard(Card card) {
        this.currentCard = card;

        revealBtn.setVisible(true);
        revealBtn.setManaged(true);
        ratingBar.setVisible(false);
        ratingBar.setManaged(false);

        cardArea.getChildren().clear();
        cardArea.getChildren().add(buildSide(
            "FRAGE", card.getQuestion(),
            card.getFrontImageData(), card.getFrontImageName(),
            "#E3F2FD", "#90CAF9", "#0D47A1"
        ));
    }

    // ── private helpers ────────────────────────────────────────────────────

    private void reveal() {
        if (currentCard == null) return;

        revealBtn.setVisible(false);
        revealBtn.setManaged(false);
        ratingBar.setVisible(true);
        ratingBar.setManaged(true);

        cardArea.getChildren().clear();
        cardArea.getChildren().add(buildSide(
            "FRAGE", currentCard.getQuestion(),
            currentCard.getFrontImageData(), currentCard.getFrontImageName(),
            "#E3F2FD", "#90CAF9", "#0D47A1"
        ));
        cardArea.getChildren().add(buildSide(
            "ANTWORT", currentCard.getAnswer(),
            currentCard.getBackImageData(), currentCard.getBackImageName(),
            "#E8F5E9", "#A5D6A7", "#1B5E20"
        ));
    }

    private VBox buildSide(String header, String text, String imageData, String imageName,
                           String bg, String border, String textColor) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(16));
        box.setStyle(String.format(
            "-fx-background-color: %s; -fx-border-color: %s; " +
            "-fx-border-radius: 12; -fx-background-radius: 12;",
            bg, border));
        box.setMaxWidth(520);
        box.setMinWidth(320);

        Label headerLabel = new Label(header);
        headerLabel.setStyle(String.format(
            "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: %s;", textColor));

        Label textLabel = new Label(text != null ? text : "");
        textLabel.setWrapText(true);
        textLabel.setStyle(String.format("-fx-font-size: 18px; -fx-text-fill: %s;", textColor));
        textLabel.setTextAlignment(TextAlignment.CENTER);
        textLabel.setAlignment(Pos.CENTER);
        textLabel.setMaxWidth(Double.MAX_VALUE);

        box.getChildren().addAll(headerLabel, textLabel);

        if (imageData != null) {
            byte[] bytes = Base64.getDecoder().decode(imageData);
            ImageView iv = new ImageView(new Image(new ByteArrayInputStream(bytes)));
            iv.setFitWidth(220);
            iv.setFitHeight(160);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            Label imgNameLabel = new Label("🖼 " + (imageName != null ? imageName : ""));
            imgNameLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");
            box.getChildren().addAll(iv, imgNameLabel);
        }

        return box;
    }

    private void rate(String rating) {
        if (onRatingSelected != null) onRatingSelected.accept(rating);
    }

    private Button createBtn(String text, String color, String hoverColor) {
        Button btn = new Button(text);
        String s1 = String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 14px; " +
            "-fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 8; -fx-cursor: hand;", color);
        String s2 = String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 14px; " +
            "-fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 8; -fx-cursor: hand;", hoverColor);
        btn.setStyle(s1);
        btn.setOnMouseEntered(e -> btn.setStyle(s2));
        btn.setOnMouseExited(e -> btn.setStyle(s1));
        return btn;
    }

    private Button createSubtleBtn(String text, String accentColor) {
        Button btn = new Button(text);
        String s1 = String.format(
            "-fx-background-color: white; -fx-border-color: %s; -fx-border-width: 1.5; " +
            "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16; " +
            "-fx-cursor: hand; -fx-font-size: 13px; -fx-text-fill: #000000; -fx-font-weight: bold;", accentColor);
        String s2 = String.format(
            "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 1.5; " +
            "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16; " +
            "-fx-cursor: hand; -fx-font-size: 13px; -fx-text-fill: white; -fx-font-weight: bold;", accentColor, accentColor);
        btn.setStyle(s1);
        btn.setOnMouseEntered(e -> btn.setStyle(s2));
        btn.setOnMouseExited(e -> btn.setStyle(s1));
        return btn;
    }

    private Button createRatingBtn(String text, String color, String hoverColor) {
        Button btn = new Button(text);
        String s1 = String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 13px; " +
            "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;", color);
        String s2 = String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 13px; " +
            "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;", hoverColor);
        btn.setStyle(s1);
        btn.setOnMouseEntered(e -> btn.setStyle(s2));
        btn.setOnMouseExited(e -> btn.setStyle(s1));
        return btn;
    }

    // ── callback setters ───────────────────────────────────────────────────

    public void setOnRatingSelected(Consumer<String> cb) { this.onRatingSelected = cb; }
    public void setOnStopRequested(Runnable cb) { this.onStopRequested = cb; }
}
