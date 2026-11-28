package Main;

import java.net.URL;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicHandler {
    private static MusicHandler instance;
    private MediaPlayer mediaPlayer;
    private double volume = 0.5;
    
    public static MusicHandler getInstance() {
        if (instance == null) {
            instance = new MusicHandler();
        }
        return instance;
    }
    public void playMusic(String musicFilePath) {
        stopMusic();
        URL musicURL = getClass().getResource(musicFilePath);
        if (musicURL != null) {
            Media media = new Media(musicURL.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(volume);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        } else {
            System.err.println("Music file not found: " + musicFilePath);
        }

    }
    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }
}