### Requirement: Share-Buttons in StatisticView
`StatisticView.java` SHALL für jeden Chart-Container einen Share-Button bereitstellen und Share-Callbacks via Setter registrierbar machen (analog zu bestehenden Button-Callbacks im MVP-Pattern).

#### Scenario: Share-Callback registrieren
- **WHEN** `MainPresenter.java` einen Share-Handler via `setOnShareEinschaetzung(Runnable)` bzw. `setOnShareKartenProTag(Runnable)` registriert
- **THEN** wird der Handler beim Klick auf den jeweiligen Share-Button aufgerufen

#### Scenario: Chart-Node für Snapshot bereitstellen
- **WHEN** der Share-Handler aufgerufen wird
- **THEN** stellt `StatisticView.java` den jeweiligen Chart-Container-Node bereit, sodass `MainPresenter.java` den Screenshot erstellen kann
