package Main;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameFrame extends Application {
    private GamePanel gamePanel;

    @Override
    public void start(Stage stage) {
        gamePanel = new GamePanel();
        Scene scene = new Scene(new Group(gamePanel), gamePanel.screenWidth, gamePanel.screenHeight);
        gamePanel.attachInput(scene);

        stage.setTitle("Game");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        gamePanel.requestFocus();
        gamePanel.startGameLoop();
    }

    @Override
    public void stop() {
        if (gamePanel != null) {
            gamePanel.stopGameLoop();
        }
    }
}