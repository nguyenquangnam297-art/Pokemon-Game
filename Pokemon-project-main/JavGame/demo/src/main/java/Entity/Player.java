package Entity;

import Main.GamePanel;
import Main.KeyHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class Player extends Entity {
    private static final int FRAME_SIZE = 128;
    private static final int FRAME_COUNT = 4;
    private static final int DIRECTION_COUNT = 4;
    private static final int ANIMATION_SPEED = 12;

    GamePanel gp;
    KeyHandler keyH;
    public boolean collisionOn = false;
    private final Image coinImage;

    public Player(GamePanel gp, KeyHandler keyH){
        this.gp = gp;
        this.keyH = keyH;
        SetDefaultValues();
        getPlayerImage();
        coinImage = loadCoinImage();
    }
    private void SetDefaultValues(){
        x = 22 * gp.tileSize;
        y = 16 * gp.tileSize;
        speed = 12;
        direction = "down";
        state = 0;
        spriteCounter = 0;
    }
    private void getPlayerImage(){
        var resource = getClass().getResource("/PlayerSprite/player.png");
        if (resource == null) {
            throw new IllegalStateException("Missing player sprite sheet: /PlayerSprite/player.png");
        }
        Image spriteSheet = new Image(resource.toExternalForm());
        int sheetWidth = FRAME_SIZE * FRAME_COUNT;
        int sheetHeight = FRAME_SIZE * DIRECTION_COUNT;
        if (spriteSheet.isError() || spriteSheet.getWidth() < sheetWidth || spriteSheet.getHeight() < sheetHeight) {
            throw new IllegalStateException(
                    "Player sprite sheet must be at least " + sheetWidth + "x" + sheetHeight + " pixels");
        }
        PixelReader reader = spriteSheet.getPixelReader();
        if (reader == null) {
            throw new IllegalStateException("Could not read player sprite sheet pixels");
        }

        frames = new Image[FRAME_COUNT * DIRECTION_COUNT];
        for (int row = 0; row < DIRECTION_COUNT; row++) {
            for (int column = 0; column < FRAME_COUNT; column++) {
                frames[row * FRAME_COUNT + column] = new WritableImage(
                        reader, column * FRAME_SIZE, row * FRAME_SIZE, FRAME_SIZE, FRAME_SIZE);
            }
        }
    }

    private Image loadCoinImage() {
        var resource = getClass().getResource("/objects/coin.png");
        if (resource == null) {
            throw new IllegalStateException("Missing coin sprite: /objects/coin.png");
        }

        Image image = new Image(resource.toExternalForm());
        if (image.isError() || image.getWidth() <= 0 || image.getHeight() <= 0) {
            throw new IllegalStateException("Could not load coin sprite: /objects/coin.png");
        }
        return image;
    }
    public void update(){  
        if(gp.npcHandler.dialogueEvent != null && gp.npcHandler.dialogueEvent.isActive()){
            return;
        }
        if(keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed){
            if(keyH.upPressed){
                direction = "up";
            }
            if(keyH.downPressed){
                direction = "down";
            }
            if(keyH.leftPressed){
                direction = "left";
            }
            if(keyH.rightPressed){
                direction = "right";
            }
            collisionOn = false;
            checkTile();
            if(!collisionOn){
                switch(direction){
                    case "up" -> y -= speed;
                    case "down" -> y += speed;
                    case "left" -> x -= speed;
                    case "right" -> x += speed;
                }
            }
            spriteCounter++;
            if(spriteCounter > ANIMATION_SPEED){
                state++;
                spriteCounter = 0;
                if(state >= FRAME_COUNT){
                    state = 0;
                }
            }
        } else {
            state = 0;
            spriteCounter = 0;
        }
    }
    public void checkTile() {
        int nextX = x;
        int nextY = y;
        switch (direction) {
            case "up" -> nextY -= speed;
            case "down" -> nextY += speed;
            case "left" -> nextX -= speed;
            case "right" -> nextX += speed;
            default -> throw new IllegalStateException("Unknown player direction: " + direction);
        }
        Rectangle2D nextPlayerBounds = new Rectangle2D(
                nextX + 8,
                nextY + 16,
                gp.tileSize - 16,
                gp.tileSize - 18);
        if (gp.objectH.collidesWith(nextPlayerBounds)) {
            collisionOn = true;
            return;
        }

        // 1. Thu gọn vùng va chạm vào phần thân dưới để đi sát mép nước tự nhiên hơn
        int entityLeftWorldX = x + 8;
        int entityRightWorldX = x + gp.tileSize - 8;
        int entityTopWorldY = y + 16;
        int entityBottomWorldY = y + gp.tileSize - 2;

        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        // Lấy giới hạn số hàng, số cột của bản đồ hiện tại
        int maxRow = gp.tileH.mapTileNum.length;
        int maxCol = gp.tileH.mapTileNum[0].length;

        int tileNum1, tileNum2;

        switch (direction) {
            case "up" -> {
                int nextTopY = entityTopWorldY - speed;
                if (nextTopY < 0) {
                    collisionOn = true;
                    return;
                }
                entityTopRow = nextTopY / gp.tileSize;
                tileNum1 = gp.tileH.mapTileNum[entityTopRow][entityLeftCol];
                tileNum2 = gp.tileH.mapTileNum[entityTopRow][entityRightCol];
                if (gp.tileH.tile[tileNum1].collision || gp.tileH.tile[tileNum2].collision) {
                    collisionOn = true;
                }
            }

            case "down" -> {
                entityBottomRow = (entityBottomWorldY + speed) / gp.tileSize;
                if (entityBottomRow >= maxRow) {
                    collisionOn = true;
                    return;
                }
                tileNum1 = gp.tileH.mapTileNum[entityBottomRow][entityLeftCol];
                tileNum2 = gp.tileH.mapTileNum[entityBottomRow][entityRightCol];
                if (gp.tileH.tile[tileNum1].collision || gp.tileH.tile[tileNum2].collision) {
                    collisionOn = true;
                }
            }

            case "left" -> {
                int nextLeftX = entityLeftWorldX - speed;
                if (nextLeftX < 0) {
                    collisionOn = true;
                    return;
                }
                entityLeftCol = nextLeftX / gp.tileSize;
                tileNum1 = gp.tileH.mapTileNum[entityTopRow][entityLeftCol];
                tileNum2 = gp.tileH.mapTileNum[entityBottomRow][entityLeftCol];
                if (gp.tileH.tile[tileNum1].collision || gp.tileH.tile[tileNum2].collision) {
                    collisionOn = true;
                }
            }

            case "right" -> {
                entityRightCol = (entityRightWorldX + speed) / gp.tileSize;
                if (entityRightCol >= maxCol) {
                    collisionOn = true;
                    return;
                }
                tileNum1 = gp.tileH.mapTileNum[entityTopRow][entityRightCol];
                tileNum2 = gp.tileH.mapTileNum[entityBottomRow][entityRightCol];
                if (gp.tileH.tile[tileNum1].collision || gp.tileH.tile[tileNum2].collision) {
                    collisionOn = true;
                }
            }
        }
    }
    public void draw(GraphicsContext graphics){
        int row = switch (direction) {
            case "down" -> 0;
            case "left" -> 1;
            case "right" -> 2;
            case "up" -> 3;
            default -> throw new IllegalStateException("Unknown player direction: " + direction);
        };
        Image image = frames[row * FRAME_COUNT + state];
        int screenX = gp.screenWidth / 2;
        int screenY = gp.screenHeight / 2;
        graphics.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize);
    }

    public void drawUI(GraphicsContext graphics) {
        int barX = 18;
        int barY = 32;
        int barWidth = 180;
        int barHeight = 24;

        graphics.setFill(javafx.scene.paint.Color.rgb(20, 20, 20, 0.85));
        graphics.fillRoundRect(barX - 6, barY - 20, barWidth + 132, barHeight + 28, 8, 8);

        graphics.setFill(javafx.scene.paint.Color.WHITE);
        graphics.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 14));
        graphics.fillText("LV " + gp.playerData.level, barX, barY - 5);

        graphics.setFill(javafx.scene.paint.Color.DARKGRAY);
        graphics.fillRoundRect(barX, barY, barWidth, barHeight, 6, 6);

        int xpNeeded = gp.playerData.getXpForNextLevel();
        double progress = (double) gp.playerData.currentXp / xpNeeded;
        progress = Math.max(0.0, Math.min(1.0, progress));
        graphics.setFill(javafx.scene.paint.Color.LIMEGREEN);
        graphics.fillRoundRect(barX, barY, barWidth * progress, barHeight, 6, 6);

        graphics.setStroke(javafx.scene.paint.Color.BLACK);
        graphics.setLineWidth(1);
        graphics.strokeRoundRect(barX, barY, barWidth, barHeight, 6, 6);

        int coinX = barX + barWidth + 16;
        int coinY = barY + 2;
        graphics.drawImage(coinImage, coinX, coinY, 20, 20);
        graphics.setFill(javafx.scene.paint.Color.WHITE);
        graphics.fillText(Integer.toString(gp.playerData.getMoney()), coinX + 26, barY + 17);
    }
}
