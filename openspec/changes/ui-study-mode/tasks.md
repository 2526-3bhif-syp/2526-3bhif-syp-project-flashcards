## 1. Study-Start-Button in FlashcardsView

- [ ] 1.1 "Study"-Button in `FlashcardsView.java` rechts unten in der Action-Bar hinzufügen
- [ ] 1.2 Callback `onStudyRequested` in `FlashcardsView.java` definieren (Runnable)
- [ ] 1.3 Button im Presenter verdrahten: `MainPresenter.java` öffnet bei Klick das Study-Popup

## 2. StudyView-Popup erstellen

- [ ] 2.1 Neue Klasse `StudyView.java` in `at.htlleonding.flashcards.view` anlegen
- [ ] 2.2 Popup als eigenes `Stage` mit 700×500 realisieren
- [ ] 2.3 BorderPane-Layout: TOP (Finish), CENTER (Karteninhalt), BOTTOM (Buttons)
- [ ] 2.4 Zwei Zustände implementieren: FRONT (Vorderseite + Aufdecken) und BACK (beide Seiten + Einschätzungsbuttons + Next Card)

## 3. Finish-Button

- [ ] 3.1 Finish-Button oben rechts im Popup platzieren (grün, `#4CAF50`)
- [ ] 3.2 Beim Klick: Popup schließen, Presenter benachrichtigen

## 4. Aufdecken / Next-Card-Buttons

- [ ] 4.1 "Aufdecken"-Button unten mittig im Popup platzieren
- [ ] 4.2 "Next Card"-Button rechts neben "Aufdecken" platzieren
- [ ] 4.3 Aufdecken wechselt in State BACK (Rückseite + Einschätzungsbuttons einblenden)
- [ ] 4.4 Next Card wechselt in State FRONT (nächste Karte anzeigen)

## 5. Einschätzungsbuttons (Falsch, Schwierig, Ok, Leicht)

- [ ] 5.1 Vier Buttons unterhalb der Karte im Popup anzeigen (nur im State BACK)
- [ ] 5.2 Klick auf Button ruft Presenter-Methode zur Protokollierung auf
- [ ] 5.3 Einschätzungsbuttons bleiben nach Auswahl sichtbar

## 6. Presenter-Verdrahtung

- [ ] 6.1 `MainPresenter.java`: Methode zum Starten des Study-Modus (aktuellen Deck übergeben)
- [ ] 6.2 Algorithmus-Logik aus learn-mode in den Presenter integrieren (gewichtete Zufallsauswahl)
- [ ] 6.3 StudyView mit Callbacks für Aufdecken, Next Card, Finish und Einschätzung verbinden
