## ADDED Requirements

### Requirement: Labels hinzufügen
Das System MUSS es dem Nutzer ermöglichen, einer Karte beim Erstellen oder Bearbeiten ein oder mehrere Labels (Tags) zuzuweisen.

#### Scenario: Label über Dialog hinzufügen
- **WHEN** der Nutzer ein Schlagwort in das Label-Eingabefeld eingibt und "Enter" drückt
- **THEN** wird das Label als Chip in der Liste der zugewiesenen Labels angezeigt

### Requirement: Labels entfernen
Der Nutzer MUSS zugewiesene Labels wieder von der Karte entfernen können.

#### Scenario: Label im Dialog entfernen
- **WHEN** der Nutzer auf das "X" oder das Lösch-Icon eines Label-Chips im Dialog klickt
- **THEN** wird das Label aus der Liste der Karte entfernt

### Requirement: Labels anzeigen
Das System MUSS zugewiesene Labels auf der Karte in der Übersicht sowie in der Detailansicht anzeigen.

#### Scenario: Labels in der Übersicht anzeigen
- **WHEN** eine Karte Labels besitzt
- **THEN** werden diese (ggf. begrenzt auf 2-3 Stück) direkt auf der Karten-Kachel in der Übersicht angezeigt
