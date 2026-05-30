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
}
