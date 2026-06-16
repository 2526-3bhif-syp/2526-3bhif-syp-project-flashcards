## Why

Lernende sollen ihre Lernstatistiken direkt aus der App heraus mit Kollegen teilen können, um Motivation zu fördern. Dies entspricht User Story #9 und Task #34.

## What Changes

- Zwei Share-Buttons werden in der `StatisticView` eingebaut — je einer direkt bei der jeweiligen Chart
- Ein Klick auf einen Share-Button erstellt einen Screenshot der Chart inkl. der aktiven Filter-Labels (Stapel + Zeitraum)
- Das erzeugte Bild wird über den nativen System-Share-Dialog des Betriebssystems geteilt
- Keine manuelle Filterauswahl im Share-Prozess — die aktuell aktiven Filter werden automatisch übernommen

## Capabilities

### New Capabilities

- `statistic-share`: Screenshot einer Statistik-Chart (inkl. Filter-Labels) erstellen und über den System-Share-Dialog teilen

### Modified Capabilities

- `statistics-view`: Share-Buttons und Screenshot-Logik werden in die bestehende StatisticView integriert

## Impact

- `StatisticView.java` — Share-Buttons, `chart.snapshot()` Screenshot-Logik, Filter-Label-Overlay
- `MainPresenter.java` — Share-Aktion verdrahten
- Abhängigkeit: Change `statistiken` muss vollständig implementiert sein (Charts + Filter vorhanden)
- GitHub: Issue #9 (User Story), Issue #34 (Task)
