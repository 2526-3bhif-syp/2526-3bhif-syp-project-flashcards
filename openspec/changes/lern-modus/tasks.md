## 1. Datenmodell erweitern

- [ ] 1.1 `LernFortschritt`-Klasse mit Zeitstempel und Einschätzung (FALSCH, SCHWIERIG, OK, LEICHT) anlegen
- [ ] 1.2 `Card.java` um Liste von `LernFortschritt`-Einträgen erweitern
- [ ] 1.3 Persistenz anpassen: `LernFortschritt`-Einträge in JSON speichern/laden

## 2. Algorithmus

- [ ] 2.1 Gewichtete Zufallsauswahl in `MainPresenter.java` implementieren (BR-003)
- [ ] 2.2 Gewichte basierend auf letzter Einschätzung berechnen (Falsch → höchstes Gewicht, Leicht → niedrigstes)

## 3. UI – Lern-Modus

- [ ] 3.1 Lern-Modus-Ansicht in `FlashcardsView.java` oder als eigene View umsetzen
- [ ] 3.2 Vorderseite anzeigen + "Aufdecken"-Button
- [ ] 3.3 Nach Aufdecken: Rückseite einblenden + vier Einschätzungs-Buttons (Falsch, Schwierig, Ok, Leicht)
- [ ] 3.4 Lern-Modus starten/beenden im `MainPresenter.java` verdrahten

## 4. Tests

- [ ] 4.1 Unit-Test für Algorithmus (Gewichtungslogik prüfen)
- [ ] 4.2 Persistenz-Test für `LernFortschritt`-Einträge
