package at.htlleonding.flashcards.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

public class Deck {
    private String name;
    private List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
    }

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
    
    public void removeCard(Card card) {
        this.cards.removeIf(c -> c.getId().equals(card.getId()));
    }
    
    public void updateCard(Card updatedCard) {
        for (int i = 0; i < this.cards.size(); i++) {
            if (this.cards.get(i).getId().equals(updatedCard.getId())) {
                this.cards.set(i, updatedCard);
                break;
            }
        }
    }

    public void setCards(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
    }

    // Getters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public List<Card> getCards() { return new ArrayList<>(cards); }
    
    @JsonIgnore
    public int getCardCount() { return cards.size(); }
}
