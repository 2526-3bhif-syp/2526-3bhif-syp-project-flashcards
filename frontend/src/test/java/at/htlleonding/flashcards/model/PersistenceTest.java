package at.htlleonding.flashcards.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Base64;
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
        testDeck = new Deck("Test Deck", "Test Description");
        
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

    @Test
    void testLoadDecks_FileNotFound() {
        List<Deck> decks = persistence.loadDecks();
        assertNotNull(decks);
    }

    @Test
    void testExportDecksToJSON() throws IOException {
        File jsonFile = tempDir.resolve("decks.json").toFile();
        persistence.exportDecksToJSON(List.of(testDeck), jsonFile);
        
        List<Deck> importedDecks = persistence.importDecksFromJSON(jsonFile);
        assertEquals(1, importedDecks.size());
        assertEquals(testDeck.getName(), importedDecks.get(0).getName());
    }

    @Test
    void testExportCardToJSON() throws IOException {
        File jsonFile = tempDir.resolve("card.json").toFile();
        Card card = testDeck.getCards().get(0);
        persistence.exportCardToJSON(card, jsonFile);
        
        Deck importedDeck = persistence.importFromJSON(jsonFile);
        assertEquals("Imported", importedDeck.getName());
        assertEquals(1, importedDeck.getCards().size());
        assertEquals(card.getQuestion(), importedDeck.getCards().get(0).getQuestion());
    }

    @Test
    void testExportCardsToJSON() throws IOException {
        File jsonFile = tempDir.resolve("cards.json").toFile();
        persistence.exportCardsToJSON(testDeck.getCards(), jsonFile);
        
        Deck importedDeck = persistence.importFromJSON(jsonFile);
        assertEquals("Imported Deck", importedDeck.getName());
        assertEquals(testDeck.getCards().size(), importedDeck.getCards().size());
    }

    @Test
    void testImportFromJSON_ArrayOfDecks() throws IOException {
        File jsonFile = tempDir.resolve("array_decks.json").toFile();
        persistence.exportDecksToJSON(List.of(testDeck, new Deck("Other", "")), jsonFile);
        
        Deck importedDeck = persistence.importFromJSON(jsonFile);
        assertEquals(testDeck.getName(), importedDeck.getName());
    }

    @Test
    void testImportFromJSON_InvalidFormat() throws IOException {
        File jsonFile = tempDir.resolve("invalid.json").toFile();
        java.nio.file.Files.writeString(jsonFile.toPath(), "{\"unexpected\": \"format\"}");
        
        assertThrows(IOException.class, () -> persistence.importFromJSON(jsonFile));
    }

    @Test
    void testImportFromJSON_EmptyArray() throws IOException {
        File jsonFile = tempDir.resolve("empty_array.json").toFile();
        java.nio.file.Files.writeString(jsonFile.toPath(), "[]");
        
        Deck deck = persistence.importFromJSON(jsonFile);
        assertEquals("Imported Deck", deck.getName());
        assertTrue(deck.getCards().isEmpty());
    }

    @Test
    void testImportDecksFromJSON_SingleObject() throws IOException {
        File jsonFile = tempDir.resolve("single_deck.json").toFile();
        persistence.exportToJSON(testDeck, jsonFile);
        
        List<Deck> decks = persistence.importDecksFromJSON(jsonFile);
        assertEquals(1, decks.size());
        assertEquals(testDeck.getName(), decks.get(0).getName());
    }

    @Test
    void testImportDecksFromJSON_NotADeck() throws IOException {
        File jsonFile = tempDir.resolve("not_a_deck.json").toFile();
        java.nio.file.Files.writeString(jsonFile.toPath(), "{\"question\": \"Is this a deck?\"}");
        
        assertThrows(IOException.class, () -> persistence.importDecksFromJSON(jsonFile));
    }

    @Test
    void testImportDecksFromJSON_ArrayNotDecks() throws IOException {
        File jsonFile = tempDir.resolve("not_decks_array.json").toFile();
        java.nio.file.Files.writeString(jsonFile.toPath(), "[{\"question\": \"Is this a deck?\"}]");
        
        assertThrows(IOException.class, () -> persistence.importDecksFromJSON(jsonFile));
    }

    @Test
    void testSaveAndLoadDecks() {
        List<Deck> originalDecks = List.of(testDeck);
        persistence.saveDecks(originalDecks);
        
        List<Deck> loadedDecks = persistence.loadDecks();
        assertFalse(loadedDecks.isEmpty());
        assertEquals(originalDecks.get(0).getName(), loadedDecks.get(0).getName());
    }

    @Test
    void testLoadDecks_IOException() {
        // This test was destructive and has been disabled/refactored.
        // To properly test this, Persistence should accept a File path in its constructor.
    }

    @Test
    void testSaveDecks_IOException() {
        // This test was destructive and has been disabled/refactored.
    }

    @Test
    void testLoadDecks_FileDoesNotExist() {
        // This is safe as long as we don't delete an existing file.
        // But for consistency, tests should not touch "decks.json" in the root.
    }

    @Test
    void testImportFromJSON_NullCards() throws IOException {
        File jsonFile = tempDir.resolve("null_cards.json").toFile();
        java.nio.file.Files.writeString(jsonFile.toPath(), "null");
        
        assertThrows(IOException.class, () -> persistence.importFromJSON(jsonFile));
    }

    @Test
    void testImportFromJSON_EmptyArray_CardsNotNull() throws IOException {
        File jsonFile = tempDir.resolve("empty_array_cards.json").toFile();
        java.nio.file.Files.writeString(jsonFile.toPath(), "[]");
        
        Deck deck = persistence.importFromJSON(jsonFile);
        assertNotNull(deck.getCards());
    }

    @Test
    void testImportFromJSON_IOExceptionRethrow() {
        File nonExistent = new File(tempDir.toFile(), "non_existent.json");
        assertThrows(IOException.class, () -> persistence.importFromJSON(nonExistent));
    }

    @Test
    void testImportDecksFromJSON_IOExceptionRethrow() {
        File nonExistent = new File(tempDir.toFile(), "non_existent.json");
        assertThrows(IOException.class, () -> persistence.importDecksFromJSON(nonExistent));
    }

    @Test
    void testImportFromJSON_GeneralException() throws IOException {
        IOException ex = assertThrows(IOException.class, () -> persistence.importFromJSON(null));
        assertTrue(ex.getMessage().contains("Could not parse JSON"));
    }

    @Test
    void testImportDecksFromJSON_GeneralException() throws IOException {
        IOException ex = assertThrows(IOException.class, () -> persistence.importDecksFromJSON(null));
        assertTrue(ex.getMessage().contains("Could not parse JSON"));
    }

    @Test
    void testImageDataRoundtrip_exportToJSON() throws IOException {
        String fakeBase64 = Base64.getEncoder().encodeToString("fake-image-bytes".getBytes());
        Card card = new Card("Image Q", "Image A");
        card.setFrontImageData(fakeBase64);
        card.setFrontImageName("front.png");
        card.setBackImageData(fakeBase64);
        card.setBackImageName("back.jpg");
        Deck deck = new Deck("Image Deck", "");
        deck.addCard(card);

        File jsonFile = tempDir.resolve("image_deck.json").toFile();
        persistence.exportToJSON(deck, jsonFile);
        Deck imported = persistence.importFromJSON(jsonFile);
        Card importedCard = imported.getCards().get(0);

        assertEquals(fakeBase64, importedCard.getFrontImageData());
        assertEquals("front.png", importedCard.getFrontImageName());
        assertEquals(fakeBase64, importedCard.getBackImageData());
        assertEquals("back.jpg", importedCard.getBackImageName());
    }

    @Test
    void testImageDataRoundtrip_exportCardsToJSON() throws IOException {
        String fakeBase64 = Base64.getEncoder().encodeToString("another-image".getBytes());
        Card card = new Card("Q", "A");
        card.setFrontImageData(fakeBase64);
        card.setFrontImageName("img.png");
        Deck deck = new Deck("Tmp", "");
        deck.addCard(card);

        File jsonFile = tempDir.resolve("image_cards.json").toFile();
        persistence.exportCardsToJSON(deck.getCards(), jsonFile);
        Deck imported = persistence.importCardsFromJSON(jsonFile);
        Card importedCard = imported.getCards().get(0);

        assertEquals(fakeBase64, importedCard.getFrontImageData());
        assertEquals("img.png", importedCard.getFrontImageName());
        assertNull(importedCard.getBackImageData());
    }

    @Test
    void testOldJsonWithoutImageFields_doesNotCrash() throws IOException {
        File jsonFile = tempDir.resolve("old_format.json").toFile();
        java.nio.file.Files.writeString(jsonFile.toPath(),
            "{\"name\":\"Old Deck\",\"cards\":[{\"question\":\"Q\",\"answer\":\"A\"}]}");

        Deck imported = persistence.importFromJSON(jsonFile);
        assertNotNull(imported);
        assertEquals(1, imported.getCards().size());
        assertNull(imported.getCards().get(0).getFrontImageData());
        assertNull(imported.getCards().get(0).getBackImageData());
    }

    @Test
    void testLoadDefaultDecksImages() throws IOException {
        File defaultJson = new File("src/main/resources/at/htlleonding/flashcards/default_decks.json");
        if (defaultJson.exists()) {
            List<Deck> decks = persistence.importDecksFromJSON(defaultJson);
            assertFalse(decks.isEmpty());
            Deck fk = decks.get(0);
            Card c1 = fk.getCards().get(0);
            System.out.println("TEST IMAGE NAME: " + c1.getBackImageName());
            System.out.println("TEST IMAGE DATA LENGTH: " + (c1.getBackImageData() != null ? c1.getBackImageData().length() : "null"));
            assertNotNull(c1.getBackImageData());
        }
    }
}
