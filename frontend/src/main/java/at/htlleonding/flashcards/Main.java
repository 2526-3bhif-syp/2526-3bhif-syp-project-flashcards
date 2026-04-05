package at.htlleonding.flashcards;

import at.htlleonding.flashcards.presenter.MainPresenter;
import at.htlleonding.flashcards.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        MainView mainView = new MainView();
        MainPresenter presenter = new MainPresenter(mainView);

        Scene scene = new Scene(presenter.getView(), 640, 480);
        
        stage.setTitle("Flashcards");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
