package Data;

import Entity.Player;
import Entity.Monster;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlayerData implements Serializable {
    private static final long serialVersionUID = 1L;

    public int x, y;
    public String direction;
    public String currentMapId = "map1";

    public List<Monster> team = new ArrayList<>();
    public List<InventorySlot> inventorySlots = new ArrayList<>();
    public List<String> badges = new ArrayList<>();

    public Inventory getInventory() {
        return new Inventory(inventorySlots);
    }

    public void syncFrom(Player player) {
        this.x = player.x;
        this.y = player.y;
        this.direction = player.direction;
    }

    public void applyTo(Player player) {
        player.x = this.x;
        player.y = this.y;
        player.direction = this.direction;
    }
}