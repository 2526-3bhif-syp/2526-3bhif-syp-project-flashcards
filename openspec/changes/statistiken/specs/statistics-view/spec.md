## ADDED Requirements

### Requirement: Lernfortschritt-Statistik anzeigen
Ein Lernender kann eine Übersicht seines Lernverhaltens einsehen.

#### Scenario: Statistik öffnen
- **WHEN** der Nutzer die Statistik-Ansicht öffnet
- **THEN** werden Einschätzungsquoten (Falsch, Schwierig, Ok, Leicht) als Chart angezeigt
- **THEN** wird die Anzahl gelernter Karten pro Tag/Woche angezeigt

### Requirement: Statistik nach Stapel filtern
Der Nutzer kann die Statistiken auf einen bestimmten Stapel einschränken.

#### Scenario: Stapel auswählen
- **WHEN** der Nutzer einen Stapel aus einer Auswahlliste wählt
- **THEN** werden nur die Statistiken für diesen Stapel angezeigt

#### Scenario: Alle Stapel anzeigen
- **WHEN** der Nutzer "Alle Stapel" auswählt
- **THEN** werden die Statistiken über alle Stapel aggregiert angezeigt

### Requirement: Leere Statistik-Ansicht
Wenn noch keine Lerneinheiten durchgeführt wurden, wird ein entsprechender Hinweis angezeigt.

#### Scenario: Keine Daten vorhanden
- **WHEN** der Nutzer die Statistik öffnet, aber noch keine Einschätzungen abgegeben hat
- **THEN** wird eine Meldung angezeigt, dass noch keine Daten vorhanden sind
