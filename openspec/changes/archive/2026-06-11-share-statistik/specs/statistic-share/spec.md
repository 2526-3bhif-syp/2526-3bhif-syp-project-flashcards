## ADDED Requirements

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
- **THEN** wird ein Bild erzeugt, das den Chart und einen Label-Text der Form "Stapel: <name> | Zeitraum: <zeitraum>" enthält

#### Scenario: Filter-Label zeigt aktuelle Auswahl
- **WHEN** der Stapel-Filter auf "Mathe" und der Zeitraum-Filter auf "Letzte 7 Tage" gesetzt ist
- **THEN** enthält das erzeugte Bild den Text "Stapel: Mathe | Zeitraum: Letzte 7 Tage"

### Requirement: Bild teilen via System-Mechanismus
Das erzeugte Screenshot-Bild SHALL als temporäre PNG-Datei gespeichert und über `Desktop.getDesktop().open()` geöffnet werden, sodass der Nutzer es über den nativen System-Image-Viewer weiterteilen kann.

#### Scenario: Erfolgreiches Teilen
- **WHEN** der Screenshot erfolgreich erzeugt wurde
- **THEN** wird die temporäre PNG-Datei im System-Temp-Verzeichnis gespeichert und mit dem Standard-Image-Viewer geöffnet

#### Scenario: Desktop nicht unterstützt (Fallback)
- **WHEN** `Desktop.isDesktopSupported()` false zurückgibt
- **THEN** wird das Bild in die Systemzwischenablage kopiert und dem Nutzer ein Hinweis angezeigt
