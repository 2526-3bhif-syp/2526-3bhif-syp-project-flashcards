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
import java.util.function.Consumer;

public class SidebarView extends VBox {
    private Consumer<String> navigationHandler;

    public SidebarView() {
        this.setPrefWidth(85); 
        this.setSpacing(10); 
        this.setPadding(new Insets(15, 0, 15, 0)); 
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: white;");

        HBox arrowBox = new HBox(5);
        arrowBox.setAlignment(Pos.CENTER);
        arrowBox.setPadding(new Insets(0, 0, 15, 0)); 
        
        addArrowItem(arrowBox, "Back", "M15.41 7.41L14 6l-6 6 6 6 1.41-1.41L10.83 12z");
        addArrowItem(arrowBox, "Forward", "M10 6L8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6z");
        
        this.getChildren().add(arrowBox);

        addNavigationItem("Home", "M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z");
        addNavigationItem("Flashcards", "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z");
        addNavigationItem("Statistic", "M5 9.2h3V19H5V9.2zM10.6 5h2.8v14h-2.8V5zm5.6 8H19v6h-2.8v-6z");
        
        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        this.getChildren().add(spacer);
        
        addNavigationItem("Settings", "M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L3.82 7.87c-.11.2-.06.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.5c-1.93 0-3.5-1.57-3.5-3.5s1.57-3.5 3.5-3.5 3.5 1.57 3.5 3.5-1.57 3.5-3.5 3.5z");
    }

    public void setOnNavigationAction(Consumer<String> handler) {
        this.navigationHandler = handler;
    }

    private void addArrowItem(HBox container, String text, String svgPath) {
        Button btn = new Button();
        btn.setPrefSize(30, 30);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        SVGPath icon = new SVGPath();
        icon.setContent(svgPath);
        icon.setFill(Color.web("#888888"));
        
        btn.setGraphic(icon);

        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setStyle("-fx-font-size: 11px; -fx-background-color: #333333; -fx-text-fill: white; -fx-padding: 3 7 3 7; -fx-background-radius: 4;");

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
            scaleIcon.setToX(1.2);
            scaleIcon.setToY(1.2);
            new ParallelTransition(fillIcon, scaleIcon).play();
            
            javafx.geometry.Bounds bounds = btn.localToScreen(btn.getBoundsInLocal());
            tooltip.show(btn, bounds.getMaxX() + 5, bounds.getMinY() + (btn.getHeight() / 2) - 12);
        });
        
        btn.setOnMouseExited(e -> {
            fillIcon.setFromValue((Color) icon.getFill());
            fillIcon.setToValue(Color.web("#888888"));
            scaleIcon.setToX(1.0);
            scaleIcon.setToY(1.0);
            new ParallelTransition(fillIcon, scaleIcon).play();
            tooltip.hide();
        });

        container.getChildren().add(btn);
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

        Tooltip tooltip = new Tooltip(text);
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