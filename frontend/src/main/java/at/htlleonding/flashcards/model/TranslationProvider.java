package at.htlleonding.flashcards.model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.io.*;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class TranslationProvider {
    private static final String BUNDLE_BASE_NAME = "at.htlleonding.flashcards.messages";
    private static final String SETTINGS_FILE = "settings.properties";
    private static final String LANGUAGE_KEY = "language";
    
    private static final ObjectProperty<Locale> locale = new SimpleObjectProperty<>();
    private static ResourceBundle bundle;

    static {
        Locale initialLocale = loadSavedLocale();
        locale.set(initialLocale);
        try {
            bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, initialLocale);
            System.out.println("DEBUG: static init -> Initial: " + initialLocale + 
                               ", Loaded Bundle Locale: " + (bundle != null ? bundle.getLocale() : "null") + 
                               ", settings.title: " + (bundle != null && bundle.containsKey("settings.title") ? bundle.getString("settings.title") : "not found"));
        } catch (Exception e) {
            System.err.println("Could not load ResourceBundle: " + e.getMessage());
        }
    }

    public static ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    public static Locale getLocale() {
        return locale.get();
    }

    public static void setLocale(Locale newLocale) {
        if (newLocale == null) {
            newLocale = Locale.ENGLISH;
        }
        locale.set(newLocale);
        try {
            bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, newLocale);
            System.out.println("DEBUG: setLocale -> Requested: " + newLocale + 
                               ", Loaded Bundle Locale: " + (bundle != null ? bundle.getLocale() : "null") + 
                               ", settings.title: " + (bundle != null && bundle.containsKey("settings.title") ? bundle.getString("settings.title") : "not found"));
        } catch (Exception e) {
            System.err.println("Could not change ResourceBundle locale: " + e.getMessage());
        }
        saveLocale(newLocale);
    }

    public static String get(String key, Object... args) {
        if (bundle == null || !bundle.containsKey(key)) {
            return "[" + key + "]";
        }
        String pattern = bundle.getString(key);
        if (args == null || args.length == 0) {
            return pattern;
        }
        try {
            return MessageFormat.format(pattern, args);
        } catch (IllegalArgumentException e) {
            return pattern;
        }
    }

    public static StringBinding createStringBinding(String key, Object... args) {
        return Bindings.createStringBinding(() -> get(key, args), locale);
    }

    private static Locale loadSavedLocale() {
        File file = new File(SETTINGS_FILE);
        if (file.exists()) {
            Properties props = new Properties();
            try (InputStream in = new FileInputStream(file)) {
                props.load(in);
                String lang = props.getProperty(LANGUAGE_KEY);
                if (lang != null && !lang.trim().isEmpty()) {
                    return Locale.forLanguageTag(lang);
                }
            } catch (IOException e) {
                System.err.println("Error loading settings: " + e.getMessage());
            }
        }
        // Fallback to system locale if de, otherwise en
        Locale system = Locale.getDefault();
        if ("de".equals(system.getLanguage())) {
            return Locale.GERMAN;
        }
        return Locale.ENGLISH;
    }

    private static void saveLocale(Locale loc) {
        Properties props = new Properties();
        props.setProperty(LANGUAGE_KEY, loc.toLanguageTag());
        try (OutputStream out = new FileOutputStream(SETTINGS_FILE)) {
            props.store(out, "Application Settings");
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }
    }
}
