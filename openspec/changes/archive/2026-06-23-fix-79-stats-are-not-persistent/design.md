## Context

Der Lernmodus (`StudyView.java`) erfasst während einer Lernsitzung Selbsteinschätzungen der Benutzer für jede Karte. Diese werden im `studyHistory`-Feld der `Card`-Klassen gespeichert. Bislang werden diese Änderungen beim Beenden der Lernsitzung in `MainPresenter.java` jedoch nicht in der `decks.json`-Datei persistiert. Zudem führt das Schließen des Fensters über den OS-Schließen-Button dazu, dass der Beendigungs-Callback überhaupt nicht getriggert wird.

## Goals / Non-Goals

**Goals:**
- Dauerhaftes Speichern der `studyHistory`-Einträge nach Beendigung einer Lernsitzung in `decks.json`.
- Zuverlässiges Triggern der Speicherung auch bei Schließen des Fensters über den OS-Fenster-Close-Button ('X').
- Vermeidung von doppeltem Triggern des Sitzungsabschlusses.

**Non-Goals:**
- Speichern der Historie nach jeder einzelnen Kartenbewertung (Performance-Vermeidung, Sammelspeicherung am Ende reicht aus).
- Modifikation des bestehenden JSON-Datenmodells für Karten oder Stapel.

## Decisions

- **Erhalt der MVP-Architektur**: Die View (`StudyView.java`) meldet den Sitzungsabschluss weiterhin über den `onSessionEnd`-Callback an den Presenter (`MainPresenter.java`). Der Presenter führt die Modell-Aktualisierung (`model.updateDeck(currentDeck)`) durch.
- **Sitzungsabschluss in `stage.setOnHidden`**: In `StudyView.java` wird der `fireSessionEnd()`-Aufruf in die `stage.setOnHidden`-Methode verlagert (bzw. dort ergänzt). Da `setOnHidden` sowohl bei expliziter Schließung (stage.close()) als auch bei OS-Schließung gefeuert wird, ist dies ein sicherer Hook.
- **Sicherheits-Guard gegen Doppelt-Triggerung**: Ein einfaches `sessionEnded` Boolean-Flag in `StudyView.java` stellt sicher, dass `onSessionEnd` exakt einmal pro Instanz ausgeführt wird.

## Risks / Trade-offs

- **Risk**: Wenn das Programm hart abstürzt (z.B. SIGKILL), gehen die Daten der aktuellen Sitzung verloren.
  - **Mitigation**: Dies ist für eine lokale JavaFX-Anwendung ohne Echtzeit-Datenbank akzeptabel und entspricht dem übrigen Verhalten der App.
