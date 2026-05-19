## ADDED Requirements

### Requirement: Lernfortschritt protokollieren
Jede Selbsteinschätzung wird mit Zeitstempel und Einschätzungswert an der zugehörigen Karte gespeichert.

#### Scenario: Einschätzung speichern
- **WHEN** der Nutzer eine Einschätzung (Falsch / Schwierig / Ok / Leicht) abgibt
- **THEN** wird ein `LernFortschritt`-Eintrag mit aktuellem Zeitstempel an die Karte angehängt
- **THEN** wird der Fortschritt lokal persistiert

### Requirement: Fortschritt bleibt nach Sitzungsende erhalten
Einschätzungen gehen beim Beenden des Lern-Modus nicht verloren.

#### Scenario: Sitzung beenden
- **WHEN** der Nutzer den Lern-Modus verlässt
- **THEN** sind alle Einschätzungen der Sitzung gespeichert und beim nächsten Start noch vorhanden
