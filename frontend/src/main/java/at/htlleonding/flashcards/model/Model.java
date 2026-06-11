package at.htlleonding.flashcards.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Model {
    private List<Deck> decks = new ArrayList<>();
    private StreakData streakData = new StreakData();
    private final Persistence persistence;

    public Model() {
        this(new Persistence());
    }

    public Model(Persistence persistence) {
        this.persistence = persistence;
        this.decks = persistence.loadDecks();
        this.streakData = persistence.loadStreakData();
        syncDecksWithStreakData();
    }

    private void syncDecksWithStreakData() {
        if (streakData == null || streakData.getRecentDecks() == null) {
            return;
        }
        for (Deck deck : decks) {
            String lastStudiedStr = streakData.getRecentDecks().get(deck.getId());
            if (lastStudiedStr != null && !lastStudiedStr.isEmpty()) {
                try {
                    deck.setLastStudied(LocalDateTime.parse(lastStudiedStr));
                } catch (Exception e) {
                    System.err.println("Error parsing lastStudied date for deck " + deck.getId() + ": " + e.getMessage());
                }
            }
        }
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
                    boolean deckLevelMatch = nameMatches || descMatches;

                    return d.getCards().stream().filter(c -> {
                        boolean inQuestion = c.getQuestion() != null && c.getQuestion().toLowerCase().contains(lowerQuery);
                        boolean inAnswer = c.getAnswer() != null && c.getAnswer().toLowerCase().contains(lowerQuery);
                        boolean inTags = c.getTags() != null && c.getTags().stream().anyMatch(t -> t.toLowerCase().contains(lowerQuery));
                        return inQuestion || inAnswer || inTags || deckLevelMatch;
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

    public List<String> getStreakDates() {
        return new ArrayList<>(streakData.getStreakDates());
    }

    public void addStreakDate(String date) {
        if (!streakData.getStreakDates().contains(date)) {
            streakData.getStreakDates().add(date);
            persistence.saveStreakData(streakData);
        }
    }

    public void recordDeckStudied(String deckId) {
        // Record streak date for today
        String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (!streakData.getStreakDates().contains(todayStr)) {
            streakData.getStreakDates().add(todayStr);
        }
        // Record last studied time
        LocalDateTime now = LocalDateTime.now();
        streakData.getRecentDecks().put(deckId, now.toString());

        // Update the deck object in memory
        for (Deck deck : decks) {
            if (deck.getId().equals(deckId)) {
                deck.setLastStudied(now);
                break;
            }
        }

        // Save the streak data
        persistence.saveStreakData(streakData);
    }

    public int calculateCurrentStreak() {
        List<String> dates = streakData.getStreakDates();
        if (dates.isEmpty()) return 0;

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Set<String> dateSet = new HashSet<>(dates);

        int streak = 0;
        LocalDate dateToCheck = today;

        // If today is not in the set, check if they studied yesterday to keep the streak alive
        if (!dateSet.contains(dateToCheck.format(formatter))) {
            dateToCheck = dateToCheck.minusDays(1);
        }

        while (dateSet.contains(dateToCheck.format(formatter))) {
            streak++;
            dateToCheck = dateToCheck.minusDays(1);
        }

        return streak;
    }
}
