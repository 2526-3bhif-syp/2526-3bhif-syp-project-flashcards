## Purpose
Dieses Dokument definiert die Anforderungen für die lokale JSON-Persistenz der Anwendung.
## Requirements
### Requirement: Lokale JSON-Persistenz
Alle Stapel und Karten SHALL lokal als JSON-Datei gespeichert und beim Anwendungsstart geladen werden.

#### Scenario: Daten beim Start laden
- **WHEN** die Anwendung startet
- **THEN** werden alle gespeicherten Stapel und Karten aus der JSON-Datei geladen

#### Scenario: Daten beim Beenden speichern
- **WHEN** der Nutzer eine Änderung vornimmt, eine Lernsitzung beendet, das Lern-Fenster schließt oder die Anwendung beendet
- **THEN** werden alle geänderten Daten (inklusive des Lernverlaufs der Karten) in die lokale JSON-Datei geschrieben

### Requirement: Medien als Base64 (BR-004)
Medien (Bilder, Audio) SHALL innerhalb der JSON-Datei als Base64-kodierte Strings eingebettet werden.

#### Scenario: Medium serialisieren
- **WHEN** eine Karte mit einem Medium gespeichert wird
- **THEN** enthält die JSON-Datei den Base64-String des Mediums unter dem entsprechenden Feld

