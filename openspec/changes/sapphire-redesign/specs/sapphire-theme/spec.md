## ADDED Requirements

### Requirement: Sapphire-Theme in Einstellungen auswählbar
Das System MUSS (MUST) im Einstellungsmenü eine zusätzliche Option namens „Sapphire“ (bzw. „Saphir“) anbieten. Bei Auswahl dieses Themes MÜSSEN sich alle UI-Komponenten (Hintergründe, Texte, Schaltflächen und Ränder) an die saphirblaue Farbpalette anpassen.

#### Scenario: Auswahl des Sapphire-Themes in den Einstellungen
- **WHEN** der Benutzer in den Einstellungen das Dropdown für das Theme öffnet und „Sapphire“ auswählt
- **THEN** ändert sich das Farbschema aller Ansichten unverzüglich auf Saphirblau
- **AND** die Einstellung wird persistent gespeichert

### Requirement: Kachel-Darstellung als saphirblaue Ordner
Das System MUSS (MUST) in der Stapel-Übersicht („Mein Stapel“) und auf der Homepage die Stapel-Kacheln als hellblaue Ordner-Kacheln mit runden Ecken (Radius 15px) darstellen. Jede Kachel MUSS das Ordner-Symbol, den Namen des Stapels, die Anzahl der Karten sowie das letzte Lerndatum („Zuletzt: ...“) anzeigen.

#### Scenario: Laden der Stapel-Übersicht im Sapphire-Theme
- **WHEN** das Sapphire-Theme aktiv ist und die Stapel-Übersicht geladen wird
- **THEN** werden alle Stapel als abgerundete hellblaue Ordnerkacheln mit weißem Ordner-Icon angezeigt
- **AND** es wird die jeweilige Anzahl der Karten sowie das letzte Lerndatum dargestellt

### Requirement: Buchstapel-Illustration auf Startseite
Das System MUSS (MUST) auf der Startseite (Homepage) im linken Bereich der zuletzt gelernten Stapel eine stilisierte Buchstapel-Grafik (mit Federzeichnung/Illustration) einblenden, um das Layout visuell aufzuwerten.

#### Scenario: Anzeige der Buchstapel-Grafik auf der Startseite
- **WHEN** die Startseite geladen wird
- **THEN** wird im linken Spaltenbereich unterhalb der zuletzt gelernten Stapel die Buchstapel-Illustration angezeigt

### Requirement: Diagramme im Saphir-Stil
Das System MUSS (MUST) in der Statistik-Ansicht die Diagramme an das Sapphire-Farbschema anpassen. Das Lernfortschrittsdiagramm MUSS als Donut-Chart (Ringdiagramm) gestaltet sein und die wöchentliche Lernzeit MUSS als weich blau gefülltes Liniendiagramm (AreaChart-Stil) dargestellt werden.

#### Scenario: Laden der Statistik-Ansicht im Sapphire-Theme
- **WHEN** das Sapphire-Theme aktiv ist und die Statistik-Ansicht geladen wird
- **THEN** wird das Lernfortschrittsdiagramm als Donut-Diagramm mit den Saphirblau-Farbabstufungen angezeigt
- **AND** das wöchentliche Lernzeitdiagramm wird als Liniendiagramm mit saphirblauer Hintergrundfüllung unter der Kurve gerendert
