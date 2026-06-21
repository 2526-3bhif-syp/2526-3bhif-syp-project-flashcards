## Context

Die JavaFX-Anwendung nutzt für das gesamte Farbschema und UI-Styling eine zentrale Klasse `ThemeProvider.java`, die im MVP-Muster als Datenquelle für Stylesheets und Inline-Styles dient. Die Views registrieren sich als Listener auf Änderungen des Themes. Das neue Saphirblau-Design erfordert die Definition neuer Farbwerte sowie spezifischer Layout-Anpassungen in einzelnen Views wie `HomepageView.java` und `HomeView.java`, um Kachel- und Bildressourcen korrekt darzustellen.

## Goals / Non-Goals

**Goals:**
- Integration einer neuen Theme-Map `"sapphire"` in `ThemeProvider.java`.
- Hinzufügen der Sapphire-Farbwerte und Tokens (`bg-primary`, `bg-card`, `text-primary`, etc.).
- Umgestaltung der Decks-Kacheln in `HomeView.java` (Ordner-Symbol, runde Ecken).
- Einbau der Buchstapel-Illustration in `HomepageView.java` (unterhalb der kürzlich gelernten Stapel).
- Anpassung von `SettingsView.java` zur Aktivierung und persistenten Speicherung des Sapphire-Themes.
- Anpassung der Diagrammtypen (Donut-Chart/Area-Chart) und deren Farbgestaltung in `StatisticView.java`.

**Non-Goals:**
- Dynamischer Import von externen CSS-Dateien während der Laufzeit.
- Komplette Neugestaltung des Navigationsflusses oder der MVP-Presenter-Struktur.

## Decisions

- **Einführung des Themes `"sapphire"`**:
  - *Rationale*: Ermöglicht die nahtlose Einbettung der neuen Saphirkarten-Ansicht in die bestehende Infrastruktur von `ThemeProvider.java` ohne Umstrukturierung der Views.
  - *Alternativen*: Ein separates CSS-Stylesheet verwenden. JavaFX-CSS ist jedoch schwer dynamisch für einzelne Custom-Controls anzupassen, da viele Stile programmatisch mit `ThemeProvider.get(...)` via Inline-Styles gesetzt werden. Der Ausbau von `ThemeProvider` ist konsistenter.
  
- **Abrunden der Kacheln und Panels per JavaFX CSS-APIs**:
  - *Rationale*: Verwenden von `-fx-background-radius: 15;` und `-fx-border-radius: 15;` direkt in den Views (`HomeView.java`, `HomepageView.java`, `MainView.java`).
  
- **Buchstapel-Visualisierung**:
  - *Rationale*: Hinzufügen einer Bild-Ressource `book_stack.png` (bzw. SVG-Pfad) im Ressourcen-Verzeichnis `/at/htlleonding/flashcards/icons/` und Laden über ein `ImageView` bzw. `SVGPath` in `HomepageView.java`.

- **Statistik-Anpassungen**:
  - *Rationale*: Anpassen von `PieChart` zu einer Ring-Optik (Donut) in `StatisticView.java` durch Überlagerung mit einem zentrierten Kreis (StackPane-basiert) und Konfiguration der Linienfarben des `LineChart` mit einer CSS-Klasse für die Flächenfüllung (Area).

## Risks / Trade-offs

- **Diagramm-Rendering in JavaFX**:
  - *Risiko*: Die optische Transformation von `PieChart` zu einem Donut-Chart in JavaFX kann Layout-Instabilitäten verursachen.
  - *Mitigation*: Verwendung eines `StackPane` in `StatisticView.java`, bei dem ein kleinerer Kreis in der Mitte mit der Hintergrundfarbe des Panels überlagert wird, um den Donut-Effekt sicher und stabil zu erzielen.
