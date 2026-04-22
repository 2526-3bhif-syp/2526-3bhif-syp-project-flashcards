package at.htlleonding.flashcards.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceTest {

    private Persistence persistence;
    private Deck testDeck;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        persistence = new Persistence();
        testDeck = new Deck("Test Deck");
        
        Card normalCard = new Card("Frage", "Antwort");
        normalCard.setTags(List.of("Tag1", "Tag2"));
        
        Card specialCharCard = new Card("Frage mit , Komma", "Antwort mit \" Anführungszeichen");
        specialCharCard.setTags(List.of("Special"));
        
        testDeck.addCard(normalCard);
        testDeck.addCard(specialCharCard);
    }

    @Test
    void testJSONExportAndImport() throws IOException {
        File jsonFile = tempDir.resolve("test.json").toFile();
        persistence.exportToJSON(testDeck, jsonFile);

        Deck importedDeck = persistence.importFromJSON(jsonFile);
        
        assertEquals(testDeck.getName(), importedDeck.getName());
        assertEquals(testDeck.getCards().size(), importedDeck.getCards().size());
        assertEquals(testDeck.getCards().get(1).getAnswer(), importedDeck.getCards().get(1).getAnswer());
    }
}
