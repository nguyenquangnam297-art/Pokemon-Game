package Entity.NPC;

import java.util.ArrayList;

import Entity.Monster;
import Entity.MonsterHandler;
import Entity.Player;
import Event.DialogueEvent;
import Main.KeyHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class NPCHandler {
    private ArrayList<NPC> npcs;
    private Player player;
    private KeyHandler keyH;
    public DialogueEvent dialogueEvent;
    private String currentDialogue = "";
    private MonsterHandler monsterHandler;

    public NPCHandler(Player player, KeyHandler keyH, MonsterHandler monsterHandler) {
        this.player = player;
        this.keyH = keyH;
        this.monsterHandler = monsterHandler;
        this.npcs = new ArrayList<>();
        createNPCs();
    }
    private void createNPCs() {
        NormalNPC oldMan = new NormalNPC(
                "Old Man",
                "Xin chao!",
                "Ban dang di dau?",
                "Chuc ban mot ngay vui ve!"
        );
        oldMan.x = 300;
        oldMan.y = 300;
        npcs.add(oldMan);
        ShopNPC shopKeeper = new ShopNPC(
                "Shop Keeper",
                "Xin chao! Chao mung ban den cua hang.",
                "Ban muon mua mot mon do nao do sao?",
                "Hay xem thu cua hang cua toi!"
        );
        shopKeeper.x = 500;
        shopKeeper.y = 300;
        npcs.add(shopKeeper);
        ArrayList<Monster> trainerMonsters =
                monsterHandler.getAllMonsters();

        TrainerNPC trainer = new TrainerNPC(
                "Trainer",
                trainerMonsters,
                "Hey! Ban muon dau voi toi u?",
                "Ta da san sang cho tran dau!",
                "Chuan bi di!"
        );
        trainer.x = 700;
        trainer.y = 300;
        npcs.add(trainer);
    }
    private NPC getNearbyNPC() {
        for (NPC npc : npcs) {
            if (isPlayerNearNPC(npc)) {
                return npc;
            }
        }

        return null;
    }

    public void draw(GraphicsContext graphics, int tileSize) {
        for(NPC npc : npcs) {
            if (isPlayerNearNPC(npc)) {
                updateNPCDirection(npc);
            }
            npc.draw(graphics, tileSize);
        }
        drawDialogueBox(graphics);
    }
    private void drawDialogueText(GraphicsContext graphics) {
        int x = 40;
        int y = 495;

        int maxWidth = 680;

        String[] words = currentDialogue.split(" ");
        String line = "";

        for (String word : words) {
            String testLine = line.isEmpty() ? word : line + " " + word;
            Text measuredText = new Text(testLine);
            measuredText.setFont(graphics.getFont());
            if (measuredText.getLayoutBounds().getWidth() > maxWidth) {
                graphics.fillText(line, x, y);
                line = word;
                y += 25;
            } else {
                line = testLine;
            }
        }

        graphics.fillText(line, x, y);
    }
    private void drawDialogueBox(GraphicsContext graphics) {
        if (currentDialogue == null || currentDialogue.isEmpty()) {
            return;
        }

        graphics.save();
        graphics.setFill(Color.BLACK);
        graphics.fillRoundRect(20, 430, 728, 120, 20, 20);

        graphics.setStroke(Color.WHITE);
        graphics.strokeRoundRect(20, 430, 728, 120, 20, 20);

        graphics.setFill(Color.WHITE);
        graphics.setFont(Font.font("Arial", 18));
        graphics.fillText(dialogueEvent.getNpc().getName(), 40, 460);

        graphics.setFont(Font.font("Arial", 16));
        drawDialogueText(graphics);

        graphics.fillText("Nhan [F] - tiep tuc", 610, 530);
        graphics.restore();
    }
    private boolean isPlayerNearNPC(NPC npc) {
        int dx = player.x - npc.x;
        int dy = player.y - npc.y;

        int distance = 60;

        return Math.abs(dx) <= distance
                && Math.abs(dy) <= distance;
    }
    private void updateNPCDirection(NPC npc) {
        int dx = player.x - npc.x;
        int dy = player.y - npc.y;

        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) {
                npc.direction = "right";
            } else {
                npc.direction = "left";
            }
        } else {
            if (dy > 0) {
                npc.direction = "down";
            } else {
                npc.direction = "up";
            }
        }
    }
    public void update() {

        if (keyH.fPressed) {

            // Nếu đang trong hội thoại
            if (dialogueEvent != null) {

                currentDialogue = dialogueEvent.nextDialogue();

                if (dialogueEvent.isCompleted()) {
                    NPC npc = dialogueEvent.getNpc();
                    dialogueEvent = null;
                    currentDialogue = "";
                    if (npc instanceof ShopNPC) {
                        ((ShopNPC) npc).openShop();
                    }
                }

            }

            // Nếu chưa có hội thoại
            else {

                NPC npc = getNearbyNPC();

                if (npc != null) {

                    dialogueEvent = new DialogueEvent(npc);

                    currentDialogue = dialogueEvent.startDialogue();
                }
            }

            keyH.fPressed = false;
        }
    }
}
