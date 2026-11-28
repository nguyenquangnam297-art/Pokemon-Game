package Data;

import java.util.HashMap;
import java.util.Map;

public class ItemDatabase {
    private static Map<String, Item> items = new HashMap<>();
    private static boolean loaded = false;

    public static void load() {
        if (loaded) return;

        // TODO: thay null bằng ImageIO.read(...) giống cách Player.java load sprite,
        items.put("potion", new Item("potion", "Potion", "Hồi 20 HP cho 1 Pokemon.", null, 50, 100));
        items.put("super potion", new Item("super potion", "Super Potion", "Hồi 50 HP cho 1 Pokemon.", null, 150, 300));
        items.put("hyper potion", new Item("hyper potion", "Hyper Potion", "Hồi 200 HP cho 1 Pokemon.", null, 450, 800));
        items.put("antidote", new Item("antidote", "Antidote", "Chữa trị trạng thái Poison (trúng độc).",null, 50, 100));
        items.put("revive", new Item("revive", "Revive", "Hồi sinh 1 Pokemon đã ngất với 50% HP tối đa.", null, 500, 1500));
        items.put("pokeball", new Item("pokeball", "Poke Ball", "Dùng để bắt Pokemon hoang dã.", null, 100, 200));
        items.put("great ball", new Item("great ball", "Great Ball", "Tỷ lệ bắt Pokemon cao hơn Pokeball.", null, 300, 600));
        items.put("ultra ball", new Item("ultra ball", "Ultra Ball", "Tỷ lệ bắt Pokemon cao hơn Great Ball.", null, 600, 1200));
        items.put("x attack", new Item("x attack", "X Attack", "Tăng Attack trong trận đấu.", null, 200, 500));
        items.put("x defense", new Item("x defense", "X Defense", "Tăng Defense trong trận đấu.", null, 250, 500));
        items.put("ether", new Item("ether", "Ether", "Hồi PP cho một skill.", null, 400, 1000));
        loaded = true;
    }

    public static Item get(String id) {
        return items.get(id);
    }
}
