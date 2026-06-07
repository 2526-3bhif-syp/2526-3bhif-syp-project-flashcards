package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.ThemeProvider;
import at.htlleonding.flashcards.model.TranslationProvider;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class StatisticView extends StackPane {
    public StatisticView() {
        Label label = new Label();
        label.textProperty().bind(TranslationProvider.createStringBinding("statistic.title"));
        this.getChildren().add(label);
        StackPane.setAlignment(label, Pos.CENTER);
        applyTheme();
    }

    public void applyTheme() {
        Label label = (Label) this.getChildren().get(0);
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: " + ThemeProvider.get("text-secondary") + ";");
    }
}