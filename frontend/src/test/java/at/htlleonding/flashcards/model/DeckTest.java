package at.htlleonding.flashcards.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class DeckTest {
    private Deck deck;
    private Card card1;
    private Card card2;

    @BeforeEach
    void setUp() {
        deck = new Deck("Test Deck", "Test Description");
        card1 = new Card("Q1", "A1");
        card2 = new Card("Q2", "A2");
    }

    @Test
    void testDeckCreation() {
        assertEquals("Test Deck", deck.getName());
        assertEquals("Test Description", deck.getDescription());
        assertEquals(0, deck.getCardCount());
    }

    @Test
    void testAddCard() {
        deck.addCard(card1);
        assertEquals(1, deck.getCardCount());
        assertEquals(card1.getQuestion(), deck.getCards().get(0).getQuestion());
    }

    @Test
    void testRemoveCard() {
        deck.addCard(card1);
        deck.removeCard(card1);
        assertEquals(0, deck.getCardCount());
    }

    @Test
    void testUpdateCard() {
        deck.addCard(card1);
        Card updatedCard = new Card("Updated Q1", "Updated A1");
        updatedCard.setId(card1.getId());
        
        deck.updateCard(updatedCard);
        
        assertEquals(1, deck.getCardCount());
        assertEquals("Updated Q1", deck.getCards().get(0).getQuestion());
    }

    @Test
    void testSetCards() {
        deck.setCards(List.of(card1, card2));
        assertEquals(2, deck.getCardCount());
    }

    @Test
    void testInvalidDeckName() {
        assertThrows(IllegalArgumentException.class, () -> new Deck("", "desc"));
        assertThrows(IllegalArgumentException.class, () -> new Deck(null, "desc"));
    }
}
