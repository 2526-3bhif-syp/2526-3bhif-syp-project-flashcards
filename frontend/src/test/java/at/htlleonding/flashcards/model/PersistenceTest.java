package at.htlleonding.flashcards.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
    void testCSVExportWithSpecialChars() throws IOException {
        File csvFile = tempDir.resolve("test.csv").toFile();
        persistence.exportToCSV(testDeck, csvFile);

        List<String> lines = Files.readAllLines(csvFile.toPath());
        
        // Header + 2 Karten
        assertEquals(3, lines.size());
        assertEquals("Question,Answer,Tags", lines.get(0));
        
        // Normale Karte
        assertTrue(lines.get(1).contains("Frage,Antwort,Tag1;Tag2"));
        
        // Karte mit Sonderzeichen (muss in Anführungszeichen stehen)
        // Das erwartete Format für "Antwort mit \" Anführungszeichen" ist "Antwort mit "" Anführungszeichen" (doppelte Quotes)
        assertTrue(lines.get(2).contains("\"Frage mit , Komma\""));
        assertTrue(lines.get(2).contains("\"Antwort mit \"\" Anführungszeichen\""));
    }

    @Test
    void testCSVImportAfterExport() throws IOException {
        File csvFile = tempDir.resolve("export_import.csv").toFile();
        persistence.exportToCSV(testDeck, csvFile);

        Deck importedDeck = persistence.importFromCSV(csvFile);
        
        assertEquals(2, importedDeck.getCards().size());
        
        Card special = importedDeck.getCards().get(1);
        assertEquals("Frage mit , Komma", special.getQuestion());
        assertEquals("Antwort mit \" Anführungszeichen", special.getAnswer());
        assertEquals(1, special.getTags().size());
        assertEquals("Special", special.getTags().get(0));
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
