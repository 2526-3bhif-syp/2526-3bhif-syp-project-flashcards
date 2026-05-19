## 1. Dialog

- [x] 1.1 `DuplicateDeckActionDialog.java` anlegen mit vier Optionen (Import All, Replace Existing, Skip Duplicates, Cancel)
- [x] 1.2 Dialog gibt Nutzerentscheidung als enum-Wert zurück

## 2. Import-Logik im Presenter

- [x] 2.1 Nach JSON-Validierung Duplikatprüfung (case-insensitiv) in `MainPresenter.java` einbauen
- [x] 2.2 Bei keinem Duplikat: direkt importieren ohne Dialog
- [x] 2.3 Bei Duplikat: `DuplicateDeckActionDialog` öffnen und auf Ergebnis reagieren
- [x] 2.4 "Import All" – Stapel einfach hinzufügen
- [x] 2.5 "Replace Existing" – bestehende Stapel löschen, neue hinzufügen
- [x] 2.6 "Skip Duplicates" – nur nicht-doppelte Stapel importieren
- [x] 2.7 "Cancel" – Import vollständig abbrechen, keine Änderungen
