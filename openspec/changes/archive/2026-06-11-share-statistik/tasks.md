## 1. StatisticView – Share-Buttons

- [x] 1.1 Share-Button für Einschätzungsquoten-Chart in `StatisticView.java` hinzufügen (direkt im Chart-Container)
- [x] 1.2 Share-Button für Karten-pro-Tag-Chart in `StatisticView.java` hinzufügen (direkt im Chart-Container)
- [x] 1.3 Share-Buttons deaktivieren (`setDisable(true)`) wenn keine Lernfortschritt-Daten vorhanden sind
- [x] 1.4 Setter `setOnShareEinschaetzung(Runnable)` und `setOnShareKartenProTag(Runnable)` in `StatisticView.java` implementieren
- [x] 1.5 Methoden `getEinschaetzungChartNode()` und `getKartenProTagChartNode()` bereitstellen (liefern den jeweiligen Chart-Container als `Node`)

## 2. Screenshot-Logik

- [x] 2.1 Hilfsmethode `createChartSnapshot(Node chartNode, String stapel, String zeitraum)` in `MainPresenter.java` implementieren: Filter-Label-`VBox` aufbauen, `Node.snapshot()` aufrufen
- [x] 2.2 Filter-Label-Text `"Stapel: <name> | Zeitraum: <zeitraum>"` als `Label` unterhalb des Charts in den Snapshot-Container einfügen
- [x] 2.3 Screenshot als temporäre PNG-Datei in `System.getProperty("java.io.tmpdir")` speichern (`ImageIO.write()`) mit `file.deleteOnExit()`

## 3. Teilen via Desktop

- [x] 3.1 `Desktop.getDesktop().open(file)` aufrufen um die temporäre PNG-Datei mit dem System-Image-Viewer zu öffnen
- [x] 3.2 Fallback implementieren: falls `Desktop.isDesktopSupported()` false → Bild in `Clipboard.getSystemClipboard()` kopieren und Toast/Hinweis anzeigen

## 4. Verdrahtung im Presenter

- [x] 4.1 Share-Handler für Einschätzungsquoten-Chart in `MainPresenter.java` registrieren (`setOnShareEinschaetzung`)
- [x] 4.2 Share-Handler für Karten-pro-Tag-Chart in `MainPresenter.java` registrieren (`setOnShareKartenProTag`)
- [x] 4.3 Aktive Stapel- und Zeitraum-Filterauswahl aus `StatisticView.java` beim Share-Aufruf abfragen und an `createChartSnapshot()` übergeben
