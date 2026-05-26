package at.htlleonding.flashcards.model;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomCardAlgorithm implements CardSelectionAlgorithm {

    private final Random rng;

    public RandomCardAlgorithm() {
        this.rng = new Random();
    }

    RandomCardAlgorithm(Random rng) {
        this.rng = rng;
    }

    @Override
    public Card selectNext(List<Card> cards, Map<String, Integer> weights) {
        if (cards == null || cards.isEmpty()) return null;
        return cards.get(rng.nextInt(cards.size()));
    }

    @Override
    public void recordRating(String cardId, String rating, Map<String, Integer> weights) {
        // Random algorithm ignores ratings
    }
}
