## ADDED Requirements

### Requirement: Dynamische Benutzeroberflächen-Übersetzung
Die Benutzeroberfläche reagiert dynamisch auf den Sprachwechsel, ohne dass geöffnete Dialoge geschlossen werden oder Daten verloren gehen.

#### Scenario: Textaktualisierung per Binding
- **WHEN** die Spracheinstellung aktualisiert wird
- **THEN** passen alle Label-, Button-, Spalten- und Tooltip-Texte in der aktiven Ansicht ihren Inhalt an die neue Sprache an
- **THEN** bleibt der Zustand (z.B. Eingaben in Formularfeldern, Selektionen in Tabellen) der aktiven Ansicht vollständig erhalten

### Requirement: Fallback bei fehlenden Übersetzungen
Sollte für ein UI-Element kein passender Übersetzungsschlüssel in der Zielsprache existieren, wird ein Fallback bereitgestellt.

#### Scenario: Fehlender Übersetzungsschlüssel
- **WHEN** ein Text geladen wird, dessen Übersetzungsschlüssel in der ausgewählten Sprachdatei fehlt
- **THEN** wird der Schlüssel in eckigen Klammern angezeigt (z.B. `[home.unknown_key]`), um einen Absturz zu verhindern und Debugging zu erleichtern
