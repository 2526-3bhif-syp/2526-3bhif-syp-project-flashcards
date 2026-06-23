## Context

Die Benutzeroberfläche der Anwendung wird deklarativ in Java (ohne FXML) über JavaFX-Layouts aufgebaut. In den Views `HomepageView.java`, `HomeView.java` und `FlashcardsView.java` werden `FlowPane`-Knoten verwendet, um die Stapel bzw. Karten in einem flexiblen Raster anzuzeigen. Diese Raster liegen in einer `ScrollPane` mit `fitToWidth = true`. Wenn das Fenster verkleinert oder vergrößert wird, passt sich die ScrollPane zwar an, die `FlowPane` behält jedoch standardmäßig ihre initiale `prefWrapLength` (400) bei.

## Goals / Non-Goals

**Goals:**
- Dynamische Anpassung des Spaltenlayouts bei Änderung der Viewport-Breite.
- Automatische Umbrüche der Kachel-Elemente (Deck- und Card-Tiles), sodass keine Ränder abgeschnitten werden.
- Vermeidung von horizontalem Scrollen durch automatische Spaltenreduktion.

**Non-Goals:**
- Keine Änderung an der MV-Presenter-Struktur oder Datenhaltung.

## Decisions

- **Viewport-Binding über Listener**: Wir nutzen die `viewportBoundsProperty` der `ScrollPane`, da diese die tatsächliche sichtbare Breite der Inhaltsfläche zurückgibt (also bereits um die Breite der vertikalen Scrollleiste korrigiert).
- **Implementierung**:
  In `HomepageView.java`:
  ```java
  recentScrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
      recentContainer.setPrefWrapLength(newValue.getWidth());
  });
  ```
  In `HomeView.java`:
  ```java
  sp.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
      deckGrid.setPrefWrapLength(newValue.getWidth());
  });
  ```
  In `FlashcardsView.java`:
  ```java
  scrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
      cardsGrid.setPrefWrapLength(newValue.getWidth());
  });
  ```

## Risks / Trade-offs

- **Performance bei schnellem Resizen**: Die kontinuierliche Berechnung der Kachelplatzierung im FlowPane bei Fenstergrößenänderungen benötigt minimal Rechenleistung, ist bei der Anzahl der Elemente in JavaFX jedoch vollkommen flüssig (< 1ms).
