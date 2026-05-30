package at.htlleonding.flashcards.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class WeightedCardAlgorithmTest {

    private WeightedCardAlgorithm algo;
    private Map<String, Integer> weights;

    @BeforeEach
    void setUp() {
        algo = new WeightedCardAlgorithm(new Random(42));
        weights = new HashMap<>();
    }

    private Card card(String question) {
        return new Card(question, "answer");
    }

    @Test
    void selectNext_nullList_returnsNull() {
        assertNull(algo.selectNext(null, weights));
    }

    @Test
    void selectNext_emptyList_returnsNull() {
        assertNull(algo.selectNext(List.of(), weights));
    }

    @Test
    void selectNext_singleCard_alwaysReturnsThatCard() {
        Card only = card("Only");
        for (int i = 0; i < 10; i++) {
            assertSame(only, algo.selectNext(List.of(only), weights));
        }
    }

    @Test
    void selectNext_returnsCardFromList() {
        Card a = card("A"), b = card("B"), c = card("C");
        List<Card> cards = List.of(a, b, c);
        assertTrue(cards.contains(algo.selectNext(cards, weights)));
    }

    @Test
    void selectNext_unratedCardsUseDefaultWeight() {
        Card a = card("A"), b = card("B");
        assertNotNull(algo.selectNext(List.of(a, b), weights));
    }

    @Test
    void selectNext_highWeightCardSelectedMoreOften() {
        WeightedCardAlgorithm seeded = new WeightedCardAlgorithm(new Random(0));
        Card falsch = card("Hard"), leicht = card("Easy");
        weights.put(falsch.getId(), WeightedCardAlgorithm.WEIGHT_FALSCH);
        weights.put(leicht.getId(), WeightedCardAlgorithm.WEIGHT_LEICHT);

        int falschCount = 0;
        for (int i = 0; i < 1000; i++) {
            if (seeded.selectNext(List.of(falsch, leicht), weights) == falsch) falschCount++;
        }
        assertTrue(falschCount > 800, "Expected >80% picks for FALSCH card, got " + falschCount);
    }

    @Test
    void selectNext_deterministicWithSeededRng() {
        Card a = card("A"), b = card("B");
        weights.put(a.getId(), WeightedCardAlgorithm.WEIGHT_FALSCH);
        weights.put(b.getId(), WeightedCardAlgorithm.WEIGHT_LEICHT);

        WeightedCardAlgorithm first = new WeightedCardAlgorithm(new Random(99));
        WeightedCardAlgorithm second = new WeightedCardAlgorithm(new Random(99));
        assertSame(first.selectNext(List.of(a, b), weights), second.selectNext(List.of(a, b), weights));
    }
}
