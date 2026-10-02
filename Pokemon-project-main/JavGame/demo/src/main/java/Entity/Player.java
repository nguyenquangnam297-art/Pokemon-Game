package Entity;

import java.awt.image.BufferedImage;

import Main.GamePanel;
import Main.KeyHandler;

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
            frames = new java.awt.image.BufferedImage[12];
            frames[0] = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/PlayerSprite/left1.png"));
            frames[1] = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/PlayerSprite/left2.png"));
            frames[2] = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/PlayerSprite/left3.png"));
            frames[3] = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/PlayerSprite/right1.png"));
            frames[4] = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/PlayerSprite/right2.png"));
            frames[5] = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/PlayerSprite/right3.png"));
            frames[6] = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/PlayerSprite/up1.png"));
            frames[7] = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/PlayerSprite/up2.png"));
            frames[8] = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/PlayerSprite/up3.png"));
            frames[9] = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/PlayerSprite/down1.png"));
            frames[10] = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/PlayerSprite/down2.png"));
            frames[11] = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/PlayerSprite/down3.png"));
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
    public void draw(java.awt.Graphics2D g2){
        BufferedImage image = null;
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
        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
    }
}
