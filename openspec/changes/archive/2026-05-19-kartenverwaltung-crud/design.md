## Context

Die Anwendung folgt dem MVP-Muster. `Model.java` hält den Zustand, `MainPresenter.java` enthält die Logik, die View-Klassen sind passiv. Persistenz erfolgt über `Persistence.java`, das alle Daten als JSON liest und schreibt.

## Goals / Non-Goals

**Goals:**
- Vollständiges CRUD für Stapel und Karten
- Medien (Bild/Audio) pro Kartenseite (max. 1 Medium je Seite)
- Lokale JSON-Persistenz ohne externe Datenbank

**Non-Goals:**
- Cloud-Sync, Mehrbenutzer, Unterstapel

## Decisions

- **Datenmodell**: `Deck` enthält eine Liste von `Card`-Objekten; jede `Card` hat zwei `Face`-Objekte (Vorder-/Rückseite)
- **Persistenz**: `Persistence.java` serialisiert/deserialisiert das gesamte Modell beim Start/Stop; kein inkrementelles Speichern
- **Dialoge**: `CreateDeckDialog` und `CreateCardDialog` als JavaFX-Dialoge, die den Presenter aufrufen
- **Medien**: Binärdaten werden als Base64-String in der JSON gespeichert (BR-004)
- **Eindeutige Stapelnamen**: Wird zur Laufzeit im Presenter validiert (BR-002)

## Risks / Trade-offs

- Beim Lesen großer JSON-Dateien mit vielen Base64-Medien kann die Startzeit steigen
- Alle Daten liegen in einer einzigen JSON-Datei – kein Merge bei gleichzeitigem Zugriff
