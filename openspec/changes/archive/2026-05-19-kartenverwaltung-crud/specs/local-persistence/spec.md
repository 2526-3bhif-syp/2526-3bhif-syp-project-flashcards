## ADDED Requirements

### Requirement: Lokale JSON-Persistenz
Alle Stapel und Karten werden lokal als JSON-Datei gespeichert und beim Anwendungsstart geladen.

#### Scenario: Daten beim Start laden
- **WHEN** die Anwendung startet
- **THEN** werden alle gespeicherten Stapel und Karten aus der JSON-Datei geladen

#### Scenario: Daten beim Beenden speichern
- **WHEN** der Nutzer eine Änderung vornimmt oder die Anwendung beendet
- **THEN** werden alle Daten in die lokale JSON-Datei geschrieben

### Requirement: Medien als Base64 (BR-004)
Medien (Bilder, Audio) werden innerhalb der JSON-Datei als Base64-kodierte Strings eingebettet.

#### Scenario: Medium serialisieren
- **WHEN** eine Karte mit einem Medium gespeichert wird
- **THEN** enthält die JSON-Datei den Base64-String des Mediums unter dem entsprechenden Feld
