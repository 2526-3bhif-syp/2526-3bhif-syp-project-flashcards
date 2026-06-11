### Requirement: Share-Button pro Chart
Für jede der zwei Statistik-Charts in `StatisticView.java` SHALL ein eigener Share-Button vorhanden sein, der direkt beim jeweiligen Chart-Container platziert ist.

#### Scenario: Share-Button sichtbar
- **WHEN** die StatisticView geöffnet wird und Statistikdaten vorhanden sind
- **THEN** ist je ein Share-Button bei der Einschätzungsquoten-Chart und bei der Karten-pro-Tag-Chart sichtbar

#### Scenario: Share-Button bei leeren Daten
- **WHEN** keine Lernfortschritt-Daten vorhanden sind
- **THEN** sind die Share-Buttons deaktiviert (disabled)

### Requirement: Screenshot mit Filter-Labels
Beim Klick auf einen Share-Button SHALL ein Screenshot des zugehörigen Chart-Containers erstellt werden, der die aktiven Filter-Labels (Stapel + Zeitraum) als Beschriftung enthält.

#### Scenario: Screenshot enthält Filter-Labels
- **WHEN** der Nutzer auf einen Share-Button klickt
- **THEN** wird ein Vorschau-Dialog geöffnet, der den Chart und einen Label-Text der Form "Stapel: <name> | Zeitraum: <zeitraum>" zeigt

#### Scenario: Filter-Label zeigt aktuelle Auswahl
- **WHEN** der Stapel-Filter auf "Mathe" und der Zeitraum-Filter auf "Letzte 7 Tage" gesetzt ist
- **THEN** enthält das erzeugte Bild den Text "Stapel: Mathe | Zeitraum: Letzte 7 Tage"

### Requirement: Bild teilen via Vorschau-Dialog
Der Nutzer SHALL nach dem Klick auf Share einen Vorschau-Dialog sehen, aus dem heraus das Bild in die Zwischenablage kopiert oder als PNG gespeichert werden kann.

#### Scenario: In Zwischenablage kopieren
- **WHEN** der Nutzer im Vorschau-Dialog auf "In Zwischenablage kopieren" klickt
- **THEN** wird das Bild in die Systemzwischenablage kopiert

#### Scenario: Als PNG speichern
- **WHEN** der Nutzer im Vorschau-Dialog auf "Speichern..." klickt
- **THEN** öffnet sich ein FileChooser und das Bild wird als PNG am gewählten Pfad gespeichert
