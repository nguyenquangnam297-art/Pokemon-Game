package Main;

import java.util.Objects;
import java.util.function.Consumer;

import Data.PlayerData;
import Data.SaveManager;
import Entity.Monster;
import Entity.MonsterHandler;
import Entity.NPC.NPCHandler;
import Entity.Player;
import Event.EventHandler;
import Event.ShopHandler;
import Event.WildBattleEvent;
import Main.MainMenu.MainMenu;
import Map.TileHandler;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GamePanel extends Canvas {
    private static final int ORIGINAL_TILE_SIZE = 64;
    private static final int SCALE = 1;
    private static final int INITIAL_SCREEN_WIDTH = 1280;
    private static final int INITIAL_SCREEN_HEIGHT = 720;
    private static final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE;


    public final int tileSize = TILE_SIZE;
    public int worldWidth;
    public int worldHeight;
    public int screenWidth = INITIAL_SCREEN_WIDTH;
    public int screenHeight = INITIAL_SCREEN_HEIGHT;

    public final int playState = 0;
    public final int menuState = 1;
    public final int titleState = 2;
    public int gameState = titleState;

    private static final String SPAWN_MAP = "/MapMatrix";

    public PlayerData playerData = new PlayerData();
    KeyHandler keyH = new KeyHandler();
    MenuUI menuUI = new MenuUI(this);
    private final MainMenu mainMenu = new MainMenu(this, keyH);
    public TileHandler tileH = new TileHandler(this);
    public Map.Objects objectH = new Map.Objects(this);
    public Player player = new Player(this, keyH);
    private ShopHandler shopHandler = new ShopHandler(playerData);
    MonsterHandler monsterHandler = new MonsterHandler();
    public NPCHandler npcHandler = new NPCHandler(this, player, keyH, monsterHandler, shopHandler); //add this
    private final EventHandler eventHandler;
    private MusicHandler musicHandler = MusicHandler.getInstance();

    private int spawnX;
    private int spawnY;
    private String spawnDirection;

    private final AnimationTimer gameLoop;
    private Consumer<WildBattleEvent> battleLauncher;
    private boolean battleActive;
    private long previousFrameTime;
    private long accumulatedTime;
    private static final long FRAME_INTERVAL = 1_000_000_000L / 60;

    public GamePanel(){
        super(INITIAL_SCREEN_WIDTH, INITIAL_SCREEN_HEIGHT);
        setFocusTraversable(true);
        widthProperty().addListener((observable, oldWidth, newWidth) ->
                screenWidth = Math.max(1, (int) Math.round(newWidth.doubleValue())));
        heightProperty().addListener((observable, oldHeight, newHeight) ->
                screenHeight = Math.max(1, (int) Math.round(newHeight.doubleValue())));
        eventHandler = new EventHandler(this, player, monsterHandler, this::beginWildBattle);

        spawnX = player.x;
        spawnY = player.y;
        spawnDirection = player.direction;

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
        return monsterHandler.getMonsterByName("Sparchu")
            .copyForBattle(playerData.getStatMultiplier());
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

        public void startNewGame() {
        playerData.copyFrom(new PlayerData());
        moveToSpawn();
        enterWorld();
    }

    public void continueGame(PlayerData saved) {
        playerData.copyFrom(saved);
        playerData.applyTo(player);
        try {
            tileH.loadMap(playerData.currentMapId);
        } catch (RuntimeException e) {
            moveToSpawn();
        }
        enterWorld();
    }

    public boolean saveGame() {
        playerData.syncFrom(player);
        playerData.currentMapId = tileH.getCurrentMap();
        SaveManager.save(playerData);
        return true;
    }

    public void closeMenu() {
        keyH.clearPressedKeys();
        gameState = playState;
    }

    //them sau
    // private void openMenu() {
    //     menuUI.open();
    //     gameState = menuState;
    // }

    // private boolean canOpenMenu() {
    //     return npcHandler.dialogueEvent == null || !npcHandler.dialogueEvent.isActive();
    // }

    private void moveToSpawn() {
        player.x = spawnX;
        player.y = spawnY;
        player.direction = spawnDirection;
        tileH.loadMap(SPAWN_MAP);
    }

    private void enterWorld() {
        keyH.clearPressedKeys();
        gameState = playState;
    }

    public void update(){
        if (battleActive) {
            return;
        }
        if(gameState == playState){
            if(shopHandler.isOpen()) {
                npcHandler.updateShop();
                return;
            }
            if (eventHandler.isTransitioning()) {
                eventHandler.update();
                return;
            }
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
        else if (gameState == titleState) {
            mainMenu.update();
        }
    }

    private void playMusicForCurrentState(){
        if(gameState == playState){
            musicHandler.playMusic("/Music/overworld.mp3");
        }
        else if(gameState == menuState){
            musicHandler.playMusic("/Music/menu.mp3");
        }
        else if(gameState == titleState){
            musicHandler.playMusic("/Music/title.mp3");
        }
    }

    private void render() {
        GraphicsContext graphics = getGraphicsContext2D();
        graphics.setImageSmoothing(false);
        graphics.clearRect(0, 0, getWidth(), getHeight());
        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, 0, getWidth(), getHeight());
        tileH.draw(graphics);
        eventHandler.draw(graphics);
        objectH.drawTransitionZones(graphics);
        objectH.drawBehind(graphics, player.y + tileSize);
        player.draw(graphics);
        objectH.drawInFront(graphics, player.y + tileSize);
        npcHandler.draw(graphics, tileSize);
        player.drawUI(graphics);
        if (gameState == menuState) menuUI.draw(graphics);
        if (gameState == titleState) mainMenu.draw(graphics);
        eventHandler.drawTransitionFade(graphics);
        if (shopHandler.isOpen()) shopHandler.draw(graphics, screenWidth, screenHeight);
    }
}
