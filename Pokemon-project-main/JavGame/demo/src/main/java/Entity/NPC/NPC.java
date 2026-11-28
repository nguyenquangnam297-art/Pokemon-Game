package Entity.NPC;

import Entity.Entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;

public class NPC extends Entity {
    private String name;
    private ArrayList<String> dialogues;
    private List<int[]> path = new ArrayList<>();
    private int currentWaypointIndex = 0;
    private int tileSize;
    private boolean loopPath = true;
    private boolean movementPaused = false;
    private Image[][] walkFrames = new Image[4][3];
    private int animationFrame = 0;
    private int animationCounter = 0;
    private boolean moving = false;
    private int waitCounter = 0;
    private int waitDuration = 60;
    private boolean waiting = false;
    private String mapId = "/MapMatrix";

    private static final int ANIMATION_SPEED = 8;

    public NPC(String name,String spriteName, String ... dialogues) {
        this.name = name;
        this.dialogues = new ArrayList<String>();

        for (String dia : dialogues) {
            this.dialogues.add(dia);
        }

        direction = "down";
        getNPCImage(spriteName);
    }

    public void setPath(List<int[]> path, int tileSize, boolean loopPath) {
        if(path == null || path.isEmpty()) {
            return;
        }
        this.path = new ArrayList<>(path);
        this.tileSize = tileSize;
        this.loopPath = loopPath;

        currentWaypointIndex = 0;
        int[] firstPoint = this.path.get(0);
        x = firstPoint[1]*tileSize;
        y = firstPoint[0]*tileSize;
    }


    public void updateMovement() {
        if (movementPaused || path.isEmpty()) {
            moving = false;
            return;
        }
        if (waiting) {
            moving = false;
            waitCounter++;

            if (waitCounter >= waitDuration) {
                waiting = false;
                waitCounter = 0;
            }

            return;
        }
        int[] target = path.get(currentWaypointIndex);
        int targetX = target[1] * tileSize;
        int targetY = target[0] * tileSize;

        // Neu da den waypoint hien tai
        if (x == targetX && y == targetY) {
            waiting = true;
            waitCounter = 0;

            currentWaypointIndex++;

            if (currentWaypointIndex >= path.size()) {
                if (loopPath) {
                    currentWaypointIndex = 0;
                } else {
                    currentWaypointIndex = path.size() - 1;
                    waiting = false;
                    moving = false;
                    return;
                }
            }

            return;
        }

        int oldX = x;
        int oldY = y;

        // Di chuyen theo truc ngang hoac doc
        if (x < targetX) {
            direction = "right";
            x = Math.min(x + speed, targetX);
        } else if (x > targetX) {
            direction = "left";
            x = Math.max(x - speed, targetX);
        } else if (y < targetY) {
            direction = "down";
            y = Math.min(y + speed, targetY);
        } else if (y > targetY) {
            direction = "up";
            y = Math.max(y - speed, targetY);
        }

        // Kiem tra NPC co thuc su di chuyen khong
        moving = (x != oldX || y != oldY);

        // Chuyen frame khi dang di chuyen
        if (moving) {
            animationCounter++;

            if (animationCounter >= ANIMATION_SPEED) {
                animationFrame = (animationFrame + 1) % 3;
                animationCounter = 0;
            }
        } else {
            animationFrame = 0;
            animationCounter = 0;
        }
    }

    public void pauseMovement() {
        movementPaused = true;
    }

    public void resumeMovement() {
        movementPaused = false;
    }

    public String getName() {
        return name;
    }
    public boolean isMoving() {
        return moving;
    }
    public void setWaitDuration(int frames) {
        waitDuration = Math.max(0, frames);
    }

    public void setMapId(String mapId) {
        if (mapId == null || mapId.trim().isEmpty()) {
            throw new IllegalArgumentException("Map ID cannot be empty");
        }
        this.mapId = mapId.startsWith("/") ? mapId : "/" + mapId;
    }

    public String getMapId() {
        return mapId;
    }

    public boolean belongsToMap(String activeMapId) {
        return mapId.equals(activeMapId);
    }

    public String interact() {
        if (dialogues.isEmpty()) {
            return "";
        }

        return dialogues.get(0);
    }
    public ArrayList<String> getDialogues() {
        return dialogues;
    }

    private void getNPCImage(String spriteName) {
        try {
            // 4 huong: down, up, left, right
            String[] directions = {"down", "up", "left", "right"};

            // Anh dung yen
            frames = new Image[4];

            for (int i = 0; i < directions.length; i++) {
                frames[i] = loadImage(
                        "/NPCSprite/" + spriteName + "_" + directions[i] + ".png"
                );
            }

            // Khoi tao 3 frame cho moi huong
            for (int i = 0; i < directions.length; i++) {
                String directionName = directions[i];

                walkFrames[i][0] = frames[i];

                try {
                    walkFrames[i][1] = loadImage(
                            "/NPCSprite/" + spriteName + "_" + directionName + "_1.png"
                    );

                    walkFrames[i][2] = loadImage(
                            "/NPCSprite/" + spriteName + "_" + directionName + "_2.png"
                    );
                } catch (IllegalStateException e) {
                    // NPC khong co frame bo sung thi dung anh goc
                    walkFrames[i][1] = frames[i];
                    walkFrames[i][2] = frames[i];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Image loadImage(String resourcePath) {
        var resource = getClass().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Missing NPC sprite: " + resourcePath);
        }
        return new Image(resource.toExternalForm());
    }


    public void draw(GraphicsContext graphics, int tileSize) {
        throw new UnsupportedOperationException("NPC.draw requires camera parameters");
    }

    public void draw(GraphicsContext graphics, int tileSize, int screenWidth, int screenHeight,
                     int playerWorldX, int playerWorldY) {
        Image image;

        int directionIndex;

        switch (direction) {
            case "down":
                directionIndex = 0;
                break;
            case "up":
                directionIndex = 1;
                break;
            case "left":
                directionIndex = 2;
                break;
            case "right":
                directionIndex = 3;
                break;
            default:
                directionIndex = 0;
                break;
        }

        if (moving) {
            image = walkFrames[directionIndex][animationFrame];
        } else {
            image = frames[directionIndex];
        }

        int screenX = x - playerWorldX + screenWidth / 2;
        int screenY = y - playerWorldY + screenHeight / 2;

        if (screenX + tileSize > 0 && screenX < screenWidth
                && screenY + tileSize > 0 && screenY < screenHeight) {
            graphics.drawImage(image, screenX, screenY, tileSize, tileSize);
        }
    }
}
