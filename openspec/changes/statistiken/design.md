## Context

`StatisticView.java` existiert bereits als leerer Stub. Der Presenter muss die `LernFortschritt`-Daten aus dem Modell aggregieren und der View übergeben. Voraussetzung: Lern-Modus (Change `lern-modus`) muss fertig sein, damit Daten vorhanden sind.

## Goals / Non-Goals

**Goals:**
- Gelernte Karten pro Tag/Woche anzeigen
- Einschätzungsquoten (Falsch, Schwierig, Ok, Leicht) als Übersicht
- Optional: filterbar nach Stapel

**Non-Goals:**
- Externe Charts-Bibliothek (JavaFX-eigene Mittel nutzen)
- Export der Statistiken

## Decisions

- **Datenzugriff**: Aggregation erfolgt im `MainPresenter.java` oder einer separaten Hilfsklasse
- **UI**: JavaFX `BarChart` oder `PieChart` für Einschätzungsquoten; `TableView` oder einfache Labels für Tagesübersicht
- **Filter**: ComboBox für Stapelauswahl (optional "Alle Stapel")

## Risks / Trade-offs

- Ohne gespeicherte `LernFortschritt`-Daten (d.h. Lern-Modus noch nicht genutzt) sind Statistiken leer
