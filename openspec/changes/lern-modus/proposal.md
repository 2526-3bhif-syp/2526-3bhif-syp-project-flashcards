## Why

Das Kernziel der Anwendung ist das aktive Lernen. Ohne Lern-Modus können Nutzer Karten nur verwalten, aber nicht damit lernen. Der Lern-Algorithmus priorisiert schwierige Karten, um den Lernerfolg zu maximieren. Dies entspricht US-003 aus dem FSD.

## What Changes

- Nutzer können den Lern-Modus für einen Stapel starten
- Karten werden einzeln angezeigt: zuerst die Vorderseite, nach Aufdecken die Rückseite
- Nach dem Aufdecken gibt der Nutzer eine Selbsteinschätzung ab: Falsch, Schwierig, Ok, Leicht
- Der Algorithmus wählt die nächste Karte basierend auf den Einschätzungen (BR-003)
- Die Einschätzungen werden als `LernFortschritt` pro Karte protokolliert (Basis für Statistiken)

## Capabilities

### New Capabilities

- `learn-mode`: Interaktiver Lern-Modus mit gewichtetem Karten-Algorithmus und Selbsteinschätzung
- `learning-progress`: Protokollierung von Selbsteinschätzungen je Karte mit Zeitstempel

### Modified Capabilities

## Impact

Betroffen: `FlashcardsView.java` (neue Lern-Ansicht), `MainPresenter.java` (Algorithmus-Logik), `Card.java` / `LernFortschritt` (Datenmodell-Erweiterung)
