    module at.htlleonding.flashcards {
    requires javafx.controls;
    requires com.fasterxml.jackson.databind;
    requires javafx.media;

    // Export packages so JavaFX and Jackson can access them
    exports at.htlleonding.flashcards;
    exports at.htlleonding.flashcards.view;
    exports at.htlleonding.flashcards.presenter;
    exports at.htlleonding.flashcards.model;

    // Allow reflective access
    opens at.htlleonding.flashcards to javafx.graphics;
    opens at.htlleonding.flashcards.model to com.fasterxml.jackson.databind;
}
