## ADDED Requirements

### Requirement: Farbschema-Auswahl in den Einstellungen
Ein Lernender kann in den Einstellungen zwischen verschiedenen Farbschemata wählen.

#### Scenario: Theme-Wechsel über Einstellungen
- **GIVEN** der Lernende öffnet die Einstellungen
- **WHEN** er im Dropdown "Color Theme" ein anderes Schema auswählt (z. B. "Dark")
- **THEN** wechselt die gesamte App sofort zum gewählten Farbschema
- **AND** die Auswahl wird in `settings.properties` gespeichert

#### Scenario: Theme bleibt nach Neustart erhalten
- **GIVEN** der Lernende hat "Dark" als Farbschema ausgewählt
- **WHEN** die App geschlossen und neu gestartet wird
- **THEN** wird das Dark-Schema beim Start geladen und angewendet

### Requirement: Zwei Farbschemata
Die App bietet mindestens ein helles und ein dunkles Farbschema.

#### Scenario: Helles Schema (Light)
- **GIVEN** die App ist im Light-Modus
- **THEN** haben Hintergründe helle Farben (weiß/hellgrau) und Text ist dunkel

#### Scenario: Dunkles Schema (Dark)
- **GIVEN** die App ist im Dark-Modus
- **THEN** haben Hintergründe dunkle Farben und Text ist hell
- **AND** alle UI-Elemente (Buttons, Karten, Dialoge) sind im dunklen Schema lesbar

### Requirement: Live-Wechsel ohne Neustart
Der Themenwechsel erfolgt sofort ohne App-Neustart.

#### Scenario: Live-Wechsel
- **GIVEN** der Lernende befindet sich in der Stapel-Übersicht
- **WHEN** er in den Einstellungen das Farbschema wechselt
- **THEN** wird die geänderte Ansicht sofort mit dem neuen Schema angezeigt
