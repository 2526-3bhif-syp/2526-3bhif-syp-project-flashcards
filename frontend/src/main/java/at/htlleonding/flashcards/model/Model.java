package at.htlleonding.flashcards.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Model {
    private List<Deck> decks = new ArrayList<>();
    private static final String FILE_PATH = "decks.json";
    private final ObjectMapper mapper = new ObjectMapper();

    public Model() {
        load();
        if (decks.isEmpty()) {
            // Initial Dummy Data if no file exists
            Deck dummyDeck = new Deck("English");
            dummyDeck.addCard(new Card("Was ist Apfel auf Englisch?", "Apple"));
            dummyDeck.addCard(new Card("Was ist Hund auf Englisch?", "Dog"));
            decks.add(dummyDeck);
            save();
        }
    }

    public List<Deck> getDecks() {
        return new ArrayList<>(decks);
    }
    
    public void updateDeck(Deck updatedDeck) {
        for (int i = 0; i < decks.size(); i++) {
            if (decks.get(i).getName().equals(updatedDeck.getName())) {
                decks.set(i, updatedDeck);
                save(); // Hier speichern
                return;
            }
        }
    }

    private void save() {
        try {
            mapper.writeValue(new File(FILE_PATH), decks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                decks = mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Deck.class));
            } catch (IOException e) {
                e.printStackTrace();
                decks = new ArrayList<>();
            }
        }
    }
}
