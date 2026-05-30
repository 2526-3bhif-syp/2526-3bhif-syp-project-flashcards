## 1. Datenmodell

- [x] 1.1 `tags: List<String>` Feld in `Card.java` hinzufügen
- [x] 1.2 Persistenz anpassen: Tags in JSON speichern/laden

## 2. Tag-Eingabe im Dialog

- [x] 2.1 Tag-Eingabefeld in `CreateCardDialog.java` hinzufügen (Eingabe + Enter/Button zum Hinzufügen)
- [x] 2.2 Tag-Chips/Labels im Dialog anzeigen mit Entfernen-Button
- [x] 2.3 Tags beim Speichern der Karte übernehmen

## 3. Suchlogik

- [x] 3.1 Suchmethode in `MainPresenter.java` implementieren: case-insensitiver Vergleich über Vorderseite, Rückseite und Tags
- [x] 3.2 Suchergebnisse aus allen Stapeln aggregieren

## 4. UI – Suchleiste

- [x] 4.1 Suchleiste in `NavbarView.java` einbauen
- [x] 4.2 Sucheingabe mit Echtzeit-Filter verdrahten (Listener auf TextField)
- [x] 4.3 Suchergebnisse in `FlashcardsView.java` oder einer Suchergebnis-Ansicht anzeigen
- [x] 4.4 Suche leeren → normale Ansicht wiederherstellen
