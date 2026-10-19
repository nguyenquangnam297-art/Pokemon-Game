package Data;

import java.io.Serializable;

public class InventorySlot implements Serializable {
    private static final long serialVersionUID = 1L;

    public String itemId;
    public int quantity;

    public InventorySlot(String itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }
}
