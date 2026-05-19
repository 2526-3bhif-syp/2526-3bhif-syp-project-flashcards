## Context

JavaFX-Styling erfolgt über CSS-Dateien (`.css`) im Ressourcenverzeichnis. Tooltips werden in den View-Klassen programmatisch gesetzt. Es gibt keinen Design-System-Ansatz – Änderungen direkt an den bestehenden Views.

## Goals / Non-Goals

**Goals:**
- Runde Ecken für Karten, Panels und Buttons via CSS
- Tooltips für alle Icons und nicht selbsterklärenden Buttons
- Konsistente Abstände und Hover-Effekte

**Non-Goals:**
- Komplettes Redesign oder neues Theme-System
- Dark Mode

## Decisions

- **CSS**: `-fx-border-radius` und `-fx-background-radius` für runde Ecken
- **Tooltips**: `Tooltip.install()` direkt in den View-Klassen
- **Hover**: `-fx-cursor: hand` und Farb-Transition via CSS `:hover` Pseudoklasse

## Risks / Trade-offs

- Zu viele CSS-Änderungen können unerwartete Layout-Probleme verursachen – schrittweise testen
