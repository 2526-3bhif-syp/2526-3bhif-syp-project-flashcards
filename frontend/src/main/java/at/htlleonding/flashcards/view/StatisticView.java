package at.htlleonding.flashcards.view;

import at.htlleonding.flashcards.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class StatisticView extends BorderPane {

    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("dd.MM");

    private final VBox contentBox;
    private final Label noDataLabel;
    private PieChart ratingChart;
    private BarChart<String, Number> dailyChart;

    public StatisticView() {
        contentBox = new VBox(24);
        contentBox.setPadding(new Insets(24));
        contentBox.setAlignment(Pos.TOP_CENTER);

        Label title = new Label();
        title.textProperty().bind(TranslationProvider.createStringBinding("statistic.title"));
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: "
                + ThemeProvider.get("text-primary") + ";");

        noDataLabel = new Label();
        noDataLabel.textProperty().bind(TranslationProvider.createStringBinding("statistic.no_data"));
        noDataLabel.setWrapText(true);
        noDataLabel.setAlignment(Pos.CENTER);
        noDataLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + ThemeProvider.get("text-muted") + ";");

        contentBox.getChildren().addAll(title, noDataLabel);

        setCenter(contentBox);
        applyTheme();
    }

    public void refresh(List<Deck> decks) {
        StatisticsAggregator agg = StatisticsAggregator.fromDecks(decks);
        renderCharts(agg);
    }

    private void renderCharts(StatisticsAggregator agg) {
        contentBox.getChildren().removeIf(n -> n instanceof PieChart || n instanceof BarChart);

        if (agg.getTotalCount() == 0) {
            noDataLabel.setVisible(true);
            noDataLabel.setManaged(true);
            return;
        }

        noDataLabel.setVisible(false);
        noDataLabel.setManaged(false);

        ratingChart = buildPieChart(agg);
        dailyChart = buildDailyBarChart(agg);
        contentBox.getChildren().addAll(ratingChart, dailyChart);
    }

    private BarChart<String, Number> buildDailyBarChart(StatisticsAggregator agg) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setMinorTickVisible(false);

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.titleProperty().bind(TranslationProvider.createStringBinding("statistic.daily_chart"));
        chart.setLegendVisible(false);
        chart.setPrefHeight(280);
        chart.setBarGap(2);
        chart.setCategoryGap(8);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<LocalDate, Long> byDay = agg.getCountByDay();
        byDay.forEach((date, count) ->
                series.getData().add(new XYChart.Data<>(date.format(DAY_FMT), count)));

        chart.getData().add(series);
        return chart;
    }

    private PieChart buildPieChart(StatisticsAggregator agg) {
        Map<String, Long> counts = agg.getCountByRating();

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        addSlice(data, "statistic.rating_falsch", counts.getOrDefault("FALSCH", 0L));
        addSlice(data, "statistic.rating_schwierig", counts.getOrDefault("SCHWIERIG", 0L));
        addSlice(data, "statistic.rating_ok", counts.getOrDefault("OK", 0L));
        addSlice(data, "statistic.rating_leicht", counts.getOrDefault("LEICHT", 0L));

        data.removeIf(d -> d.getPieValue() == 0);

        PieChart chart = new PieChart(data);
        chart.titleProperty().bind(TranslationProvider.createStringBinding("statistic.rating_chart"));
        chart.setLabelsVisible(true);
        chart.setLegendVisible(true);
        chart.setPrefHeight(320);
        chart.setStyle("-fx-text-fill: " + ThemeProvider.get("text-primary") + ";");
        return chart;
    }

    private void addSlice(ObservableList<PieChart.Data> data, String labelKey, long count) {
        if (count > 0) {
            String label = TranslationProvider.get(labelKey) + " (" + count + ")";
            data.add(new PieChart.Data(label, count));
        }
    }

    public void applyTheme() {
        setStyle("-fx-background-color: " + ThemeProvider.get("bg-primary") + ";");

        if (!contentBox.getChildren().isEmpty() && contentBox.getChildren().get(0) instanceof Label title) {
            title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: "
                    + ThemeProvider.get("text-primary") + ";");
        }
        noDataLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + ThemeProvider.get("text-muted") + ";");
    }
}
