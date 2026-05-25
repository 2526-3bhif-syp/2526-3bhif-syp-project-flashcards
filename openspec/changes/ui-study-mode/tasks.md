## 1. Study-Start-Button in FlashcardsView

- [x] 1.1 "Study"-Button in `FlashcardsView.java` rechts in der Action-Bar hinzufügen
- [x] 1.2 Callback `onStudyRequested` in `FlashcardsView.java` definieren (Runnable)
- [x] 1.3 Button im Presenter verdrahten: `MainPresenter.java` öffnet bei Klick das Study-Popup

## 2. StudyView-Popup erstellen

- [x] 2.1 Neue Klasse `StudyView.java` in `at.htlleonding.flashcards.view` anlegen
- [x] 2.2 Popup als eigenes `Stage` mit ~1200×800 realisieren
- [x] 2.3 BorderPane-Layout: TOP (Finish), CENTER (Karteninhalt), BOTTOM (Buttons)
- [x] 2.4 Zwei Zustände implementieren: FRONT (Vorderseite + Aufdecken) und BACK (50/50-Split mit beiden Seiten + Einschätzungsbuttons + Next Card)

## 3. Finish-Button

- [x] 3.1 Finish-Button oben rechts im Popup platzieren (grün, `#4CAF50`)
- [x] 3.2 Beim Klick: Popup schließen, Presenter benachrichtigen

## 4. Aufdecken / Next-Card-Buttons

- [x] 4.1 "Aufdecken"-Button unten mittig im Popup platzieren
- [x] 4.2 "Next Card"-Button rechts neben "Aufdecken" platzieren
- [x] 4.3 Aufdecken wechselt in State BACK (Rückseite + Einschätzungsbuttons + Medien einblenden)
- [x] 4.4 Next Card wechselt in State FRONT (nächste Karte anzeigen)

## 5. Einschätzungsbuttons (Falsch, Schwierig, Ok, Leicht)

- [x] 5.1 Vier Buttons unterhalb der Karte im Popup anzeigen (nur im State BACK)
- [x] 5.2 Klick auf Button ruft Presenter-Methode zur Protokollierung auf
- [x] 5.3 Einschätzungsbuttons bleiben nach Auswahl sichtbar

## 6. Medienanzeige in StudyView

- [x] 6.1 `buildImageUI` und `buildAudioPlayerUI` in `FlashcardsView.java` als statische Methoden extrahieren
- [x] 6.2 Bilder auf Vorder- und Rückseite der Karte im Study-Popup anzeigen (State BACK, zentriert)
- [x] 6.3 Audio-Player für Vorder- und Rückseite im Study-Popup einbauen (State BACK)
- [x] 6.4 Audio-Ressourcen beim Schließen des Popups und bei Kartennavigation freigeben (`stopAllAudio`)

## 7. Presenter-Verdrahtung

- [x] 7.1 `MainPresenter.java`: Methode zum Starten des Study-Modus (aktuellen Deck übergeben)
- [x] 7.2 StudyView mit Callbacks für Aufdecken, Next Card, Finish und Einschätzung verbinden
