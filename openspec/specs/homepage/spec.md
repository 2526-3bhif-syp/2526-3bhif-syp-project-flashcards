# homepage Specification

## Purpose
TBD - created by archiving change homepage. Update Purpose after archive.
## Requirements
### Requirement: Empfohlene Stapel anzeigen
Das System MUSS (MUST) auf der Startseite (Homepage) oben links eine Liste von empfohlenen Stapeln anzeigen. Als empfohlen gelten Stapel, die der Benutzer am längsten nicht mehr gelernt hat. Stapel, die noch nie gelernt wurden, haben die höchste Priorität.

#### Scenario: Anzeige empfohlener Stapel beim Laden der Homepage
- **GIVEN** der Benutzer öffnet die App oder navigiert zur Homepage
- **WHEN** die Homepage geladen wird
- **THEN** zeigt das System oben links die empfohlenen Stapel an, sortiert nach dem Zeitpunkt des letzten Lernens (älteste zuerst)

### Requirement: Streak-Kalender und Streak-Zähler
Das System MUSS (MUST) auf der Startseite unten links einen Kalender anzeigen, in dem jeder Tag, an dem der Benutzer eine Lernsitzung durchgeführt hat, markiert ist. Zusätzlich MUSS (MUST) der aktuelle Streak (aufeinanderfolgende Lerntage) als Zahl angezeigt werden.

#### Scenario: Markierung gelesener Tage und Streak-Erhöhung
- **GIVEN** der Benutzer schließt eine Lernsitzung ab
- **WHEN** die Homepage geladen oder aktualisiert wird
- **THEN** wird der heutige Tag im Kalender markiert
- **AND** der Streak-Zähler erhöht sich um eins, falls gestern ebenfalls gelernt wurde

### Requirement: Zuletzt gelernte Stapel anzeigen
Das System MUSS (MUST) auf der Startseite auf der rechten Seite eine Liste der kürzlich gelernten Stapel anzeigen.

#### Scenario: Anzeige der kürzlich gelernten Stapel
- **GIVEN** der Benutzer öffnet die App oder navigiert zur Homepage
- **WHEN** die Homepage geladen wird
- **THEN** zeigt das System auf der rechten Seite die kürzlich gelernten Stapel an, sortiert nach dem Zeitpunkt des letzten Lernens (neueste zuerst)

### Requirement: Layout-Responsivität und Zeilenumbruch (FlowPane)
Das System MUSS (MUST) das Kachel-Layout für Stapel auf der Startseite (Homepage) dynamisch an die verfügbare Breite des Viewports der ScrollPane anpassen. Es MUSS (MUST) sichergestellt werden, dass keine Kacheln horizontal abgeschnitten werden und keine horizontalen Scrollleisten entstehen.

#### Scenario: Dynamischer Kachel-Umbruch bei Breitenänderung
- **GIVEN** der Benutzer befindet sich auf der Startseite
- **WHEN** die Breite des Anwendungsfensters oder der Spalte geändert wird
- **THEN** wird die Zeilenumbruchbreite (prefWrapLength) des FlowPane-Layouts automatisch an die neue Viewport-Breite der ScrollPane angepasst
- **AND** die Stapelkacheln ordnen sich fließend neu an

### Requirement: Mindesthöhe für empfohlene Stapel
Das System MUSS (MUST) eine Mindesthöhe für den Bereich der empfohlenen Stapel gewährleisten, sodass dieser Bereich bei einer großen Anzahl kürzlich gelernter Stapel nicht vertikal zusammengestaucht oder unsichtbar wird.

#### Scenario: Ausreichende Höhe der empfohlenen Stapel
- **GIVEN** der Benutzer hat eine große Anzahl an kürzlich gelernten Stapeln
- **WHEN** die Startseite geladen oder vertikal verkleinert wird
- **THEN** behält der obere Bereich für die empfohlenen Stapel eine feste Mindesthöhe bei
- **AND** der untere Bereich der kürzlich gelernten Stapel wird über eine vertikale Scrollleiste bedienbar gemacht

