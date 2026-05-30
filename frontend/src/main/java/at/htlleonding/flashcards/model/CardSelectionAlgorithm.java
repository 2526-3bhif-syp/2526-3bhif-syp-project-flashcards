package at.htlleonding.flashcards.model;

import java.util.List;
import java.util.Map;

public interface CardSelectionAlgorithm {
    Card selectNext(List<Card> cards, Map<String, Integer> weights);
    void recordRating(String cardId, String rating, Map<String, Integer> weights);
}
