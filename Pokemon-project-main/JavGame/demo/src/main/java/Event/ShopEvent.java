package Event;

import Entity.NPC.ShopNPC;

public class ShopEvent extends Event {
    private ShopNPC shopNPC;

    public ShopEvent(ShopNPC shopNPC) {
        this.shopNPC = shopNPC;
    }

    public ShopNPC getShopNPC() {
        return shopNPC;
    }
    public void startShop() {
        start();
        shopNPC.openShop();
    }
    public void endShop() {
        complete();
    }
}
