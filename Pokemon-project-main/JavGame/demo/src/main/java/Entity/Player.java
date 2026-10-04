package Entity;

import Main.GamePanel;
import Main.KeyHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;
    
    public Player(GamePanel gp, KeyHandler keyH){
        this.gp = gp;
        this.keyH = keyH;
        SetDefaultValues();
        getPlayerImage();
    }
    private void SetDefaultValues(){
        x = 100;
        y = 100;
        speed = 4;
        direction = "down";
        state = 0;
        spriteCounter = 0;
    }
    private void getPlayerImage(){
        try{
            frames = new Image[12];
            frames[0] = loadImage("/PlayerSprite/left1.png");
            frames[1] = loadImage("/PlayerSprite/left2.png");
            frames[2] = loadImage("/PlayerSprite/left3.png");
            frames[3] = loadImage("/PlayerSprite/right1.png");
            frames[4] = loadImage("/PlayerSprite/right2.png");
            frames[5] = loadImage("/PlayerSprite/right3.png");
            frames[6] = loadImage("/PlayerSprite/up1.png");
            frames[7] = loadImage("/PlayerSprite/up2.png");
            frames[8] = loadImage("/PlayerSprite/up3.png");
            frames[9] = loadImage("/PlayerSprite/down1.png");
            frames[10] = loadImage("/PlayerSprite/down2.png");
            frames[11] = loadImage("/PlayerSprite/down3.png");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void update(){  
        if(gp.npcHandler.dialogueEvent != null && gp.npcHandler.dialogueEvent.isActive()){
            return;
        }
        if(keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed){
            if(keyH.upPressed){
                direction = "up";
                y -= speed;
            }
            if(keyH.downPressed){
                direction = "down";
                y += speed;
            }
            if(keyH.leftPressed){
                direction = "left";
                x -= speed;
            }
            if(keyH.rightPressed){
                direction = "right";
                x += speed;
            }
            spriteCounter++;
            if(spriteCounter > 12){
                state++;
                spriteCounter = 0;
                if(state > 2){
                    state = 0;
                }
            }
        }
    }
    private Image loadImage(String resourcePath) {
        var resource = getClass().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Missing player sprite: " + resourcePath);
        }
        return new Image(resource.toExternalForm());
    }

    public void draw(GraphicsContext graphics){
        Image image = null;
        if(direction.equals("up")){
            switch (state) {
                case 0 -> {
                    image = frames[6];
                }
                case 1 -> {
                    image = frames[7];
                }
                case 2 -> {
                    image = frames[8];
                }
            }
        }
        if(direction.equals("down")){
            switch (state) {
                case 0 -> {
                    image = frames[9];
                }
                case 1 -> {
                    image = frames[10];
                }
                case 2 -> {
                    image = frames[11];
                }
            }
        }
        if(direction.equals("left")){
            switch (state) {
                case 0 -> image = frames[1];
                case 1 -> image = frames[0];
                case 2 -> image = frames[2];
            }
        }
        if(direction.equals("right")){
            switch (state) {
                case 0 -> image = frames[3];
                case 1 -> image = frames[4];
                case 2 -> image = frames[5];
            }
        }
        graphics.drawImage(image, x, y, gp.tileSize, gp.tileSize);
    }
}
