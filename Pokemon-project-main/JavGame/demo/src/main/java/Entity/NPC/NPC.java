package Entity.NPC;

import Entity.Entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
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
            frames = new Image[4];

            frames[0] = loadImage("/NPCSprite/" + spriteName + "_down.png");

            frames[1] = loadImage("/NPCSprite/" + spriteName + "_up.png");

            frames[2] = loadImage("/NPCSprite/" + spriteName + "_left.png");
            frames[3] = loadImage("/NPCSprite/" + spriteName + "_right.png");
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
        Image image = null;

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

        graphics.drawImage(image, x, y, tileSize, tileSize);
    }
}
