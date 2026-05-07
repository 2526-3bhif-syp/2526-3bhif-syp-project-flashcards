package at.htlleonding.flashcards.view;

import javafx.scene.image.Image;
import java.io.InputStream;
import java.util.*;

/**
 * Manages icon loading for deck icons.
 * Icons are stored as PNG files generated from SVG sources in the resources directory.
 * This provides SVG-quality vector graphics (rendered once to PNG) with full JavaFX compatibility.
 */
public class IconManager {
    private static final String ICON_DIRECTORY = "at/htlleonding/flashcards/icons/png/";
    private static final String ICON_EXTENSION = ".png";
    
    private static final Map<String, Image> ICON_CACHE = new HashMap<>();
    
    /**
     * Loads a JavaFX Image for the given icon ID.
     * Images are cached in memory to avoid repeated file reads.
     *
     * @param iconId the ID of the icon (e.g., "default", "math", "science")
     * @return a JavaFX Image, or a fallback icon if the requested icon is not found
     */
    public static Image getIcon(String iconId) {
        if (iconId == null || iconId.isEmpty()) {
            iconId = "default";
        }
        
        // Return cached icon if available
        if (ICON_CACHE.containsKey(iconId)) {
            Image cachedIcon = ICON_CACHE.get(iconId);
            if (cachedIcon != null) {
                return cachedIcon;
            }
        }
        
        // Try to load the icon from resources
        Image image = loadIconFromResources(iconId);
        if (image != null && !image.isError()) {
            ICON_CACHE.put(iconId, image);
            return image;
        }
        
        // Fallback to default icon if not found
        if (!iconId.equals("default")) {
            return getIcon("default");
        }
        
        // If even default is not found, return a placeholder
        Image placeholder = createPlaceholderIcon();
        if (placeholder != null) {
            ICON_CACHE.put(iconId, placeholder);
        }
        return placeholder;
    }
    
    /**
     * Loads an icon (PNG) from the classpath resources.
     * PNG files provide scalable quality as they are generated from SVG sources.
     *
     * @param iconId the icon ID without extension (e.g., "default")
     * @return a JavaFX Image or null if the icon cannot be loaded
     */
    private static Image loadIconFromResources(String iconId) {
        try {
            String resourcePath = "/" + ICON_DIRECTORY + iconId + ICON_EXTENSION;
            InputStream resourceStream = IconManager.class.getResourceAsStream(resourcePath);
            
            if (resourceStream == null) {
                System.err.println("Icon not found at: " + resourcePath);
                return null;
            }
            
            Image image = new Image(resourceStream);
            if (image.isError()) {
                System.err.println("Error decoding image: " + iconId);
                return null;
            }
            return image;
        } catch (Exception e) {
            System.err.println("Exception loading icon " + iconId + ": " + e.getMessage());
            return null;
        }
    }
    
    private static Image createPlaceholderIcon() {
        // Return a very small empty image instead of crashing with SVG
        return new Image(new java.io.ByteArrayInputStream(new byte[0]));
    }
    
    /**
     * Clears the icon cache. Useful for testing or if icons are updated at runtime.
     */
    public static void clearCache() {
        ICON_CACHE.clear();
    }
    
    /**
     * Returns the list of available icon IDs.
     *
     * @return a list of icon IDs
     */
    public static List<String> getAvailableIconIds() {
        return List.of("default", "math", "science", "code", "history", "language", "art", "music");
    }
}
