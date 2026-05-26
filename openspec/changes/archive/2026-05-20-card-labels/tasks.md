## 1. UI - Label-Eingabe im Dialog

- [x] 1.1 `TextField` und "Add"-Button für Labels in `CreateCardDialog.java` hinzufügen
- [x] 1.2 Anzeige von hinzugefügten Labels als "Chips" im Dialog implementieren
- [x] 1.3 Lösch-Logik für Labels im Dialog (Klick auf Chip-Lösch-Icon) umsetzen
- [x] 1.4 Labels beim Speichern der Karte in das `Card`-Objekt übernehmen

## 2. UI - Anzeige in der Übersicht

- [x] 2.1 Bereich für Labels in der `cardTile` innerhalb von `FlashcardsView.renderCards` hinzufügen
- [x] 2.2 Logik zur Begrenzung der angezeigten Labels (z.B. max. 3) in der Kachel implementieren
- [x] 2.3 Styling der Labels in der Kachel an das bestehende Design anpassen

## 3. Verifikation & Tests

- [x] 3.1 Manueller Test: Karte mit mehreren Labels erstellen und prüfen, ob diese in der Übersicht und im Detail-Panel korrekt angezeigt werden
- [x] 3.2 Manueller Test: Labels bearbeiten (hinzufügen/löschen) und Persistenz prüfen (Neustart der App)
- [x] 3.3 JUnit-Test für das `Card`-Modell erweitern, um sicherzustellen, dass Labels korrekt gesetzt und abgerufen werden können
