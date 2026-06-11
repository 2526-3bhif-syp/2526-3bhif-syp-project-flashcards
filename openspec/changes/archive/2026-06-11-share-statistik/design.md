## Context

`StatisticView.java` existiert als Stub und wird durch den Change `statistiken` mit zwei Charts befüllt:
- Chart 1: Einschätzungsquoten (z.B. PieChart)
- Chart 2: Karten pro Tag (BarChart)

Beide Charts besitzen aktive Filter: Stapel-ComboBox und Zeitraum-ComboBox. Dieser Change setzt voraus, dass `statistiken` vollständig implementiert ist.

## Goals / Non-Goals

**Goals:**
- Je ein Share-Button direkt bei jeder Chart in `StatisticView.java`
- Screenshot der jeweiligen Chart via `Node.snapshot()` (JavaFX-Standard)
- Filter-Labels (Stapel + Zeitraum) als sichtbare Beschriftung im Screenshot
- Bild über den nativen System-Mechanismus teilen (Desktop-Plattform)

**Non-Goals:**
- Kein Dialog zur Filterauswahl beim Teilen (aktive Filter werden direkt übernommen)
- Kein Cloud-Upload, keine Netzwerkanbindung
- Kein mobiles Share-Sheet

## Decisions

**Screenshot via `Node.snapshot()`**
JavaFX stellt `Node.snapshot(SnapshotParameters, WritableImage)` bereit. Damit wird ein `VBox`-Container (Chart + Filter-Label-Zeile) gerendert, nicht nur die Chart allein. Das liefert ein vollständiges, kontextreiches Bild ohne externe Bibliothek.

Alternative: `Robot.getScreenCapture()` — abgelehnt, da plattformabhängig und schwer zu beschneiden.

**Filter-Labels im Screenshot**
Ein `Label` mit dem Text `"Stapel: <name> | Zeitraum: <zeitraum>"` wird direkt unterhalb der Chart in einen `VBox`-Wrapper eingefügt. Dieser Wrapper wird dann als Ganzes gesnapshot. Das Label wird nur für den Screenshot erzeugt und danach verworfen (nicht dauerhaft in der View sichtbar).

**Teilen via `Desktop.getDesktop()`**
Das Screenshot-Bild wird als temporäre PNG-Datei in `System.getProperty("java.io.tmpdir")` gespeichert (`ImageIO.write()`). Danach wird `Desktop.getDesktop().open(file)` aufgerufen, was den nativen Standard-Image-Viewer öffnet und dem Nutzer das Weiterteilen ermöglicht.

Fallback: Falls `Desktop` nicht unterstützt wird (z.B. Headless-Umgebung), wird das Bild in die Systemzwischenablage kopiert (`Clipboard.getSystemClipboard()`) und ein Toast/Hinweis angezeigt.

Alternative: `Runtime.exec("xdg-open ...")` auf Linux — abgelehnt, da nicht plattformunabhängig und fehleranfällig.

**MVP-Verdrahtung**
`StatisticView.java` liefert die Share-Callbacks über Setter (analog zu bestehenden Button-Callbacks). `MainPresenter.java` implementiert die Share-Logik (Screenshot, Speichern, Öffnen) und registriert sich als Handler.

## Risks / Trade-offs

- [Desktop.open öffnet Viewer, kein echtes Share-Sheet] → Auf Desktop-Betriebssystemen gibt es keinen universellen Share-Dialog wie auf Mobile. `Desktop.open()` ist die nächstbeste plattformübergreifende Lösung.
- [Temporäre Dateien] → Temp-Datei wird beim App-Shutdown nicht automatisch gelöscht. Mitigation: `file.deleteOnExit()` registrieren.
- [Chart noch nicht gerendert] → `snapshot()` muss nach dem JavaFX-Render-Zyklus aufgerufen werden. Mitigation: Aufruf in `Platform.runLater()` wrappen falls nötig.
