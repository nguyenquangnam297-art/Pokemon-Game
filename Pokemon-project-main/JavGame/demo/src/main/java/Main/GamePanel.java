package Main;

import Data.PlayerData;
import Entity.Monster;
import Entity.MonsterHandler;
import Entity.NPC.NPCHandler;
import Entity.Player;
import Event.EventHandler;
import Event.WildBattleEvent;
import Map.TileHandler;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Objects;
import java.util.function.Consumer;

public class GamePanel extends Canvas {
    private static final int ORIGINAL_TILE_SIZE = 16;
    private static final int SCALE = 3;
    private static final int MAX_SCREEN_COL = 16;
    private static final int MAX_SCREEN_ROW = 12;
    private static final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE;
    private static final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL;
    private static final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW;

    public final int tileSize = TILE_SIZE;
    public final int screenWidth = SCREEN_WIDTH;
    public final int screenHeight = SCREEN_HEIGHT;

    public final int playState = 0;
    public final int menuState = 1;
    public int gameState = playState;

    public PlayerData playerData = new PlayerData();
    MenuUI menuUI = new MenuUI(this);

    KeyHandler keyH = new KeyHandler();
    TileHandler tileH = new TileHandler(this);
    Player player = new Player(this, keyH);
    MonsterHandler monsterHandler = new MonsterHandler();
    public NPCHandler npcHandler = new NPCHandler(player, keyH, monsterHandler);
    private final EventHandler eventHandler;

    private final AnimationTimer gameLoop;
    private Consumer<WildBattleEvent> battleLauncher;
    private boolean battleActive;
    private long previousFrameTime;
    private long accumulatedTime;
    private static final long FRAME_INTERVAL = 1_000_000_000L / 60;

    public GamePanel(){
        super(SCREEN_WIDTH, SCREEN_HEIGHT);
        setFocusTraversable(true);
        eventHandler = new EventHandler(player, monsterHandler, this::beginWildBattle);

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (previousFrameTime == 0) {
                    previousFrameTime = now;
                }
                accumulatedTime += Math.min(now - previousFrameTime, FRAME_INTERVAL * 5);
                previousFrameTime = now;

                while (accumulatedTime >= FRAME_INTERVAL) {
                    update();
                    accumulatedTime -= FRAME_INTERVAL;
                }
                render();
            }
        };
    }

    public void startGameLoop() {
        previousFrameTime = 0;
        accumulatedTime = 0;
        gameLoop.start();
    }

    public void stopGameLoop() {
        gameLoop.stop();
    }

    public void attachInput(Scene scene) {
        keyH.attachTo(scene);
    }

    public void setBattleLauncher(Consumer<WildBattleEvent> battleLauncher) {
        this.battleLauncher = Objects.requireNonNull(battleLauncher);
    }

    public Monster createBattlePlayerMonster() {
        return monsterHandler.getMonsterByName("Pikachu").copyForBattle();
    }

    public void resumeAfterBattle() {
        keyH.clearPressedKeys();
        battleActive = false;
        startGameLoop();
    }

    private void beginWildBattle(WildBattleEvent wildBattle) {
        if (battleLauncher == null) {
            throw new IllegalStateException("Battle launcher has not been configured");
        }
        battleActive = true;
        keyH.clearPressedKeys();
        stopGameLoop();
        battleLauncher.accept(wildBattle);
    }

    public void update(){
        if (battleActive) {
            return;
        }
        if(gameState == playState){
            if (keyH.escapePressed) {
                keyH.escapePressed = false;
                gameState = menuState;
                return;
            }
            player.update();  
            npcHandler.update();
            eventHandler.update();
        }
        else if(gameState == menuState){
            menuUI.update();
        }
    }

    private void render() {
        GraphicsContext graphics = getGraphicsContext2D();
        graphics.setImageSmoothing(false);
        graphics.clearRect(0, 0, screenWidth, screenHeight);
        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, 0, screenWidth, screenHeight);
        tileH.draw(graphics);
        eventHandler.draw(graphics);
        player.draw(graphics);
        npcHandler.draw(graphics, tileSize);
        if (gameState == menuState) menuUI.draw(graphics);
    }
}
