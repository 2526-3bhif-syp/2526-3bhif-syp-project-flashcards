## Why

Bei der Anzeige von vielen kürzlich gelernten Stapeln (recent decks) auf der Startseite oder beim Ändern der Fenstergröße kam es zu Darstellungsfehlern. Die Stapelkarten wurden auf der rechten Seite abgeschnitten, da das Layout (eine `FlowPane` innerhalb einer `ScrollPane`) die Zeilenumbruchbreite (`prefWrapLength`) nicht dynamisch anpasste, sondern auf dem Standardwert (400) verblieb. Dies betraf auch die Stapelübersicht (Home) und die Kartenübersicht eines Stapels.

## What Changes

- Einbau eines dynamischen Listeners auf die `viewportBoundsProperty` der `ScrollPane` in `HomepageView.java` (kürzlich gelernte Stapel).
- Einbau des gleichen Listeners in `HomeView.java` (alle Stapel).
- Einbau des gleichen Listeners in `FlashcardsView.java` (Kartenübersicht).
- Dadurch wird `prefWrapLength` der jeweiligen `FlowPane` bei Größenänderungen der `ScrollPane` automatisch aktualisiert.

## Capabilities

### New Capabilities

- `responsive-grid-wrapping`: Die Gitter-Layouts (FlowPane) passen ihre Spaltenanzahl und Umbrüche nun dynamisch an die tatsächliche Breite des Viewports der ScrollPane an, was Darstellungsfehler und abgeschnittene Ränder verhindert.

## Impact

Die Änderung betrifft ausschließlich das GUI-Layout in den Klassen `HomepageView.java`, `HomeView.java` und `FlashcardsView.java`. Logik, Modelle und Persistenz bleiben unberührt.
