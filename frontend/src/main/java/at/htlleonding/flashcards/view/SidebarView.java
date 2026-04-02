package at.htlleonding.flashcards.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;

public class SidebarView extends VBox {

    public SidebarView() {
        this.setPrefWidth(250);
        this.setSpacing(5);
        this.setPadding(new Insets(20, 10, 20, 10));
        this.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 1 0 0;");

        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        brandBox.setPadding(new Insets(0, 0, 30, 10));
        
        Label brandLabel = new Label("FLASHCARDS");
        brandLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        brandBox.getChildren().add(brandLabel);
        this.getChildren().add(brandBox);

        addNavigationItem("Home", "M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z");
        addNavigationItem("Library", "M4 6H2v14c0 1.1.9 2 2 2h14v-2H4V6zm16-4H8c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-1 9H9V9h10v2zm-4 4H9v-2h6v2zm4-8H9V5h10v2z");
        addNavigationItem("Flashcards", "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z");
        addNavigationItem("Practice", "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 14.5v-9l6 4.5-6 4.5z");
        
        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        this.getChildren().add(spacer);
        
        addNavigationItem("Settings", "M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L3.82 7.87c-.11.2-.06.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.5c-1.93 0-3.5-1.57-3.5-3.5s1.57-3.5 3.5-3.5 3.5 1.57 3.5 3.5-1.57 3.5-3.5 3.5z");
    }

    private void addNavigationItem(String text, String svgPath) {
        Button btn = new Button();
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(10, 15, 10, 15));
        
        SVGPath icon = new SVGPath();
        icon.setContent(svgPath);
        icon.setFill(Color.web("#666666"));
        icon.setScaleX(0.8);
        icon.setScaleY(0.8);
        
        btn.setGraphic(icon);
        btn.setGraphicTextGap(15);
        btn.setText(text);
        
        btn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #666666; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand;"
        );

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #666666; -fx-font-size: 14px; -fx-cursor: hand;"));

        this.getChildren().add(btn);
    }
}
