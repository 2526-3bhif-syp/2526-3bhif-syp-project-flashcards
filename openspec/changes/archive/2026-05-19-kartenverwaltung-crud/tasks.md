## 1. Datenmodell

- [x] 1.1 `Deck.java` mit Name, Beschreibung und Kartenliste anlegen
- [x] 1.2 `Card.java` mit zwei `Face`-Objekten (Vorder-/Rückseite) anlegen
- [x] 1.3 Medientyp (IMAGE, AUDIO, NONE) und Base64-Daten im `Face` modellieren

## 2. Persistenz

- [x] 2.1 `Persistence.java` implementieren: alle Decks als JSON speichern/laden
- [x] 2.2 Base64-Serialisierung für Medien umsetzen
- [x] 2.3 JUnit-Tests für Persistenz schreiben (`PersistenceTest.java`)

## 3. Stapel-Verwaltung (CRUD)

- [x] 3.1 Stapel erstellen via `CreateDeckDialog.java`
- [x] 3.2 Eindeutigkeit des Stapelnamens im `MainPresenter.java` validieren (BR-002)
- [x] 3.3 Stapel löschen inkl. aller enthaltenen Karten
- [x] 3.4 `HomeView.java` zeigt alle Stapel in einer Übersicht

## 4. Karten-Verwaltung (CRUD)

- [x] 4.1 Karte erstellen via `CreateCardDialog.java` mit Text-Eingabe für beide Seiten
- [x] 4.2 Medien-Upload (Bild/Audio) pro Seite im Dialog (max. 1 Medium je Seite, BR-001)
- [x] 4.3 Karte löschen und Sidebar-Panel leeren (`SidebarView.java`)
- [x] 4.4 `FlashcardsView.java` zeigt Kartenliste des gewählten Stapels
