package Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Entity.Monster;
import Entity.Player;

public class PlayerData implements Serializable {
    private static final long serialVersionUID = 1L;

    public int x, y;
    public String direction;
    public String currentMapId = "/MapMatrix";

    public List<Monster> team = new ArrayList<>();
    public List<InventorySlot> inventorySlots = new ArrayList<>();
    public List<String> badges = new ArrayList<>();
    private int money = 10;

    public int level = 1;
    public int currentXp = 0;

    public Inventory getInventory() {
        return new Inventory(inventorySlots);
    }

    public void copyFrom(PlayerData other) {
        this.x = other.x;
        this.y = other.y;
        this.direction = other.direction;
        this.currentMapId = other.currentMapId;

        this.team = other.team != null ? new ArrayList<>(other.team) : new ArrayList<>();
        this.badges = other.badges != null ? new ArrayList<>(other.badges) : new ArrayList<>();

        this.inventorySlots = new ArrayList<>();
        if (other.inventorySlots != null) {
            for (InventorySlot slot : other.inventorySlots) {
                this.inventorySlots.add(new InventorySlot(slot.itemId, slot.quantity));
            }
        }

        this.money = other.money;
        this.level = Math.max(1, other.level);
        this.currentXp = Math.max(0, other.currentXp);
    }

    public void syncFrom(Player player) {
        this.x = player.x;
        this.y = player.y;
        this.direction = player.direction;
    }

    public void applyTo(Player player) {
        player.x = this.x;
        player.y = this.y;
        if (this.direction != null) {
            player.direction = this.direction;
        }
    }
    public int getMoney() { return money;}
    public boolean spendMoney(int amount) {
        if (amount <= 0 || money < amount) {
            return false;
        }

        money -= amount;
        return true;
    }
    public void addMoney(int amount) {
        if(amount > 0) {
            money += amount;
        }
    }

    public int getXpForNextLevel() {
        return 100 + (level - 1) * 50;
    }

    public int addXp(int amount) {
        if (amount <= 0) {
            return 0;
        }

        currentXp += amount;
        int levelsGained = 0;
        while (currentXp >= getXpForNextLevel()) {
            currentXp -= getXpForNextLevel();
            level++;
            levelsGained++;
        }
        return levelsGained;
    }

    public double getStatMultiplier() {
        return 1.0 + (level - 1) * 0.08;
    }
}
