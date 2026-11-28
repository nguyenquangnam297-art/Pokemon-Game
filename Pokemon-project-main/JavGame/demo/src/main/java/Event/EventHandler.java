package Event;

import java.util.ArrayList;
import java.util.function.Consumer;

import Entity.MonsterHandler;
import Entity.Player;
import Main.GamePanel;
import Map.Objects.MapTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class EventHandler {
    private static final int TRANSITION_FADE_TICKS = 18;

    private final GamePanel gp;
    private final Player player;
    private final MonsterHandler monsterHandler;
    private final ArrayList<GrassEvent> grassEvents;
    private final Consumer<WildBattleEvent> onBattleStarted;
    private MapTransition pendingTransition;
    private int transitionFadeTick;
    public EventHandler(
            GamePanel gp,
            Player player,
            MonsterHandler monsterHandler,
            Consumer<WildBattleEvent> onBattleStarted
    ) {
        this.gp = gp;
        this.player = player;
        this.monsterHandler = monsterHandler;
        this.onBattleStarted = onBattleStarted;
        grassEvents = new ArrayList<>();
        createEvents();
    }
    private void createEvents() {
        // int[][] grassZones = {
        //         {28, 42, 45, 56},
        //         {38, 72, 50, 86},
        //         {58, 8, 64, 24},
        //         {78, 34, 88, 52},
        //         {40, 3, 48, 22}
        // };

        // for (int[] zone : grassZones) {
        //     GrassEvent grass = new GrassEvent(
        //             zone[1] * gp.tileSize,
        //             zone[0] * gp.tileSize,
        //             (zone[3] - zone[1] + 1) * gp.tileSize,
        //             (zone[2] - zone[0] + 1) * gp.tileSize,
        //             0.03,
        //             monsterHandler.getAllMonsters(),
        //             onBattleStarted
        //     );
        //     grass.setPlayer(player);
        //     grass.setMapId("/MapMatrix");
        //     grassEvents.add(grass);
        // }
    }
    public void update() {
        if (pendingTransition != null) {
            updateTransition();
            return;
        }

        String activeMap = gp.tileH.getCurrentMap();
        for (GrassEvent grass : grassEvents) {
            if (grass.belongsToMap(activeMap)) {
                grass.update();
            }
        }
        checkMapTransition();
    }
    public void checkMapTransition() {
        Rectangle2D playerBounds = new Rectangle2D(
                player.x + 8,
                player.y + 16,
                gp.tileSize - 16,
                gp.tileSize - 18);
        pendingTransition = gp.objectH.getTransitionAt(playerBounds);
        transitionFadeTick = 0;
    }

    public boolean isTransitioning() {
        return pendingTransition != null;
    }

    private void updateTransition() {
        transitionFadeTick++;
        if (transitionFadeTick == TRANSITION_FADE_TICKS) {
            gp.tileH.loadMap(pendingTransition.mapId());
            player.x = pendingTransition.playerX();
            player.y = pendingTransition.playerY();
        } else if (transitionFadeTick >= TRANSITION_FADE_TICKS * 2) {
            pendingTransition = null;
            transitionFadeTick = 0;
        }
    }
    public void draw(GraphicsContext graphics) {
        String activeMap = gp.tileH.getCurrentMap();

        graphics.save();
        graphics.setGlobalAlpha(0.55);
        graphics.setFill(Color.GREEN);
        for (GrassEvent grass : grassEvents) {
            if (!grass.belongsToMap(activeMap)) {
                continue;
            }

            int screenX = grass.getX() - player.x + gp.screenWidth / 2;
            int screenY = grass.getY() - player.y + gp.screenHeight / 2;

            graphics.fillRect(
                    screenX,
                    screenY,
                    grass.getWidth(),
                    grass.getHeight()
            );
        }
        graphics.restore();

    }

    public void drawTransitionFade(GraphicsContext graphics) {
        if (pendingTransition == null) {
            return;
        }

        double opacity = transitionFadeTick <= TRANSITION_FADE_TICKS
                ? (double) transitionFadeTick / TRANSITION_FADE_TICKS
                : (double) (TRANSITION_FADE_TICKS * 2 - transitionFadeTick) / TRANSITION_FADE_TICKS;
        graphics.setFill(Color.BLACK);
        graphics.setGlobalAlpha(Math.max(0, Math.min(1, opacity)));
        graphics.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        graphics.setGlobalAlpha(1);
    }
}
