## Context

Die Anwendung hat derzeit keine zentrale Startseite (Homepage). Wenn die App gestartet wird, gelangt der Benutzer direkt zur Stapel-Übersicht (`HomeView.java`), welche alle Stapel in einem Grid-Layout auflistet. Mit dem neuen Feature (Issue #64) soll eine neue Startseite implementiert werden, die dem Benutzer eine Übersicht über empfohlene Stapel, seinen täglichen Streak und kürzlich gelernte Stapel bietet.

## Goals / Non-Goals

**Goals:**
- Erstellung einer neuen `HomepageView.java` zur Anzeige der Startseite.
- Einbindung der neuen View in `MainPresenter.java` unter dem Namen `"Home"`.
- Anpassung der Navigation in `SidebarView.java`:
  - Der Menüpunkt `"Home"` führt zur neuen Startseite (`HomepageView`).
  - Der bisherige Menüpunkt für die reine Karten-Ansicht (`"Flashcards"`) wird aus der Sidebar gelöscht.
  - Ein neuer Menüpunkt `"Decks"` wird in der Sidebar eingeführt, der zur bisherigen Stapel-Übersicht (`HomeView.java`) führt.
  - `FlashcardsView.java` wird weiterhin intern zur Verwaltung von Karten innerhalb eines ausgewählten Stapels genutzt (Navigation via Doppelklick/Klick auf einen Stapel in `HomeView`), ist aber nicht mehr direkt über die Sidebar erreichbar.
- Implementierung der drei UI-Bereiche auf der Startseite:
  1. **Empfohlene Stapel** (oben links): Anzeige von Stapeln, sortiert nach dem Zeitpunkt des letzten Lernens (älteste zuerst oder noch nie gelernte).
  2. **Streak-Kalender** (unten links): Visueller Kalender des aktuellen Monats, der gelernte Tage markiert und den aktuellen Streak anzeigt.
  3. **Zuletzt gelernte Stapel** (rechts): Kürzlich gelernte Stapel, sortiert nach dem Zeitpunkt des letzten Lernens (neueste zuerst).
- Speichern des letzten Lern-Zeitpunkts pro Stapel in `decks.json`.
- Speichern der gelernten Tage in einer separaten Datei `streaks.json`.

**Non-Goals:**
- Keine Synchronisation der Streak-Daten über eine Cloud (lokale Persistenz reicht aus).
- Keine komplexen Statistiken (diese verbleiben in `StatisticView.java`).

## Decisions

1. **Erweiterung des Datenmodells (`Deck.java`)**:
   - Klasse `Deck` erhält ein neues Feld `private LocalDateTime lastStudied;` inklusive Getter und Setter, um festzustellen, wann ein Stapel zuletzt gelernt wurde.
   - Beim Laden/Speichern in `Persistence.java` wird dieses Feld im JSON-Format berücksichtigt. Ältere JSON-Dateien ohne dieses Feld setzen es standardmäßig auf `null` (bedeutet: noch nie gelernt).

2. **Streak-Historie in `streaks.json` speichern**:
   - Die gelernten Tage (Daten/Dates) werden in einer separaten Datei `streaks.json` im selben Verzeichnis wie `decks.json` abgelegt.
   - Das Dateiformat ist ein einfaches JSON-Array von Datums-Strings (Format `"yyyy-MM-dd"`), an denen mindestens eine Lernsitzung erfolgreich abgeschlossen wurde:
     ```json
     [
       "2026-06-10",
       "2026-06-11"
     ]
     ```
   - `Persistence.java` wird um Methoden zum Laden und Speichern dieser Datei erweitert.
   - Der aktuelle Streak wird dynamisch berechnet, indem ausgehend vom heutigen Tag geprüft wird, wie viele aufeinanderfolgende Tage in der Liste vorhanden sind.

3. **Navigation & Sidebar-Anpassungen**:
   - `SidebarView.java` entfernt den `"Flashcards"`-Button und fügt stattdessen einen `"Decks"`-Button (Stapelliste) hinzu.
   - `MainPresenter.java` registriert `HomepageView` als `"Home"` und `HomeView` als `"Decks"`.

4. **UI-Layout der Homepage (`HomepageView.java`)**:
   - Verwendung eines `BorderPane` oder `GridPane`, um das Layout zu strukturieren.
   - Oben Links: Eine Box mit empfohlenen Decks als kleinere Kacheln.
   - Unten Links: Streak-Kalender (ein `GridPane` mit Wochentagen und Tagen des aktuellen Monats).
   - Rechts: Eine vertikale Liste (`VBox` / `ListView`) der kürzlich gelernten Stapel.

## Risks / Trade-offs

- **[Risiko] Inkompatibilität alter JSON-Dateien**: Wenn bestehende Daten importiert oder geladen werden, fehlt das Feld `lastStudied`.
  - *Mitigation*: Robuste Deserialisierung in `Persistence.java`, die fehlende Felder toleriert und als `null` einliest.
