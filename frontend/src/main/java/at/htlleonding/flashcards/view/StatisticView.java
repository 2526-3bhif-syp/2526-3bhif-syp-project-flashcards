package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatisticView extends BorderPane {

    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("dd.MM");
    private static final String ALL_DECKS_KEY = "statistic.filter_all_decks";

    private final VBox contentBox;
    private final ScrollPane scrollPane;
    private final Label noDataLabel;
    private final ComboBox<String> deckCombo;
    private final ComboBox<String> timeframeCombo;

    private List<Deck> allDecks = new ArrayList<>();
    private PieChart ratingChart;
    private LineChart<String, Number> dailyChart;

    public StatisticView() {
        contentBox = new VBox(20);
        contentBox.setPadding(new Insets(24));
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setMaxWidth(900);

        Label title = new Label();
        title.textProperty().bind(TranslationProvider.createStringBinding("statistic.title"));
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: "
                + ThemeProvider.get("text-primary") + ";");

        deckCombo = new ComboBox<>();
        deckCombo.setMinWidth(160);
        deckCombo.setOnAction(e -> applyFilters());

        timeframeCombo = new ComboBox<>();
        timeframeCombo.setMinWidth(120);
        timeframeCombo.setOnAction(e -> applyFilters());

        HBox filterBar = new HBox(12, deckCombo, timeframeCombo);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        noDataLabel = new Label();
        noDataLabel.textProperty().bind(TranslationProvider.createStringBinding("statistic.no_data"));
        noDataLabel.setWrapText(true);
        noDataLabel.setAlignment(Pos.CENTER);
        noDataLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + ThemeProvider.get("text-muted") + ";");

        contentBox.getChildren().addAll(title, filterBar, noDataLabel);

        VBox wrapper = new VBox(contentBox);
        wrapper.setAlignment(Pos.TOP_CENTER);

        scrollPane = new ScrollPane(wrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        setCenter(scrollPane);
        applyTheme();
        ThemeProvider.addThemeListener(this::applyTheme);
    }

    public void refresh(List<Deck> decks) {
        this.allDecks = new ArrayList<>(decks);
        rebuildDeckCombo();
        rebuildTimeframeCombo();
        applyFilters();
    }

    private void rebuildDeckCombo() {
        String allDecksLabel = TranslationProvider.get(ALL_DECKS_KEY);
        List<String> items = new ArrayList<>();
        items.add(allDecksLabel);
        allDecks.forEach(d -> items.add(d.getName()));
        deckCombo.setItems(FXCollections.observableArrayList(items));
        deckCombo.getSelectionModel().selectFirst();
    }

    private void rebuildTimeframeCombo() {
        List<String> items = List.of(
                TranslationProvider.get("statistic.timeframe_day"),
                TranslationProvider.get("statistic.timeframe_week"),
                TranslationProvider.get("statistic.timeframe_month"),
                TranslationProvider.get("statistic.timeframe_all")
        );
        timeframeCombo.setItems(FXCollections.observableArrayList(items));
        timeframeCombo.getSelectionModel().select(3);
    }

    private void applyFilters() {
        StatisticsAggregator agg = buildAggregator();
        StatisticsAggregator.Timeframe tf = selectedTimeframe();
        renderCharts(agg.filtered(tf));
    }

    private StatisticsAggregator buildAggregator() {
        int deckIdx = deckCombo.getSelectionModel().getSelectedIndex();
        if (deckIdx <= 0 || allDecks.isEmpty()) {
            return StatisticsAggregator.fromDecks(allDecks);
        }
        int realIdx = deckIdx - 1;
        if (realIdx < allDecks.size()) {
            return StatisticsAggregator.fromDeck(allDecks.get(realIdx));
        }
        return StatisticsAggregator.fromDecks(allDecks);
    }

    private StatisticsAggregator.Timeframe selectedTimeframe() {
        int idx = timeframeCombo.getSelectionModel().getSelectedIndex();
        return switch (idx) {
            case 0 -> StatisticsAggregator.Timeframe.DAY;
            case 1 -> StatisticsAggregator.Timeframe.WEEK;
            case 2 -> StatisticsAggregator.Timeframe.MONTH;
            default -> StatisticsAggregator.Timeframe.ALL;
        };
    }

    private void renderCharts(StatisticsAggregator agg) {
        contentBox.getChildren().removeIf(n -> n instanceof PieChart || n instanceof LineChart);

        if (agg.getTotalCount() == 0) {
            noDataLabel.setVisible(true);
            noDataLabel.setManaged(true);
            return;
        }

        noDataLabel.setVisible(false);
        noDataLabel.setManaged(false);

        ratingChart = buildPieChart(agg);
        dailyChart = buildDailyLineChart(agg);
        contentBox.getChildren().addAll(ratingChart, dailyChart);
    }

    private PieChart buildPieChart(StatisticsAggregator agg) {
        Map<String, Long> counts = agg.getCountByRating();

        // colors mirror the rating buttons: FALSCH=red, SCHWIERIG=orange, OK=blue, LEICHT=green
        String[] ratingKeys  = {"FALSCH",        "SCHWIERIG",       "OK",            "LEICHT"};
        String[] labelKeys   = {"statistic.rating_falsch", "statistic.rating_schwierig",
                                "statistic.rating_ok",     "statistic.rating_leicht"};
        String[] colorTokens = {"accent-red", "accent-orange", "accent-blue", "accent-green"};

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        List<String> sliceColors = new ArrayList<>();
        for (int i = 0; i < ratingKeys.length; i++) {
            long count = counts.getOrDefault(ratingKeys[i], 0L);
            if (count > 0) {
                String label = TranslationProvider.get(labelKeys[i]) + " (" + count + ")";
                data.add(new PieChart.Data(label, count));
                sliceColors.add(ThemeProvider.get(colorTokens[i]));
            }
        }

        PieChart chart = new PieChart(data);
        chart.titleProperty().bind(TranslationProvider.createStringBinding("statistic.rating_chart"));
        chart.setLabelsVisible(true);
        chart.setLegendVisible(true);
        chart.setPrefHeight(320);
        chart.setStyle("-fx-background-color: " + ThemeProvider.get("bg-card") + ";");

        Platform.runLater(() -> {
            for (int i = 0; i < Math.min(data.size(), sliceColors.size()); i++) {
                if (data.get(i).getNode() != null) {
                    data.get(i).getNode().setStyle("-fx-pie-color: " + sliceColors.get(i) + ";");
                }
            }
        });
        return chart;
    }

    private LineChart<String, Number> buildDailyLineChart(StatisticsAggregator agg) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setMinorTickVisible(false);

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.titleProperty().bind(TranslationProvider.createStringBinding("statistic.daily_chart"));
        chart.setLegendVisible(false);
        chart.setPrefHeight(280);
        chart.setCreateSymbols(true);
        chart.setStyle("-fx-background-color: " + ThemeProvider.get("bg-card")
                + "; -fx-text-fill: " + ThemeProvider.get("text-primary") + ";");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<LocalDate, Long> byDay = agg.getCountByDay();
        byDay.forEach((date, count) ->
                series.getData().add(new XYChart.Data<>(date.format(DAY_FMT), count)));

        chart.getData().add(series);
        return chart;
    }

    public void applyTheme() {
        setStyle("-fx-background-color: " + ThemeProvider.get("bg-primary") + ";");
        scrollPane.setStyle("-fx-background-color: " + ThemeProvider.get("bg-primary")
                + "; -fx-background: " + ThemeProvider.get("bg-primary") + ";");

        if (!contentBox.getChildren().isEmpty() && contentBox.getChildren().get(0) instanceof Label title) {
            title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: "
                    + ThemeProvider.get("text-primary") + ";");
        }
        noDataLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + ThemeProvider.get("text-muted") + ";");

        String comboStyle = "-fx-background-color: " + ThemeProvider.get("bg-card")
                + "; -fx-border-color: " + ThemeProvider.get("border-default")
                + "; -fx-border-radius: 6; -fx-background-radius: 6;"
                + " -fx-text-fill: " + ThemeProvider.get("text-primary") + ";";
        deckCombo.setStyle(comboStyle);
        timeframeCombo.setStyle(comboStyle);

        if (!allDecks.isEmpty()) {
            applyFilters();
        }
    }
}
