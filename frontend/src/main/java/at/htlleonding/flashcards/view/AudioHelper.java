package at.htlleonding.flashcards.view;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.io.File;
import java.util.function.Consumer;

public class AudioHelper {
    public static void getDuration(File file, Consumer<String> callback) {
        getDurationInSeconds(file, seconds -> {
            int mins = (int) (seconds / 60);
            int secs = (int) (seconds % 60);
            callback.accept(String.format("%02d:%02d", mins, secs));
        });
    }

    public static void getDurationInSeconds(File file, Consumer<Double> callback) {
        try {
            Media media = new Media(file.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnReady(() -> {
                double seconds = media.getDuration().toSeconds();
                callback.accept(seconds);
                mediaPlayer.dispose();
            });
            mediaPlayer.setOnError(() -> callback.accept(0.0));
        } catch (Exception e) {
            callback.accept(0.0);
        }
    }
}
