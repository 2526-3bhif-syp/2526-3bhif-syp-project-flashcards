package at.htlleonding.flashcards.model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.io.*;
import java.util.*;

public class ThemeProvider {
    private static final String SETTINGS_FILE = "settings.properties";
    private static final String THEME_KEY = "theme";

    private static final ObjectProperty<String> currentTheme = new SimpleObjectProperty<>("light");
    private static final Map<String, Map<String, String>> themes = new LinkedHashMap<>();
    private static final List<Runnable> listeners = new ArrayList<>();

    static {
        themes.put("light", Map.ofEntries(
            Map.entry("bg-primary",       "#f8f9fa"),
            Map.entry("bg-secondary",     "#f5f5f5"),
            Map.entry("bg-card",          "#ffffff"),
            Map.entry("bg-hover",         "#eeeeee"),
            Map.entry("bg-active",        "#e0e0e0"),
            Map.entry("text-primary",     "#333333"),
            Map.entry("text-secondary",   "#555555"),
            Map.entry("text-muted",       "#666666"),
            Map.entry("text-subtle",      "#757575"),
            Map.entry("text-disabled",    "#999999"),
            Map.entry("text-placeholder", "#aaaaaa"),
            Map.entry("text-hint",        "#bbbbbb"),
            Map.entry("border-default",   "#cccccc"),
            Map.entry("border-light",     "#e0e0e0"),
            Map.entry("border-muted",     "#BDBDBD"),
            Map.entry("accent-blue",      "#2196F3"),
            Map.entry("accent-blue-hover","#1976D2"),
            Map.entry("accent-blue-active","#1565C0"),
            Map.entry("accent-blue-strong","#0D47A1"),
            Map.entry("accent-blue-light", "#90CAF9"),
            Map.entry("accent-blue-bg",   "#E3F2FD"),
            Map.entry("accent-green",     "#4CAF50"),
            Map.entry("accent-green-strong","#2E7D32"),
            Map.entry("accent-green-dark", "#1B5E20"),
            Map.entry("accent-green-light","#A5D6A7"),
            Map.entry("accent-green-bg",  "#E8F5E9"),
            Map.entry("accent-orange",    "#FF9800"),
            Map.entry("accent-orange-hover","#F57C00"),
            Map.entry("accent-orange-active","#e65100"),
            Map.entry("accent-red",       "#dc3545"),
            Map.entry("accent-red-hover", "#b02a37"),
            Map.entry("accent-red-strong","#D32F2F"),
            Map.entry("neutral-gray",     "#607D8B"),
            Map.entry("neutral-gray-dark","#455A64"),
            Map.entry("accent-link",      "#007bff"),
            Map.entry("fg-black",         "#000000"),
            Map.entry("fg-dark",          "#212121"),
            Map.entry("text-on-primary",  "#ffffff"),
            Map.entry("text-on-accent",   "#ffffff")
        ));

        themes.put("dark", Map.ofEntries(
            Map.entry("bg-primary",       "#121212"),
            Map.entry("bg-secondary",     "#1e1e2e"),
            Map.entry("bg-card",          "#2a2a3e"),
            Map.entry("bg-hover",         "#353550"),
            Map.entry("bg-active",        "#404060"),
            Map.entry("text-primary",     "#e0e0e0"),
            Map.entry("text-secondary",   "#b0b0b0"),
            Map.entry("text-muted",       "#909090"),
            Map.entry("text-subtle",      "#808080"),
            Map.entry("text-disabled",    "#606060"),
            Map.entry("text-placeholder", "#505050"),
            Map.entry("text-hint",        "#444444"),
            Map.entry("border-default",   "#444444"),
            Map.entry("border-light",     "#3a3a3a"),
            Map.entry("border-muted",     "#555555"),
            Map.entry("accent-blue",      "#64B5F6"),
            Map.entry("accent-blue-hover","#42A5F5"),
            Map.entry("accent-blue-active","#2196F3"),
            Map.entry("accent-blue-strong","#1565C0"),
            Map.entry("accent-blue-light", "#90CAF9"),
            Map.entry("accent-blue-bg",   "#1a237e"),
            Map.entry("accent-green",     "#81C784"),
            Map.entry("accent-green-strong","#66BB6A"),
            Map.entry("accent-green-dark", "#4CAF50"),
            Map.entry("accent-green-light","#A5D6A7"),
            Map.entry("accent-green-bg",  "#1b5e20"),
            Map.entry("accent-orange",    "#FFB74D"),
            Map.entry("accent-orange-hover","#FFA726"),
            Map.entry("accent-orange-active","#F57C00"),
            Map.entry("accent-red",       "#E57373"),
            Map.entry("accent-red-hover", "#EF5350"),
            Map.entry("accent-red-strong","#F44336"),
            Map.entry("neutral-gray",     "#90A4AE"),
            Map.entry("neutral-gray-dark","#78909C"),
            Map.entry("accent-link",      "#64B5F6"),
            Map.entry("fg-black",         "#e0e0e0"),
            Map.entry("fg-dark",          "#e0e0e0"),
            Map.entry("text-on-primary",  "#121212"),
            Map.entry("text-on-accent",   "#000000")
        ));

        String saved = loadSavedTheme();
        if (saved != null && themes.containsKey(saved)) {
            currentTheme.set(saved);
        }
    }

    public static ObjectProperty<String> themeProperty() {
        return currentTheme;
    }

    public static String getTheme() {
        return currentTheme.get();
    }

    public static void setTheme(String theme) {
        if (theme == null || !themes.containsKey(theme)) {
            theme = "light";
        }
        currentTheme.set(theme);
        saveTheme(theme);
        notifyListeners();
    }

    public static String get(String token) {
        Map<String, String> themeColors = themes.get(currentTheme.get());
        if (themeColors == null || !themeColors.containsKey(token)) {
            return "#000000";
        }
        return themeColors.get(token);
    }

    public static StringBinding createColorBinding(String token) {
        return Bindings.createStringBinding(() -> get(token), currentTheme);
    }

    public static void addThemeListener(Runnable callback) {
        listeners.add(callback);
    }

    public static void removeThemeListener(Runnable callback) {
        listeners.remove(callback);
    }

    private static void notifyListeners() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    private static String loadSavedTheme() {
        File file = new File(SETTINGS_FILE);
        if (file.exists()) {
            Properties props = new Properties();
            try (InputStream in = new FileInputStream(file)) {
                props.load(in);
                return props.getProperty(THEME_KEY);
            } catch (IOException e) {
                System.err.println("Error loading theme: " + e.getMessage());
            }
        }
        return null;
    }

    private static void saveTheme(String theme) {
        File file = new File(SETTINGS_FILE);
        Properties props = new Properties();
        if (file.exists()) {
            try (InputStream in = new FileInputStream(file)) {
                props.load(in);
            } catch (IOException e) {
                System.err.println("Error reading settings: " + e.getMessage());
            }
        }
        props.setProperty(THEME_KEY, theme);
        try (OutputStream out = new FileOutputStream(SETTINGS_FILE)) {
            props.store(out, "Application Settings");
        } catch (IOException e) {
            System.err.println("Error saving theme: " + e.getMessage());
        }
    }
}
