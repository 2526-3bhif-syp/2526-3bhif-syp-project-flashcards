## ADDED Requirements

### Requirement: Kontextsensitive Suche in der Navbar
Ein Lernender kann über die Suchleiste in der Navbar Karten suchen.

#### Scenario: Suche nach Text
- **WHEN** der Nutzer einen Suchbegriff eingibt
- **THEN** werden alle Karten angezeigt, deren Vorderseite, Rückseite oder Tags den Begriff enthalten (case-insensitiv)

#### Scenario: Leere Suche
- **WHEN** der Nutzer die Suche leert
- **THEN** wird die normale Ansicht wiederhergestellt

### Requirement: Stapelübergreifende Suchergebnisse
Die Suche durchsucht alle Stapel, nicht nur den aktuell ausgewählten.

#### Scenario: Karten aus mehreren Stapeln
- **WHEN** der Suchbegriff zu Karten in verschiedenen Stapeln passt
- **THEN** werden Karten aus allen Stapeln in den Ergebnissen angezeigt

### Requirement: Tag-basierter Filter
Der Nutzer kann Karten gezielt nach einem Tag filtern.

#### Scenario: Nach Tag filtern
- **WHEN** der Nutzer einen Tag auswählt oder in die Suche eingibt
- **THEN** werden nur Karten mit diesem Tag angezeigt
