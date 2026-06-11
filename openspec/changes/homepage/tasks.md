## 1. Datenmodell & Persistenz

- [ ] 1.1 `Deck.java` erweitern: Feld `lastStudied` (LocalDateTime) mit Getter/Setter hinzufügen.
- [ ] 1.2 `Persistence.java` anpassen: Serialisierung und Deserialisierung von `lastStudied` in JSON umsetzen (inkl. Null-Handling).
- [ ] 1.3 `Persistence.java` & `Model.java` erweitern: Speichern und Laden der gelernten Tage in/aus `streaks.json` implementieren.

## 2. Navigation & Sidebar

- [ ] 2.1 `SidebarView.java` anpassen: Menüpunkt `"Flashcards"` entfernen, neuen Menüpunkt `"Decks"` (Stapel-Übersicht mit `HomeView`) hinzufügen und `"Home"` für die neue Startseite (`HomepageView`) nutzen.
- [ ] 2.2 `MainPresenter.java` anpassen: Registrierung von `HomeView` unter dem Namen `"Decks"` und `HomepageView` unter dem Namen `"Home"`.
- [ ] 2.3 Übersetzungsschlüssel in den Message-Dateien (`messages_de.properties` und `messages_en.properties`) für die neuen Sidebar-Einträge (`sidebar.decks`, `sidebar.home` etc.) anpassen und veraltete Einträge bereinigen.

## 3. UI Homepage (Startseite)

- [ ] 3.1 `HomepageView.java` (neu) erstellen: Hauptlayout mit `BorderPane` oder `GridPane` aufbauen und die drei Layout-Bereiche definieren.
- [ ] 3.2 Empfohlene Stapel (oben links) in `HomepageView.java` rendern: Kleinere Kacheln für Stapel, die am längsten nicht gelernt wurden.
- [ ] 3.3 Streak-Kalender (unten links) in `HomepageView.java` implementieren: Ein monatliches Raster (GridPane) erstellen, das Tage farblich markiert, an denen gelernt wurde (gelesen aus `streaks.json`), und den aktuellen Streak anzeigt.
- [ ] 3.4 Verlauf der zuletzt gelernten Stapel (rechts) in `HomepageView.java` als vertikale Liste implementieren.

## 4. Presenter-Logik & Event-Handling

- [ ] 4.1 `HomepageView.java` und `MainPresenter.java` verknüpfen: Daten aus dem `Model` abrufen, sortieren und der View zur Verfügung stellen.
- [ ] 4.2 Event-Handling nach Lernsitzung: Nach Abschluss eines Lern-Modus in `MainPresenter.java` oder `StudyView.java` den Zeitstempel `lastStudied` des Decks aktualisieren, den heutigen Tag in `streaks.json` eintragen und die Homepage-Daten neu laden.
- [ ] 4.3 Navigations-Callbacks implementieren: Klick auf Kacheln der Homepage öffnet direkt den zugehörigen Stapel in der Kartenübersicht (`FlashcardsView.java`) oder startet direkt den Lernmodus.
