package at.htlleonding.flashcards.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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

    public void saveDecks(List<Deck> decks) {
        try {
            mapper.writeValue(new File(DEFAULT_FILE), decks);
        } catch (IOException e) {
            System.err.println("Error saving decks: " + e.getMessage());
        }
    }

    /** Export a single deck (name + all cards). */
    public void exportToJSON(Deck deck, File file) throws IOException {
        mapper.writeValue(file, deck);
    }

    /** Export multiple decks as a JSON array. */
    public void exportDecksToJSON(List<Deck> decks, File file) throws IOException {
        mapper.writeValue(file, decks);
    }

    /** Export a single card as a plain JSON object. */
    public void exportCardToJSON(Card card, File file) throws IOException {
        mapper.writeValue(file, card);
    }

    /** Export multiple cards as a JSON array. */
    public void exportCardsToJSON(List<Card> cards, File file) throws IOException {
        mapper.writeValue(file, cards);
    }

    /**
     * Import cards from a JSON file into a deck.
     * Handles: single Deck object, array of Decks (first used), array of Cards, single Card object.
     */
    public Deck importFromJSON(File file) throws IOException {
        try {
            JsonNode node = mapper.readTree(file);
            if (node.isArray()) {
                if (node.size() > 0 && node.get(0).has("name")) {
                    // Array of decks — use first
                    List<Deck> decks = mapper.convertValue(node, new TypeReference<List<Deck>>() {});
                    return decks.get(0);
                }
                // Array of cards
                List<Card> cards = mapper.convertValue(node, new TypeReference<List<Card>>() {});
                Deck deck = new Deck("Imported Deck", "");
                if (cards != null) deck.setCards(cards);
                return deck;
            } else {
                if (node.has("name")) {
                    return mapper.treeToValue(node, Deck.class);
                } else if (node.has("question")) {
                    // Single card object
                    Card card = mapper.treeToValue(node, Card.class);
                    Deck deck = new Deck("Imported", "");
                    deck.addCard(card);
                    return deck;
                }
                throw new IOException("Unrecognised JSON format");
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Could not parse JSON: " + e.getMessage());
        }
    }

    /**
     * Import one or more decks from a JSON file.
     * Handles: single Deck object, array of Deck objects.
     */
    public List<Deck> importDecksFromJSON(File file) throws IOException {
        try {
            JsonNode node = mapper.readTree(file);
            if (node.isArray()) {
                if (node.size() > 0 && node.get(0).has("name")) {
                    return mapper.convertValue(node, new TypeReference<List<Deck>>() {});
                }
                throw new IOException("JSON array does not contain deck objects");
            } else {
                if (node.has("name")) {
                    return List.of(mapper.treeToValue(node, Deck.class));
                }
                throw new IOException("JSON object is not a deck");
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Could not parse JSON: " + e.getMessage());
        }
    }
}
