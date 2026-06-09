package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.ThemeProvider;
import at.htlleonding.flashcards.model.TranslationProvider;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Locale;

public class SettingsView extends VBox {

    private final Label title;
    private final Label languageLabel;
    private final ComboBox<String> languageDropdown;
    private final VBox languageSection;
    private final Label themeLabel;
    private final ComboBox<String> themeDropdown;
    private final VBox themeSection;

    public SettingsView() {
        this.setPadding(new Insets(30));
        this.setSpacing(24);
        this.setAlignment(Pos.TOP_LEFT);

        // ── Title ──────────────────────────────────────────────────────────
        title = new Label();
        title.textProperty().bind(TranslationProvider.createStringBinding("settings.title"));
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-primary") + ";");

        // ── Language Section ───────────────────────────────────────────────
        languageLabel = new Label();
        languageLabel.textProperty().bind(TranslationProvider.createStringBinding("settings.language_label"));
        languageLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-secondary") + ";");

        languageDropdown = new ComboBox<>();
        languageDropdown.getItems().addAll("Deutsch", "English");
        styleComboBox(languageDropdown);
        languageDropdown.setPrefWidth(200);

        Locale current = TranslationProvider.getLocale();
        if ("de".equals(current.getLanguage())) {
            languageDropdown.setValue("Deutsch");
        } else {
            languageDropdown.setValue("English");
        }

        TranslationProvider.localeProperty().addListener((obs, oldLocale, newLocale) -> {
            if (newLocale != null) {
                String expected = "de".equals(newLocale.getLanguage()) ? "Deutsch" : "English";
                if (!expected.equals(languageDropdown.getValue())) {
                    languageDropdown.setValue(expected);
                }
            }
        });

        languageDropdown.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Locale targetLocale = "Deutsch".equals(newVal) ? Locale.GERMAN : Locale.ENGLISH;
                if (!targetLocale.equals(TranslationProvider.getLocale())) {
                    TranslationProvider.setLocale(targetLocale);
                }
            }
        });

        HBox languageRow = new HBox(16, languageLabel, languageDropdown);
        languageRow.setAlignment(Pos.CENTER_LEFT);

        languageSection = new VBox(12, languageRow);
        languageSection.setPadding(new Insets(16));
        languageSection.setStyle(
            "-fx-background-color: " + ThemeProvider.get("bg-secondary") + "; -fx-border-color: " + ThemeProvider.get("border-light") + "; " +
            "-fx-border-radius: 12; -fx-background-radius: 12;"
        );

        // ── Theme Section ──────────────────────────────────────────────────
        themeLabel = new Label();
        themeLabel.textProperty().bind(TranslationProvider.createStringBinding("settings.theme_label"));
        themeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-secondary") + ";");

        themeDropdown = new ComboBox<>();
        themeDropdown.getItems().addAll(
            TranslationProvider.get("settings.theme_light"),
            TranslationProvider.get("settings.theme_dark")
        );
        styleComboBox(themeDropdown);
        themeDropdown.setPrefWidth(200);

        String currentTheme = ThemeProvider.getTheme();
        themeDropdown.setValue("light".equals(currentTheme)
            ? TranslationProvider.get("settings.theme_light")
            : TranslationProvider.get("settings.theme_dark"));

        ThemeProvider.themeProperty().addListener((obs, oldTheme, newTheme) -> {
            if (newTheme != null) {
                String expected = "light".equals(newTheme)
                    ? TranslationProvider.get("settings.theme_light")
                    : TranslationProvider.get("settings.theme_dark");
                if (!expected.equals(themeDropdown.getValue())) {
                    themeDropdown.setValue(expected);
                }
            }
        });

        themeDropdown.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String targetTheme = newVal.equals(TranslationProvider.get("settings.theme_light")) ? "light" : "dark";
                if (!targetTheme.equals(ThemeProvider.getTheme())) {
                    ThemeProvider.setTheme(targetTheme);
                }
            }
        });

        HBox themeRow = new HBox(16, themeLabel, themeDropdown);
        themeRow.setAlignment(Pos.CENTER_LEFT);

        themeSection = new VBox(12, themeRow);
        themeSection.setPadding(new Insets(16));
        themeSection.setStyle(
            "-fx-background-color: " + ThemeProvider.get("bg-secondary") + "; -fx-border-color: " + ThemeProvider.get("border-light") + "; " +
            "-fx-border-radius: 12; -fx-background-radius: 12;"
        );

        this.getChildren().addAll(title, languageSection, themeSection);

        TranslationProvider.localeProperty().addListener((obs, oldLocale, newLocale) -> {
            themeDropdown.getItems().setAll(
                TranslationProvider.get("settings.theme_light"),
                TranslationProvider.get("settings.theme_dark")
            );
            themeDropdown.setValue("light".equals(ThemeProvider.getTheme())
                ? TranslationProvider.get("settings.theme_light")
                : TranslationProvider.get("settings.theme_dark"));
        });
    }

    public void applyTheme() {
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-primary") + ";");
        languageLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-secondary") + ";");
        styleComboBox(languageDropdown);
        languageSection.setStyle(
            "-fx-background-color: " + ThemeProvider.get("bg-secondary") + "; -fx-border-color: " + ThemeProvider.get("border-light") + "; " +
            "-fx-border-radius: 12; -fx-background-radius: 12;"
        );
        themeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-secondary") + ";");
        styleComboBox(themeDropdown);
        themeSection.setStyle(
            "-fx-background-color: " + ThemeProvider.get("bg-secondary") + "; -fx-border-color: " + ThemeProvider.get("border-light") + "; " +
            "-fx-border-radius: 12; -fx-background-radius: 12;"
        );
    }

    private void styleComboBox(ComboBox<String> comboBox) {
        comboBox.setStyle(
            "-fx-font-size: 14px; -fx-padding: 6 12; -fx-background-radius: 8; " +
            "-fx-border-radius: 8; -fx-border-color: " + ThemeProvider.get("border-default") + "; " +
            "-fx-cursor: hand; " +
            "-fx-text-fill: " + ThemeProvider.get("text-primary") + "; " +
            "-fx-background-color: " + ThemeProvider.get("bg-card") + "; " +
            "-fx-mark-color: " + ThemeProvider.get("text-primary") + ";"
        );
        comboBox.setButtonCell(new StyledListCell());
        comboBox.setCellFactory(cb -> new StyledListCell());
    }

    private static class StyledListCell extends ListCell<String> {
        StyledListCell() {}
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item);
                setStyle(
                    "-fx-text-fill: " + ThemeProvider.get("text-primary") + "; " +
                    "-fx-background-color: " + ThemeProvider.get("bg-card") + "; " +
                    "-fx-font-size: 14px; -fx-padding: 6 12;"
                );
                setOnMouseEntered(e -> {
                    if (!isEmpty()) setStyle(
                        "-fx-text-fill: " + ThemeProvider.get("text-primary") + "; " +
                        "-fx-background-color: " + ThemeProvider.get("bg-hover") + "; " +
                        "-fx-font-size: 14px; -fx-padding: 6 12;"
                    );
                });
                setOnMouseExited(e -> {
                    if (!isEmpty()) setStyle(
                        "-fx-text-fill: " + ThemeProvider.get("text-primary") + "; " +
                        "-fx-background-color: " + ThemeProvider.get("bg-card") + "; " +
                        "-fx-font-size: 14px; -fx-padding: 6 12;"
                    );
                });
            }
        }
    }
}