package at.htlleonding.flashcards.view;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.util.function.Consumer;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class AudioHelper {
    private static final Set<MediaPlayer> pendingPlayers = new HashSet<>();

    public static void getDuration(File file, Consumer<String> callback) {
        getDurationInSeconds(file, seconds -> {
            if (seconds <= 0) {
                callback.accept("Unknown");
                return;
            }
            int mins = (int) (seconds / 60);
            int secs = (int) (seconds % 60);
            callback.accept(String.format("%02d:%02d", mins, secs));
        });
    }

    public static void getDurationInSeconds(File file, Consumer<Double> callback) {
        try {
            Media media = new Media(file.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            
            pendingPlayers.add(mediaPlayer);

            mediaPlayer.setOnReady(() -> {
                double seconds = media.getDuration().toSeconds();
                callback.accept(seconds);
                pendingPlayers.remove(mediaPlayer);
                mediaPlayer.dispose();
            });

            mediaPlayer.setOnError(() -> {
                callback.accept(0.0); // Use 0.0 to signal unknown but not necessarily blocking
                pendingPlayers.remove(mediaPlayer);
                mediaPlayer.dispose();
            });
            
            // Safety timeout: if it takes more than 2 seconds, assume it failed but continue
            new Thread(() -> {
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                if (pendingPlayers.contains(mediaPlayer)) {
                    javafx.application.Platform.runLater(() -> {
                        callback.accept(0.0);
                        pendingPlayers.remove(mediaPlayer);
                        mediaPlayer.dispose();
                    });
                }
            }).start();

        } catch (Exception e) {
            callback.accept(0.0);
        }
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
