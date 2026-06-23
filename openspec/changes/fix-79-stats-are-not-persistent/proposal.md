## Why

Die Studiendaten und Lernstatistiken von Lernsitzungen werden derzeit nach dem Schließen der Anwendung nicht dauerhaft gespeichert. Dadurch gehen historische Lernfortschrittsdaten verloren. Dies ist ein Fehler in der Persistierung von Sprint 3 (GitHub Issue #79).

## What Changes

- Speichern des aktuellen Stapels (inkl. der neuen StudyRecords der Karten) nach Beenden einer Lernsitzung in `MainPresenter.java`.
- Absicherung des Schließen-Events (OS-spezifischer Close-Button / 'X') in `StudyView.java`, sodass dieses ebenfalls den Sitzungsabschluss triggert und die Daten persistiert werden.
- Einführung eines `sessionEnded`-Flags in `StudyView.java` zur Vermeidung von mehrfachen Aufrufen des Sitzungsabschlusses.

## Capabilities

### New Capabilities
<!-- None -->

### Modified Capabilities
- local-persistence: Die Speicherung von Lernsitzungsdaten beim Beenden des Fensters wird in die lokale JSON-Persistenz integriert.

## Impact

- `MainPresenter.java`: Anpassung des `setOnSessionEnd`-Callbacks von `StudyView`, um `model.updateDeck(currentDeck)` aufzurufen.
- `StudyView.java`: Aufruf von `fireSessionEnd()` in `stage.setOnHidden` sowie Absicherung mittels `sessionEnded` Boolean-Flag.
