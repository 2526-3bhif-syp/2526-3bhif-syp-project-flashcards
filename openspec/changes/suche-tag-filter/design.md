## Context

Tags werden als `List<String>` an der `Card`-Klasse gespeichert. Die Suchleiste in `NavbarView.java` löst eine Filterung im `MainPresenter.java` aus. Suchergebnisse können in einer eigenen Ansicht oder als gefilterte Kartenliste angezeigt werden.

## Goals / Non-Goals

**Goals:**
- Tags an Karten hinzufügen/entfernen (im `CreateCardDialog` oder Bearbeitungsdialog)
- Suche nach Tagname oder Freitext (Vorderseite/Rückseite)
- Stapelübergreifende Suchergebnisse

**Non-Goals:**
- Volltextsuche in Medien (Audio/Bilder)
- Eigene Tag-Verwaltungsansicht

## Decisions

- **Datenstruktur**: `tags: List<String>` in `Card.java` (String-basiert, kein eigenes Tag-Objekt)
- **Suchalgorithmus**: Case-insensitiver String-Vergleich über Vorderseiten-Text, Rückseiten-Text und Tags
- **UI**: Sucheingabe in `NavbarView.java` triggert Echtzeit-Filter; Ergebnisse in `FlashcardsView.java` oder als Overlay

## Risks / Trade-offs

- Bei sehr vielen Karten könnte die Echtzeit-Suche beim Tippen zu Verzögerungen führen (JavaFX-Event-Filter nötig)
