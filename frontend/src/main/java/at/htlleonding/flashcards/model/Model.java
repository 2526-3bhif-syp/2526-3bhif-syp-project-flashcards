package at.htlleonding.flashcards.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
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
            Deck dummyDeck = new Deck("English");
            dummyDeck.addCard(new Card("Was ist Apfel auf Englisch?", "Apple"));
            dummyDeck.addCard(new Card("Was ist Hund auf Englisch?", "Dog"));
            decks.add(dummyDeck);
            persistence.saveDecks(decks);
        }
    }

    public List<Deck> getDecks() {
        return new ArrayList<>(decks);
    }
    
    public void updateDeck(Deck updatedDeck) {
        for (int i = 0; i < decks.size(); i++) {
            if (decks.get(i).getName().equals(updatedDeck.getName())) {
                decks.set(i, updatedDeck);
                persistence.saveDecks(decks);
                return;
            }
        }
    }

    public Persistence getPersistence() {
        return persistence;
    }
}

