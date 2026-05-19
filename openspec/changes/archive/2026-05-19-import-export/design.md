## Context

Import und Export bauen auf der bestehenden JSON-Persistenz auf. Die Dateiauswahl erfolgt über JavaFX `FileChooser`. Fehlerhafte JSON-Dateien sollen dem Nutzer verständlich gemeldet werden, ohne Stack Traces auszugeben.

## Goals / Non-Goals

**Goals:**
- Stapel vollständig (inkl. Base64-Medien) als JSON exportieren
- Stapel aus valider JSON-Datei importieren
- Klare Fehlermeldungen bei ungültigem Format

**Non-Goals:**
- Import aus anderen Formaten (CSV, Anki, etc.)
- Cloud-Upload

## Decisions

- **Format**: JSON-Array von Deck-Objekten (selbes Format wie lokale Persistenz)
- **Validierung**: JSON wird geparst und validiert, bevor der Nutzer nach dem Ziel-Stapel gefragt wird
- **Fehlerbehandlung**: Bei Parse-Fehler wird nur eine benutzerfreundliche Meldung ausgegeben (kein Stack Trace)
- **Trennung**: Import und Export sind separate Menüpunkte; Kartei-Import und Stapel-Import sind klar unterschieden

## Risks / Trade-offs

- Große JSON-Dateien mit vielen Base64-Medien können den UI-Thread kurz blockieren
