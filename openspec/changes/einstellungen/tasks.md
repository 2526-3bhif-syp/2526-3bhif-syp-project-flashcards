## 1. Ressourcen und Helper vorbereiten

- [ ] 1.1 Übersetzungsdateien `messages.properties` (Englisch) und `messages_de.properties` (Deutsch) unter `src/main/resources/at/htlleonding/flashcards/` anlegen
- [ ] 1.2 `TranslationProvider.java` in `at.htlleonding.flashcards` implementieren (mit `localeProperty`, `createStringBinding`, `get` und Persistierung in `settings.properties`)
- [ ] 1.3 `module-info.java` prüfen, ob Ressourcen für Reflection geöffnet sind (falls nötig für ResourceBundle)

## 2. Views lokalisieren

- [ ] 2.1 `SidebarView.java` anpassen (Tooltips binden, Navigationsschlüssel beibehalten)
- [ ] 2.2 `NavbarView.java` anpassen (Suchfeld-Prompt-Text binden)
- [ ] 2.3 `HomeView.java` anpassen (Titel, Buttons, Dialoge zur Stapel-Erstellung und -Bearbeitung lokalisieren)
- [ ] 2.4 `FlashcardsView.java` anpassen (Spaltentitel, Buttons, Karten-Erstellungsdialog lokalisieren)
- [ ] 2.5 `StudyModeView.java` und `StudyView.java` anpassen (Lernmodus-Buttons und Selbsteinschätzung lokalisieren)
- [ ] 2.6 `StatisticView.java` anpassen (Statistiktitel und Beschriftungen lokalisieren)

## 3. Einstellungen-UI

- [ ] 3.1 `SettingsView.java` implementieren (Überschrift, Dropdown zur Sprachauswahl "Deutsch" / "English")
- [ ] 3.2 Event-Handling in `SettingsView.java` verdrahten, um `TranslationProvider.setLocale` bei Auswahl aufzurufen
- [ ] 3.3 Layout von `SettingsView.java` an das Design-System anpassen (Abstände, Schriftarten, Farben)

## 4. Tests

- [ ] 4.1 Unit-Test `TranslationProviderTest.java` erstellen (Ressourcen-Laden, Locale-Wechsel und Fallback-Handling prüfen)
- [ ] 4.2 Test für die Persistenz der Spracheinstellungen (Prüfen, ob nach Neustart die korrekte Sprache geladen wird)
