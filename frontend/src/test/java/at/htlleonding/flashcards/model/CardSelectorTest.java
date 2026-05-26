package at.htlleonding.flashcards.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CardSelectorTest {

    private static Card card(String question) {
        return new Card(question, "answer");
    }

    // ── RandomCardAlgorithm ────────────────────────────────────────────────

    @Test
    void random_selectsFromList() {
        RandomCardAlgorithm algo = new RandomCardAlgorithm(new Random(42));
        Card a = card("A"), b = card("B"), c = card("C");
        Card selected = algo.selectNext(List.of(a, b, c), new java.util.HashMap<>());
        assertNotNull(selected);
        assertTrue(List.of(a, b, c).contains(selected));
    }

    @Test
    void random_returnsNull_emptyList() {
        RandomCardAlgorithm algo = new RandomCardAlgorithm();
        assertNull(algo.selectNext(List.of(), new java.util.HashMap<>()));
    }

    @Test
    void random_ignoresRatings() {
        RandomCardAlgorithm algo = new RandomCardAlgorithm();
        java.util.Map<String, Integer> weights = new java.util.HashMap<>();
        algo.recordRating("some-id", "FALSCH", weights);
        assertTrue(weights.isEmpty());
    }

    // ── WeightedCardAlgorithm ──────────────────────────────────────────────

    @Test
    void weighted_recordsRatingsCorrectly() {
        WeightedCardAlgorithm algo = new WeightedCardAlgorithm();
        java.util.Map<String, Integer> weights = new java.util.HashMap<>();

        algo.recordRating("id1", "FALSCH",    weights);
        algo.recordRating("id2", "SCHWIERIG", weights);
        algo.recordRating("id3", "OK",        weights);
        algo.recordRating("id4", "LEICHT",    weights);

        assertEquals(WeightedCardAlgorithm.WEIGHT_FALSCH,    weights.get("id1"));
        assertEquals(WeightedCardAlgorithm.WEIGHT_SCHWIERIG, weights.get("id2"));
        assertEquals(WeightedCardAlgorithm.WEIGHT_OK,        weights.get("id3"));
        assertEquals(WeightedCardAlgorithm.WEIGHT_LEICHT,    weights.get("id4"));
    }

    @Test
    void weighted_selectsFromList() {
        WeightedCardAlgorithm algo = new WeightedCardAlgorithm(new Random(7));
        Card a = card("A"), b = card("B");
        Card selected = algo.selectNext(List.of(a, b), new java.util.HashMap<>());
        assertNotNull(selected);
    }

    @Test
    void weighted_returnsNull_emptyList() {
        WeightedCardAlgorithm algo = new WeightedCardAlgorithm();
        assertNull(algo.selectNext(List.of(), new java.util.HashMap<>()));
    }

    @Test
    void weighted_highWeightCardSelectedMoreOften() {
        // Give card A weight=8 (FALSCH), card B weight=1 (LEICHT).
        // Over 1000 picks, A should win far more often.
        WeightedCardAlgorithm algo = new WeightedCardAlgorithm(new Random(0));
        Card a = card("A"), b = card("B");
        java.util.Map<String, Integer> weights = new java.util.HashMap<>();
        weights.put(a.getId(), WeightedCardAlgorithm.WEIGHT_FALSCH);
        weights.put(b.getId(), WeightedCardAlgorithm.WEIGHT_LEICHT);

        int countA = 0;
        for (int i = 0; i < 1000; i++) {
            if (algo.selectNext(List.of(a, b), weights) == a) countA++;
        }
        assertTrue(countA > 700, "Expected A selected >70% of time, got " + countA);
    }

    @Test
    void weighted_ignoresNullCardId() {
        WeightedCardAlgorithm algo = new WeightedCardAlgorithm();
        java.util.Map<String, Integer> weights = new java.util.HashMap<>();
        algo.recordRating(null, "FALSCH", weights);
        assertTrue(weights.isEmpty());
    }

    // ── CardSelector ──────────────────────────────────────────────────────

    @Test
    void selector_of_random_works() {
        CardSelector sel = CardSelector.of(CardSelector.AlgorithmType.RANDOM);
        Card a = card("A");
        assertNotNull(sel.selectNext(List.of(a)));
    }

    @Test
    void selector_of_weighted_works() {
        CardSelector sel = CardSelector.of(CardSelector.AlgorithmType.WEIGHTED);
        Card a = card("A");
        assertNotNull(sel.selectNext(List.of(a)));
    }

    @Test
    void selector_recordRating_and_reset() {
        CardSelector sel = CardSelector.of(CardSelector.AlgorithmType.WEIGHTED);
        Card a = card("A"), b = card("B");
        // After rating A as LEICHT, reset should clear weights
        sel.selectNext(List.of(a, b));
        sel.recordRating(a, "LEICHT");
        sel.reset();
        // No crash and still selects after reset
        assertNotNull(sel.selectNext(List.of(a, b)));
    }

    @Test
    void selector_recordRating_nullCard_noException() {
        CardSelector sel = CardSelector.of(CardSelector.AlgorithmType.WEIGHTED);
        assertDoesNotThrow(() -> sel.recordRating(null, "FALSCH"));
    }
}
