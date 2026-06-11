package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.Deck;
import at.htlleonding.flashcards.model.ThemeProvider;
import at.htlleonding.flashcards.model.TranslationProvider;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class HomepageView extends VBox {

    // ── callback definitions ───────────────────────────────────────────────
    private Consumer<Deck> onDeckSelected;
    private Consumer<Deck> onEditDeckRequested;
    private Consumer<Deck> onDeleteDeckRequested;

    // ── ui elements ────────────────────────────────────────────────────────
    private final ScrollPane recommendedScrollPane;
    private final HBox recommendedContainer;
    private final FlowPane recentContainer;
    private final VBox calendarContainer;
    private final Label streakCountLabel;
    private final Label currentMonthLabel;

    private List<Deck> recommendedDecks = new ArrayList<>();
    private List<Deck> recentDecks = new ArrayList<>();
    private List<String> streakDates = new ArrayList<>();
    private int currentStreak = 0;
    private LocalDate currentViewMonth = LocalDate.now();

    public HomepageView() {
        this.setPadding(new Insets(24));
        this.setSpacing(24);

        // 1. Header
        Label title = new Label();
        title.textProperty().bind(TranslationProvider.createStringBinding("homepage.title"));
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-primary") + ";");

        // 2. Content Layout HBox
        HBox contentBox = new HBox(24);
        VBox.setVgrow(contentBox, Priority.ALWAYS);

        // Left Column (Recommended & Recent)
        VBox leftCol = new VBox(20);
        leftCol.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(leftCol, Priority.ALWAYS);
        leftCol.prefWidthProperty().bind(contentBox.widthProperty().multiply(0.42));

        // -- Recommended section
        VBox recommendedBox = new VBox(12);
        Label recHeader = new Label();
        recHeader.textProperty().bind(TranslationProvider.createStringBinding("homepage.recommended"));
        recHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-primary") + ";");

        recommendedContainer = new HBox(16);
        recommendedContainer.setPadding(new Insets(6));
        recommendedScrollPane = new ScrollPane(recommendedContainer);
        recommendedScrollPane.setFitToHeight(true);
        recommendedScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        recommendedScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        recommendedScrollPane.setPrefHeight(250);
        recommendedScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        recommendedBox.getChildren().addAll(recHeader, recommendedScrollPane);

        // -- Horizontal Separator Line (H-Line)
        Region hLine = new Region();
        hLine.setPrefHeight(1.5);
        hLine.setMinHeight(1.5);
        hLine.setStyle("-fx-background-color: " + ThemeProvider.get("border-default") + ";");

        // -- Recent section
        VBox recentBox = new VBox(12);
        VBox.setVgrow(recentBox, Priority.ALWAYS);

        Label recentHeader = new Label();
        recentHeader.textProperty().bind(TranslationProvider.createStringBinding("homepage.recent"));
        recentHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-primary") + ";");

        recentContainer = new FlowPane();
        recentContainer.setHgap(16);
        recentContainer.setVgap(16);
        recentContainer.setPadding(new Insets(6));

        ScrollPane recentScrollPane = new ScrollPane(recentContainer);
        recentScrollPane.setFitToWidth(true);
        recentScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(recentScrollPane, Priority.ALWAYS);

        recentBox.getChildren().addAll(recentHeader, recentScrollPane);

        leftCol.getChildren().addAll(recommendedBox, hLine, recentBox);

        // -- Vertical Separator Line (V-Line)
        Region vLine = new Region();
        vLine.setPrefWidth(1.5);
        vLine.setMinWidth(1.5);
        vLine.setStyle("-fx-background-color: " + ThemeProvider.get("border-default") + ";");
        HBox.setHgrow(vLine, Priority.NEVER);

        // Right Column (Streak / Calendar)
        VBox rightCol = new VBox(12);
        HBox.setHgrow(rightCol, Priority.ALWAYS);
        rightCol.prefWidthProperty().bind(contentBox.widthProperty().multiply(0.56));

        // -- Streak section
        VBox streakBox = new VBox(12);
        VBox.setVgrow(streakBox, Priority.ALWAYS);
        HBox streakHeaderBox = new HBox(12);
        streakHeaderBox.setAlignment(Pos.CENTER_LEFT);

        Label streakHeader = new Label();
        streakHeader.textProperty().bind(TranslationProvider.createStringBinding("homepage.streak"));
        streakHeader.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-primary") + ";");

        streakCountLabel = new Label("🔥 0 Days Streak");
        streakCountLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("accent-orange") + ";");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button prevMonthBtn = new Button("◀");
        prevMonthBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + ThemeProvider.get("text-primary") + "; -fx-cursor: hand; -fx-font-size: 16px;");
        prevMonthBtn.setOnAction(e -> handlePrevMonth());
        addHoverAnimation(prevMonthBtn);

        currentMonthLabel = new Label();
        currentMonthLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-primary") + "; -fx-min-width: 150px; -fx-alignment: center;");

        Button nextMonthBtn = new Button("▶");
        nextMonthBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + ThemeProvider.get("text-primary") + "; -fx-cursor: hand; -fx-font-size: 16px;");
        nextMonthBtn.setOnAction(e -> handleNextMonth());
        addHoverAnimation(nextMonthBtn);

        HBox navBox = new HBox(10, prevMonthBtn, currentMonthLabel, nextMonthBtn);
        navBox.setAlignment(Pos.CENTER_RIGHT);

        streakHeaderBox.getChildren().addAll(streakHeader, streakCountLabel, headerSpacer, navBox);

        calendarContainer = new VBox();
        calendarContainer.setAlignment(Pos.CENTER_LEFT);
        calendarContainer.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(calendarContainer, Priority.ALWAYS);
        streakBox.getChildren().addAll(streakHeaderBox, calendarContainer);

        rightCol.getChildren().addAll(streakBox);

        contentBox.getChildren().addAll(leftCol, vLine, rightCol);

        this.getChildren().addAll(title, contentBox);
    }

    public void applyTheme() {
        this.setStyle("-fx-background-color: transparent;");
        renderData();
    }

    public void setData(List<Deck> recommended, List<Deck> recent, List<String> streaks, int streakCount) {
        this.recommendedDecks = recommended;
        this.recentDecks = recent;
        this.streakDates = streaks;
        this.currentStreak = streakCount;
        renderData();
    }

    private void renderData() {
        // Render recommended
        recommendedContainer.getChildren().clear();
        for (Deck deck : recommendedDecks) {
            recommendedContainer.getChildren().add(createDeckTile(deck));
        }

        // Render recent
        recentContainer.getChildren().clear();
        if (recentDecks.isEmpty()) {
            Label emptyLabel = new Label();
            emptyLabel.textProperty().bind(TranslationProvider.createStringBinding("homepage.recent_empty"));
            emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + ThemeProvider.get("text-muted") + "; -fx-font-style: italic; -fx-padding: 20 0 0 10;");
            recentContainer.getChildren().add(emptyLabel);
        } else {
            for (Deck deck : recentDecks) {
                recentContainer.getChildren().add(createDeckTile(deck));
            }
        }

        // Render streak count text
        streakCountLabel.setText("🔥 " + currentStreak + " " + TranslationProvider.get("homepage.streak_days"));

        // Render current month label
        if (currentMonthLabel != null) {
            java.util.Locale currentLocale = TranslationProvider.getLocale();
            if (currentLocale == null) {
                currentLocale = java.util.Locale.getDefault();
            }
            java.time.format.DateTimeFormatter monthYearFormatter = java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy", currentLocale);
            currentMonthLabel.setText(currentViewMonth.format(monthYearFormatter));
        }

        // Render calendar
        calendarContainer.getChildren().clear();
        calendarContainer.getChildren().add(buildCalendar());
    }

    private StackPane createDeckTile(Deck deck) {
        String borderColor = ThemeProvider.get("border-default");
        String bgColor     = ThemeProvider.get("bg-card");

        StackPane tile = new StackPane();
        tile.setPrefSize(160, 210);
        tile.setMinSize(160, 210);
        tile.setMaxSize(160, 210);
        tile.setStyle(String.format(
            "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 1; " +
            "-fx-border-radius: 15; -fx-background-radius: 15; -fx-cursor: hand;",
            bgColor, borderColor));

        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(12));

        Image iconImage = IconManager.getIcon(deck.getIconId());
        ImageView iconView = new ImageView(iconImage);
        iconView.setFitWidth(70);
        iconView.setFitHeight(70);
        iconView.setPreserveRatio(true);
        iconView.setSmooth(true);

        Label nameLabel = new Label(deck.getName() != null ? deck.getName() : "");
        nameLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-primary") + ";");
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setMaxWidth(140);

        Label countLabel = new Label();
        countLabel.textProperty().bind(TranslationProvider.createStringBinding("home.cards", deck.getCardCount()));
        countLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + ThemeProvider.get("text-muted") + ";");

        content.getChildren().addAll(iconView, nameLabel, countLabel);
        tile.getChildren().add(content);

        // Edit/Delete overlays
        HBox topRow = new HBox(2);
        topRow.setPadding(new Insets(6));
        topRow.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        topRow.setPickOnBounds(false);
        StackPane.setAlignment(topRow, Pos.TOP_LEFT);

        Button editBtn = iconBtn("✎", ThemeProvider.get("text-placeholder"), ThemeProvider.get("accent-link"));
        editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + ThemeProvider.get("text-placeholder") + "; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 3;");
        editBtn.setOnAction(e -> { e.consume(); if (onEditDeckRequested != null) onEditDeckRequested.accept(deck); });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button deleteBtn = iconBtn("✖", ThemeProvider.get("text-placeholder"), ThemeProvider.get("accent-red"));
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + ThemeProvider.get("text-placeholder") + "; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 3;");
        deleteBtn.setOnAction(e -> { e.consume(); if (onDeleteDeckRequested != null) onDeleteDeckRequested.accept(deck); });

        topRow.getChildren().addAll(editBtn, spacer, deleteBtn);
        tile.getChildren().add(topRow);

        tile.setOnMouseClicked(e -> { if (onDeckSelected != null) onDeckSelected.accept(deck); });

        return tile;
    }

    private Button iconBtn(String icon, String normal, String hover) {
        Button btn = new Button(icon);
        String s1 = "-fx-background-color: transparent; -fx-text-fill: " + normal + "; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 3;";
        String s2 = "-fx-background-color: transparent; -fx-text-fill: " + hover  + "; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 0 3;";
        btn.setStyle(s1);
        btn.setOnMouseEntered(ev -> btn.setStyle(s2));
        btn.setOnMouseExited(ev -> btn.setStyle(s1));
        return btn;
    }

    private GridPane buildCalendar() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));
        grid.setStyle("-fx-background-color: " + ThemeProvider.get("bg-card") + "; -fx-border-color: " + ThemeProvider.get("border-default") + "; -fx-border-radius: 15; -fx-background-radius: 15; -fx-border-width: 1;");
        grid.setMaxWidth(Double.MAX_VALUE); // Stretch grid to full width of container
        grid.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(grid, Priority.ALWAYS);

        // Add ColumnConstraints to divide width evenly (100% / 7 = 14.28% per column)
        for (int i = 0; i < 7; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 7.0);
            cc.setHgrow(Priority.ALWAYS);
            cc.setFillWidth(true);
            grid.getColumnConstraints().add(cc);
        }

        // Add RowConstraints to divide height evenly (header + up to 6 rows = 7 rows total)
        for (int i = 0; i < 7; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100.0 / 7.0);
            rc.setVgrow(Priority.ALWAYS);
            rc.setFillHeight(true);
            grid.getRowConstraints().add(rc);
        }

        // Days of week header
        String[] daysOfWeek = {"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"};
        for (int col = 0; col < 7; col++) {
            Label dayHeader = new Label(daysOfWeek[col]);
            dayHeader.setStyle("-fx-font-size: 16px; -fx-text-fill: " + ThemeProvider.get("text-muted") + "; -fx-font-weight: bold;");
            dayHeader.setAlignment(Pos.CENTER);
            dayHeader.setMaxWidth(Double.MAX_VALUE); // Expand header label
            dayHeader.setMaxHeight(Double.MAX_VALUE);
            grid.add(dayHeader, col, 0);
        }

        LocalDate realToday = LocalDate.now();
        LocalDate firstOfMonth = currentViewMonth.withDayOfMonth(1);
        int dayOfWeekVal = firstOfMonth.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday
        int startCol = dayOfWeekVal - 1; // 0 to 6

        int daysInMonth = currentViewMonth.lengthOfMonth();

        int row = 1;
        int col = startCol;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Set<String> studiedSet = new HashSet<>(streakDates);

        // Current active streak: consecutive days back from today
        Set<String> currentStreakSet = new HashSet<>();
        LocalDate cursor = realToday;
        if (!studiedSet.contains(cursor.format(formatter))) cursor = cursor.minusDays(1);
        while (studiedSet.contains(cursor.format(formatter))) {
            currentStreakSet.add(cursor.format(formatter));
            cursor = cursor.minusDays(1);
        }

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentViewMonth.withDayOfMonth(day);
            String dateStr = date.format(formatter);
            boolean isStudied = studiedSet.contains(dateStr);
            boolean isActive  = currentStreakSet.contains(dateStr);
            boolean isToday   = date.equals(realToday);

            StackPane dayCell = new StackPane();
            dayCell.setMaxWidth(Double.MAX_VALUE);
            dayCell.setMaxHeight(Double.MAX_VALUE);

            if (isToday) {
                dayCell.setStyle(String.format(
                    "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 2.5; -fx-border-radius: 10; -fx-background-radius: 10;",
                    ThemeProvider.get("accent-blue-bg"), ThemeProvider.get("accent-blue")
                ));
            } else {
                dayCell.setStyle(String.format(
                    "-fx-background-color: %s; -fx-background-radius: 10;",
                    ThemeProvider.get("bg-primary")
                ));
            }

            if (isStudied) {
                Group flameGroup = buildFlameGroup(isActive);
                double baseScale = 2.2;
                flameGroup.setScaleX(baseScale);
                flameGroup.setScaleY(baseScale);
                dayCell.getChildren().add(flameGroup);

                if (isActive) {
                    SVGPath flameLayer = (SVGPath) flameGroup.getChildren().get(0);

                    // Breathe: scale 2.2 ↔ 2.5
                    ScaleTransition breathe = new ScaleTransition(Duration.millis(950), flameGroup);
                    breathe.setFromX(baseScale); breathe.setFromY(baseScale);
                    breathe.setToX(baseScale * 1.14); breathe.setToY(baseScale * 1.14);
                    breathe.setAutoReverse(true); breathe.setCycleCount(Animation.INDEFINITE);
                    breathe.setInterpolator(Interpolator.EASE_BOTH); breathe.play();

                    // Sway: -9° ↔ +9°
                    RotateTransition sway = new RotateTransition(Duration.millis(1300), flameGroup);
                    sway.setFromAngle(-9); sway.setToAngle(9);
                    sway.setAutoReverse(true); sway.setCycleCount(Animation.INDEFINITE);
                    sway.setInterpolator(Interpolator.EASE_BOTH); sway.play();

                    // Glow pulse
                    DropShadow glow = (DropShadow) flameLayer.getEffect();
                    Timeline glowPulse = new Timeline(
                        new KeyFrame(Duration.ZERO,        new KeyValue(glow.radiusProperty(), 4,  Interpolator.EASE_BOTH)),
                        new KeyFrame(Duration.millis(950), new KeyValue(glow.radiusProperty(), 18, Interpolator.EASE_BOTH))
                    );
                    glowPulse.setAutoReverse(true); glowPulse.setCycleCount(Animation.INDEFINITE); glowPulse.play();

                    Label dayLabel = new Label(String.valueOf(day));
                    dayLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");
                    dayCell.getChildren().add(dayLabel);
                } else {
                    Label dayLabel = new Label(String.valueOf(day));
                    dayLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-muted") + ";");
                    dayCell.getChildren().add(dayLabel);
                }
            } else {
                Label dayLabel = new Label(String.valueOf(day));
                dayLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + ThemeProvider.get("text-primary") + ";");
                dayCell.getChildren().add(dayLabel);
            }

            grid.add(dayCell, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }

        return grid;
    }

    private Group buildFlameGroup(boolean active) {
        SVGPath flame = new SVGPath();
        flame.setContent("M17.06,12.38c-0.34-0.65-0.78-1.24-1.31-1.74C14.71,9.6,14,8.1,14,6.5c0-1-0.27-1.92-0.74-2.7C12.3,5.1,11,7.1,11,9.5c0,0.85,0.18,1.67,0.51,2.42c-0.96-0.32-1.75-0.93-2.31-1.68C8.52,11.36,8,12.63,8,14c0,3.31,2.69,6,6,6s6-2.69,6-6C20,13.43,18.9,12.78,17.06,12.38z");

        SVGPath inner = new SVGPath();
        inner.setContent("M14,18c-1.1,0-2-0.56-2-1.25c0-0.69,0.45-1.25,1-1.75s0.75-1.25,0.75-1.25s0.56,0.44,0.75,1c0.19,0.56,0.75,0.94,1.25,1.25C15.75,16.5,16,17,16,17.5C16,17.78,15.1,18,14,18z");
        inner.setFill(Color.web("#FFE082"));

        if (active) {
            flame.setFill(Color.web(ThemeProvider.get("accent-orange")));
            DropShadow glow = new DropShadow();
            glow.setColor(Color.web("#FF6D00", 0.8));
            glow.setRadius(8); glow.setSpread(0.1);
            flame.setEffect(glow);
            return new Group(flame, inner);
        } else {
            flame.setFill(Color.web("#9E9E9E"));
            flame.setOpacity(0.5);
            inner.setOpacity(0.0);
            return new Group(flame, inner);
        }
    }

    private void handlePrevMonth() {
        currentViewMonth = currentViewMonth.minusMonths(1);
        renderData();
    }

    private void handleNextMonth() {
        currentViewMonth = currentViewMonth.plusMonths(1);
        renderData();
    }

    private void addHoverAnimation(Button btn) {
        btn.setOnMouseEntered(e -> {
            javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(200), btn);
            st.setToX(1.3);
            st.setToY(1.3);
            st.play();
        });
        btn.setOnMouseExited(e -> {
            javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(200), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    // Callbacks
    public void setOnDeckSelected(Consumer<Deck> cb) { this.onDeckSelected = cb; }
    public void setOnEditDeckRequested(Consumer<Deck> cb) { this.onEditDeckRequested = cb; }
    public void setOnDeleteDeckRequested(Consumer<Deck> cb) { this.onDeleteDeckRequested = cb; }
}
