## 1. StudyView Anpassung (Schließen-Event)

- [x] 1.1 `sessionEnded` Boolean-Flag in `StudyView.java` deklarieren
- [x] 1.2 `fireSessionEnd()` Methode in `StudyView.java` anpassen, um das Flag zu prüfen und zu setzen
- [x] 1.3 `fireSessionEnd()` in `stage.setOnHidden` Callback in `StudyView.java` integrieren

## 2. MainPresenter Anpassung (Speichern des Stapels)

- [x] 2.1 `studyView.setOnSessionEnd` Callback in `MainPresenter.java` lokalisieren
- [x] 2.2 Aufruf von `model.updateDeck(currentDeck)` im `setOnSessionEnd` Callback von `MainPresenter.java` hinzufügen
