module at.htlleonding.flashcards {
    requires javafx.controls;

    // Export packages so JavaFX can access them
    exports at.htlleonding.flashcards;
    exports at.htlleonding.flashcards.view;
    exports at.htlleonding.flashcards.presenter;
    exports at.htlleonding.flashcards.model;

    // Allow reflective access for JavaFX graphics
    opens at.htlleonding.flashcards to javafx.graphics;
}
