package at.htlleonding.flashcards;

import at.htlleonding.flashcards.presenter.MainPresenter;
import at.htlleonding.flashcards.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class using the MVP (Model-View-Presenter) pattern.
 * Layout is created purely in code.
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) {
        // Initialize the view and presenter
        MainView mainView = new MainView();
        MainPresenter presenter = new MainPresenter(mainView);

        // Setup the scene with the view provided by the presenter
        Scene scene = new Scene(presenter.getView(), 640, 480);
        
        stage.setTitle("Flashcards (MVP)");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
