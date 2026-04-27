package at.htlleonding.flashcards.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class CardTest {
    private Card card;

    @BeforeEach
    void setUp() {
        card = new Card("Question 1", "Answer 1");
    }

    @Test
    void testCardCreation() {
        assertEquals("Question 1", card.getQuestion());
        assertEquals("Answer 1", card.getAnswer());
        assertNotNull(card.getId());
        assertTrue(card.getTags().isEmpty());
    }

    @Test
    void testCardCreationWithInvalidData() {
        assertThrows(IllegalArgumentException.class, () -> new Card("", "Answer"));
        assertThrows(IllegalArgumentException.class, () -> new Card("Question", null));
    }

    @Test
    void testTags() {
        card.setTags(List.of("Tag1", "Tag2"));
        assertEquals(2, card.getTags().size());
        assertTrue(card.getTags().contains("Tag1"));
    }

    @Test
    void testSetters() {
        card.setQuestion("New Question");
        card.setAnswer("New Answer");
        assertEquals("New Question", card.getQuestion());
        assertEquals("New Answer", card.getAnswer());
    }
}
