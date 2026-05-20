## Why

Lernende möchten Karten kategorisieren können, um den Überblick über ihre Lerninhalte zu behalten. Das Hinzufügen von Labels (Tags) ermöglicht eine strukturierte Organisation der Karten innerhalb und außerhalb von Stapeln. Dies entspricht US-002 aus dem FSD und GitHub Issue #50.

## What Changes

- Karten (`Card`) erhalten eine Liste von Labels/Tags.
- Der `CreateCardDialog` wird um eine Eingabemöglichkeit für Labels erweitert.
- Labels werden persistent in der JSON-Datei gespeichert.
- Labels werden auf der Karte in der Benutzeroberfläche angezeigt.

## Capabilities

### New Capabilities
- `card-tagging`: Verwalten (Hinzufügen, Entfernen) von Labels an Karteikarten.

### Modified Capabilities
- `card-management`: Erweiterung der Karten-Erstellung und -Anzeige um die Label-Funktionalität.

## Impact

Betroffen sind das Datenmodell (`Card.java`), die Persistenzlogik (`Persistence.java`) sowie die UI-Komponenten für die Kartenerstellung (`CreateCardDialog.java`) und die Kartenanzeige.
