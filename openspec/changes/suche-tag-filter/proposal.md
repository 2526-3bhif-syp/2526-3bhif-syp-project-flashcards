## Why

Lernende sollen Karten nicht nur innerhalb eines Stapels, sondern auch stapelübergreifend via Tags finden können. Eine Suchfunktion in der Navbar ermöglicht das schnelle Auffinden von Inhalten. Dies entspricht US-002 (Tag-Filter) und Sprint-3-Feature "Kontextsensitive Suche" aus dem FSD.

## What Changes

- Karten können mit einem oder mehreren Tags versehen werden
- Eine Suchleiste in der `NavbarView` filtert Karten nach Tag oder Text
- Suchergebnisse zeigen Karten aus allen Stapeln (stapelübergreifend)

## Capabilities

### New Capabilities

- `card-tagging`: Hinzufügen, Bearbeiten und Entfernen von Tags an Karteikarten
- `card-search`: Kontextsensitive Suche und Tag-basierter Filter über alle Stapel

### Modified Capabilities

- `card-management`: Karten-Erstellungs- und Bearbeitungsdialog um Tag-Eingabe erweitern

## Impact

Betroffen: `Card.java` (Tags-Feld), `CreateCardDialog.java` (Tag-Eingabe), `NavbarView.java` (Suchleiste), `MainPresenter.java` (Suchlogik), `FlashcardsView.java` oder neue Suchergebnisansicht
