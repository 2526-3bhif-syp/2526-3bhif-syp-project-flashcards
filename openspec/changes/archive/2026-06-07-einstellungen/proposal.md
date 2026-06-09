## Why

Bisher ist die Anwendung nur auf Englisch verfügbar. Um die Benutzbarkeit zu verbessern, soll ein Übersetzungssystem (Lokalisierung) eingeführt werden. Dies ermöglicht es Benutzern, zwischen Deutsch und Englisch zu wechseln. Das Übersetzungs- und Einstellungssystem adressiert das Issue #57 und erweitert die Barrierefreiheit der Anwendung, was über die ursprünglichen Einschränkungen im FSD (wo Mehrsprachigkeit ein Nicht-Ziel war) hinausgeht.

## What Changes

- Einführung eines Übersetzungssystems mit Ressourcenbündeln (`messages.properties` für Englisch, `messages_de.properties` für Deutsch).
- Implementierung des `TranslationProvider` zur Steuerung der Sprache und Bereitstellung von JavaFX-Bindings zur dynamischen UI-Aktualisierung im laufenden Betrieb.
- Ausarbeitung der `SettingsView` mit einem Dropdown-Menü zur Sprachauswahl.
- Persistierung der Sprache in `settings.properties`, damit die Einstellung nach einem Neustart erhalten bleibt.
- Umstellung aller Views (`HomeView`, `FlashcardsView`, `SidebarView`, `NavbarView`, `StatisticView`, `StudyView`) und Dialoge auf lokalisierte Texte.

## Capabilities

### New Capabilities

- `settings-management`: Verwaltung und Persistierung von Benutzereinstellungen (Sprachauswahl).
- `localization`: Dynamische Übersetzung der Benutzeroberfläche zur Laufzeit.

### Modified Capabilities

- `navigation`: Anpassung der Sidebar-Navigation zur Entkopplung interner Routing-Schlüssel von sichtbaren Bezeichnern.

## Impact

Betroffen:
- `SettingsView.java` (UI für die Sprachauswahl)
- `MainPresenter.java` (Verdrahtung des Sprachwechsels)
- `SidebarView.java` (Lokalisierung der Tooltips und Entkopplung des Routings)
- Alle anderen Views und Dialoge (Einbindung des `TranslationProvider` für UI-Texte)
