package at.htlleonding.flashcards.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        assertThrows(IllegalArgumentException.class, () -> new Card(null, "Answer"));
        assertThrows(IllegalArgumentException.class, () -> new Card("Question", "   "));
    }

    @Test
    void testEmptyConstructor() {
        Card emptyCard = new Card();
        assertNotNull(emptyCard.getId());
        assertNotNull(emptyCard.getTags());
        assertTrue(emptyCard.getTags().isEmpty());
    }

    @Test
    void testIdManagement() {
        String newId = UUID.randomUUID().toString();
        card.setId(newId);
        assertEquals(newId, card.getId());
        
        Card lazyCard = new Card();
        lazyCard.setId(null);
        assertNotNull(lazyCard.getId(), "getId should generate a new UUID if current id is null");
    }

    @Test
    void testTags() {
        List<String> tags = new ArrayList<>();
        tags.add("Tag1");
        tags.add("Tag2");
        card.setTags(tags);
        assertEquals(2, card.getTags().size());
        assertTrue(card.getTags().contains("Tag1"));
        assertTrue(card.getTags().contains("Tag2"));
        assertEquals(tags, card.getTags());

        // Test modification
        card.getTags().add("Tag3");
        assertEquals(3, card.getTags().size());
        assertTrue(card.getTags().contains("Tag3"));

        // Test clearing
        card.setTags(new ArrayList<>());
        assertTrue(card.getTags().isEmpty());
    }

    @Test
    void testSetters() {
        card.setQuestion("New Question");
        card.setAnswer("New Answer");
        assertEquals("New Question", card.getQuestion());
        assertEquals("New Answer", card.getAnswer());
    }

    @Test
    void testFrontAudioFields() {
        String audioData = "any-string-as-base64";
        String audioName = "front.mp3";
        String audioDuration = "00:30";

        card.setFrontAudioData(audioData);
        card.setFrontAudioName(audioName);
        card.setFrontAudioDuration(audioDuration);

        assertEquals(audioData, card.getFrontAudioData());
        assertEquals(audioName, card.getFrontAudioName());
        assertEquals(audioDuration, card.getFrontAudioDuration());
    }

    @Test
    void testBackAudioFields() {
        String audioData = "any-string-as-base64-back";
        String audioName = "back.mp3";
        String audioDuration = "01:15";

        card.setBackAudioData(audioData);
        card.setBackAudioName(audioName);
        card.setBackAudioDuration(audioDuration);

        assertEquals(audioData, card.getBackAudioData());
        assertEquals(audioName, card.getBackAudioName());
        assertEquals(audioDuration, card.getBackAudioDuration());
    }
}
