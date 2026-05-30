package at.htlleonding.flashcards.model;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Weighted random selection. Cards rated FALSCH/SCHWIERIG appear more often;
 * cards rated LEICHT appear less often. Implements BR-003.
 *
 * Weights: FALSCH=8, SCHWIERIG=4, OK=2, LEICHT=1, unrated=3
 */
public class WeightedCardAlgorithm implements CardSelectionAlgorithm {

    static final int WEIGHT_FALSCH    = 8;
    static final int WEIGHT_SCHWIERIG = 4;
    static final int WEIGHT_OK        = 2;
    static final int WEIGHT_LEICHT    = 1;
    static final int WEIGHT_DEFAULT   = 3;

    private final Random rng;

    public WeightedCardAlgorithm() {
        this.rng = new Random();
    }

    WeightedCardAlgorithm(Random rng) {
        this.rng = rng;
    }

    @Override
    public Card selectNext(List<Card> cards, Map<String, Integer> weights) {
        if (cards == null || cards.isEmpty()) return null;

        int totalWeight = 0;
        for (Card c : cards) {
            totalWeight += weights.getOrDefault(c.getId(), WEIGHT_DEFAULT);
        }

        int pick = rng.nextInt(totalWeight);
        int cumulative = 0;
        for (Card c : cards) {
            cumulative += weights.getOrDefault(c.getId(), WEIGHT_DEFAULT);
            if (pick < cumulative) return c;
        }
        return cards.get(cards.size() - 1);
    }

    @Override
    public void recordRating(String cardId, String rating, Map<String, Integer> weights) {
        if (cardId == null || rating == null) return;
        int weight = switch (rating.toUpperCase()) {
            case "FALSCH"    -> WEIGHT_FALSCH;
            case "SCHWIERIG" -> WEIGHT_SCHWIERIG;
            case "OK"        -> WEIGHT_OK;
            case "LEICHT"    -> WEIGHT_LEICHT;
            default          -> WEIGHT_DEFAULT;
        };
        weights.put(cardId, weight);
    }
}
