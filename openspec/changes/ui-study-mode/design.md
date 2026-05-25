## Context

Der übergeordnete Design des "lern-modus" definiert die Algorithmus- und Datenmodell-Architektur. Dieses Design konkretisiert die UI-Komponente als separates Popup-Fenster, das die Lernlogik aus `MainPresenter.java` nutzt.

## Goals / Non-Goals

**Goals:**
- Study-Start-Button in der `FlashcardsView.java` rechts unten positionieren
- Eigenständiges Popup-Fenster (`StudyView.java`) für den Lern-Modus, kleiner als das Hauptfenster
- "Finish"-Button oben rechts (grün) zum Beenden der Sitzung
- "Aufdecken"-Button unten Mitte zum Umdrehen der Karte
- "Next Card"-Button rechts neben dem Aufdecken-Button
- Nach Aufdecken: Vier Einschätzungsbuttons (Falsch, Schwierig, Ok, Leicht) unterhalb der Karte anzeigen
- Bilder auf Vorder- und Rückseite der Karte im Study-Popup anzeigen
- Audio-Dateien auf Vorder- und Rückseite mit Play/Pause-Steuerung im Study-Popup abspielen
- Popup schließt sich bei Finish; bisherige Fortschritte bleiben gespeichert (siehe `learning-progress`)

**Non-Goals:**
- Algorithmus-Logik (wird im Presenter wiederverwendet)
- Persistenz von Lernfortschritten (bereits in `learning-progress` spezifiziert)
- Kein eigenes Navigator-Backend für das Popup

## Decisions

- **Popup statt eigener View**: Der Lern-Modus wird als modales Popup (`Stage`) realisiert, nicht als Austausch der Hauptansicht. Dadurch bleibt der Kartenverwaltungs-Kontext erhalten.
- **StudyView als eigenes JavaFX-Fenster**: Neue Klasse `StudyView.java` im Package `at.htlleonding.flashcards.view`. Sie erhält eine Referenz auf den Presenter für Callbacks.
- **Studienstart-Button**: In `FlashcardsView.java` rechts unten über den bestehenden Import/Export-Buttons positioniert (siehe Issue #29).
- **Layout StudyView**:
  - `BorderPane` als Root
  - TOP: `HBox` mit "Finish"-Button (rechts ausgerichtet, grün `#4CAF50`)
  - CENTER: Kartenanzeige (`VBox` mit Frage/Antwort-Labels, je nach State FRONT/BACK)
  - BOTTOM: `HBox` mit "Aufdecken" (links), "Next Card" (rechts), und nach Aufdecken zusätzlich vier Einschätzungsbuttons
- **Größe**: Popup ~1200×800
- **State**: Zwei Zustände analog zum Design in lern-modus: FRONT (nur Vorderseite + Aufdecken) und BACK (beide Seiten + Einschätzungsbuttons + Next Card)
- **Medienanzeige**: Bilder und Audio werden erst nach dem Aufdecken (State BACK) eingeblendet, damit der Fokus auf dem Text bleibt. Die UI-Hilfsmethoden aus `FlashcardsView.java` (`buildImageUI`, `buildAudioPlayerUI`) werden als statische Methoden wiederverwendet.

## Risks / Trade-offs

- Bei sehr kleinen Stapeln (1–2 Karten) wiederholt sich der Algorithmus schnell; UI zeigt dies transparent an.
