package Data;

import java.util.HashMap;
import java.util.Map;

public class ItemDatabase {
    private static Map<String, Item> items = new HashMap<>();
    private static boolean loaded = false;

    public static void load() {
        if (loaded) return;

        // TODO: thay null bằng ImageIO.read(...) giống cách Player.java load sprite,
        items.put("potion", new Item("potion", "Potion", "Hồi 20 HP cho 1 Pokemon.", null));
        items.put("pokeball", new Item("pokeball", "Poke Ball", "Dùng để bắt Pokemon hoang dã.", null));
    }

    public static Item get(String id) {
        return items.get(id);
    }
}
