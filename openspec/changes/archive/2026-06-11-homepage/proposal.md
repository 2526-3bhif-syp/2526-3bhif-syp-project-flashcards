## Why

Nutzer haben beim Starten der App keine zentrale Übersicht über ihren Lernfortschritt, aktuelle Decks (Stapel) oder Empfehlungen. Eine Homepage (Startseite) bietet einen schnellen Einstieg, motiviert durch Streak-Tracking und schlägt gezielt Decks vor, die länger nicht gelernt wurden. Dies entspricht GitHub Issue #64.

## What Changes

- Einführung einer zentralen Startseite (Homepage) beim Öffnen der Anwendung.
- **Empfohlene Stapel** (oben links): Anzeige von Stapeln, die der Benutzer schon länger nicht mehr gelernt hat.
- **Streak-Kalender** (unten links): Ein Kalender, der den täglichen Lern-Streak visualisiert. Jeder Tag, an dem gelernt wurde, wird markiert.
- **Zuletzt gelernte Stapel** (rechts): Auflistung der kürzlich gelernten Stapel.
- **Navigations-Anpassung in der Sidebar**:
  - Der Menüpunkt `"Home"` führt zur neuen Startseite (`HomepageView`).
  - Der bisherige Menüpunkt für die reine Karten-Ansicht (`"Flashcards"`) wird gelöscht.
  - Ein neuer Menüpunkt `"Decks"` (Stapelliste / `HomeView`) wird in der Sidebar hinzugefügt.
- **Speicherung der Streaks**: Einführung einer neuen JSON-Datei `streaks.json` zur persistenten Speicherung der gelernten Tage (Streak-Daten).

## Capabilities

### New Capabilities

- `homepage`: Zentrale Startseite mit Empfehlungen für ungelernte Stapel, Streak-Tracking im Kalender und Verlauf der zuletzt gelernten Stapel.

### Modified Capabilities

<!-- Keine geänderten Anforderungen an bestehende Spezifikationen -->

## Impact

Betroffen:
- `HomepageView.java` (neu): GUI für die Startseite.
- `HomepagePresenter.java` (neu): Presenter-Logik für die Startseite.
- `SidebarView.java` (Modifikation): Entfernen des "Flashcards"-Buttons, Hinzufügen des "Decks"-Buttons.
- `MainPresenter.java` (Modifikation): Navigation und Integration der Homepage.
- `Deck.java` / `Persistence.java` (Modifikation): Erfassung des letzten Lern-Zeitpunkts pro Stapel und Implementierung der `streaks.json` Persistenz.
