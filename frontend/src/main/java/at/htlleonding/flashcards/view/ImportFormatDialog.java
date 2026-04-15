package at.htlleonding.flashcards.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class ImportFormatDialog {
    private final Stage stage;
    private String selectedFormat = null;

    public ImportFormatDialog(Stage owner, String actionType) {
        stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(actionType + " Format");

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setPrefWidth(300);
        root.setStyle("-fx-background-color: #ffffff; -fx-border-color: #eeeeee; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label label = new Label("Select " + actionType + " Format:");
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        HBox formatButtons = new HBox(15);
        formatButtons.setAlignment(Pos.CENTER);

        Button jsonBtn = createStyledButton("JSON");
        Button csvBtn = createStyledButton("CSV");

        jsonBtn.setOnAction(e -> {
            selectedFormat = "JSON";
            stage.close();
        });
        csvBtn.setOnAction(e -> {
            selectedFormat = "CSV";
            stage.close();
        });

        formatButtons.getChildren().addAll(jsonBtn, csvBtn);
        root.getChildren().addAll(label, formatButtons);

        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    private Button createStyledButton(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(100, 40);
        btn.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8; -fx-cursor: hand; -fx-font-size: 14px; -fx-text-fill: #333333;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #aaaaaa; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8; -fx-cursor: hand; -fx-font-size: 14px; -fx-text-fill: #000000;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8; -fx-cursor: hand; -fx-font-size: 14px; -fx-text-fill: #333333;"));
        return btn;
    }

    public Optional<String> showAndWait() {
        stage.showAndWait();
        return Optional.ofNullable(selectedFormat);
    }
}
