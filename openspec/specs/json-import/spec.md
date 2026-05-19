## ADDED Requirements

### Requirement: Stapel aus JSON importieren
Ein Lernender kann eine JSON-Datei importieren und die enthaltenen Stapel der Anwendung hinzufügen.

#### Scenario: Import einer validen Datei
- **WHEN** der Nutzer eine valide JSON-Datei auswählt
- **THEN** werden alle Stapel und Karten importiert und in der Anwendung angezeigt

### Requirement: Validierung vor Import
Die JSON-Datei wird geparst und validiert, bevor der Nutzer nach weiteren Optionen gefragt wird.

#### Scenario: Ungültige JSON-Datei
- **WHEN** der Nutzer eine Datei auswählt, die kein gültiges JSON-Format hat
- **THEN** wird eine benutzerfreundliche Fehlermeldung angezeigt (kein Stack Trace)
- **THEN** wird kein Stapel importiert

### Requirement: Getrennte Import-Aktionen
Karten-Import und Stapel-Import sind voneinander getrennte Aktionen.

#### Scenario: Aktionen klar unterschieden
- **WHEN** der Nutzer einen Import startet
- **THEN** ist klar erkennbar, ob Karten oder Stapel importiert werden
