package at.htlleonding.flashcards.model;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private final List<Deck> decks = new ArrayList<>();

    public Model() {
        // Initial Dummy Data
        Deck dummyDeck = new Deck("English");
        dummyDeck.addCard(new Card("Was ist Apfel auf Englisch?", "Apple"));
        dummyDeck.addCard(new Card("Was ist Hund auf Englisch?", "Dog"));
        decks.add(dummyDeck);
    }

    public List<Deck> getDecks() {
        return new ArrayList<>(decks);
    }
}
