package at.htlleonding.flashcards.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Deck {
    private String id;
    private String name;
    private String description;
    private String iconId;
    private List<Card> cards;

    public Deck() {
        this.id = UUID.randomUUID().toString();
        this.cards = new ArrayList<>();
        this.iconId = "default";
    }

    public Deck(String name, String description) {
        this(name, description, "default");
    }

    public Deck(String name, String description, String iconId) {
        this.id = UUID.randomUUID().toString();
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Deck name is mandatory.");
        }
        this.name = name;
        this.description = description;
        this.iconId = iconId != null ? iconId : "default";
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
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getIconId() { return iconId; }
    public void setIconId(String iconId) { this.iconId = iconId; }
    
    public List<Card> getCards() { return new ArrayList<>(cards); }
    
    @JsonIgnore
    public int getCardCount() { return cards.size(); }
}
