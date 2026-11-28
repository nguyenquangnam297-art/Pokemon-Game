package Event;

import Event.ShopHandler;
import Entity.NPC.ShopNPC;

public class ShopEvent extends Event {
    private ShopNPC shopNPC;
    private ShopHandler shopHandler;

    public ShopEvent(ShopNPC shopNPC, ShopHandler shopHandler) {
        this.shopHandler = shopHandler;
        this.shopNPC = shopNPC;
    }

    public ShopNPC getShopNPC() {
        return shopNPC;
    }
    public void startShop() {
        start();
        shopHandler.open();
    }
    public void endShop() {
        shopHandler.close();
        complete();
    }
}
