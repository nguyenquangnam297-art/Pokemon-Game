package Main;

import Data.InventorySlot;
import Data.Item;
import Data.ItemDatabase;
import Data.SaveManager;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

public class MenuUI {
    private final GamePanel gp;

    public final int mainMenu = 0;
    public final int itemMenu = 1;
    public int subState = mainMenu;

    private final String[] mainOptions = {"Pokemon", "Item", "Save", "Thoat"};
    public int commandNum = 0;

    private int inputCooldown = 0;

    private final Font font = Font.font("Arial", 20);
    private final Color panelColor = Color.rgb(0, 0, 0, 210 / 255.0);

    public MenuUI(GamePanel gp) {
        this.gp = gp;
    }

    public void update() {
        if (inputCooldown > 0) {
            inputCooldown--;
        }

        if (gp.keyH.escapePressed && subState == mainMenu) {
            close();
            gp.keyH.escapePressed = false;
            return;
        }

        if (subState == mainMenu) {
            handleMainMenuInput();
        } else if (subState == itemMenu) {
            handleItemMenuInput();
        }
    }

    private void handleMainMenuInput() {
        if (inputCooldown == 0) {
            if (gp.keyH.upPressed) {
                commandNum = Math.max(0, commandNum - 1);
                inputCooldown = 10;
            }
            if (gp.keyH.downPressed) {
                commandNum = Math.min(mainOptions.length - 1, commandNum + 1);
                inputCooldown = 10;
            }
        }

        if (gp.keyH.enterPressed) {
            gp.keyH.enterPressed = false;
            inputCooldown = 10;

            switch (commandNum) {
                case 0 -> {
                    /* TODO: Mở màn hình danh sách Pokemon (team) */
                }
                case 1 -> {
                    subState = itemMenu;
                    commandNum = 0;
                }
                case 2 -> {
                    gp.playerData.syncFrom(gp.player);
                    SaveManager.save(gp.playerData);
                    System.out.println("Đã lưu game!");
                }
                case 3 -> close();
            }
        }
    }

    private void handleItemMenuInput() {
        List<InventorySlot> slots = gp.playerData.inventorySlots;
        int size = Math.max(slots.size(), 1);

        if (inputCooldown == 0) {
            if (gp.keyH.upPressed) {
                commandNum = Math.max(0, commandNum - 1);
                inputCooldown = 10;
            }
            if (gp.keyH.downPressed) {
                commandNum = Math.min(size - 1, commandNum + 1);
                inputCooldown = 10;
            }
        }

        if (gp.keyH.enterPressed) {
            gp.keyH.enterPressed = false;
            inputCooldown = 10;
            // TODO: Dùng item đang chọn
            // if (!slots.isEmpty()) gp.playerData.getInventory().useItem(slots.get(commandNum).itemId);
        }

        if (gp.keyH.escapePressed) {
            gp.keyH.escapePressed = false;
            subState = mainMenu;
            commandNum = 0;
            inputCooldown = 10;
        }
    }

    private void close() {
        gp.gameState = gp.playState;
        subState = mainMenu;
        commandNum = 0;
        inputCooldown = 0;
    }

    public void draw(GraphicsContext gc) {
        gc.save();
        gc.setFont(font);

        if (subState == mainMenu) {
            drawMainMenu(gc);
        } else if (subState == itemMenu) {
            drawItemMenu(gc);
        }

        gc.restore();
    }

    private void drawMainMenu(GraphicsContext gc) {
        int x = gp.tileSize * 9;
        int y = gp.tileSize;
        int width = gp.tileSize * 6;
        int height = gp.tileSize * 5;
        drawSubWindow(gc, x, y, width, height);

        x += gp.tileSize / 2;
        y += gp.tileSize;
        gc.setFill(Color.WHITE);
        for (int i = 0; i < mainOptions.length; i++) {
            gc.fillText(mainOptions[i], x, y);
            if (i == commandNum) {
                gc.fillText(">", x - 25, y);
            }
            y += 40;
        }
    }

    private void drawItemMenu(GraphicsContext gc) {
        int x = gp.tileSize;
        int y = gp.tileSize / 2;
        int width = gp.tileSize * 10;
        int height = gp.tileSize * 9;
        drawSubWindow(gc, x, y, width, height);

        List<InventorySlot> slots = gp.playerData.inventorySlots;
        int textX = x + gp.tileSize / 2;
        int textY = y + gp.tileSize;

        gc.setFill(Color.WHITE);
        if (slots == null || slots.isEmpty()) {
            gc.fillText("(Túi đồ trống)", textX, textY);
            return;
        }

        for (int i = 0; i < slots.size(); i++) {
            InventorySlot slot = slots.get(i);
            Item item = ItemDatabase.get(slot.itemId);
            String label = (item != null ? item.name : slot.itemId) + "  x" + slot.quantity;

            gc.fillText(label, textX, textY);
            if (i == commandNum) {
                gc.fillText(">", textX - 25, textY);
            }
            textY += 40;
        }
    }

    private void drawSubWindow(GraphicsContext gc, int x, int y, int width, int height) {
        gc.setFill(panelColor);
        gc.fillRoundRect(x, y, width, height, 25, 25);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
    }
}