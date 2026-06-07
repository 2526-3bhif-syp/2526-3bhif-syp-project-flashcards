package at.htlleonding.flashcards.model;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.*;

class TranslationProviderTest {
    private static final String SETTINGS_FILE = "settings.properties";
    private static File backupFile;

    @BeforeAll
    static void backupSettings() {
        File file = new File(SETTINGS_FILE);
        if (file.exists()) {
            backupFile = new File(SETTINGS_FILE + ".bak");
            file.renameTo(backupFile);
        }
    }

    @AfterAll
    static void restoreSettings() {
        File file = new File(SETTINGS_FILE);
        if (file.exists()) {
            file.delete();
        }
        if (backupFile != null && backupFile.exists()) {
            backupFile.renameTo(new File(SETTINGS_FILE));
        }
    }

    @BeforeEach
    void setUp() {
        TranslationProvider.setLocale(Locale.ENGLISH);
    }

    @Test
    void testEnglishTranslation() {
        assertEquals("Search", TranslationProvider.get("navbar.search_prompt"));
        assertEquals("Home", TranslationProvider.get("sidebar.home"));
    }

    @Test
    void testGermanTranslation() {
        TranslationProvider.setLocale(Locale.GERMAN);
        assertEquals("Suchen", TranslationProvider.get("navbar.search_prompt"));
        assertEquals("Startseite", TranslationProvider.get("sidebar.home"));
    }

    @Test
    void testFallbackTranslation() {
        assertEquals("[non.existent.key]", TranslationProvider.get("non.existent.key"));
    }

    @Test
    void testFormattedTranslation() {
        assertEquals("0 cards", TranslationProvider.get("home.cards", 0));
        assertEquals("1 card", TranslationProvider.get("home.cards", 1));
        assertEquals("5 cards", TranslationProvider.get("home.cards", 5));
        
        TranslationProvider.setLocale(Locale.GERMAN);
        assertEquals("0 Karten", TranslationProvider.get("home.cards", 0));
        assertEquals("1 Karte", TranslationProvider.get("home.cards", 1));
        assertEquals("5 Karten", TranslationProvider.get("home.cards", 5));
    }
}
