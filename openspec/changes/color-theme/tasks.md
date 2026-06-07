## 1. ThemeProvider

- [x] 1.1 `ThemeProvider.java` in `model/` erstellen (analog zu `TranslationProvider.java`)
- [x] 1.2 Theme-Definitionen für `light` und `dark` als Map<String, Map<String, String>> hinterlegen
- [x] 1.3 `ObjectProperty<String> currentTheme` mit Initialisierung aus `settings.properties` (Key `theme`)
- [x] 1.4 `setTheme()` mit Persistenz in `settings.properties` und `addThemeListener()` für Callback-Registrierung
- [x] 1.5 `get(String token)` und `createColorBinding(String token)` bereitstellen

## 2. SettingsView – Theme-Auswahl

- [x] 2.1 `ComboBox<String>` für Theme-Auswahl ("Light" / "Dark") in `SettingsView.java` einfügen
- [x] 2.2 Bidirektionale Bindung zwischen ComboBox und `ThemeProvider.themeProperty()` (analog zum Language-Dropdown)
- [x] 2.3 Übersetzungsschlüssel `settings.theme_label`, `settings.theme_light`, `settings.theme_dark` in `messages*.properties`

## 3. MainPresenter – Theme-Wechsel-Infrastruktur

- [x] 3.1 `applyThemeToCurrentView()`-Methode in `MainPresenter.java` – ruft `applyTheme()` auf der aktuellen View auf
- [x] 3.2 Listener auf `ThemeProvider.themeProperty()` registrieren, der `applyThemeToCurrentView()` triggert

## 4. SidebarView + NavbarView + MainView + StatisticView

- [x] 4.1 `SidebarView.java`: Alle `setStyle()`-Aufrufe auf ThemeProvider-Tokens umstellen, `applyTheme()`-Methode
- [x] 4.2 `NavbarView.java`: Suchfeld-Styling auf Token umstellen
- [x] 4.3 `MainView.java`: Hintergrundfarbe des BorderPane auf Token umstellen
- [x] 4.4 `StatisticView.java`: Hintergrundfarbe auf Token umstellen

## 5. HomeView

- [x] 5.1 `HomeView.java`: Alle `setStyle()`-Aufrufe auf ThemeProvider-Tokens umstellen (Header, Deck-Tiles, Buttons)
- [x] 5.2 `applyTheme()`-Methode implementieren, hover-Effekte tokenisieren

## 6. FlashcardsView

- [x] 6.1 `FlashcardsView.java`: Alle `setStyle()`-Aufrufe auf ThemeProvider-Tokens umstellen (Karten-Grid, Detail-Panel, Buttons)
- [x] 6.2 `applyTheme()`-Methode implementieren, hover-Effekte tokenisieren

## 7. StudyModeView + StudyView

- [x] 7.1 `StudyModeView.java`: Karten-Vorder-/Rückseite, Rating-Buttons, Header auf Token umstellen
- [x] 7.2 `StudyView.java`: Popup-Fenster-Styling auf Token umstellen
- [x] 7.3 `applyTheme()`-Methoden in beiden Views implementieren

## 8. CreateDeckDialog + CreateCardDialog

- [x] 8.1 `CreateDeckDialog.java`: Dialog-Styling auf Token umstellen
- [x] 8.2 `CreateCardDialog.java`: Dialog-Styling auf Token umstellen

## 9. DuplicateActionDialog + DuplicateDeckActionDialog

- [x] 9.1 `DuplicateActionDialog.java`: Dialog-Styling auf Token umstellen
- [x] 9.2 `DuplicateDeckActionDialog.java`: Dialog-Styling auf Token umstellen
