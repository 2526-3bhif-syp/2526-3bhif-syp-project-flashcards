## Why

Der übergeordnete Change "lern-modus" definiert den Lern-Modus fachlich (Algorithmus, Selbsteinschätzung, Lernfortschritt). Für die Umsetzung fehlt noch die konkrete UI: Der Nutzer muss den Modus starten können, Karten sehen, umdrehen und bewerten können – und das in einem eigenständigen Fenster. Dieses Task (#29) setzt die UI-Komponente des Lern-Modus um und ist Teil von US-003 / Issue #23.

## What Changes

- In `FlashcardsView.java` wird ein "Study"-Button hinzugefügt, rechts unten oberhalb der Import/Export-Buttons.
- Ein neues Popup-Fenster (StudyWindow) wird erstellt, das kleiner als das Hauptfenster ist.
- Im Popup: Vorderseite der Karte anzeigen, "Aufdecken"-Button (unten Mitte), nach Aufdecken: Rückseite einblenden und vier Einschätzungsbuttons.
- "Finish"-Button oben rechts im Popup (grün) zum Beenden der Sitzung.
- "Next Card"-Button rechts neben dem "Aufdecken"-Button.

## Capabilities

### New Capabilities

- `ui-study-mode`: Komplette UI für den Lern-Modus in einem eigenständigen Popup-Fenster.

### Modified Capabilities

- `card-management`: Study-Start-Button in der FlashcardsView.
- `learn-mode`: Die bereits spezifizierte Lernlogik wird an die neue UI angebunden.

## Impact

Betroffen: `FlashcardsView.java` (neuer Study-Button), neue Klasse `StudyView.java` (Popup-UI), `MainPresenter.java` (Verdrahtung und Steuerung des Popups). Die fachliche Logik (Algorithmus, LernFortschritt) bleibt im Presenter.
