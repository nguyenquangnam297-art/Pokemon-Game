package Main;

import Entity.MonsterHandler;
import Entity.NPC.NPCHandler;
import Entity.Player;
import Event.GrassEvent;
import Map.TileHandler;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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


    KeyHandler keyH = new KeyHandler();
    TileHandler tileH = new TileHandler(this);
    Player player = new Player(this, keyH);
    MonsterHandler monsterHandler = new MonsterHandler();
    public NPCHandler npcHandler = new NPCHandler(player, keyH, monsterHandler);
    //vung co
    GrassEvent grassEvent = new GrassEvent(
            200,
            200,
            150,
            100,
            1,
            monsterHandler.getAllMonsters()
    );
    private final AnimationTimer gameLoop;
    private long previousFrameTime;
    private long accumulatedTime;
    private static final long FRAME_INTERVAL = 1_000_000_000L / 60;

    public GamePanel(){
        super(SCREEN_WIDTH, SCREEN_HEIGHT);
        setFocusTraversable(true);

        //temp
        grassEvent.setPlayer(player);

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

    public void update(){
        player.update();
        npcHandler.update();
        //temp
        grassEvent.update();
    }

    private void render() {
        GraphicsContext graphics = getGraphicsContext2D();
        graphics.setImageSmoothing(false);
        graphics.clearRect(0, 0, screenWidth, screenHeight);
        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, 0, screenWidth, screenHeight);
        tileH.draw(graphics);

        //temp
        graphics.setFill(Color.GREEN);
        graphics.fillRect(
            grassEvent.getX(),
            grassEvent.getY(),
            grassEvent.getWidth(),
            grassEvent.getHeight()
        );


        player.draw(graphics);
        npcHandler.draw(graphics, tileSize);
    }
}
