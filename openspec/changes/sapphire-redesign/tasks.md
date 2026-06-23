## 1. Theme-Infrastruktur & Einstellungen

- [x] 1.1 Neue Farb-Map `"sapphire"` in `ThemeProvider.java` definieren und befüllen
- [x] 1.2 `"sapphire"` als Option in das Theme-Dropdown von `SettingsView.java` aufnehmen
- [x] 1.3 Lokalisierungsdateien `messages_de.properties` und `messages_en.properties` um die Bezeichnungen für das Sapphire-Theme ergänzen
- [x] 1.4 Testen des Theme-Wechsels in den Einstellungen und Überprüfen der Persistenz in `settings.properties`

## 2. Anpassung der Homepage (Startseite)

- [x] 2.1 Buchstapel-Grafik/Illustration unter `/at/htlleonding/flashcards/icons/` ablegen
- [x] 2.2 In `HomepageView.java` ein `ImageView` bzw. `SVGPath` für die Buchstapel-Illustration hinzufügen und positionieren
- [x] 2.3 Streak-Kalender in `HomepageView.java` farblich auf das Sapphire-Farbschema abstimmen
- [x] 2.4 Eckenrundungen (Viertelkreis) von Panels und Kacheln in `HomepageView.java` vereinheitlichen

## 3. Umgestaltung des Stapel-Grids (Mein Stapel)

- [x] 3.1 Ordner-Icons und Symbole (z.B. SVG-Pfade) in `IconManager.java` prüfen und erweitern
- [x] 3.2 Die Methode `createDeckTile` in `HomeView.java` so anpassen, dass die Kacheln wie abgerundete hellblaue Ordnerkacheln gerendert werden
- [x] 3.3 Textdarstellung (Name, Kartenanzahl, Letztes Lerndatum) auf den Ordner-Kacheln in `HomeView.java` platzieren und formatieren
- [x] 3.4 Hover-Effekte auf den Deck-Kacheln (sanfte Farbänderung) implementieren

## 4. Statistiken & Feinjustierung

- [x] 4.1 In `StatisticView.java` das PieChart in ein Donut-Chart (Ringdiagramm) umbauen (durch Hinzufügen einer kreisförmigen StackPane-Überlagerung)
- [x] 4.2 Liniendiagramm in `StatisticView.java` so anpassen, dass die Fläche unter dem Graphen weich blau gefüllt wird
- [x] 4.3 Alle restlichen Controls (Buttons, Inputs, Tooltips) in der App auf das neue Farbschema und die Eckenrundungen hin überprüfen
