## ADDED Requirements

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
