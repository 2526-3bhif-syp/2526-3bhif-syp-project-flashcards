package at.htlleonding.flashcards.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        initialDecks.add(new Deck("TestDeck", "Desc"));
        // Standardverhalten für die meisten Tests: Persistenz liefert ein Deck
        lenient().when(persistence.loadDecks()).thenReturn(initialDecks);
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
        assertEquals(1, model.getDecks().get(0).getCards().size());
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
        assertEquals(1, model.getDecks().get(0).getCards().size());
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
}
