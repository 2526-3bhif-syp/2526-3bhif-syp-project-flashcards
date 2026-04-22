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
            String resourcePath = ICON_DIRECTORY + iconId + ICON_EXTENSION;
            
            // Try to get the resource as a stream
            InputStream resourceStream = IconManager.class.getClassLoader()
                .getResourceAsStream(resourcePath);
            
            // If that fails, try with a leading slash
            if (resourceStream == null) {
                resourceStream = IconManager.class.getResourceAsStream("/" + resourcePath);
            }
            
            // If still null, try the system classloader
            if (resourceStream == null) {
                resourceStream = ClassLoader.getSystemResourceAsStream(resourcePath);
            }
            
            if (resourceStream == null) {
                System.err.println("Icon resource not found: " + resourcePath);
                return null;
            }
            
            // Create Image directly from PNG stream
            // JavaFX handles PNG natively with full quality
            try {
                Image image = new Image(resourceStream);
                if (!image.isError()) {
                    System.out.println("✓ Loaded icon: " + iconId);
                    return image;
                } else {
                    System.err.println("Error loading SVG for icon: " + iconId);
                    return null;
                }
            } catch (Exception e) {
                System.err.println("Exception creating Image from SVG stream: " + iconId);
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error loading icon: " + iconId);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Creates a simple placeholder icon using a basic SVG.
     * This is a fallback when no icons can be loaded.
     *
     * @return a placeholder JavaFX Image
     */
    private static Image createPlaceholderIcon() {
        try {
            // Create a simple SVG as a string and wrap it in a stream
            String placeholderSvg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"200\" height=\"200\" viewBox=\"0 0 200 200\">" +
                    "  <rect width=\"200\" height=\"200\" fill=\"#cccccc\"/>" +
                    "  <text x=\"100\" y=\"120\" font-size=\"80\" text-anchor=\"middle\" fill=\"white\" font-weight=\"bold\">?</text>" +
                    "</svg>";
            
            InputStream svgStream = new java.io.ByteArrayInputStream(placeholderSvg.getBytes("UTF-8"));
            Image image = new Image(svgStream);
            
            if (!image.isError()) {
                System.out.println("✓ Created placeholder icon");
                return image;
            } else {
                System.err.println("Failed to create placeholder icon");
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error creating placeholder icon");
            e.printStackTrace();
            return null;
        }
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
