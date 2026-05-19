## Why

Die Kernfunktionalität der Anwendung – das Erstellen, Bearbeiten und Löschen von Stapeln und Karteikarten – wird benötigt, damit Lernende ihre Inhalte digital erfassen können. Dies entspricht US-001 aus dem FSD und bildet das Fundament für alle weiteren Features.

## What Changes

- Benutzer können Stapel (Decks) anlegen, umbenennen und löschen
- Benutzer können Karteikarten mit Vorder- und Rückseite (Text) innerhalb eines Stapels erstellen, bearbeiten und löschen
- Karten unterstützen optional ein Medium (Bild oder Audio) pro Seite
- Stapel und Karten werden lokal als JSON persistiert
- Die Hauptansicht zeigt alle Stapel in einer Übersicht (HomeView)
- Eine Sidebar zeigt die Details der ausgewählten Karte

## Capabilities

### New Capabilities

- `deck-management`: Erstellen, Anzeigen, Bearbeiten und Löschen von Stapeln
- `card-management`: Erstellen, Anzeigen, Bearbeiten und Löschen von Karteikarten mit Vorder-/Rückseite und optionalen Medien
- `local-persistence`: Lokales Speichern und Laden aller Daten als JSON

### Modified Capabilities

## Impact

Betroffen: `Model.java`, `Persistence.java`, `MainPresenter.java`, `HomeView.java`, `FlashcardsView.java`, `SidebarView.java`, `CreateCardDialog.java`, `CreateDeckDialog.java`
