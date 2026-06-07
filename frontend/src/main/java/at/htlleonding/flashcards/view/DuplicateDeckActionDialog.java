package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.TranslationProvider;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class DuplicateDeckActionDialog {
    public enum Action { ALLOW_ALL, REPLACE, SKIP, CANCEL }

    private final Stage stage;
    private Action selectedAction = Action.CANCEL;

    public DuplicateDeckActionDialog(Stage owner) {
        stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(TranslationProvider.get("duplicate.import_duplicates_title"));

        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setPrefWidth(350);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #ffffff; -fx-border-color: #eeeeee; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label headerLabel = new Label(TranslationProvider.get("duplicate.deck_header"));
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        Label subLabel = new Label(TranslationProvider.get("duplicate.deck_sub"));
        subLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;");
        subLabel.setWrapText(true);

        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button allowAllBtn = createStyledButton(TranslationProvider.get("duplicate.allow_all"), "#2196F3", "white");
        Button replaceBtn  = createStyledButton(TranslationProvider.get("duplicate.replace_decks"), "#FF9800", "white");
        Button skipBtn     = createStyledButton(TranslationProvider.get("duplicate.skip_duplicates"),         "white",   "#555555");
        Button cancelBtn   = createStyledButton(TranslationProvider.get("duplicate.cancel_import"),           "#f8f9fa", "#666666");

        allowAllBtn.setOnAction(e -> { selectedAction = Action.ALLOW_ALL; stage.close(); });
        replaceBtn .setOnAction(e -> { selectedAction = Action.REPLACE;   stage.close(); });
        skipBtn    .setOnAction(e -> { selectedAction = Action.SKIP;      stage.close(); });
        cancelBtn  .setOnAction(e -> { selectedAction = Action.CANCEL;    stage.close(); });

        buttonBox.getChildren().addAll(allowAllBtn, replaceBtn, skipBtn, cancelBtn);
        root.getChildren().addAll(headerLabel, subLabel, buttonBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    private Button createStyledButton(String text, String bgColor, String textColor) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(40);

        String baseStyle = String.format("-fx-background-color: %s; -fx-border-color: #cccccc; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8; -fx-cursor: hand; -fx-font-size: 14px; -fx-text-fill: %s; -fx-font-weight: bold;", bgColor, textColor);
        btn.setStyle(baseStyle);

        btn.setOnMouseEntered(e -> btn.setStyle(baseStyle + " -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 1); -fx-opacity: 0.9;"));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));

        return btn;
    }

    public Optional<Action> showAndWait() {
        stage.showAndWait();
        return Optional.ofNullable(selectedAction);
    }
}
