## ADDED Requirements

### Requirement: Stapel erstellen
Ein Lernender kann einen neuen Stapel mit einem eindeutigen Namen anlegen.

#### Scenario: Stapel mit neuem Namen anlegen
- **WHEN** der Nutzer einen Stapelnamen eingibt, der noch nicht existiert
- **THEN** wird der Stapel gespeichert und in der Übersicht angezeigt

#### Scenario: Stapelname bereits vorhanden
- **WHEN** der Nutzer einen Namen eingibt, der einem bestehenden Stapel entspricht (case-insensitiv)
- **THEN** wird eine Fehlermeldung angezeigt und kein Stapel angelegt

### Requirement: Stapel bearbeiten und löschen
Ein Lernender kann einen bestehenden Stapel umbenennen oder löschen.

#### Scenario: Stapel löschen
- **WHEN** der Nutzer einen Stapel löscht
- **THEN** werden der Stapel und alle enthaltenen Karten dauerhaft entfernt
