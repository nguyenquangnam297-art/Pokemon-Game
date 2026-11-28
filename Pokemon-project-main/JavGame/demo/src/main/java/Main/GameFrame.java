package Main;

import Battle.FightScreen;
import Entity.Monster;
import Event.WildBattleEvent;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameFrame extends Application {
    private GamePanel gamePanel;
    private Stage primaryStage;
    private Scene overworldScene;
    private String overworldTitle;
    private boolean overworldResizable;
    private boolean overworldFullScreen;
    private KeyCombination overworldFullScreenExitKeyCombination;
    private String overworldFullScreenExitHint;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        gamePanel = new GamePanel();
        StackPane overworldRoot = new StackPane(gamePanel);
        overworldScene = new Scene(overworldRoot, gamePanel.screenWidth, gamePanel.screenHeight);
        gamePanel.widthProperty().bind(overworldScene.widthProperty());
        gamePanel.heightProperty().bind(overworldScene.heightProperty());
        gamePanel.setBattleLauncher(this::startWildBattle);
        gamePanel.attachInput(overworldScene);

        stage.setTitle("Game");
        stage.setResizable(true);
        overworldTitle = stage.getTitle();
        overworldResizable = stage.isResizable();
        overworldFullScreen = stage.isFullScreen();
        overworldFullScreenExitKeyCombination = stage.getFullScreenExitKeyCombination();
        overworldFullScreenExitHint = stage.getFullScreenExitHint();
        stage.setScene(overworldScene);
        stage.sizeToScene();
        stage.show();

        gamePanel.requestFocus();
        gamePanel.startGameLoop();
    }

    private void startWildBattle(WildBattleEvent wildBattle) {
        Monster playerMonster = gamePanel.createBattlePlayerMonster();
        new FightScreen(playerMonster, wildBattle.getWildMonster()).start(primaryStage, () -> {
            wildBattle.endBattle();
            primaryStage.setScene(overworldScene);
            primaryStage.setTitle(overworldTitle);
            primaryStage.setResizable(overworldResizable);
            primaryStage.setFullScreenExitKeyCombination(overworldFullScreenExitKeyCombination);
            primaryStage.setFullScreenExitHint(overworldFullScreenExitHint);
            primaryStage.setFullScreen(overworldFullScreen);
            gamePanel.resumeAfterBattle();
            gamePanel.requestFocus();
        }, playerWon -> {
            if (playerWon) {
                gamePanel.playerData.addXp(50);
            }
        });
    }

    @Override
    public void stop() {
        if (gamePanel != null) {
            gamePanel.stopGameLoop();
        }
    }
}