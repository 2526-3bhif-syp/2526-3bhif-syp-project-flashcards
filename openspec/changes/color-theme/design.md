## Context

Alle Farben sind aktuell als feste Hex-Werte in `setStyle()`-Aufrufen der View-Klassen hartkodiert (39 Farben, ~148 Vorkommen in 13 Dateien). Es gibt keine Abstraktionsschicht für das Farbschema. Das `TranslationProvider`-Pattern (Singleton, `ObjectProperty`, reactive Bindings, `settings.properties`-Persistenz) dient als Vorlage.

## Goals / Non-Goals

**Goals:**
- Abstraktion aller Farben hinter semantischen Tokens
- Zwei vollständige Farbschemata: `light` (aktuelles Design), `dark`
- Auswahl über `ComboBox` in `SettingsView`
- Live-Wechsel ohne App-Neustart
- Persistenz in `settings.properties`

**Non-Goals:**
- Benutzerdefinierte Farben (Custom-Theming)
- Mehr als zwei Schemata in dieser Iteration
- Dynamische Themes zur Laufzeit (Skinning-Engine)

## Decisions

- **Muster**: `ThemeProvider` als Singleton mit statischen Zugriffsmethoden — identisch zu `TranslationProvider`
- **Datenstruktur**: `Map<String, Map<String, String>>` — Theme-Name → (Token → Hex)
- **Reaktivität**: `ObjectProperty<String> currentTheme` mit `addListener` in Views — analog zu `TranslationProvider.localeProperty()`
- **Persistenz**: Schlüssel `theme` in `settings.properties` — selbe Datei wie `language`
- **Theme-Tokens**: ca. 30 semantische Tokens (bg-primary, text-primary, accent-blue, usw.), die alle existierenden Hex-Farben abdecken
- **View-Updates**: Jede View registriert einen Listener auf `ThemeProvider.themeProperty()` und ruft eine `applyTheme()`-Methode auf, die alle `setStyle()`-Aufrufe neu setzt
- **Namenskonvention**: Tokens im Format `<kategorie>-<farbe>-<variante>` (z. B. `accent-blue-hover`, `bg-secondary`, `text-muted`)

## Risks / Trade-offs

- **Aufwändige Refactoring**: Alle 148 `setStyle()`-Aufrufe müssen auf Token umgestellt werden — hohes Risiko für übersehene Stellen
- **Hover-Zustände**: Viele Buttons haben manuelle `setOnMouseEntered`/`setOnMouseExited`-Handler mit hartkodierten Farben — diese müssen ebenfalls tokenisiert werden
- **Abwärtskompatibilität**: Kein Fallback-Mechanismus nötig, da `light`-Theme den aktuellen Farben entspricht
- **Testbarkeit**: Theme-Wechsel kann über `ThemeProvider.setTheme()` getestet werden; visuelle Regression erfordert manuelle Prüfung

## Detail: Token-Map

```
bg-primary           #f8f9fa     → #121212
bg-secondary         #f5f5f5     → #1e1e2e
bg-card              #ffffff     → #2a2a3e
bg-hover             #eeeeee     → #353550
bg-active            #e0e0e0     → #404060
text-primary         #333333     → #e0e0e0
text-secondary       #555555     → #b0b0b0
text-muted           #666666     → #909090
text-subtle          #757575     → #808080
text-disabled        #999999     → #606060
text-placeholder     #aaaaaa     → #505050
border-default       #cccccc     → #444444
border-light         #e0e0e0     → #3a3a3a
accent-blue          #2196F3     → #64B5F6
accent-blue-hover    #1976D2     → #42A5F5
accent-blue-active   #1565C0     → #2196F3
accent-blue-strong   #0D47A1     → #1565C0
accent-blue-light    #90CAF9     → #90CAF9
accent-blue-bg       #E3F2FD     → #1a237e
accent-green         #4CAF50     → #81C784
accent-green-strong  #2E7D32     → #66BB6A
accent-green-dark    #1B5E20     → #4CAF50
accent-green-light   #A5D6A7     → #A5D6A7
accent-green-bg      #E8F5E9     → #1b5e20
accent-orange        #FF9800     → #FFB74D
accent-orange-hover  #F57C00     → #FFA726
accent-orange-active #e65100     → #F57C00
accent-red           #dc3545     → #E57373
accent-red-hover     #b02a37     → #EF5350
accent-red-strong    #D32F2F     → #F44336
neutral-gray         #607D8B     → #90A4AE
neutral-gray-dark    #455A64     → #78909C
text-on-primary      #ffffff     → #121212
text-on-accent       #ffffff     → #000000
```

## Detail: Theme-Provider API

```java
public class ThemeProvider {
    // Öffentliche Methoden (statisch)
    public static ObjectProperty<String> themeProperty();
    public static String getTheme();
    public static void setTheme(String theme);
    public static String get(String token);                           // Farbe als Hex
    public static StringBinding createColorBinding(String token);     // Reaktives Binding
    public static void addThemeListener(Runnable callback);           // Für View-Refresh
}
```

## Detail: View-Aktualisierung

Jede View erhält eine `applyTheme()`-Methode, die alle `setStyle()`-Aufrufe neu setzt. Der Listener wird entweder im Konstruktor oder in `MainPresenter` registriert:

```java
// Beispiel SidebarView:
public void applyTheme() {
    homeBtn.setStyle("-fx-background-color: " + ThemeProvider.get("bg-card") + "; " +
                     "-fx-text-fill: " + ThemeProvider.get("text-primary") + "; " +
                     "-fx-background-radius: 8;");
    // ... alle weiteren setStyle-Aufrufe
}
```

Alternative: `MainPresenter` ruft bei Themenwechsel `applyTheme()` auf allen aktiven Views auf.
