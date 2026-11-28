package Battle;

import Data.Inventory;
import Data.InventorySlot;
import Entity.Monster;
import java.util.ArrayList;
import java.util.List;

public class BattleItemHandler {

    public static boolean isBallItem(String itemId) {
        if (itemId == null) {
            return false;
        }
        String id = itemId.trim().toLowerCase();
        return id.equals("pokeball") || id.equals("great ball") || id.equals("ultra ball");
    }

    public static boolean isBattleUsableItem(String itemId) {
        if (itemId == null) {
            return false;
        }
        String id = itemId.trim().toLowerCase();
        return id.equals("heal_potion")
            || id.equals("atk_buff")
            || id.equals("spd_buff")
            || id.equals("potion")
            || id.equals("super potion")
            || id.equals("hyper potion")
            || id.equals("x attack")
            || id.equals("x defense");
    }

    public static int getTotalBallCount(Inventory inventory) {
        int total = 0;
        for (int i = 0; i < inventory.getSlots().size(); i++) {
            InventorySlot slot = inventory.getSlots().get(i);
            if (isBallItem(slot.itemId) && slot.quantity > 0) {
                total += slot.quantity;
            }
        }
        return total;
    }

    public static int getTotalBattleItemCount(Inventory inventory) {
        int total = 0;
        for (int i = 0; i < inventory.getSlots().size(); i++) {
            InventorySlot slot = inventory.getSlots().get(i);
            if (isBattleUsableItem(slot.itemId) && slot.quantity > 0) {
                total += slot.quantity;
            }
        }
        return total;
    }

    public static List<InventorySlot> getAvailableBallSlots(Inventory inventory) {
        List<InventorySlot> balls = new ArrayList<InventorySlot>();
        for (int i = 0; i < inventory.getSlots().size(); i++) {
            InventorySlot slot = inventory.getSlots().get(i);
            if (isBallItem(slot.itemId) && slot.quantity > 0) {
                balls.add(slot);
            }
        }
        return balls;
    }

    public static List<InventorySlot> getAvailableBattleItemSlots(Inventory inventory) {
        List<InventorySlot> items = new ArrayList<InventorySlot>();
        for (int i = 0; i < inventory.getSlots().size(); i++) {
            InventorySlot slot = inventory.getSlots().get(i);
            if (isBattleUsableItem(slot.itemId) && slot.quantity > 0) {
                items.add(slot);
            }
        }
        return items;
    }

    public static String getDefaultBallId(Inventory inventory) {
        List<InventorySlot> balls = getAvailableBallSlots(inventory);
        if (!balls.isEmpty()) {
            return balls.get(0).itemId;
        }
        return "pokeball";
    }

    public static InventorySlot findSlotIgnoreCase(Inventory inventory, String itemId) {
        if (inventory == null || itemId == null) {
            return null;
        }
        for (int i = 0; i < inventory.getSlots().size(); i++) {
            InventorySlot slot = inventory.getSlots().get(i);
            if (slot.itemId != null && slot.itemId.equalsIgnoreCase(itemId) && slot.quantity > 0) {
                return slot;
            }
        }
        return null;
    }

    public static String useItem(Monster p1, Inventory inventory, String itemId) {
        InventorySlot slot = findSlotIgnoreCase(inventory, itemId);
        if (slot == null) {
            return null;
        }

        String exactId = slot.itemId;
        if (!inventory.useItem(exactId)) {
            return null;
        }

        String normalized = exactId.trim().toLowerCase();
        String msg = "";
        if (normalized.equals("heal_potion")) {
            int healAmount = p1.getMaxHp() * 10 / 100;
            p1.heal(healAmount);
            msg = "Bạn đã dùng Thuốc Hồi Máu!\n(Hồi " + healAmount + " HP)";
        } else if (normalized.equals("potion")) {
            p1.heal(20);
            msg = "Bạn đã dùng Potion!\n(Hồi 20 HP)";
        } else if (normalized.equals("super potion")) {
            p1.heal(50);
            msg = "Bạn đã dùng Super Potion!\n(Hồi 50 HP)";
        } else if (normalized.equals("hyper potion")) {
            p1.heal(200);
            msg = "Bạn đã dùng Hyper Potion!\n(Hồi 200 HP)";
        } else if (normalized.equals("atk_buff") || normalized.equals("x attack")) {
            p1.applyAtkBuff(3);
            msg = "Bạn đã dùng Thuốc Sức Mạnh!\n(Tăng atk 2 lượt tới)";
        } else if (normalized.equals("spd_buff")) {
            p1.applySpdBuff(3);
            msg = "Bạn đã dùng Thuốc Tốc Độ!\n(Tăng tốc trong 2 lượt tới)";
        } else if (normalized.equals("x defense")) {
            p1.applySpdBuff(3);
            msg = "Bạn đã dùng X Defense!";
        }

        return msg;
    }
}