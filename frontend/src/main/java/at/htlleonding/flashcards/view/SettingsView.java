package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.TranslationProvider;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Locale;

public class SettingsView extends VBox {

    public SettingsView() {
        this.setPadding(new Insets(30));
        this.setSpacing(24);
        this.setAlignment(Pos.TOP_LEFT);

        // ── Title ──────────────────────────────────────────────────────────
        Label title = new Label();
        title.textProperty().bind(TranslationProvider.createStringBinding("settings.title"));
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        // ── Language Section ───────────────────────────────────────────────
        Label languageLabel = new Label();
        languageLabel.textProperty().bind(TranslationProvider.createStringBinding("settings.language_label"));
        languageLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555555;");

        ComboBox<String> languageDropdown = new ComboBox<>();
        languageDropdown.getItems().addAll("Deutsch", "English");
        languageDropdown.setStyle(
            "-fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 8; " +
            "-fx-border-radius: 8; -fx-border-color: #cccccc; -fx-cursor: hand;"
        );
        languageDropdown.setPrefWidth(200);

        // Set initial selection based on current locale
        Locale current = TranslationProvider.getLocale();
        if ("de".equals(current.getLanguage())) {
            languageDropdown.getSelectionModel().select("Deutsch");
        } else {
            languageDropdown.getSelectionModel().select("English");
        }

        // Wire dropdown to TranslationProvider
        languageDropdown.setOnAction(e -> {
            String selected = languageDropdown.getSelectionModel().getSelectedItem();
            if ("Deutsch".equals(selected)) {
                TranslationProvider.setLocale(Locale.GERMAN);
            } else {
                TranslationProvider.setLocale(Locale.ENGLISH);
            }
        });

        HBox languageRow = new HBox(16, languageLabel, languageDropdown);
        languageRow.setAlignment(Pos.CENTER_LEFT);

        VBox languageSection = new VBox(12, languageRow);
        languageSection.setPadding(new Insets(16));
        languageSection.setStyle(
            "-fx-background-color: #f5f5f5; -fx-border-color: #e0e0e0; " +
            "-fx-border-radius: 12; -fx-background-radius: 12;"
        );

        this.getChildren().addAll(title, languageSection);
    }
}