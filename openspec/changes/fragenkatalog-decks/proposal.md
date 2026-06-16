## Why

Aktuell startet die Flashcard-App ohne vordefinierte Inhalte. Für eine optimale First-Time-User-Experience (FTUX) und zum direkten Testen der App soll der offizielle "Fragenkatalog" (für Programmieren und Projektentwicklung) als standardmäßig geladener Beispiel-Stapel bereitstehen. Dieser Katalog deckt wichtige Fachbereiche wie Git, Docker, Kubernetes, UML und Java ab. Zudem muss die Darstellung im Lern-Modus für lange Antworten und Bilder scrollbar gemacht und die Formatierung bereinigt werden.

## What Changes

- Einbindung eines neuen Standard-Stapels "Fragenkatalog" in die lokale `decks.json` und in `default_decks.json`.
- Der Stapel enthält alle 83 Fragen und Antworten aus `fragenkatalog.adoc`.
- Die Karten werden mit den entsprechenden Themen (z. B. `Git`, `Docker`, `Asciidoctor`) und den Schwierigkeitsgraden (z. B. `Foundation`, `Advanced`) getaggt.
- Bilder aus dem Verzeichnis `fragenkatalog_images/` (wie `git-architecture.png`) werden im Base64-Format direkt in die JSON-Struktur der entsprechenden Karten eingebettet (Rückseite/Antwort).
- Asciidoctor-spezifische Markup-Reste (z. B. `[source,shell]`, `----`, `++++`, `====`) und leere Image-Tags werden bei der Extraktion bereinigt und aufeinanderfolgende Leerzeilen kollabiert.
- Einbau eines `ScrollPane` in `StudyModeView.java` zur scrollbaren Ansicht von langen Texten und Grafiken.

## Capabilities

### New Capabilities

- `premade-decks-fragenkatalog`: Die App startet bei einer Erstinstallation direkt mit einem Standard-Stapel mit 83 Lernkarten inklusive eingebetteter Grafiken.
- `scrollable-study-mode`: Unterstützung für scrollbare Karteninhalte (Texte/Bilder) im Lern-Modus.

## Impact

Betroffen ist die Datei `frontend/src/main/resources/at/htlleonding/flashcards/default_decks.json` (neuer Fragenkatalog-Stapel) und `StudyModeView.java` (Scrollbar-Layout). Die restliche Persistenz bleibt unberührt.
