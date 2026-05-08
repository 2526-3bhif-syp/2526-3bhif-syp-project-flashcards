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

    public static File saveTempAudio(String base64Data) {
        try {
            byte[] bytes = java.util.Base64.getDecoder().decode(base64Data);
            File tempFile = File.createTempFile("flashcard_audio_", ".mp3");
            tempFile.deleteOnExit();
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile)) {
                fos.write(bytes);
            }
            return tempFile;
        } catch (Exception e) {
            return null;
        }
    }
}
