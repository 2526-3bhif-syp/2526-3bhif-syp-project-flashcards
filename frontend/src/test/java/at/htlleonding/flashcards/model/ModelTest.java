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
        initialDecks.add(new Deck("TestDeck"));
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
        Deck updatedDeck = new Deck("TestDeck");
        updatedDeck.addCard(new Card("Q", "A"));

        model.updateDeck(updatedDeck);

        // Prüfe ob saveDecks aufgerufen wurde
        verify(persistence).saveDecks(anyList());
        assertEquals(1, model.getDecks().size());
        assertEquals(1, model.getDecks().get(0).getCards().size());
    }

    @Test
    void testUpdateDeckThrowsExceptionWhenNotFound() {
        Model model = new Model(persistence);
        Deck nonExistentDeck = new Deck("Unknown");

        assertThrows(IllegalArgumentException.class, () -> {
            model.updateDeck(nonExistentDeck);
        });
        
        // Save sollte niemals aufgerufen werden wenn das Deck nicht existiert
        verify(persistence, times(0)).saveDecks(anyList());
    }

    @Test
    void testInitializationWithDummyData() {
        // Separater Mock für den Fall dass die Persistenz leer ist
        Persistence emptyPersistence = mock(Persistence.class);
        when(emptyPersistence.loadDecks()).thenReturn(new ArrayList<>());

        Model model = new Model(emptyPersistence);

        assertEquals(1, model.getDecks().size());
        assertEquals("English", model.getDecks().get(0).getName());
        // Dummy Daten sollten direkt gespeichert werden
        verify(emptyPersistence).saveDecks(anyList());
    }
}
