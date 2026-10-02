package Entity.NPC;

import Entity.Monster;
import Entity.MonsterHandler;
import Entity.Player;

import java.awt.*;
import java.util.ArrayList;
import java.awt.Font;
import Event.DialogueEvent;
import Main.KeyHandler;

public class NPCHandler {
    private ArrayList<NPC> npcs;
    private Player player;
    private KeyHandler keyH;
    private DialogueEvent dialogueEvent;
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

    public void draw(Graphics2D g2, int tileSize) {
        for(NPC npc : npcs) {
            if (isPlayerNearNPC(npc)) {
                updateNPCDirection(npc);
            }
            npc.draw(g2, tileSize);
        }
        drawDialogueBox(g2);
    }
    private void drawDialogueText(Graphics2D g2) {
        FontMetrics fm = g2.getFontMetrics();

        int x = 40;
        int y = 495;

        int maxWidth = 680;

        String[] words = currentDialogue.split(" ");
        String line = "";

        for (String word : words) {

            String testLine = line + word + " ";

            if (fm.stringWidth(testLine) > maxWidth) {
                g2.drawString(line, x, y);

                line = word + " ";
                y += 25;
            } else {
                line = testLine;
            }
        }

        g2.drawString(line, x, y);
    }
    private void drawDialogueBox(Graphics2D g2) {
        if (currentDialogue == null || currentDialogue.isEmpty()) {
            return;
        }

        // Lưu trạng thái đồ họa hiện tại
        Color oldColor = g2.getColor();
        Font oldFont = g2.getFont();

        // Vẽ khung hội thoại
        g2.setColor(Color.BLACK);
        g2.fillRoundRect(20, 430, 728, 120, 20, 20);

        // Vẽ viền trắng
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(20, 430, 728, 120, 20, 20);

        // Vẽ tên NPC
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString(dialogueEvent.getNpc().getName(), 40, 460);

        // Vẽ nội dung hội thoại
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        drawDialogueText(g2);

        // Gợi ý phím đóng hội thoại
        g2.drawString("Nhan [F] - tiep tuc", 610, 530);

        // Khôi phục trạng thái đồ họa
        g2.setColor(oldColor);
        g2.setFont(oldFont);
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
