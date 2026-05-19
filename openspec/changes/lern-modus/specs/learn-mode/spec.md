## ADDED Requirements

### Requirement: Lern-Modus starten
Ein Lernender kann den Lern-Modus für einen ausgewählten Stapel starten.

#### Scenario: Lern-Modus starten
- **WHEN** der Nutzer den Lern-Modus für einen Stapel startet
- **THEN** wird die erste Karte mit ihrer Vorderseite angezeigt

### Requirement: Karte aufdecken
Der Nutzer kann die Rückseite einer Karte aufdecken, um die Antwort zu sehen.

#### Scenario: Rückseite aufdecken
- **WHEN** der Nutzer auf "Aufdecken" klickt
- **THEN** werden Vorder- und Rückseite der Karte angezeigt
- **THEN** erscheinen vier Einschätzungs-Buttons: Falsch, Schwierig, Ok, Leicht

### Requirement: Selbsteinschätzung abgeben
Nach dem Aufdecken kann der Nutzer seinen Wissensstand einschätzen.

#### Scenario: Einschätzung wählen
- **WHEN** der Nutzer eine Einschätzung (Falsch / Schwierig / Ok / Leicht) wählt
- **THEN** wird die Einschätzung protokolliert
- **THEN** erscheint die nächste Karte basierend auf dem Algorithmus (BR-003)

### Requirement: Gewichteter Karten-Algorithmus (BR-003)
Karten mit schlechter Einschätzung erscheinen mit höherer Wahrscheinlichkeit erneut.

#### Scenario: Schwierige Karte priorisieren
- **WHEN** eine Karte als "Falsch" oder "Schwierig" eingeschätzt wird
- **THEN** hat sie in der laufenden Sitzung ein höheres Gewicht bei der nächsten Karten-Auswahl
