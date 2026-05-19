## Why

Nutzer sollen ihren Lernfortschritt über die Zeit verfolgen können. Die gesammelten `LernFortschritt`-Einträge (aus dem Lern-Modus) sollen in einer Statistik-Ansicht ausgewertet werden. Dies ist Teil von Sprint 3 aus dem FSD.

## What Changes

- Eine neue `StatisticView` (bereits als Stub vorhanden) wird mit Inhalt befüllt
- Angezeigt werden: Karten pro Tag/Woche gelernt, Einschätzungsquoten (Falsch/Schwierig/Ok/Leicht)
- Die Statistiken beziehen sich auf alle Stapel oder einen ausgewählten Stapel

## Capabilities

### New Capabilities

- `statistics-view`: Auswertung und Anzeige von Lernfortschritt-Daten als Statistiken

### Modified Capabilities

- `learning-progress`: Fortschrittsdaten werden nun auch für Statistiken ausgewertet

## Impact

Betroffen: `StatisticView.java` (bereits vorhanden, Stub), `MainPresenter.java`, `Model.java` (Aggregationslogik)
