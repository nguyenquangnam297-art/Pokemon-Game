package Data;

import java.awt.image.BufferedImage;

public class Item {
    public String id;
    public String name;
    public String description;
    public BufferedImage icon;
    public int sellprice;
    public int buyprice;

    public Item(String id, String name, String description, BufferedImage icon, int sellprice, int buyprice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sellprice = sellprice;
        this.buyprice = buyprice;
    }
}
