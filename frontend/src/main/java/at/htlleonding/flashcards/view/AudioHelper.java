package at.htlleonding.flashcards.view;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.io.File;
import java.util.function.Consumer;

public class AudioHelper {
    public static void getDuration(File file, Consumer<String> callback) {
        try {
            Media media = new Media(file.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnReady(() -> {
                Duration duration = media.getDuration();
                double seconds = duration.toSeconds();
                int mins = (int) (seconds / 60);
                int secs = (int) (seconds % 60);
                callback.accept(String.format("%02d:%02d", mins, secs));
                mediaPlayer.dispose();
            });
        } catch (Exception e) {
            callback.accept("00:00");
        }
    }
}
