## Context

Der Lern-Modus ersetzt die normale Kartenansicht in `FlashcardsView.java` oder wird als eigene View umgesetzt. Die Algorithmus-Logik gehört in den `MainPresenter.java`. Einschätzungen werden in der `Card`-Klasse als Liste von `LernFortschritt`-Einträgen gespeichert.

## Goals / Non-Goals

**Goals:**
- Karten einzeln anzeigen (Vorderseite → Rückseite aufdecken → Einschätzung)
- Gewichteter Algorithmus: Falsch/Schwierig → häufiger wiederholen (BR-003)
- Einschätzungen persistieren (Grundlage für Statistiken)

**Non-Goals:**
- Spaced-Repetition über mehrere Tage (nur innerhalb einer Sitzung)
- Kartei-Selektion vor dem Lern-Modus

## Decisions

- **Algorithmus**: Gewichtete Zufallsauswahl – Karten mit "Falsch" oder "Schwierig" bekommen höheres Gewicht
- **UI-Zustand**: Zwei Zustände pro Runde: FRONT (Vorderseite sichtbar) und BACK (beide Seiten + Einschätzungsbuttons)
- **Persistenz**: `LernFortschritt`-Einträge werden direkt nach jeder Einschätzung gespeichert
- **Sitzungsende**: Lern-Modus kann jederzeit beendet werden; bisherige Fortschritte bleiben gespeichert

## Risks / Trade-offs

- Bei sehr kleinen Stapeln (1-2 Karten) wiederholt sich der Algorithmus zwangsläufig schnell
