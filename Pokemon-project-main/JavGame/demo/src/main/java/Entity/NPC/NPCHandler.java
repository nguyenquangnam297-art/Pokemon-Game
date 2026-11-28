package Entity.NPC;

import java.util.ArrayList;

import Entity.Monster;
import Entity.MonsterHandler;
import Entity.Player;
import Event.ShopHandler;
import Event.DialogueEvent;
import Event.ShopEvent;
import Main.GamePanel;
import Main.KeyHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class NPCHandler {
    private ArrayList<NPC> npcs;
    private Player player;
    private KeyHandler keyH;

    GamePanel gp;
    public DialogueEvent dialogueEvent;
    private String currentDialogue = "";
    private MonsterHandler monsterHandler;
    private ShopHandler shopHandler;
    private ShopEvent shopEvent;

     public NPCHandler(GamePanel gp, Player player, KeyHandler keyH2, MonsterHandler monsterHandler2, ShopHandler shopHandler) {
        this.gp = gp;
        this.player = player;
        this.keyH = keyH2;
        this.monsterHandler = monsterHandler2;
        this.shopHandler = shopHandler;
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
        oldMan.speed = 2;

        ArrayList<int[]> oldManPath = new ArrayList<>();
        oldManPath.add(new int[]{2, 2});
        oldManPath.add(new int[]{2, 4});
        oldManPath.add(new int[]{4, 4});
        oldManPath.add(new int[]{4, 2});

        oldMan.setPath(oldManPath, 48, true);
        oldMan.setMapId("/MapMatrix");
        npcs.add(oldMan);
        ShopNPC shopKeeper = new ShopNPC(
                "Shop Keeper",
                "Xin chao! Chao mung ban den cua hang.",
                "Ban muon mua mot mon do nao do sao?",
                "Hay xem thu cua hang cua toi!"
        );
        shopKeeper.x = 900;
        shopKeeper.y = 700;
        shopKeeper.setMapId("/MapMatrix");
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

        trainer.speed = 2;

        ArrayList<int[]> trainerPath = new ArrayList<>();
        trainerPath.add(new int[]{7, 10});
        trainerPath.add(new int[]{7, 13});
        trainerPath.add(new int[]{9, 14});
        trainerPath.add(new int[]{9, 14});
        trainerPath.add(new int[]{10, 12});
        trainerPath.add(new int[]{9, 10});

        trainer.setPath(trainerPath, 48, true);
        trainer.setWaitDuration(180);
        trainer.setMapId("/MapMatrix");

        npcs.add(trainer);
    }
    public NPC getNearbyNPC() {
        String activeMap = gp.tileH.getCurrentMap();
        for (NPC npc : npcs) {
            if (npc.belongsToMap(activeMap) && isPlayerNearNPC(npc)) {
                return npc;
            }
        }
        return null;
    }

    public void updateShop() {
        if (shopEvent == null) {
            return;
        }
        shopHandler.update(keyH);
        if (!shopHandler.isOpen()) {
            shopEvent.endShop();
            shopEvent = null;
        }
    }

    public void draw(GraphicsContext graphics, int tileSize) {
        String activeMap = gp.tileH.getCurrentMap();
        for (NPC npc : npcs) {
            if (!npc.belongsToMap(activeMap)) {
                continue;
            }

            if (!npc.isMoving() && isPlayerNearNPC(npc)) {
                updateNPCDirection(npc);
            }

            npc.draw(graphics, tileSize, gp.screenWidth, gp.screenHeight, player.x, player.y);
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
        String activeMap = gp.tileH.getCurrentMap();
        for (NPC npc : npcs) {
            if (npc.belongsToMap(activeMap)) {
                npc.updateMovement();
            }
        }

        if (!keyH.fPressed) return;

        if (dialogueEvent != null) {
            currentDialogue = dialogueEvent.nextDialogue();

            if (dialogueEvent.isCompleted()) {
                NPC npc = dialogueEvent.getNpc();
                dialogueEvent = null;
                currentDialogue = "";

                resumeAllNPCs();

                if (npc instanceof ShopNPC) {
                    shopEvent = new ShopEvent((ShopNPC) npc, shopHandler);
                    shopEvent.startShop();
                }
            }

            keyH.fPressed = false;
            return;
        }

        NPC npc = getNearbyNPC();

        if (npc != null) {
            pauseAllNPCs();
            updateNPCDirection(npc);

            dialogueEvent = new DialogueEvent(npc);
            currentDialogue = dialogueEvent.startDialogue();

            keyH.fPressed = false;
        }
    }
    private void pauseAllNPCs() {
        for (NPC npc : npcs) {
            npc.pauseMovement();
        }
    }

    private void resumeAllNPCs() {
        for (NPC npc : npcs) {
            npc.resumeMovement();
        }
    }
}
