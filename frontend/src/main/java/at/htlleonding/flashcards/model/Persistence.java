package at.htlleonding.flashcards.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Persistence {
    private static final String DEFAULT_FILE = "decks.json";
    private final ObjectMapper mapper;

    public Persistence() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Loads decks from the internal application storage.
     */
    public List<Deck> loadDecks() {
        File file = new File(DEFAULT_FILE);
        if (!file.exists()) return new ArrayList<>();
        
        try {
            return mapper.readValue(file, new TypeReference<List<Deck>>() {});
        } catch (IOException e) {
            System.err.println("Error loading decks: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Saves decks to the internal application storage.
     */
    public void saveDecks(List<Deck> decks) {
        try {
            mapper.writeValue(new File(DEFAULT_FILE), decks);
        } catch (IOException e) {
            System.err.println("Error saving decks: " + e.getMessage());
        }
    }

    /**
     * Exports a deck to a JSON file.
     */
    public void exportToJSON(Deck deck, File file) throws IOException {
        mapper.writeValue(file, deck);
    }

    /**
     * Imports a deck from a JSON file.
     * Supports both a single Deck object, a List of Decks, or a List of Cards.
     */
    public Deck importFromJSON(File file) throws IOException {
        try {
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(file);
            if (node.isArray()) {
                // Try to parse as List of Decks first
                try {
                    List<Deck> decks = mapper.convertValue(node, new TypeReference<List<Deck>>() {});
                    if (decks != null && !decks.isEmpty() && decks.get(0).getName() != null) {
                        return decks.get(0);
                    }
                } catch (Exception ignored) {}

                // Fallback: Try to parse as List of Cards and wrap in a Deck
                List<Card> cards = mapper.convertValue(node, new TypeReference<List<Card>>() {});
                Deck deck = new Deck("Imported Deck");
                if (cards != null) {
                    deck.setCards(cards);
                }
                return deck;
            } else {
                return mapper.treeToValue(node, Deck.class);
            }
        } catch (Exception e) {
            throw new IOException("Could not parse JSON: " + e.getMessage());
        }
    }

}
