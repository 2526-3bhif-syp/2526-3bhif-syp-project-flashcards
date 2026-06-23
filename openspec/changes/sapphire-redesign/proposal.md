## Why

Die Benutzeroberfläche der Flashcard-Lernapplikation soll grundlegend modernisiert werden. Das neue „Saphirkarten-Design“ sorgt für ein einheitliches, ästhetisches Erscheinungsbild mit weichen Blau-Tönen, konsistenten Eckenrundungen und ansprechenden visuellen Elementen, um die Benutzerfreundlichkeit und Motivation der Lernenden zu steigern (bezugnehmend auf den Sprint 3 UI-Feinschliff aus dem FSD).

## What Changes

- Einführung eines neuen einheitlichen Sapphire-Farbschemas als dritte auswählbare Theme-Option (neben Light und Dark).
- Überarbeitung der Kachel-Darstellung in der Stapel-Übersicht (Mein Stapel) hin zu saphirblauen Ordner-Kacheln mit weißen Dateiordner-Symbolen und Informationen zum letzten Lerntag.
- Hinzufügen einer dekorativen Buchstapel-Illustration in der HomepageView zur visuellen Aufwertung der Startseite.
- Optimierung aller Abstände, Eckenrundungen (Viertelkreis-Radien) und Hover-Effekte auf Buttons und Eingabefeldern im gesamten Interface.
- Anpassung der Statistiken (Donut-Chart für Lernfortschritt, weich gefülltes Liniendiagramm für die wöchentliche Lernzeit) im passenden Saphirblau-Stil.

## Capabilities

### New Capabilities
- `sapphire-theme`: Ermöglicht die Auswahl und das Rendering der App im neuen Sapphire-Farbschema inklusive aller Visuals (Buchstapel-Illustration und verändertes Kachel-Design).

### Modified Capabilities
- `ui-polish`: Anpassung der bestehenden UI-Komponenten zur Unterstützung des neuen Themes und der standardisierten Eckenrundungen sowie verbesserten Hover-Zustände.

## Impact

Betroffen sind der `ThemeProvider.java` zur Hinterlegung der neuen Farb-Tokens, `SettingsView.java` für die Theme-Auswahl, `HomeView.java` für das neue Deck-Grid-Design, `HomepageView.java` für die Buchstapel-Visualisierung sowie `StatisticView.java` für das restylte Diagramm-Layout.
