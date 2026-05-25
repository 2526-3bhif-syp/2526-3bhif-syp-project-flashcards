## ADDED Requirements

### Requirement: Studienstart-Button in FlashcardsView
Der Nutzer kann den Lern-Modus aus der Kartenansicht eines Stapels starten.

#### Scenario: Button-Position und -Verhalten
- **GIVEN** der Nutzer befindet sich in der Kartenansicht eines Stapels (`FlashcardsView`)
- **THEN** ist rechts unten oberhalb der Import/Export-Buttons ein "Study"-Button sichtbar
- **WHEN** der Nutzer auf "Study" klickt
- **THEN** öffnet sich das Study-Popup mit der ersten Karte des Stapels

### Requirement: Popup-Fenstergröße
Das Study-Popup ist kleiner als das Hauptfenster.

#### Scenario: Fenstergröße
- **GIVEN** das Study-Popup wird geöffnet
- **THEN** hat es eine Größe von ~1200×800 Pixeln
- **AND** das Hauptfenster bleibt unverändert (~1200×800)

### Requirement: Finish-Button
Der Nutzer kann die Lernsitzung jederzeit beenden.

#### Scenario: Finish-Button
- **GIVEN** das Study-Popup ist geöffnet
- **THEN** ist oben rechts ein grüner "Finish"-Button sichtbar
- **WHEN** der Nutzer auf "Finish" klickt
- **THEN** wird das Popup geschlossen
- **AND** alle bisherigen Einschätzungen bleiben gespeichert

### Requirement: Karte aufdecken
Der Nutzer kann die Rückseite der aktuellen Karte aufdecken.

#### Scenario: Aufdecken-Button
- **GIVEN** das Study-Popup zeigt die Vorderseite einer Karte
- **THEN** ist unten mittig ein "Aufdecken"-Button sichtbar
- **WHEN** der Nutzer auf "Aufdecken" klickt
- **THEN** wird die Rückseite eingeblendet
- **AND** es erscheinen vier Einschätzungsbuttons (Falsch, Schwierig, Ok, Leicht) unterhalb der Karte

### Requirement: Next-Card-Button
Nach dem Aufdecken und Bewerten kann der Nutzer zur nächsten Karte wechseln.

#### Scenario: Next-Card-Button
- **GIVEN** die Karte wurde aufgedeckt (State BACK)
- **THEN** ist rechts neben dem "Aufdecken"-Button ein "Next Card"-Button sichtbar
- **WHEN** der Nutzer auf "Next Card" klickt
- **THEN** wird die nächste Karte (basierend auf Algorithmus, siehe learn-mode spec) mit ihrer Vorderseite angezeigt (State FRONT)

### Requirement: Bildanzeige im Lern-Modus
Bilder, die an Karten hinterlegt sind, werden im Study-Popup nach dem Aufdecken angezeigt.

#### Scenario: Vorderseiten-Bild anzeigen
- **GIVEN** die aktuelle Karte hat ein Bild auf der Vorderseite
- **WHEN** der Nutzer die Karte aufdeckt (State BACK)
- **THEN** wird das Vorderseiten-Bild unterhalb des Vorderseiten-Texts angezeigt

#### Scenario: Rückseiten-Bild anzeigen
- **GIVEN** die aktuelle Karte hat ein Bild auf der Rückseite
- **WHEN** der Nutzer die Karte aufdeckt (State BACK)
- **THEN** wird das Rückseiten-Bild unterhalb des Rückseiten-Texts angezeigt

### Requirement: Audio-Wiedergabe im Lern-Modus
Audio-Dateien, die an Karten hinterlegt sind, können im Study-Popup nach dem Aufdecken abgespielt werden.

#### Scenario: Audio-Player anzeigen und abspielen
- **GIVEN** die aktuelle Karte hat eine Audio-Datei auf der Vorder- oder Rückseite
- **WHEN** der Nutzer die Karte aufdeckt (State BACK)
- **THEN** wird ein Audio-Player (Play/Pause, Fortschritt, Lautstärke) unterhalb des zugehörigen Texts angezeigt
- **WHEN** der Nutzer auf Play klickt
- **THEN** wird die Audio-Datei abgespielt

### Requirement: Selbsteinschätzung protokollieren
Nach dem Aufdecken kann der Nutzer seinen Wissensstand einschätzen.

#### Scenario: Einschätzung wählen
- **WHEN** der Nutzer eine Einschätzung (Falsch / Schwierig / Ok / Leicht) klickt
- **THEN** wird die Einschätzung via Presenter an das Datenmodell weitergegeben (siehe `learning-progress` spec)
- **THEN** die Einschätzungsbuttons bleiben sichtbar, bis "Next Card" geklickt wird
