package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Deck;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class CreateDeckDialog {
    private final Stage stage;
    private final TextField nameField;
    private final TextArea descriptionArea;
    private final Button saveButton;
    private String selectedIconId = "default";
    private DeckResult result;

    private static final java.util.Map<String, String> ICONS = java.util.LinkedHashMap.newLinkedHashMap(8);
    static {
        ICONS.put("default", "🗂");
        ICONS.put("math", "±");
        ICONS.put("science", "\uD83D\uDDFA");
        ICONS.put("history", "📜");
        ICONS.put("language", "🗣");
        ICONS.put("code", "💻");
        ICONS.put("art", "🎨");
        ICONS.put("music", "🎵");
    }

    public static record DeckResult(String name, String description, String iconId) {}

    public CreateDeckDialog(Stage owner) {
        this(owner, null);
    }

    public CreateDeckDialog(Stage owner, Deck deckToEdit) {
        stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        
        if (deckToEdit == null) {
            stage.setTitle("Create New Deck");
        } else {
            stage.setTitle("Edit Deck");
            selectedIconId = deckToEdit.getIconId();
        }

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setPrefWidth(350);

        Label nameLabel = new Label("Deck Name:");
        nameLabel.setStyle("-fx-font-weight: bold;");
        nameField = new TextField();
        nameField.setPromptText("e.g. Mathematics, History...");

        Label descLabel = new Label("Description:");
        descLabel.setStyle("-fx-font-weight: bold;");
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("What is this deck about?");
        descriptionArea.setPrefRowCount(2);
        descriptionArea.setWrapText(true);

        Label iconLabel = new Label("Choose Icon:");
        iconLabel.setStyle("-fx-font-weight: bold;");
        
        FlowPane iconPicker = new FlowPane(10, 10);
        iconPicker.setAlignment(Pos.CENTER_LEFT);
        
        ICONS.forEach((id, glyph) -> {
            Button iconBtn = new Button(glyph);
            iconBtn.setPrefSize(40, 40);
            updateIconButtonStyle(iconBtn, id.equals(selectedIconId));
            
            iconBtn.setOnAction(e -> {
                selectedIconId = id;
                iconPicker.getChildren().forEach(node -> {
                    if (node instanceof Button b) {
                        updateIconButtonStyle(b, glyph.equals(b.getText()));
                    }
                });
            });
            iconPicker.getChildren().add(iconBtn);
        });

        if (deckToEdit != null) {
            nameField.setText(deckToEdit.getName());
            descriptionArea.setText(deckToEdit.getDescription());
        }

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        saveButton = new Button(deckToEdit == null ? "Create Deck" : "Save Changes");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        
        if (deckToEdit == null) {
            saveButton.setDisable(true);
        }
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> stage.close());

        buttonBox.getChildren().addAll(cancelButton, saveButton);

        // Validation
        nameField.textProperty().addListener((obs, oldVal, newVal) -> validate());

        saveButton.setOnAction(e -> {
            result = new DeckResult(nameField.getText().trim(), descriptionArea.getText().trim(), selectedIconId);
            stage.close();
        });

        root.getChildren().addAll(nameLabel, nameField, descLabel, descriptionArea, iconLabel, iconPicker, buttonBox);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    private void updateIconButtonStyle(Button btn, boolean selected) {
        if (selected) {
            btn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 0;");
        } else {
            btn.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #cccccc; -fx-text-fill: black; -fx-font-size: 18px; -fx-padding: 0;");
        }
    }

    private void validate() {
        saveButton.setDisable(nameField.getText().trim().isEmpty());
    }

    public Optional<DeckResult> showAndWait() {
        stage.showAndWait();
        return Optional.ofNullable(result);
    }
}
