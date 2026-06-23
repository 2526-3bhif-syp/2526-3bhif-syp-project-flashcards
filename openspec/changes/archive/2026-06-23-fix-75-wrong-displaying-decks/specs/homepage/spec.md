## ADDED Requirements

### Requirement: Layout-Responsivität und Zeilenumbruch (FlowPane)
Das System MUSS (MUST) das Kachel-Layout für Stapel auf der Startseite (Homepage) dynamisch an die verfügbare Breite des Viewports der ScrollPane anpassen. Es MUSS (MUST) sichergestellt werden, dass keine Kacheln horizontal abgeschnitten werden und keine horizontalen Scrollleisten entstehen.

#### Scenario: Dynamischer Kachel-Umbruch bei Breitenänderung
- **GIVEN** der Benutzer befindet sich auf der Startseite
- **WHEN** die Breite des Anwendungsfensters oder der Spalte geändert wird
- **THEN** wird die Zeilenumbruchbreite (prefWrapLength) des FlowPane-Layouts automatisch an die neue Viewport-Breite der ScrollPane angepasst
- **AND** die Stapelkacheln ordnen sich fließend neu an

### Requirement: Mindesthöhe für empfohlene Stapel
Das System MUSS (MUST) eine Mindesthöhe für den Bereich der empfohlenen Stapel gewährleisten, sodass dieser Bereich bei einer großen Anzahl kürzlich gelernter Stapel nicht vertikal zusammengestaucht oder unsichtbar wird.

#### Scenario: Ausreichende Höhe der empfohlenen Stapel
- **GIVEN** der Benutzer hat eine große Anzahl an kürzlich gelernten Stapeln
- **WHEN** die Startseite geladen oder vertikal verkleinert wird
- **THEN** behält der obere Bereich für die empfohlenen Stapel eine feste Mindesthöhe bei
- **AND** der untere Bereich der kürzlich gelernten Stapel wird über eine vertikale Scrollleiste bedienbar gemacht
