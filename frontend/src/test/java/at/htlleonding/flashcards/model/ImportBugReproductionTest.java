package at.htlleonding.flashcards.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

class ImportBugReproductionTest {
    @TempDir
    Path tempDir;

    @Test
    void reproduceAndFixNullQuestionException() throws IOException {
        Persistence persistence = new Persistence();
        File jsonFile = tempDir.resolve("bad_card.json").toFile();
        
        // JSON with a card missing the question field
        try (FileWriter fw = new FileWriter(jsonFile)) {
            fw.write("{\"name\":\"Test Deck\",\"cards\":[{\"answer\":\"Only Answer\"}]}");
        }

        Deck importedDeck = persistence.importFromJSON(jsonFile);
        assertNotNull(importedDeck);
        assertEquals(1, importedDeck.getCards().size());
        
        Card card = importedDeck.getCards().get(0);
        assertNull(card.getQuestion()); // Question is still null because it's missing in JSON
        
        // The fix is in the filtering logic in MainPresenter, but we also ensured 
        // that persistence itself doesn't crash.
    }

    @Test
    void testDirectCardListImport() throws IOException {
        Persistence persistence = new Persistence();
        File jsonFile = tempDir.resolve("cards_only.json").toFile();
        
        // JSON with an array of cards directly
        try (FileWriter fw = new FileWriter(jsonFile)) {
            fw.write("[{\"question\":\"Q1\",\"answer\":\"A1\"},{\"question\":\"Q2\",\"answer\":\"A2\"}]");
        }

        Deck importedDeck = persistence.importFromJSON(jsonFile);
        assertNotNull(importedDeck);
        assertEquals("Imported Deck", importedDeck.getName());
        assertEquals(2, importedDeck.getCards().size());
        assertEquals("Q1", importedDeck.getCards().get(0).getQuestion());
    }

    @Test
    void testUnknownFieldsAreIgnored() throws IOException {
        Persistence persistence = new Persistence();
        File jsonFile = tempDir.resolve("unknown_fields.json").toFile();
        
        // JSON with unknown fields
        try (FileWriter fw = new FileWriter(jsonFile)) {
            fw.write("{\"name\":\"Test Deck\",\"version\":\"1.0\",\"cards\":[{\"question\":\"Q1\",\"answer\":\"A1\",\"extra\":\"data\"}]}");
        }

        Deck importedDeck = persistence.importFromJSON(jsonFile);
        assertNotNull(importedDeck);
        assertEquals(1, importedDeck.getCards().size());
    }
}
