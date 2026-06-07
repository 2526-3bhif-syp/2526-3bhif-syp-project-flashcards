## Why

Issue #57 (Settings) fordert eine benutzerdefinierte Farbauswahl für die App. Lernende sollen zwischen verschiedenen Farbschemata wählen können, um ihre Lernerfahrung zu personalisieren. Das Übersetzungssystem wurde bereits umgesetzt — der Farbschema-Teil fehlt noch.

Aktuell sind alle Farben als feste Hex-Werte in den View-Klassen hartkodiert (39 eindeutige Farben, ~148 Vorkommen in 13 Dateien). Dies macht Änderungen am Erscheinungsbild aufwändig und fehleranfällig.

## What Changes

- Neue `ThemeProvider`-Klasse (Singleton nach Vorbild von `TranslationProvider`) mit `ObjectProperty<String> currentTheme`
- Zwei Farbschemata: `light` (aktuelles Design) und `dark` (dunkles Schema)
- Farbschema-Definitionen als Map von Farb-Token → Hex-Wert pro Theme
- `SettingsView`-Erweiterung um eine `ComboBox` zur Themenauswahl
- Alle View-Klassen erhalten Listener auf `ThemeProvider.themeProperty()` und aktualisieren ihre `setStyle()`-Aufrufe beim Wechsel
- Persistenz des gewählten Themas in `settings.properties`

## Capabilities

### New Capabilities

- `color-theme`: Farbauswahl über die Einstellungen, zwei Schemata (Light/Dark), Live-Updates

## Impact

Betroffen: `ThemeProvider.java` (neu), `SettingsView.java` (Theme-Auswahl), alle 13 View-Klassen (setStyle-Aktualisierung), `MainPresenter.java` (Neu-Laden bei Themenwechsel), `settings.properties` (neuer Key `theme`)
