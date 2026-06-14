## 1. Datenextraktion & Parsen

- [x] 1.1 Parser-Skript (`parse_fragenkatalog.py`) erstellen
- [x] 1.2 Regex zur Bereinigung von Asciidoctor-Markup und Extraktion von Badges (`Foundation`/`Advanced`) implementieren
- [x] 1.3 State Machine zur fehlerfreien Zuordnung von Fragen zu ihren Themen (Git, Docker, etc.) umsetzen, auch wenn `.Lösung` fehlt
- [x] 1.4 Regex zur Reduzierung von aufeinanderfolgenden Leerzeilen einbauen, um Formatierungslücken zu beheben

## 2. Medien-Konvertierung

- [x] 2.1 Erkennung von `image::` im Antworttext und Zuordnung zur entsprechenden PNG-Datei
- [x] 2.2 Base64-Konvertierung der Bilddaten und Speicherung im Feld `backImageData` der Karte

## 3. Generierung der `decks.json`

- [x] 3.1 JSON-Struktur für das Deck "Fragenkatalog" mit eindeutiger UUID aufbauen
- [x] 3.2 Zusammenführung mit vorhandenen lokalen Decks in `frontend/decks.json` ohne Datenverlust anderer Decks

## 4. UI-Verbesserungen im Lern-Modus

- [x] 4.1 Einbindung von `ScrollPane` in `StudyModeView.java` für scrollbaren Inhalt
- [x] 4.2 Transparentes Styling und Layout-Vgrow für den ScrollPane konfigurieren

## 5. Validierung und Commit

- [x] 5.1 Prüfung, ob die App den Fragenkatalog korrekt einliest und alle 83 Karten inkl. Bilder anzeigt und scrollbar darstellt
- [x] 5.2 Commit der Änderungen im conventional-commit-Stil
