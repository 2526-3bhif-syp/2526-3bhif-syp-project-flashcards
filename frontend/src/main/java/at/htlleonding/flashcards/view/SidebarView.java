package at.htlleonding.flashcards.view;

import javafx.animation.FillTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.ParallelTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import at.htlleonding.flashcards.model.ThemeProvider;
import at.htlleonding.flashcards.model.TranslationProvider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.function.Consumer;

public class SidebarView extends VBox {
    private Consumer<String> navigationHandler;
    private Button backBtn;
    private Button forwardBtn;

    public SidebarView() {
        this.setPrefWidth(85); 
        this.setSpacing(10); 
        this.setPadding(new Insets(15, 0, 15, 0)); 
        this.setAlignment(Pos.TOP_CENTER);

        HBox arrowBox = new HBox(5);
        arrowBox.setAlignment(Pos.CENTER);
        arrowBox.setPadding(new Insets(0, 0, 15, 0)); 
        
        backBtn = createArrowButton("Back", "M15.41 7.41L14 6l-6 6 6 6 1.41-1.41L10.83 12z");
        forwardBtn = createArrowButton("Forward", "M10 6L8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6z");
        
        arrowBox.getChildren().addAll(backBtn, forwardBtn);
        this.getChildren().add(arrowBox);

        addNavigationItem("Home", "M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z");
        addNavigationItem("Decks", "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z");
        addNavigationItem("Statistic", "M5 9.2h3V19H5V9.2zM10.6 5h2.8v14h-2.8V5zm5.6 8H19v6h-2.8v-6z");
        
        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        this.getChildren().add(spacer);
        
        addNavigationItem("Settings", loadSvgPath("/at/htlleonding/flashcards/icons/settings-gear.txt"));
    }

    private static String loadSvgPath(String resourcePath) {
        try (InputStream is = SidebarView.class.getResourceAsStream(resourcePath)) {
            if (is == null) return "";
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                return reader.lines().collect(Collectors.joining("\n")).trim();
            }
        } catch (IOException e) {
            return "";
        }
    }

    public void applyTheme() {
        this.setStyle("-fx-background-color: " + ThemeProvider.get("bg-card") + ";");
    }

    public void setOnNavigationAction(Consumer<String> handler) {
        this.navigationHandler = handler;
    }

    public void setBackEnabled(boolean enabled) {
        backBtn.setDisable(!enabled);
        updateButtonStyle(backBtn, enabled);
    }

    public void setForwardEnabled(boolean enabled) {
        forwardBtn.setDisable(!enabled);
        updateButtonStyle(forwardBtn, enabled);
    }

    private void updateButtonStyle(Button btn, boolean enabled) {
        SVGPath icon = (SVGPath) btn.getGraphic();
        if (enabled) {
            icon.setFill(Color.web("#888888"));
            btn.setOpacity(1.0);
        } else {
            icon.setFill(Color.web("#cccccc"));
            btn.setOpacity(0.4);
        }
    }

    private Button createArrowButton(String text, String svgPath) {
        Button btn = new Button();
        btn.setPrefSize(30, 30);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        SVGPath icon = new SVGPath();
        icon.setContent(svgPath);
        icon.setFill(Color.web("#888888"));
        
        btn.setGraphic(icon);

        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(TranslationProvider.createStringBinding("sidebar." + text.toLowerCase()));
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setStyle("-fx-font-size: 11px; -fx-background-color: #333333; -fx-text-fill: white; -fx-padding: 3 7 3 7; -fx-background-radius: 4;");

        FillTransition fillIcon = new FillTransition(Duration.millis(200), icon);
        ScaleTransition scaleIcon = new ScaleTransition(Duration.millis(200), icon);
        
        btn.setOnAction(e -> {
            if (navigationHandler != null && !btn.isDisable()) {
                navigationHandler.accept(text);
            }
        });

        btn.setOnMouseEntered(e -> {
            if (!btn.isDisable()) {
                fillIcon.setFromValue((Color) icon.getFill());
                fillIcon.setToValue(Color.web("#2196F3"));
                scaleIcon.setToX(1.2);
                scaleIcon.setToY(1.2);
                new ParallelTransition(fillIcon, scaleIcon).play();
                
                javafx.geometry.Bounds bounds = btn.localToScreen(btn.getBoundsInLocal());
                tooltip.show(btn, bounds.getMaxX() + 5, bounds.getMinY() + (btn.getHeight() / 2) - 12);
            }
        });
        
        btn.setOnMouseExited(e -> {
            // Tooltip immer verstecken, unabhängig vom Disabled-Status
            tooltip.hide();
            
            if (!btn.isDisable()) {
                fillIcon.setFromValue((Color) icon.getFill());
                fillIcon.setToValue(Color.web("#888888"));
                scaleIcon.setToX(1.0);
                scaleIcon.setToY(1.0);
                new ParallelTransition(fillIcon, scaleIcon).play();
            }
        });

        return btn;
    }

    private void addNavigationItem(String text, String svgPath) {
        Button btn = new Button();
        btn.setPrefSize(45, 45);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        SVGPath icon = new SVGPath();
        icon.setContent(svgPath);
        icon.setFill(Color.web("#555555"));
        icon.setScaleX(1.1); 
        icon.setScaleY(1.1);
        
        btn.setGraphic(icon);

        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(TranslationProvider.createStringBinding("sidebar." + text.toLowerCase()));
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setStyle("-fx-font-size: 12px; -fx-background-color: #333333; -fx-text-fill: white; -fx-padding: 5 10 5 10; -fx-background-radius: 5;");

        FillTransition fillIcon = new FillTransition(Duration.millis(200), icon);
        ScaleTransition scaleIcon = new ScaleTransition(Duration.millis(200), icon);
        
        btn.setOnAction(e -> {
            if (navigationHandler != null) {
                navigationHandler.accept(text);
            }
        });

        btn.setOnMouseEntered(e -> {
            fillIcon.setFromValue((Color) icon.getFill());
            fillIcon.setToValue(Color.web("#2196F3"));
            scaleIcon.setToX(1.25);
            scaleIcon.setToY(1.25);
            new ParallelTransition(fillIcon, scaleIcon).play();
            
            javafx.geometry.Bounds bounds = btn.localToScreen(btn.getBoundsInLocal());
            tooltip.show(btn, bounds.getMaxX() + 10, bounds.getMinY() + (btn.getHeight() / 2) - 15);
        });
        
        btn.setOnMouseExited(e -> {
            fillIcon.setFromValue((Color) icon.getFill());
            fillIcon.setToValue(Color.web("#555555"));
            scaleIcon.setToX(1.1);
            scaleIcon.setToY(1.1);
            new ParallelTransition(fillIcon, scaleIcon).play();
            tooltip.hide();
        });

        this.getChildren().add(btn);
    }
}