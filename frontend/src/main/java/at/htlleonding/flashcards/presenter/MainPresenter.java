package at.htlleonding.flashcards.presenter;

import at.htlleonding.flashcards.model.*;
import at.htlleonding.flashcards.view.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Modality;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MainPresenter {
    private final MainView view;
    private final Model model;
    private final Map<String, Node> views = new HashMap<>();

    private final Stack<String> backStack = new Stack<>();
    private final Stack<String> forwardStack = new Stack<>();
    private String currentViewName;
    private Deck currentDeck; // null = "all cards" mode
    private List<Card> studyQueue = new ArrayList<>();
    private int studyQueueIdx = 0;
    private Card lastStudyCard;
    private String viewBeforeSearch = null;

    public MainPresenter(MainView view) {
        this.view = view;
        this.model = new Model();

        HomeView homeView = new HomeView();
        homeView.setOnDeckSelected(this::handleDeckSelected);
        homeView.setOnImportDeckRequested(() -> handleDeckImportRequested(homeView));
        homeView.setOnCreateDeckRequested(() -> handleCreateDeckRequested(homeView));
        homeView.setOnEditDeckRequested(deck -> handleEditDeckRequested(homeView, deck));
        homeView.setOnDeleteDeckRequested(deck -> handleDeleteDeckRequested(homeView, deck));
        homeView.setOnExportDeckRequested(deck -> handleSingleDeckExportRequested(deck, homeView));
        homeView.setOnExportSelectedDecksRequested(decks -> handleDecksExportRequested(decks, homeView));
        homeView.setOnDeleteSelectedDecksRequested(decks -> handleDeleteSelectedDecksRequested(decks, homeView));
        homeView.renderDecks(model.getDecks());

        HomepageView homepageView = new HomepageView();
        homepageView.setOnDeckSelected(this::handleDeckSelected);
        homepageView.setOnEditDeckRequested(deck -> handleEditDeckRequested(homeView, deck));
        homepageView.setOnDeleteDeckRequested(deck -> handleDeleteDeckRequested(homeView, deck));

        FlashcardsView flashcardsView = new FlashcardsView();
        flashcardsView.setOnAddCardRequested(() -> handleAddCardRequested(flashcardsView));
        flashcardsView.setOnEditCardRequested(card -> handleEditCardRequested(flashcardsView, card));
        flashcardsView.setOnDeleteCardRequested(card -> handleDeleteCardRequested(flashcardsView, card));
        flashcardsView.setOnImportRequested(() -> handleImportRequested(flashcardsView));
        flashcardsView.setOnStudyRequested(() -> handleStudyRequested(flashcardsView));
        flashcardsView.setOnExportCardRequested(card -> handleCardExportRequested(card, flashcardsView));
        flashcardsView.setOnExportSelectedCardsRequested(cards -> handleCardsExportRequested(cards, flashcardsView));
        flashcardsView.setOnDeleteSelectedCardsRequested(cards -> handleDeleteSelectedCardsRequested(cards, flashcardsView));
        flashcardsView.setDeckNameResolver(card -> { Deck d = findDeckForCard(card); return d != null ? d.getName() : null; });
        flashcardsView.setOnStartLearnModeRequested(this::handleStartLearnMode);

        StudyModeView studyModeView = new StudyModeView();
        studyModeView.setOnRatingSelected(this::handleLearnRating);
        studyModeView.setOnStopRequested(this::handleStopLearnMode);

        StatisticView statisticView = new StatisticView();
        statisticView.setOnShareEinschaetzung(
                () -> shareChart(statisticView.getEinschaetzungChartNode(), statisticView));
        statisticView.setOnShareKartenProTag(
                () -> shareChart(statisticView.getKartenProTagChartNode(), statisticView));

        views.put("Home", homepageView);
        views.put("Decks", homeView);
        views.put("Flashcards", flashcardsView);
        views.put("StudyMode", studyModeView);
        views.put("Statistic", statisticView);
        views.put("Settings", new SettingsView());

        view.getSidebar().setOnNavigationAction(this::handleNavigation);
        view.getNavbar().setOnSearchTextChanged(this::handleSearch);

        navigateTo("Home", false);

        ThemeProvider.addThemeListener(this::applyThemeToAllViews);
    }

    // ── Search ─────────────────────────────────────────────────────────────

    private void handleSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            if (viewBeforeSearch != null) {
                String targetView = viewBeforeSearch;
                viewBeforeSearch = null;
                if (targetView.equals(currentViewName)) {
                    if ("Flashcards".equals(currentViewName)) {
                        refreshFlashcardsView();
                    } else if ("Home".equals(currentViewName)) {
                        refreshHomepageView();
                    } else if ("Decks".equals(currentViewName)) {
                        refreshHomeView();
                    }
                } else {
                    navigateTo(targetView, false);
                }
            } else {
                if ("Flashcards".equals(currentViewName)) {
                    refreshFlashcardsView();
                } else if ("Home".equals(currentViewName)) {
                    refreshHomepageView();
                } else if ("Decks".equals(currentViewName)) {
                    refreshHomeView();
                }
            }
            return;
        }

        if (viewBeforeSearch == null) {
            viewBeforeSearch = currentViewName;
        }

        List<Card> filteredCards = model.searchCards(query);

        FlashcardsView fView = (FlashcardsView) views.get("Flashcards");
        fView.setDeckInfo("Search Results", "Found " + filteredCards.size() + " card(s) for '" + query + "'", null);
        fView.renderCards(filteredCards);

        if (!"Flashcards".equals(currentViewName)) {
            navigateTo("Flashcards", true);
        }
    }

    // ── Home: deck import ──────────────────────────────────────────────────

    private void handleDeckImportRequested(HomeView homeView) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import Deck");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        File file = fc.showOpenDialog(homeView.getScene().getWindow());
        if (file == null) return;

        try {
            List<Deck> imported = model.getPersistence().importDecksFromJSON(file);

            if (imported == null || imported.isEmpty()) {
                alert(Alert.AlertType.INFORMATION, "No decks found in the imported file.");
                return;
            }

            List<Deck> existingDecks = model.getDecks();
            boolean hasDuplicates = imported.stream().anyMatch(imp ->
                existingDecks.stream().anyMatch(ex ->
                    ex.getName() != null &&
                    ex.getName().trim().equalsIgnoreCase(imp.getName() != null ? imp.getName().trim() : "")
                )
            );

            DuplicateDeckActionDialog.Action action = DuplicateDeckActionDialog.Action.ALLOW_ALL;
            if (hasDuplicates) {
                DuplicateDeckActionDialog dialog = new DuplicateDeckActionDialog((Stage) homeView.getScene().getWindow());
                action = dialog.showAndWait().orElse(DuplicateDeckActionDialog.Action.CANCEL);
            }

            if (action == DuplicateDeckActionDialog.Action.CANCEL) return;

            int count = 0;
            for (Deck deck : imported) {
                Deck existing = existingDecks.stream()
                    .filter(ex -> ex.getName() != null &&
                                  ex.getName().trim().equalsIgnoreCase(deck.getName() != null ? deck.getName().trim() : ""))
                    .findFirst().orElse(null);
                boolean isDuplicate = existing != null;

                if (isDuplicate) {
                    if (action == DuplicateDeckActionDialog.Action.SKIP) continue;
                    if (action == DuplicateDeckActionDialog.Action.REPLACE) model.removeDeck(existing);
                    // ALLOW_ALL: regenerate UUID so both decks stay independent
                    if (action == DuplicateDeckActionDialog.Action.ALLOW_ALL) {
                        deck.setId(java.util.UUID.randomUUID().toString());
                    }
                }
                // Also guard against UUID collision with any existing deck (e.g. non-duplicate name but same UUID from export)
                boolean uuidCollision = existingDecks.stream()
                    .anyMatch(ex -> ex.getId() != null && ex.getId().equals(deck.getId()));
                if (uuidCollision) {
                    deck.setId(java.util.UUID.randomUUID().toString());
                }
                model.addDeck(deck);
                count++;
            }

            refreshHomeView();
            alert(Alert.AlertType.INFORMATION, count + " deck(s) imported successfully.");
        } catch (Exception e) {
            alert(Alert.AlertType.ERROR, "Deck could not be loaded. Please check the JSON format.\n\nDetails: " + e.getMessage());
        }
    }

    // ── Home: deck CRUD ────────────────────────────────────────────────────

    private void handleCreateDeckRequested(HomeView homeView) {
        Stage owner = (Stage) homeView.getScene().getWindow();
        CreateDeckDialog dialog = new CreateDeckDialog(owner);
        dialog.showAndWait().ifPresent(deckResult -> {
            Deck newDeck = new Deck(deckResult.name(), deckResult.description(), deckResult.iconId());
            model.addOrMergeDeck(newDeck);
            refreshHomeView();
            refreshHomepageView();
        });
    }

    private void handleEditDeckRequested(HomeView homeView, Deck deck) {
        Stage owner = (Stage) homeView.getScene().getWindow();
        CreateDeckDialog dialog = new CreateDeckDialog(owner, deck);
        dialog.showAndWait().ifPresent(deckResult -> {
            deck.setName(deckResult.name());
            deck.setDescription(deckResult.description());
            deck.setIconId(deckResult.iconId());
            model.updateDeck(deck);
            refreshHomeView();
            refreshHomepageView();
        });
    }

    private void handleSingleDeckExportRequested(Deck deck, HomeView homeView) {
        File file = saveDialog(homeView, deck.getName().replaceAll("\\s+", "_") + ".json");
        if (file == null) return;
        try {
            model.getPersistence().exportToJSON(deck, file);
        } catch (IOException e) {
            alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage());
        }
    }

    private void handleDecksExportRequested(List<Deck> decks, HomeView homeView) {
        File file = saveDialog(homeView, "decks_export.json");
        if (file == null) return;
        try {
            model.getPersistence().exportDecksToJSON(decks, file);
            homeView.exitSelectMode();
            refreshHomeView();
            refreshHomepageView();
            alert(Alert.AlertType.INFORMATION, decks.size() + " deck(s) exported successfully.");
        } catch (IOException e) {
            alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage());
        }
    }

    private void handleDeleteSelectedDecksRequested(List<Deck> decks, HomeView homeView) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Decks");
        confirm.setHeaderText("Delete " + decks.size() + " deck(s)?");
        confirm.setContentText("This action cannot be undone.");
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == javafx.scene.control.ButtonType.OK) {
                decks.forEach(model::removeDeck);
                if (decks.contains(currentDeck)) {
                    currentDeck = null;
                    refreshFlashcardsView();
                }
                homeView.exitSelectMode();
                refreshHomeView();
                refreshHomepageView();
            }
        });
    }

    private void handleDeleteDeckRequested(HomeView homeView, Deck deck) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Deck");
        alert.setHeaderText("Are you sure you want to delete the deck \"" + deck.getName() + "\"?");
        alert.setContentText("This deck contains " + deck.getCardCount() + " cards. This action cannot be undone.");

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == javafx.scene.control.ButtonType.OK) {
                model.removeDeck(deck);
                if (deck == currentDeck) {
                    currentDeck = null;
                    refreshFlashcardsView();
                }
                refreshHomeView();
                refreshHomepageView();
            }
        });
    }

    // ── Flashcards: card import / export ───────────────────────────────────

    private void handleImportRequested(FlashcardsView flashcardsView) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import Cards");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        File file = fc.showOpenDialog(flashcardsView.getScene().getWindow());
        if (file != null) performImport(file, flashcardsView);
    }

    private void performImport(File file, FlashcardsView flashcardsView) {
        try {
            Deck importedDeck = model.getPersistence().importCardsFromJSON(file);

            if (importedDeck == null || importedDeck.getCards().isEmpty()) {
                alert(Alert.AlertType.INFORMATION, "No cards found in the imported file.");
                return;
            }

            Deck resolved = currentDeck;
            if (resolved == null) {
                resolved = pickDeck((Stage) flashcardsView.getScene().getWindow());
                if (resolved == null) return;
            }
            final Deck deck = resolved;

            List<Card> validImportedCards = importedDeck.getCards().stream()
                .filter(c -> c != null &&
                             c.getQuestion() != null && !c.getQuestion().isBlank() &&
                             c.getAnswer() != null && !c.getAnswer().isBlank())
                .toList();

            if (validImportedCards.isEmpty()) {
                alert(Alert.AlertType.WARNING, "No valid cards (missing question or answer) found.");
                return;
            }

            boolean hasDuplicates = validImportedCards.stream().anyMatch(ic ->
                deck.getCards().stream().anyMatch(ex ->
                    ex.getQuestion() != null &&
                    ex.getQuestion().trim().equalsIgnoreCase(ic.getQuestion().trim())
                )
            );

            DuplicateActionDialog.Action action = DuplicateActionDialog.Action.ALLOW_ALL;
            if (hasDuplicates) {
                DuplicateActionDialog dialog = new DuplicateActionDialog((Stage) flashcardsView.getScene().getWindow());
                action = dialog.showAndWait().orElse(DuplicateActionDialog.Action.CANCEL);
            }

            if (action == DuplicateActionDialog.Action.CANCEL) return;

            int count = 0;
            for (Card c : validImportedCards) {
                Card existing = deck.getCards().stream()
                    .filter(ex -> ex.getQuestion() != null &&
                                  ex.getQuestion().trim().equalsIgnoreCase(c.getQuestion().trim()))
                    .findFirst().orElse(null);
                boolean isDuplicate = existing != null;

                if (isDuplicate) {
                    if (action == DuplicateActionDialog.Action.SKIP) continue;
                    if (action == DuplicateActionDialog.Action.REPLACE) deck.removeCard(existing);
                    // ALLOW_ALL: regenerate UUID so both cards stay independent
                    if (action == DuplicateActionDialog.Action.ALLOW_ALL) {
                        c.setId(java.util.UUID.randomUUID().toString());
                    }
                }
                // Guard against UUID collision with any card already in the deck
                boolean uuidCollision = deck.getCards().stream()
                    .anyMatch(ex -> ex.getId() != null && ex.getId().equals(c.getId()));
                if (uuidCollision) {
                    c.setId(java.util.UUID.randomUUID().toString());
                }
                deck.addCard(c);
                count++;
            }

            model.updateDeck(deck);
            refreshFlashcardsView();
            alert(Alert.AlertType.INFORMATION, count + " cards imported successfully.");

        } catch (Exception e) {
            alert(Alert.AlertType.ERROR, "Cards could not be imported. Please check the JSON format.\n\nDetails: " + e.getMessage());
        }
    }

    private void handleCardExportRequested(Card card, FlashcardsView flashcardsView) {
        File file = saveDialog(flashcardsView, "card_export.json");
        if (file == null) return;
        try {
            model.getPersistence().exportCardToJSON(card, file);
        } catch (IOException e) {
            alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage());
        }
    }

    private void handleCardsExportRequested(List<Card> cards, FlashcardsView flashcardsView) {
        File file = saveDialog(flashcardsView, "cards_export.json");
        if (file == null) return;
        try {
            model.getPersistence().exportCardsToJSON(cards, file);
            flashcardsView.exitSelectMode();
            refreshFlashcardsView();
        } catch (IOException e) {
            alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage());
        }
    }

    // ── Flashcards: card CRUD ──────────────────────────────────────────────

    private void handleAddCardRequested(FlashcardsView flashcardsView) {
        Stage owner = (Stage) flashcardsView.getScene().getWindow();
        Deck deck = currentDeck != null ? currentDeck : pickDeck(owner);
        if (deck == null) return;
        CreateCardDialog dialog = new CreateCardDialog(owner);
        dialog.showAndWait().ifPresent(newCard -> {
            deck.addCard(newCard);
            model.updateDeck(deck);
            refreshFlashcardsView();
        });
    }

    private void handleEditCardRequested(FlashcardsView flashcardsView, Card cardToEdit) {
        Stage owner = (Stage) flashcardsView.getScene().getWindow();
        Deck deck = currentDeck != null ? currentDeck : findDeckForCard(cardToEdit);
        if (deck == null) return;
        CreateCardDialog dialog = new CreateCardDialog(owner, cardToEdit);
        dialog.showAndWait().ifPresent(updatedCard -> {
            deck.updateCard(updatedCard);
            model.updateDeck(deck);
            refreshFlashcardsView();
        });
    }

    private void handleDeleteCardRequested(FlashcardsView flashcardsView, Card cardToDelete) {
        Deck deck = currentDeck != null ? currentDeck : findDeckForCard(cardToDelete);
        if (deck == null) return;
        deck.removeCard(cardToDelete);
        model.updateDeck(deck);
        flashcardsView.clearSelectedCard();
        refreshFlashcardsView();
    }

    private void handleDeleteSelectedCardsRequested(List<Card> cards, FlashcardsView flashcardsView) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Cards");
        confirm.setHeaderText("Delete " + cards.size() + " card(s)?");
        confirm.setContentText("This action cannot be undone.");
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == javafx.scene.control.ButtonType.OK) {
                for (Card card : cards) {
                    Deck deck = currentDeck != null ? currentDeck : findDeckForCard(card);
                    if (deck != null) {
                        deck.removeCard(card);
                        model.updateDeck(deck);
                    }
                }
                flashcardsView.clearSelectedCard();
                flashcardsView.exitSelectMode();
                refreshFlashcardsView();
            }
        });
    }

    // ── study mode ─────────────────────────────────────────────────────────

    private void handleStudyRequested(FlashcardsView flashcardsView) {
        if (currentDeck == null) {
            alert(Alert.AlertType.WARNING, "Please select a deck first.");
            return;
        }
        List<Card> cards = currentDeck.getCards();
        if (cards.isEmpty()) {
            alert(Alert.AlertType.WARNING, "This deck has no cards.");
            return;
        }

        StudyView studyView = new StudyView(cards);
        studyView.setOnSessionEnd(() -> {
            if (currentDeck != null) {
                model.recordDeckStudied(currentDeck.getId());
                model.updateDeck(currentDeck);
            } else {
                model.addStreakDate(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            refreshHomepageView();
            refreshHomeView();
        });
        studyView.show();
    }

    // ── navigation ─────────────────────────────────────────────────────────

    private void handleDeckSelected(Deck deck) {
        currentDeck = deck;
        FlashcardsView fView = (FlashcardsView) views.get("Flashcards");
        fView.setDeckInfo(deck.getName(), deck.getDescription() != null ? deck.getDescription() : "", deck.getIconId());
        fView.renderCards(deck.getCards());
        navigateTo("Flashcards", true);
    }

    private void handleNavigation(String destination) {
        if (destination.equals("Back")) goBack();
        else if (destination.equals("Forward")) goForward();
        else {
            navigateTo(destination, true);
        }
    }

    private void applyThemeToAllViews() {
        view.applyTheme();
        view.getSidebar().applyTheme();
        view.getNavbar().applyTheme();
        for (Node v : views.values()) {
            if (v instanceof HomeView hv) hv.applyTheme();
            else if (v instanceof HomepageView hpv) hpv.applyTheme();
            else if (v instanceof FlashcardsView fv) fv.applyTheme();
            else if (v instanceof StudyModeView smv) smv.applyTheme();
            else if (v instanceof SettingsView sv) sv.applyTheme();
            else if (v instanceof StatisticView sv) sv.applyTheme();
        }
    }

    private void navigateTo(String destination, boolean addToHistory) {
        if (destination.equals(currentViewName)) return;

        if (destination.equals("Home")) {
            refreshHomepageView();
        } else if (destination.equals("Decks")) {
            refreshHomeView();
        } else if (destination.equals("Statistic")) {
            ((StatisticView) views.get("Statistic")).refresh(model.getDecks());
        } else if (destination.equals("Flashcards") && currentDeck == null) {
            FlashcardsView fView = (FlashcardsView) views.get("Flashcards");
            fView.clearDeckInfo();
            List<Card> allCards = model.getDecks().stream()
                    .flatMap(d -> d.getCards().stream())
                    .collect(Collectors.toList());
            fView.renderCards(allCards);
        }

        Node targetView = views.get(destination);
        if (targetView != null) {
            if (addToHistory && currentViewName != null) {
                backStack.push(currentViewName);
                forwardStack.clear();
            }
            currentViewName = destination;
            view.setView(targetView);
            updateArrowStates();
        }
    }

    private void goBack() {
        if (!backStack.isEmpty()) {
            forwardStack.push(currentViewName);
            currentViewName = backStack.pop();
            view.setView(views.get(currentViewName));
            updateArrowStates();
        }
    }

    private void goForward() {
        if (!forwardStack.isEmpty()) {
            backStack.push(currentViewName);
            currentViewName = forwardStack.pop();
            view.setView(views.get(currentViewName));
            updateArrowStates();
        }
    }

    private void updateArrowStates() {
        view.getSidebar().setBackEnabled(!backStack.isEmpty());
        view.getSidebar().setForwardEnabled(!forwardStack.isEmpty());
    }

    // ── learn mode ─────────────────────────────────────────────────────────

    private void handleStartLearnMode() {
        if (currentDeck == null || currentDeck.getCards().isEmpty()) {
            alert(Alert.AlertType.WARNING, "Der Stapel enthält keine Karten.");
            return;
        }
        studyQueue = new ArrayList<>(currentDeck.getCards());
        Collections.shuffle(studyQueue);
        studyQueueIdx = 0;
        lastStudyCard = studyQueue.get(0);
        StudyModeView studyView = (StudyModeView) views.get("StudyMode");
        studyView.setDeckName(currentDeck.getName());
        studyView.showCard(lastStudyCard);
        navigateTo("StudyMode", true);
    }

    private void handleLearnRating(String rating) {
        if (lastStudyCard == null) return;
        lastStudyCard.getStudyHistory().add(new StudyRecord(rating));
        model.updateDeck(currentDeck);
        studyQueueIdx++;
        StudyModeView studyView = (StudyModeView) views.get("StudyMode");
        if (studyQueueIdx >= studyQueue.size()) {
            studyView.showSessionDone();
            return;
        }
        lastStudyCard = studyQueue.get(studyQueueIdx);
        studyView.showCard(lastStudyCard);
    }

    private void handleStopLearnMode() {
        studyQueue.clear();
        studyQueueIdx = 0;
        lastStudyCard = null;
        if (currentDeck != null) {
            model.recordDeckStudied(currentDeck.getId());
        } else {
            model.addStreakDate(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        refreshHomepageView();
        refreshHomeView();
        navigateTo("Flashcards", true);
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private void refreshHomeView() {
        HomeView hv = (HomeView) views.get("Decks");
        hv.renderDecks(model.getDecks());
    }

    private void refreshHomepageView() {
        HomepageView hpView = (HomepageView) views.get("Home");
        if (hpView == null) return;

        List<Deck> decks = model.getDecks();

        // Recommended: oldest lastStudied first, never studied (null) first
        List<Deck> recommended = new ArrayList<>(decks);
        recommended.sort((d1, d2) -> {
            if (d1.getLastStudied() == null && d2.getLastStudied() == null) return 0;
            if (d1.getLastStudied() == null) return -1;
            if (d2.getLastStudied() == null) return 1;
            return d1.getLastStudied().compareTo(d2.getLastStudied());
        });

        // Recent: newest lastStudied first, filter out null
        List<Deck> recent = decks.stream()
                .filter(d -> d.getLastStudied() != null)
                .sorted((d1, d2) -> d2.getLastStudied().compareTo(d1.getLastStudied()))
                .collect(Collectors.toList());

        List<String> streaks = model.getStreakDates();
        int streakCount = model.calculateCurrentStreak();

        hpView.setData(recommended, recent, streaks, streakCount);
    }

    private void shareChart(Node chartNode, StatisticView sv) {
        if (chartNode == null) return;
        VBox container = (VBox) chartNode;

        // Hide the share-button bar so it doesn't appear in the screenshot
        Node buttonBar = container.getChildren().get(0);
        buttonBar.setVisible(false);
        buttonBar.setManaged(false);

        String stapel = sv.getSelectedDeckLabel();
        String zeitraum = sv.getSelectedTimeframeLabel();
        Label filterLabel = new Label("Stapel: " + stapel + " | Zeitraum: " + zeitraum);
        filterLabel.setStyle("-fx-font-size: 12px; -fx-padding: 4 8 8 8; -fx-text-fill: "
                + ThemeProvider.get("text-muted") + ";");
        container.getChildren().add(filterLabel);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.web(ThemeProvider.get("bg-card")));
        WritableImage image = container.snapshot(params, null);

        container.getChildren().remove(filterLabel);
        buttonBar.setVisible(true);
        buttonBar.setManaged(true);

        showSharePreview(image);
    }

    private void showSharePreview(WritableImage image) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Statistik teilen");

        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(600);

        Button copyBtn = new Button("In Zwischenablage kopieren");
        copyBtn.setOnAction(e -> {
            ClipboardContent content = new ClipboardContent();
            content.putImage(image);
            Clipboard.getSystemClipboard().setContent(content);
        });

        Button saveBtn = new Button("Speichern...");
        saveBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Statistik speichern");
            fc.setInitialFileName("statistik.png");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
            File file = fc.showSaveDialog(dialog);
            if (file == null) return;
            new Thread(() -> {
                try {
                    int w = (int) image.getWidth();
                    int h = (int) image.getHeight();
                    BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                    javafx.scene.image.PixelReader reader = image.getPixelReader();
                    for (int y = 0; y < h; y++)
                        for (int x = 0; x < w; x++)
                            buf.setRGB(x, y, reader.getArgb(x, y));
                    ImageIO.write(buf, "png", file);
                } catch (IOException ex) {
                    javafx.application.Platform.runLater(() ->
                            alert(Alert.AlertType.ERROR, "Speichern fehlgeschlagen: " + ex.getMessage()));
                }
            }).start();
        });

        Button closeBtn = new Button("Schließen");
        closeBtn.setOnAction(e -> dialog.close());

        HBox buttons = new HBox(8, copyBtn, saveBtn, closeBtn);
        buttons.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(8, 0, 0, 0));

        VBox root = new VBox(12, imageView, buttons);
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: " + ThemeProvider.get("bg-primary") + ";");

        dialog.setScene(new Scene(root));
        dialog.show();
    }

    private File saveDialog(Node anchor, String initialName) {
        FileChooser fc = new FileChooser();
        fc.setInitialFileName(initialName);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        return fc.showSaveDialog(anchor.getScene().getWindow());
    }

    private void alert(Alert.AlertType type, String message) {
        new Alert(type, message).showAndWait();
    }

    private Deck findDeckForCard(Card card) {
        return model.getDecks().stream()
                .filter(d -> d.getCards().stream().anyMatch(c -> c.getId().equals(card.getId())))
                .findFirst().orElse(null);
    }

    private Deck pickDeck(Stage owner) {
        List<Deck> decks = model.getDecks();
        if (decks.isEmpty()) {
            alert(Alert.AlertType.WARNING, "No decks available. Create a deck first.");
            return null;
        }
        if (decks.size() == 1) return decks.get(0);
        List<String> names = decks.stream().map(Deck::getName).collect(Collectors.toList());
        ChoiceDialog<String> dialog = new ChoiceDialog<>(names.get(0), names);
        dialog.initOwner(owner);
        dialog.setTitle("Select Deck");
        dialog.setHeaderText("Choose the deck:");
        dialog.setContentText("Deck:");
        return dialog.showAndWait()
                .flatMap(name -> decks.stream().filter(d -> d.getName().equals(name)).findFirst())
                .orElse(null);
    }

    private void refreshFlashcardsView() {
        FlashcardsView fView = (FlashcardsView) views.get("Flashcards");
        if (currentDeck != null) {
            fView.setDeckInfo(currentDeck.getName(), currentDeck.getDescription() != null ? currentDeck.getDescription() : "", currentDeck.getIconId());
            fView.renderCards(currentDeck.getCards());
        } else {
            fView.clearDeckInfo();
            List<Card> allCards = model.getDecks().stream()
                    .flatMap(d -> d.getCards().stream())
                    .collect(Collectors.toList());
            fView.renderCards(allCards);
        }
    }

    public MainView getView() { return view; }
}
