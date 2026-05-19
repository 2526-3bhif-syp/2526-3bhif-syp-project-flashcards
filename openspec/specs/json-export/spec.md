## ADDED Requirements

### Requirement: Stapel als JSON exportieren
Ein Lernender kann einen oder mehrere Stapel als JSON-Datei auf dem lokalen Dateisystem speichern.

#### Scenario: Export erfolgreich
- **WHEN** der Nutzer den Export-Menüpunkt auswählt und einen Speicherort wählt
- **THEN** wird eine JSON-Datei erstellt, die alle Stapel mit Karten und Base64-kodierten Medien enthält

### Requirement: Vollständiges Datenformat (BR-004)
Die exportierte JSON enthält alle Texte, Tags und Medien als Base64-Strings.

#### Scenario: Medium im Export
- **WHEN** eine Karte ein Bild oder Audio enthält
- **THEN** ist das Medium als Base64-String in der JSON-Datei eingebettet
