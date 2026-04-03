package at.htlleonding.flashcards.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class FlashcardsView extends StackPane {
    public FlashcardsView() {
        Label label = new Label("Flashcards View");
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: #555555;");
        this.getChildren().add(label);
        StackPane.setAlignment(label, Pos.CENTER);
    }
}