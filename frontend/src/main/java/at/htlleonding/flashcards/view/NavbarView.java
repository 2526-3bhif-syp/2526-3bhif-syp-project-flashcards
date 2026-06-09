package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.ThemeProvider;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import at.htlleonding.flashcards.model.TranslationProvider;
import java.util.function.Consumer;

public class NavbarView extends HBox {

    private final TextField searchField;

    public NavbarView() {
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(10, 20, 10, 20));
        this.setPrefHeight(60);

        HBox leftSection = new HBox(10);
        leftSection.setAlignment(Pos.CENTER_LEFT);
        
        StackPane searchContainer = new StackPane();
        searchContainer.setMaxWidth(400);
        HBox.setHgrow(searchContainer, Priority.ALWAYS);

        searchField = new TextField();
        searchField.promptTextProperty().bind(TranslationProvider.createStringBinding("navbar.search_prompt"));
        searchField.setStyle(
            "-fx-background-radius: 20; " +
            "-fx-border-radius: 20; " +
            "-fx-border-color: " + ThemeProvider.get("border-default") + "; " +
            "-fx-background-color: transparent; " +
            "-fx-padding: 5 10 5 30;"
        );

        SVGPath searchIcon = new SVGPath();
        searchIcon.setContent("M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z");
        searchIcon.setFill(Color.GRAY);
        searchIcon.setScaleX(0.8);
        searchIcon.setScaleY(0.8);

        StackPane.setAlignment(searchIcon, Pos.CENTER_LEFT);
        StackPane.setMargin(searchIcon, new Insets(0, 0, 0, 10));

        searchContainer.getChildren().addAll(searchField, searchIcon);

        HBox rightSection = new HBox();
        rightSection.setAlignment(Pos.CENTER_RIGHT);

        HBox.setHgrow(leftSection, Priority.ALWAYS);
        HBox.setHgrow(rightSection, Priority.ALWAYS);

        this.getChildren().addAll(leftSection, searchContainer, rightSection);
        applyTheme();
    }

    public void applyTheme() {
        this.setStyle("-fx-background-color: " + ThemeProvider.get("bg-card") + ";");
        searchField.setStyle(
            "-fx-background-radius: 20; " +
            "-fx-border-radius: 20; " +
            "-fx-border-color: " + ThemeProvider.get("border-default") + "; " +
            "-fx-background-color: transparent; " +
            "-fx-padding: 5 10 5 30;"
        );
    }

    public void setOnSearchTextChanged(Consumer<String> listener) {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (listener != null) {
                listener.accept(newVal);
            }
        });
    }

    public void clearSearch() {
        searchField.clear();
    }
}