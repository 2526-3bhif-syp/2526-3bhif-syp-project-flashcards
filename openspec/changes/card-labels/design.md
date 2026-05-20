## Context

Karten im System (`Card.java`) verfügen bereits über ein `tags`-Feld (Liste von Strings), und die Persistenz (`Persistence.java`) unterstützt dies durch Jackson-Serialisierung automatisch. Die `FlashcardsView` zeigt Tags bereits in der Detailansicht an. Was fehlt, ist die Möglichkeit, Tags beim Erstellen oder Bearbeiten einer Karte hinzuzufügen sowie die Anzeige der Tags direkt auf den Karten-Kacheln in der Übersicht.

## Goals / Non-Goals

**Goals:**
- Tags im `CreateCardDialog` hinzufügen und entfernen können.
- Tags direkt auf den `cardTile`-Komponenten in der `FlashcardsView` anzeigen (begrenzt auf eine kleine Auswahl).
- Tags persistent speichern (bereits teilweise vorhanden, muss verifiziert werden).

**Non-Goals:**
- Globale Tag-Verwaltung (Tags löschen, umbenennen über alle Karten hinweg).
- Autocomplete für Tags im Dialog (vorerst nur manuelle Eingabe).

## Decisions

- **Eingabe im Dialog:** Im `CreateCardDialog` wird ein Textfeld mit einem "Hinzufügen"-Button (oder Enter-Key) implementiert. Hinzugefügte Tags werden als "Chips" (Labels mit Lösch-Button) unter dem Textfeld angezeigt.
- **Speicherung:** Die Liste der Tags wird im `Card`-Objekt gespeichert. Da `Persistence.java` Jackson nutzt, ist keine weitere Änderung an der Speicherlogik notwendig.
- **Anzeige in der Übersicht:** In `FlashcardsView.renderCards` wird in der `cardTile` ein kleiner Bereich für Tags reserviert. Falls zu viele Tags vorhanden sind, werden nur die ersten 2-3 angezeigt.

## Risks / Trade-offs

- **Platzmangel:** Tags auf den kleinen Kacheln könnten den Text der Frage verdrängen. → Lösung: Schriftgröße klein halten und Anzahl der angezeigten Tags begrenzen.
- **UI-Konsistenz:** Die Chips im Dialog sollten den Chips in der Detailansicht ähneln.
