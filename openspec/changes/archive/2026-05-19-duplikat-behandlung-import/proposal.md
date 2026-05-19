## Why

Beim Import einer JSON-Datei können importierte Stapel denselben Namen wie bestehende haben. Ohne Behandlung würden Daten unkontrolliert überschrieben oder dupliziert. Dies entspricht US-004b aus dem FSD.

## What Changes

- Beim Import wird geprüft, ob ein importierter Stapelname (case-insensitiv) bereits existiert
- Falls Duplikate gefunden werden, erscheint ein Dialog mit vier Optionen: Import All, Replace Existing, Skip Duplicates, Cancel Import
- Bei keinem Duplikat wird ohne Dialog direkt importiert

## Capabilities

### New Capabilities

- `duplicate-deck-handling`: Erkennung von Duplikaten beim Import und Nutzerdialog zur Auflösung

### Modified Capabilities

- `json-import`: Import-Flow um Duplikat-Prüfung erweitert

## Impact

Betroffen: `MainPresenter.java`, `DuplicateDeckActionDialog.java` (neu), `DuplicateActionDialog.java`
