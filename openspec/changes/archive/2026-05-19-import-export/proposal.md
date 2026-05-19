## Why

Lernende sollen ihre Stapel sichern und teilen können. Dazu wird ein JSON-basierter Import/Export benötigt, der auch Medien (Base64-kodiert) enthält. Dies entspricht US-004 aus dem FSD.

## What Changes

- Nutzer können einen oder mehrere Stapel als JSON-Datei exportieren
- Nutzer können eine JSON-Datei importieren und die enthaltenen Stapel der Anwendung hinzufügen
- Medien (Bild/Audio) werden als Base64-Strings in der JSON eingebettet (BR-004)
- Import und Export sind separate, klar voneinander getrennte Aktionen in der UI

## Capabilities

### New Capabilities

- `json-export`: Export von Stapeln inkl. aller Karten und Base64-Medien als JSON-Datei
- `json-import`: Import einer JSON-Datei und Hinzufügen der enthaltenen Stapel

### Modified Capabilities

## Impact

Betroffen: `MainPresenter.java`, `NavbarView.java` (Menüpunkte), `Persistence.java`
