## ADDED Requirements

### Requirement: Tags an Karte hinzufügen
Ein Lernender kann beim Erstellen oder Bearbeiten einer Karte Tags hinzufügen.

#### Scenario: Tag hinzufügen
- **WHEN** der Nutzer im Karten-Dialog ein Schlagwort eingibt und bestätigt
- **THEN** wird der Tag der Karte fest zugeordnet und gespeichert

### Requirement: Tags entfernen
Ein Lernender kann Tags von einer bestehenden Karte entfernen.

#### Scenario: Tag entfernen
- **WHEN** der Nutzer einen Tag im Karten-Dialog löscht
- **THEN** ist der Tag nach dem Speichern nicht mehr mit der Karte verknüpft

### Requirement: Mehrere Tags pro Karte
Eine Karte kann mehrere Tags gleichzeitig haben.

#### Scenario: Mehrere Tags
- **WHEN** der Nutzer mehrere Tags eingibt
- **THEN** sind alle Tags der Karte zugeordnet
