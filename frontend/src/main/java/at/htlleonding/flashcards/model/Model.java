package at.htlleonding.flashcards.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Model {
    private List<Deck> decks = new ArrayList<>();
    private final Persistence persistence;

    public Model() {
        this(new Persistence());
    }

    public Model(Persistence persistence) {
        this.persistence = persistence;
        this.decks = persistence.loadDecks();

    }

    public List<Card> searchCards(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String lowerQuery = query.toLowerCase();
        return decks.stream()
                .flatMap(d -> {
                    boolean nameMatches = d.getName() != null && d.getName().toLowerCase().contains(lowerQuery);
                    boolean descMatches = d.getDescription() != null && d.getDescription().toLowerCase().contains(lowerQuery);

                    // To avoid flooding results with every card in a deck for short queries (like "k"),
                    // we only include all cards of a deck if the query is at least 3 chars long
                    // or if it's an exact match for the deck name.
                    boolean includeAllDueToDeck = (nameMatches || descMatches) && (lowerQuery.length() >= 3 ||
                            (d.getName() != null && d.getName().equalsIgnoreCase(query)));

                    return d.getCards().stream().filter(c -> {
                        boolean inQuestion = c.getQuestion() != null && c.getQuestion().toLowerCase().contains(lowerQuery);
                        boolean inAnswer = c.getAnswer() != null && c.getAnswer().toLowerCase().contains(lowerQuery);
                        boolean inTags = c.getTags() != null && c.getTags().stream().anyMatch(t -> t.toLowerCase().contains(lowerQuery));
                        return inQuestion || inAnswer || inTags || includeAllDueToDeck;
                    });
                })
                .collect(Collectors.toList());
    }

    public List<Deck> getDecks() {
        return new ArrayList<>(decks);
    }

    public void updateDeck(Deck updatedDeck) {
        for (int i = 0; i < decks.size(); i++) {
            if (decks.get(i).getId().equals(updatedDeck.getId())) {
                decks.set(i, updatedDeck);
                persistence.saveDecks(decks);
                return;
            }
        }
        throw new IllegalArgumentException("Deck with name " + updatedDeck.getName() + " not found");
    }

    public void removeDeck(Deck deck) {
        decks.removeIf(d -> d.getId().equals(deck.getId()));
        persistence.saveDecks(decks);
    }

    public void addDeck(Deck deck) {
        decks.add(deck);
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
