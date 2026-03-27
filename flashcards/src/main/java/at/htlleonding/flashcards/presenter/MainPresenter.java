package at.htlleonding.flashcards.presenter;

import at.htlleonding.flashcards.view.MainView;

/**
 * Presenter for the main screen.
 * Orchestrates interaction between model and view.
 */
public class MainPresenter {
    private final MainView view;

    public MainPresenter(MainView view) {
        this.view = view;
        // In the future, model interaction logic goes here
    }

    public MainView getView() {
        return view;
    }
}
