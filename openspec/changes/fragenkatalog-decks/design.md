## Context

Der Fragenkatalog liegt als Asciidoctor-Dokument (`fragenkatalog.adoc`) vor. Die Applikation verwendet `frontend/decks.json` zur Persistenz aller Decks. Um den Fragenkatalog als Standarddeck einzubinden, wird das Dokument geparst und ein strukturiertes JSON-Objekt erzeugt, das den Klassen `Deck.java` und `Card.java` entspricht. Die dazugehörigen Grafiken werden in Base64 konvertiert und direkt in das JSON integriert. Um lange Antworten und Grafiken ohne Abschneiden anzuzeigen, wird der Kartenbereich in einen ScrollPane eingebettet.

## Goals / Non-Goals

**Goals:**
- Automatisches Parsen aller 83 Fragen und Antworten aus der `.adoc`-Quelle.
- Einbettung aller 7 relevanten PNG-Grafiken als Base64 in die Karten-Rückseiten.
- Bereinigung von Formatierungs-Markup und Reduzierung aufeinanderfolgender Leerzeilen für eine saubere Textdarstellung in JavaFX.
- Beibehaltung vorhandener Test-Decks in `frontend/decks.json` durch Zusammenführen (Merge).
- Scrollbarkeit von langen Antworten und Bildern im Lern-Modus.

**Non-Goals:**
- Keine dynamische Interpretation von Asciidoctor-Syntax zur Laufzeit in JavaFX.
- Kein Download von Grafiken zur Laufzeit (alle Bilder lokal offline eingebettet).

## Decisions

- **Parser-Technologie**: Ein Python-Skript führt das Parsen der `.adoc`-Datei, das Base64-Encoding der Bilddateien und das Generieren/Mergen der `decks.json` aus.
- **Bereinigungsregeln**:
  - Codeblock-Delimiter (`----`, `++++`, `====`, `|===`) und Typangaben (z.B. `[source,shell]`) werden entfernt.
  - Bild-Tags (`image::*.png[]`) werden aus dem Text entfernt, da die App Bilder über `backImageData` rendert.
  - Bullet-Points (`* ` am Zeilenanfang) werden gesäubert.
  - Aufeinanderfolgende Leerzeilen werden durch Regex `re.sub(r'\n\s*\n(\s*\n)*', '\n\n', text)` auf maximal eine Leerzeile kollabiert.
- **Layout-Änderung in JavaFX**:
  - Der `VBox cardArea` wird in ein transparentes `ScrollPane` eingebettet, welches das flexible Scrollen ermöglicht:
    ```java
    ScrollPane scrollPane = new ScrollPane(cardArea);
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);
    scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    VBox.setVgrow(scrollPane, Priority.ALWAYS);
    ```
- **Merge-Verhalten**: Falls `frontend/decks.json` bereits existiert, wird sie eingelesen. Ein eventuell vorhandenes altes "Fragenkatalog"-Deck wird entfernt, und das neu generierte Deck wird hinzugefügt. Andere Decks (wie `test`) bleiben erhalten.

## Risks / Trade-offs

- **Dateigröße**: Die Einbettung von Grafiken in Base64 bläht `decks.json` auf ca. 2.5 MB auf. Da dies rein lokal auf dem Gerät gelesen wird (SSD), ist die Ladeverzögerung vernachlässigbar (< 50ms in JavaFX).
