package Event;

import java.util.ArrayList;

import Entity.MonsterHandler;
import Entity.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class EventHandler {
    private final Player player;
    private final MonsterHandler monsterHandler;
    private final ArrayList<GrassEvent> grassEvents;

    public EventHandler(Player player, MonsterHandler monsterHandler) {
        this.player = player;
        this.monsterHandler = monsterHandler;
        grassEvents = new ArrayList<>();
        createEvents();
    }
    private void createEvents() {
        GrassEvent grass1 = new GrassEvent(
                200,
                200,
                150,
                100,
                1,
                monsterHandler.getAllMonsters()
        );

        grass1.setPlayer(player);
        grassEvents.add(grass1);
    }
    public void update() {
        for (GrassEvent grass : grassEvents) {
            grass.update();
        }
    }
    public void draw(GraphicsContext graphics) {
        graphics.setFill(Color.GREEN);
        for (GrassEvent grass : grassEvents) {
            graphics.fillRect(
                    grass.getX(),
                    grass.getY(),
                    grass.getWidth(),
                    grass.getHeight()
            );
        }
    }
}
