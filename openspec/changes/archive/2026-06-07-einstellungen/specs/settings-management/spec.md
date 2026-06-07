## ADDED Requirements

### Requirement: Spracheinstellung anzeigen
Ein Benutzer kann in den Einstellungen die aktuell gewählte Sprache einsehen.

#### Scenario: Einstellungen aufrufen
- **WHEN** der Benutzer auf den Menüpunkt "Settings" (Einstellungen) in der Navigation klickt
- **THEN** wird die Einstellungsansicht angezeigt
- **THEN** ist ein Dropdown-Menü zur Sprachauswahl sichtbar, das die aktuell aktive Sprache anzeigt

### Requirement: Sprache ändern
Ein Benutzer kann die Sprache der Benutzeroberfläche zur Laufzeit ändern.

#### Scenario: Sprache von Englisch auf Deutsch umstellen
- **WHEN** der Benutzer im Dropdown-Menü "Deutsch" auswählt
- **THEN** ändert sich die Sprache aller Benutzeroberflächen-Elemente sofort auf Deutsch (z.B. "Settings" wird zu "Einstellungen", Tooltips, Buttons, Labels etc. ändern ihren Text)

### Requirement: Spracheinstellung persistieren
Die gewählte Spracheinstellung wird über Sitzungsgrenzen hinweg gespeichert.

#### Scenario: Sprachwahl speichern und laden
- **WHEN** der Benutzer eine Sprache auswählt
- **THEN** wird die Einstellung in `settings.properties` gespeichert
- **WHEN** die Anwendung das nächste Mal gestartet wird
- **THEN** liest die Anwendung die Einstellung aus `settings.properties` und startet direkt in der gespeicherten Sprache
