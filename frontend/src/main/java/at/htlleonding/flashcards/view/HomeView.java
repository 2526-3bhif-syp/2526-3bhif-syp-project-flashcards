package at.htlleonding.flashcards.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class HomeView extends StackPane {
    public HomeView() {
        Label label = new Label("Home View");
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: #555555;");
        this.getChildren().add(label);
        StackPane.setAlignment(label, Pos.CENTER);
    }
}