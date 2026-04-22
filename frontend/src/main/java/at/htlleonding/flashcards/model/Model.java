package at.htlleonding.flashcards.model;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private List<Deck> decks = new ArrayList<>();
    private final Persistence persistence;

    public Model() {
        this.persistence = new Persistence();
        this.decks = persistence.loadDecks();

        if (decks.isEmpty()) {
            // Initial Dummy Data if no file exists
            Deck dummyDeck = new Deck("English", "Basic English vocabulary");
            dummyDeck.addCard(new Card("Was ist Apfel auf Englisch?", "Apple"));
            dummyDeck.addCard(new Card("Was ist Hund auf Englisch?", "Dog"));
            decks.add(dummyDeck);
            persistence.saveDecks(decks);
        }
    }

    public List<Deck> getDecks() {
        return new ArrayList<>(decks);
    }

    public void addDeck(Deck deck) {
        this.decks.add(deck);
        persistence.saveDecks(decks);
    }

    public void updateDeck(Deck updatedDeck) {
        for (int i = 0; i < decks.size(); i++) {
            if (decks.get(i).getId().equals(updatedDeck.getId())) {
                decks.set(i, updatedDeck);
                persistence.saveDecks(decks);
                return;
            }
        }
    }

    public void removeDeck(Deck deck) {
        decks.removeIf(d -> d.getId().equals(deck.getId()));
        persistence.saveDecks(decks);
    }

    /**
     * Adds a new deck, or merges its cards into the existing deck if the name already exists.
     */
    public void addOrMergeDeck(Deck incoming) {
        for (int i = 0; i < decks.size(); i++) {
            if (decks.get(i).getName().equals(incoming.getName())) {
                Deck existing = decks.get(i);
                for (Card card : incoming.getCards()) {
                    existing.addCard(card);
                }
                decks.set(i, existing);
                persistence.saveDecks(decks);
                return;
            }
        }
        decks.add(incoming);
        persistence.saveDecks(decks);
    }

    public Persistence getPersistence() {
        return persistence;
    }
}
