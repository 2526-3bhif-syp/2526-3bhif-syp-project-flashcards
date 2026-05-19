## Context

Der Import-Flow in `MainPresenter.java` wird um eine Duplikat-Prüfung vor dem tatsächlichen Hinzufügen der Stapel erweitert. Der neue `DuplicateDeckActionDialog.java` zeigt dem Nutzer die Optionen und gibt die Entscheidung zurück.

## Goals / Non-Goals

**Goals:**
- Duplikate (case-insensitiv) erkennen
- Nutzer hat volle Kontrolle über Umgang mit Duplikaten
- Kein Dialog bei duplikatfreiem Import

**Non-Goals:**
- Inhaltlicher Vergleich der Karten (nur Namensprüfung)

## Decisions

- **Vergleich**: Stapelnamen werden toLowerCase() verglichen
- **Optionen**: Import All (beide behalten) | Replace Existing (bestehenden ersetzen) | Skip Duplicates (nur neue) | Cancel Import (Abbruch)
- **Dialog**: Eigener `DuplicateDeckActionDialog` als JavaFX-Dialog, gibt enum-Wert zurück
- **Timing**: Prüfung erfolgt nach JSON-Validierung, aber vor Persistenz

## Risks / Trade-offs

- Bei "Import All" entstehen zwei Stapel mit identischem Namen (widerspricht BR-002 bewusst, da Nutzer dies explizit gewählt hat)
