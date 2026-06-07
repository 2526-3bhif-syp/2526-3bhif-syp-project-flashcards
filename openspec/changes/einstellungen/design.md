## Context

Die Lokalisierung der JavaFX-Anwendung soll ohne Neustart der App erfolgen (dynamisches Umschalten). Da die Views in Java programmatisch aufgebaut sind, verwenden wir JavaFX Property Bindings. Wenn die ausgewählte `Locale` im `TranslationProvider` geändert wird, aktualisieren sich alle gebundenen Steuerelemente automatisch. Die Speicherung der Einstellungen erfolgt in einer einfachen `settings.properties`-Datei im Arbeitsverzeichnis.

## Goals / Non-Goals

**Goals:**
- Unterstützung von Englisch und Deutsch.
- Dynamischer Sprachwechsel zur Laufzeit ohne Anwendungsneustart.
- Persistierung der gewählten Sprache in `settings.properties`.
- Übersetzung aller sichtbaren Texte (Buttons, Labels, Tooltips, Tabellenüberschriften, Dialoge).

**Non-Goals:**
- Übersetzung von Benutzerinhalten (die Texte auf den Karteikarten selbst bleiben unverändert).
- Unterstützung weiterer Sprachen über Englisch und Deutsch hinaus in dieser Phase.

## Decisions

- **Übersetzungs-Dateien**: Nutzung des Java `ResourceBundle`-Standards. Die Dateien `messages.properties` (Englisch) und `messages_de.properties` (Deutsch) werden unter `src/main/resources/at/htlleonding/flashcards/` abgelegt.
- **TranslationProvider**: Einführung einer Hilfsklasse `TranslationProvider.java` im Package `at.htlleonding.flashcards.model` (oder direkt im Hauptpaket), die eine statische `ObjectProperty<Locale>` verwaltet und `StringBinding`-Erzeuger bereitstellt.
- **Entkopplung Navigation**: In `SidebarView.java` bleiben die Navigations-IDs (`"Home"`, `"Flashcards"`, `"Statistic"`, `"Settings"`) als interne Routing-Schlüssel erhalten. Nur die sichtbaren Tooltips werden lokalisiert:
  `tooltip.textProperty().bind(TranslationProvider.createStringBinding("sidebar." + text.toLowerCase()));`
- **Dynamic Updates**:
  - Label- und Buttontexte werden über `bind()` an ein `StringBinding` gebunden.
  - Dialoge und temporäre Meldungen laden den Text zum Erstellungszeitpunkt direkt mittels `TranslationProvider.get(...)`.
  - Komponenten mit komplexem Zustands-Text (z.B. der Import-Button oder Tabellenspalten) registrieren einen Listener auf die `localeProperty()`, um ihre Texte manuell zu aktualisieren.
- **Persistenz**: Die Einstellungen werden beim Start über `TranslationProvider` geladen (standardmäßig System-Locale, falls keine Datei existiert) und bei Änderungen sofort in `settings.properties` gespeichert.

## Risks / Trade-offs

- **Layout-Clipping**: Deutsche Begriffe sind häufig länger als englische (z.B. "Select" -> "Auswählen", "Delete" -> "Löschen"). Es muss sichergestellt werden, dass Layout-Container flexibel wachsen und Buttons nicht abgeschnitten werden.
- **Zustandsbehaftete Texte**: Schaltflächen wie `selectToggleBtn` in `HomeView.java`, die ihren Text basierend auf dem Zustand der Ansicht wechseln ("Select" vs. "Cancel"), müssen sowohl auf Sprachwechsel als auch auf Zustandsänderungen reagieren. Dies wird durch manuelle Aktualisierungsmethoden oder kombinierte Bindings gelöst.
