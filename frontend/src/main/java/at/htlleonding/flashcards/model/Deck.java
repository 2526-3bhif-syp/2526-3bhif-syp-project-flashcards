package at.htlleonding.flashcards.model;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private String name;
    private List<Card> cards;

    public Deck(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Deck name is mandatory.");
        }
        this.name = name;
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        if (card == null) return;
        this.cards.add(card);
    }

    // Getters
    public String getName() { return name; }
    public List<Card> getCards() { return new ArrayList<>(cards); }
    
    public int getCardCount() { return cards.size(); }
}
