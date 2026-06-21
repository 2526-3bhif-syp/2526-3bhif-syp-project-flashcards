package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Deck;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import at.htlleonding.flashcards.model.ThemeProvider;
import at.htlleonding.flashcards.model.TranslationProvider;
import java.util.Optional;

public class CreateDeckDialog {
    private final Stage stage;
    private final TextField nameField;
    private final TextArea descriptionArea;
    private final Button saveButton;
    private String selectedIconId = "default";
    private DeckResult result;
    private Button selectedButton;

    public static record DeckResult(String name, String description, String iconId) {}

    public CreateDeckDialog(Stage owner) {
        this(owner, null);
    }

    public CreateDeckDialog(Stage owner, Deck deckToEdit) {
        stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        
        if (deckToEdit == null) {
            stage.setTitle(TranslationProvider.get("deck.create_title"));
        } else {
            stage.setTitle(TranslationProvider.get("deck.edit_title"));
            selectedIconId = deckToEdit.getIconId();
        }

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setPrefWidth(550);
        root.setStyle("-fx-background-color: " + ThemeProvider.get("bg-primary") + "; -fx-border-color: " + ThemeProvider.get("border-muted") + "; -fx-border-width: 1.5;");

        String inputBg = ThemeProvider.getTheme().equals("dark") ? ThemeProvider.get("bg-card") : "#ffffff";
        String inputText = ThemeProvider.getTheme().equals("dark") ? ThemeProvider.get("text-primary") : "#333333";
        String inputPrompt = ThemeProvider.getTheme().equals("dark") ? ThemeProvider.get("text-placeholder") : "#888888";

        Label nameLabel = new Label(TranslationProvider.get("deck.name_label"));
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-primary") + ";");
        nameField = new TextField();
        nameField.setPromptText(TranslationProvider.get("deck.name_prompt"));
        setupInputField(nameField, inputBg, inputText, inputPrompt);

        Label descLabel = new Label(TranslationProvider.get("deck.description_label"));
        descLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-primary") + ";");
        descriptionArea = new TextArea();
        descriptionArea.setPromptText(TranslationProvider.get("deck.description_prompt"));
        descriptionArea.setPrefRowCount(2);
        descriptionArea.setWrapText(true);
        setupInputField(descriptionArea, inputBg, inputText, inputPrompt);

        Label iconLabel = new Label(TranslationProvider.get("deck.icon_label"));
        iconLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-primary") + ";");
        
        FlowPane iconPicker = new FlowPane(10, 10);
        iconPicker.setAlignment(Pos.CENTER_LEFT);
        
        // Create icon buttons with proper selection tracking
        for (String iconId : IconManager.getAvailableIconIds()) {
            Image iconImage = IconManager.getIcon(iconId);
            ImageView iconView = new ImageView(iconImage);
            iconView.setFitWidth(64);
            iconView.setFitHeight(64);
            iconView.setPreserveRatio(true);
            iconView.setSmooth(true);
            
            Button iconBtn = new Button();
            iconBtn.setGraphic(iconView);
            iconBtn.setPrefSize(74, 74);
            iconBtn.setUserData(iconId); // Store the icon ID as user data
            
            boolean isSelected = iconId.equals(selectedIconId);
            updateIconButtonStyle(iconBtn, isSelected);
            
            if (isSelected) {
                selectedButton = iconBtn;
            }
            
            iconBtn.setOnAction(e -> {
                selectedIconId = iconId;
                
                // Deselect previous button
                if (selectedButton != null) {
                    updateIconButtonStyle(selectedButton, false);
                }
                
                // Select new button
                selectedButton = iconBtn;
                updateIconButtonStyle(selectedButton, true);
            });
            
            iconPicker.getChildren().add(iconBtn);
        }

        if (deckToEdit != null) {
            nameField.setText(deckToEdit.getName());
            descriptionArea.setText(deckToEdit.getDescription());
        }

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        saveButton = new Button(deckToEdit == null ? TranslationProvider.get("deck.create_btn") : TranslationProvider.get("deck.save_btn"));
        saveButton.setStyle("-fx-background-color: " + ThemeProvider.get("accent-green") + "; -fx-text-fill: " + ThemeProvider.get("text-on-primary") + "; -fx-font-weight: bold;");
        saveButton.setDefaultButton(true);
        
        if (deckToEdit == null) {
            saveButton.setDisable(true);
        }
        
        Button cancelButton = new Button(TranslationProvider.get("deck.cancel_btn"));
        cancelButton.setStyle("-fx-background-color: " + ThemeProvider.get("bg-card") + "; -fx-text-fill: " + ThemeProvider.get("text-secondary") + "; -fx-border-color: " + ThemeProvider.get("border-muted") + "; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-weight: bold;");
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: " + ThemeProvider.get("bg-hover") + "; -fx-text-fill: " + ThemeProvider.get("text-primary") + "; -fx-border-color: " + ThemeProvider.get("border-muted") + "; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-weight: bold;"));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: " + ThemeProvider.get("bg-card") + "; -fx-text-fill: " + ThemeProvider.get("text-secondary") + "; -fx-border-color: " + ThemeProvider.get("border-muted") + "; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5; -fx-font-weight: bold;"));
        cancelButton.setOnAction(e -> stage.close());

        buttonBox.getChildren().addAll(cancelButton, saveButton);

        // Validation
        nameField.textProperty().addListener((obs, oldVal, newVal) -> validate());

        saveButton.setOnAction(e -> {
            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            String desc = descriptionArea.getText() == null ? "" : descriptionArea.getText().trim();
            result = new DeckResult(name, desc, selectedIconId);
            stage.close();
        });

        root.getChildren().addAll(nameLabel, nameField, descLabel, descriptionArea, iconLabel, iconPicker, buttonBox);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    private void updateIconButtonStyle(Button btn, boolean selected) {
        if (selected) {
            btn.setStyle("-fx-background-color: " + ThemeProvider.get("accent-link") + "; -fx-border-color: " + ThemeProvider.get("accent-link") + "; -fx-border-radius: 5; -fx-padding: 0;");
        } else {
            btn.setStyle("-fx-background-color: " + ThemeProvider.get("bg-primary") + "; -fx-border-color: " + ThemeProvider.get("border-default") + "; -fx-border-radius: 5; -fx-padding: 0;");
        }
    }

    private void validate() {
        String name = nameField.getText();
        saveButton.setDisable(name == null || name.trim().isEmpty());
    }

    public Optional<DeckResult> showAndWait() {
        stage.showAndWait();
        return Optional.ofNullable(result);
    }

    private void setupInputField(javafx.scene.control.TextInputControl input, String inputBg, String inputText, String inputPrompt) {
        String baseStyle = String.format(
            "-fx-control-inner-background: %s; " +
            "-fx-background-color: %s; " +
            "-fx-text-fill: %s; " +
            "-fx-text-inner-color: %s; " +
            "-fx-prompt-text-fill: %s; " +
            "-fx-border-color: %s; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 5; " +
            "-fx-background-radius: 5; " +
            "-fx-focus-color: transparent; " +
            "-fx-faint-focus-color: transparent;",
            inputBg, inputBg, inputText, inputText, inputPrompt, ThemeProvider.get("border-muted")
        );
        input.setStyle(baseStyle);

        input.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                input.setStyle(baseStyle + " -fx-border-color: " + ThemeProvider.get("accent-blue") + ";");
            } else {
                input.setStyle(baseStyle);
            }
        });
    }
}
