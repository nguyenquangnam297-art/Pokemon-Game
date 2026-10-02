package Entity.NPC;

public class ShopNPC extends NPC {
    public ShopNPC(String name, String ... dialogues) {
        super(name,"shop_keeper", dialogues);
    }
    public void openShop() {
        System.out.println("SHOP OPEN");
    }
}
