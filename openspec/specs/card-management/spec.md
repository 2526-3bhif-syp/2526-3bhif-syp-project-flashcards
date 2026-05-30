## ADDED Requirements

### Requirement: Karte erstellen
Ein Lernender kann eine neue Karteikarte mit Vorder- und Rückseite sowie optionalen Labels innerhalb eines Stapels anlegen.

#### Scenario: Karte mit Text erstellen
- **WHEN** der Nutzer Vorder- und Rückseiten-Text eingibt und bestätigt
- **THEN** wird die Karte im Stapel gespeichert und in der Kartenliste angezeigt

#### Scenario: Karte mit Medium erstellen
- **WHEN** der Nutzer optional ein Bild oder eine Audio-Datei für eine Seite hinzufügt
- **THEN** wird das Medium in der JSON als Base64-String gespeichert und korrekt verknüpft

#### Scenario: Karte mit Labels erstellen
- **WHEN** der Nutzer ein oder mehrere Labels im Dialog hinzufügt
- **THEN** werden diese der neuen Karte fest zugeordnet und gespeichert

### Requirement: Medienlimit pro Seite (BR-001)
Pro Kartenseite darf maximal ein Bild ODER eine Audio-Datei vorhanden sein.

#### Scenario: Zweites Medium ablehnen
- **WHEN** der Nutzer versucht ein zweites Medium auf derselben Seite hinzuzufügen
- **THEN** wird dies verhindert oder das bestehende Medium ersetzt

### Requirement: Karte bearbeiten und löschen
Ein Lernender kann bestehende Karten ändern oder aus einem Stapel entfernen.

#### Scenario: Karte löschen
- **WHEN** der Nutzer eine Karte löscht
- **THEN** verschwindet sie aus der Kartenliste und wird dauerhaft entfernt
- **THEN** wird das Detail-Panel in der Sidebar geleert
