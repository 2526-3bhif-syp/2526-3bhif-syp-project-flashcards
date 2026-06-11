package at.htlleonding.flashcards.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelTest {

    @Mock
    private Persistence persistence;

    private List<Deck> initialDecks;

    @BeforeEach
    void setUp() {
        initialDecks = new ArrayList<>();
        Deck deck = new Deck("TestDeck", "Desc");
        deck.addCard(new Card("Q1", "A1"));
        initialDecks.add(deck);
        // Standardverhalten für die meisten Tests: Persistenz liefert ein Deck
        lenient().when(persistence.loadDecks()).thenReturn(initialDecks);
        lenient().when(persistence.loadStreakData()).thenReturn(new StreakData());
    }

    @Test
    void testGetDecksReturnsCopy() {
        Model model = new Model(persistence);
        List<Deck> returnedDecks = model.getDecks();
        
        returnedDecks.clear();
        
        assertEquals(1, model.getDecks().size(), "Die interne Liste darf nicht gelöscht werden");
        assertNotSame(returnedDecks, model.getDecks(), "Es müssen unterschiedliche Listen-Instanzen sein");
    }

    @Test
    void testUpdateDeckSuccessfully() {
        Model model = new Model(persistence);
        // Get the existing deck to ensure we have the correct ID
        Deck existingDeck = model.getDecks().get(0);
        
        // Create an updated version or modify the existing one
        existingDeck.addCard(new Card("Q", "A"));

        model.updateDeck(existingDeck);

        // Verify saveDecks was called
        verify(persistence).saveDecks(anyList());
        assertEquals(1, model.getDecks().size());
        assertEquals(2, model.getDecks().get(0).getCards().size());
    }

    @Test
    void testUpdateDeckThrowsExceptionWhenNotFound() {
        Model model = new Model(persistence);
        Deck nonExistentDeck = new Deck("Unknown", "Desc");

        assertThrows(IllegalArgumentException.class, () -> {
            model.updateDeck(nonExistentDeck);
        });
        
        // Save sollte niemals aufgerufen werden wenn das Deck nicht existiert
        verify(persistence, times(0)).saveDecks(anyList());
    }

    @Test
    void testInitializationWithEmptyPersistence() {
        // Separater Mock für den Fall dass die Persistenz leer ist
        Persistence emptyPersistence = mock(Persistence.class);
        when(emptyPersistence.loadDecks()).thenReturn(new ArrayList<>());

        Model model = new Model(emptyPersistence);

        assertTrue(model.getDecks().isEmpty(), "Model should be empty when persistence is empty");
    }

    @Test
    void testRemoveDeck() {
        Model model = new Model(persistence);
        Deck deckToRemove = initialDecks.get(0);

        model.removeDeck(deckToRemove);

        assertTrue(model.getDecks().isEmpty());
        verify(persistence).saveDecks(anyList());
    }

    @Test
    void testAddOrMergeDeck_New() {
        Model model = new Model(persistence);
        Deck newDeck = new Deck("New Deck", "Desc");

        model.addOrMergeDeck(newDeck);

        assertEquals(2, model.getDecks().size());
        verify(persistence).saveDecks(anyList());
    }

    @Test
    void testAddOrMergeDeck_Merge() {
        Model model = new Model(persistence);
        Deck incoming = new Deck("TestDeck", "Desc");
        incoming.addCard(new Card("New Q", "New A"));

        model.addOrMergeDeck(incoming);

        assertEquals(1, model.getDecks().size());
        assertEquals(2, model.getDecks().get(0).getCards().size());
        verify(persistence).saveDecks(anyList());
    }

    @Test
    void testAddDeck() {
        Model model = new Model(persistence);
        Deck deck = new Deck("Brand New", "Desc");
        model.addDeck(deck);

        assertEquals(2, model.getDecks().size());
        verify(persistence).saveDecks(anyList());
    }

    @Test
    void testGetPersistence() {
        Model model = new Model(persistence);
        assertEquals(persistence, model.getPersistence());
    }

    @Test
    void testDefaultConstructor() {
        // This will attempt to load from decks.json
        Model model = new Model();
        assertNotNull(model.getPersistence());
        assertNotNull(model.getDecks());
    }

    // ── Search Tests ───────────────────────────────────────────────────────

    @Test
    void testSearchOneCharMatchesDeck() {
        initialDecks.add(new Deck("Mathe", "Mathematics"));
        initialDecks.get(1).addCard(new Card("1+1", "2"));
        Model model = new Model(persistence);

        // "M" should match "Mathe" deck and return its cards
        List<Card> results = model.searchCards("M");
        assertEquals(1, results.size());
        assertEquals("1+1", results.get(0).getQuestion());
    }

    @Test
    void testSearchShortQueryMatchesCardContent() {
        Model model = new Model(persistence);
        // "Q1" is length 2, matches card question "Q1"
        List<Card> results = model.searchCards("Q1");
        assertEquals(1, results.size());
    }

    @Test
    void testSearchLongerQueryMatchesDeck() {
        Model model = new Model(persistence);
        // "Test" matches "TestDeck" (length >= 3)
        List<Card> results = model.searchCards("Test");
        assertEquals(1, results.size()); // all cards in deck
    }

    @Test
    void testSearchExactDeckMatch() {
        Model model = new Model(persistence);
        List<Card> results = model.searchCards("TestDeck");
        assertEquals(1, results.size());
    }

    @Test
    void testSearchNullOrEmptyQuery() {
        Model model = new Model(persistence);
        assertTrue(model.searchCards(null).isEmpty());
        assertTrue(model.searchCards("").isEmpty());
        assertTrue(model.searchCards("   ").isEmpty());
    }

    @Test
    void testSearchMatchesDescription() {
        Model model = new Model(persistence);
        // "Desc" is in deck description
        List<Card> results = model.searchCards("Desc");
        assertEquals(1, results.size());
    }

    @Test
    void testSearchMatchesAnswer() {
        Model model = new Model(persistence);
        // "A1" is in card1's answer
        List<Card> results = model.searchCards("A1");
        assertEquals(1, results.size());
    }

    @Test
    void testSearchMatchesTags() {
        Model model = new Model(persistence);
        Card card3 = new Card("Q3", "A3");
        List<String> tags = new ArrayList<>();
        tags.add("Important");
        card3.setTags(tags);
        initialDecks.get(0).addCard(card3);

        List<Card> results = model.searchCards("Import");
        assertEquals(1, results.size());
        assertEquals("Q3", results.get(0).getQuestion());
    }

    @Test
    void testSearchRobustnessWithNullFields() {
        Deck nullDeck = new Deck();
        nullDeck.setName(null);
        nullDeck.setDescription(null);
        Card nullCard = new Card();
        nullCard.setQuestion(null);
        nullCard.setAnswer(null);
        nullCard.setTags(null);
        nullDeck.addCard(nullCard);

        List<Deck> decks = new ArrayList<>();
        decks.add(nullDeck);
        Persistence emptyPersistence = mock(Persistence.class);
        when(emptyPersistence.loadDecks()).thenReturn(decks);
        Model m = new Model(emptyPersistence);

        assertDoesNotThrow(() -> m.searchCards("any"));
        assertTrue(m.searchCards("any").isEmpty());
    }

    @Test
    void testSearchExactMatchCaseInsensitive() {
        Model model = new Model(persistence);
        // Search for "testdeck" (lowercase) - should match "TestDeck" exactly
        List<Card> results = model.searchCards("testdeck");
        assertEquals(1, results.size());
    }

    @Test
    void testStreakCalculationEmpty() {
        Model model = new Model(persistence);
        assertEquals(0, model.calculateCurrentStreak());
    }

    @Test
    void testStreakCalculationActiveToday() {
        Model model = new Model(persistence);
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        model.addStreakDate(today.format(formatter));
        model.addStreakDate(today.minusDays(1).format(formatter));
        model.addStreakDate(today.minusDays(2).format(formatter));

        assertEquals(3, model.calculateCurrentStreak());
    }

    @Test
    void testStreakCalculationActiveYesterday() {
        Model model = new Model(persistence);
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Did not study today, but studied yesterday and day before
        model.addStreakDate(today.minusDays(1).format(formatter));
        model.addStreakDate(today.minusDays(2).format(formatter));

        assertEquals(2, model.calculateCurrentStreak());
    }

    @Test
    void testStreakCalculationBroken() {
        Model model = new Model(persistence);
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Studied today, but skipped yesterday, studied day before yesterday
        model.addStreakDate(today.format(formatter));
        model.addStreakDate(today.minusDays(2).format(formatter));

        assertEquals(1, model.calculateCurrentStreak());
    }

    @Test
    void testRecordDeckStudied() {
        Model model = new Model(persistence);
        String deckId = "test-deck-id";
        model.recordDeckStudied(deckId);

        // Verify saveStreakData was called with streakData updated
        verify(persistence).saveStreakData(any(StreakData.class));

        // Verify today's date was added to streak dates
        String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertTrue(model.getStreakDates().contains(todayStr));
    }

    @Test
    void testSyncDecksWithStreakData() {
        StreakData streakData = new StreakData();
        String deckId = "TestDeckId";
        initialDecks.get(0).setId(deckId);
        streakData.getRecentDecks().put(deckId, "2026-06-11T12:00:00");

        Persistence mockPersistence = mock(Persistence.class);
        when(mockPersistence.loadDecks()).thenReturn(initialDecks);
        when(mockPersistence.loadStreakData()).thenReturn(streakData);

        Model model = new Model(mockPersistence);
        Deck deck = model.getDecks().get(0);
        assertNotNull(deck.getLastStudied());
        assertEquals(java.time.LocalDateTime.parse("2026-06-11T12:00:00"), deck.getLastStudied());
    }
}
