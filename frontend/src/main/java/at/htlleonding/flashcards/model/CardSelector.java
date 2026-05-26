package at.htlleonding.flashcards.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardSelector {

    public enum AlgorithmType { RANDOM, WEIGHTED }

    private final CardSelectionAlgorithm algorithm;
    private final Map<String, Integer> weights = new HashMap<>();

    public CardSelector(CardSelectionAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public static CardSelector of(AlgorithmType type) {
        return switch (type) {
            case RANDOM   -> new CardSelector(new RandomCardAlgorithm());
            case WEIGHTED -> new CardSelector(new WeightedCardAlgorithm());
        };
    }

    public Card selectNext(List<Card> cards) {
        return algorithm.selectNext(cards, weights);
    }

    public void recordRating(Card card, String rating) {
        if (card == null) return;
        algorithm.recordRating(card.getId(), rating, weights);
    }

    public void reset() {
        weights.clear();
    }
}
