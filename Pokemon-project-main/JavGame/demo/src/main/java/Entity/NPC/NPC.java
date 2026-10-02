package Entity.NPC;

import Entity.Entity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class NPC extends Entity {
    private String name;
    private ArrayList<String> dialogues;

    public NPC(String name,String spriteName, String ... dialogues) {
        this.name = name;
        this.dialogues = new ArrayList<String>();

        for (String dia : dialogues) {
            this.dialogues.add(dia);
        }

        direction = "down";
        getNPCImage(spriteName);
    }

    public String getName() {
        return name;
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
            frames = new BufferedImage[4];

            frames[0] = ImageIO.read(
                    getClass().getResourceAsStream("/NPCSprite/" + spriteName + "_down.png")
            );

            frames[1] = ImageIO.read(
                    getClass().getResourceAsStream("/NPCSprite/" + spriteName + "_up.png")
            );

            frames[2] = ImageIO.read(
                    getClass().getResourceAsStream("/NPCSprite/" +  spriteName + "_left.png")
            );
            frames[3] = ImageIO.read(
                    getClass().getResourceAsStream("/NPCSprite/" +  spriteName + "_right.png")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void draw(Graphics2D g2, int tileSize) {
        BufferedImage image = null;

        switch (direction) {
            case "down":
                image = frames[0];
                break;

            case "up":
                image = frames[1];
                break;

            case "left":
                image = frames[2];
                break;

            case "right":
                image = frames[3];
                break;
        }

        g2.drawImage(image, x, y, tileSize, tileSize, null);
    }
}
