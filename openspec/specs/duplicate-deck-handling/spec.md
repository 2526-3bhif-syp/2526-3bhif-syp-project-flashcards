## ADDED Requirements

### Requirement: Duplikat-Erkennung beim Import
Beim Import wird geprüft, ob ein importierter Stapelname (case-insensitiv) einem bestehenden Stapel entspricht.

#### Scenario: Kein Duplikat vorhanden
- **WHEN** kein importierter Stapelname mit einem bestehenden übereinstimmt
- **THEN** werden alle Stapel ohne Dialog direkt importiert

#### Scenario: Duplikat erkannt
- **WHEN** mindestens ein importierter Stapelname (case-insensitiv) bereits existiert
- **THEN** erscheint ein Dialog mit den Optionen: Import All, Replace Existing Decks, Skip Duplicates, Cancel Import

### Requirement: Import All (beide behalten)
- **WHEN** der Nutzer "Import All" wählt
- **THEN** wird der importierte Stapel zusätzlich hinzugefügt; beide Stapel existieren nebeneinander

### Requirement: Replace Existing Decks
- **WHEN** der Nutzer "Replace Existing Decks" wählt
- **THEN** werden bestehende Stapel mit gleichem Namen gelöscht und durch die importierten ersetzt

### Requirement: Skip Duplicates
- **WHEN** der Nutzer "Skip Duplicates" wählt
- **THEN** werden nur Stapel ohne Namenskollision importiert; Duplikate werden übersprungen

### Requirement: Cancel Import
- **WHEN** der Nutzer "Cancel Import" wählt
- **THEN** wird der gesamte Import abgebrochen; keine Änderungen an den bestehenden Daten
