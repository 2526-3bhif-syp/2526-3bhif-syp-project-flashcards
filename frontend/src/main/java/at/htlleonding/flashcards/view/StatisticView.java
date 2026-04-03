package at.htlleonding.flashcards.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class StatisticView extends StackPane {
    public StatisticView() {
        Label label = new Label("Statistic View");
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: #555555;");
        this.getChildren().add(label);
        StackPane.setAlignment(label, Pos.CENTER);
    }
}