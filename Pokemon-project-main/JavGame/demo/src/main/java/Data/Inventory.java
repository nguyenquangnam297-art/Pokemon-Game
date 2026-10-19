package Data;

import java.util.List;


public class Inventory {
    private List<InventorySlot> slots;

    public Inventory(List<InventorySlot> slots) {
        this.slots = slots;
    }

    public void addItem(String itemId, int qty) {
        for (InventorySlot slot : slots) {
            if (slot.itemId.equals(itemId)) {
                slot.quantity += qty;
                return;
            }
        }
        slots.add(new InventorySlot(itemId, qty));
    }

    public boolean useItem(String itemId) {
        for (int i = 0; i < slots.size(); i++) {
            InventorySlot slot = slots.get(i);
            if (slot.itemId.equals(itemId) && slot.quantity > 0) {
                slot.quantity--;
                if (slot.quantity == 0) {
                    slots.remove(i);
                }
                return true;
            }
        }
        return false;
    }

    public List<InventorySlot> getSlots() {
        return slots;
    }
}
