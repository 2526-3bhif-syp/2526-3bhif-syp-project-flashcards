package at.htlleonding.flashcards.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticsAggregator {

    public enum Timeframe {
        DAY, WEEK, MONTH, ALL
    }

    private final List<StudyRecord> records;

    public StatisticsAggregator(List<StudyRecord> records) {
        this.records = new ArrayList<>(records);
    }

    public static StatisticsAggregator fromDecks(List<Deck> decks) {
        List<StudyRecord> all = decks.stream()
                .flatMap(d -> d.getCards().stream())
                .flatMap(c -> c.getStudyHistory().stream())
                .collect(Collectors.toList());
        return new StatisticsAggregator(all);
    }

    public static StatisticsAggregator fromDeck(Deck deck) {
        List<StudyRecord> all = deck.getCards().stream()
                .flatMap(c -> c.getStudyHistory().stream())
                .collect(Collectors.toList());
        return new StatisticsAggregator(all);
    }

    public StatisticsAggregator filtered(Timeframe timeframe) {
        if (timeframe == Timeframe.ALL) return this;
        LocalDate cutoff = switch (timeframe) {
            case DAY   -> LocalDate.now().minusDays(1);
            case WEEK  -> LocalDate.now().minusWeeks(1);
            case MONTH -> LocalDate.now().minusMonths(1);
            default    -> LocalDate.MIN;
        };
        List<StudyRecord> filtered = records.stream()
                .filter(r -> toLocalDate(r) != null && !toLocalDate(r).isBefore(cutoff))
                .collect(Collectors.toList());
        return new StatisticsAggregator(filtered);
    }

    public Map<String, Long> getCountByRating() {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("FALSCH", 0L);
        result.put("SCHWIERIG", 0L);
        result.put("OK", 0L);
        result.put("LEICHT", 0L);
        records.stream()
                .filter(r -> r.getRating() != null)
                .collect(Collectors.groupingBy(r -> r.getRating().toUpperCase(), Collectors.counting()))
                .forEach((k, v) -> result.merge(k, v, Long::sum));
        return result;
    }

    public Map<LocalDate, Long> getCountByDay() {
        return records.stream()
                .filter(r -> toLocalDate(r) != null)
                .collect(Collectors.groupingBy(
                        this::toLocalDate,
                        TreeMap::new,
                        Collectors.counting()
                ));
    }

    public int getTotalCount() {
        return records.size();
    }

    private LocalDate toLocalDate(StudyRecord r) {
        if (r == null || r.getTimestamp() == null) return null;
        try {
            return Instant.parse(r.getTimestamp()).atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Exception e) {
            return null;
        }
    }
}
