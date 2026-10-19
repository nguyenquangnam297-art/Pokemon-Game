package Data;

import java.awt.image.BufferedImage;

public class Item {
    public String id;
    public String name;
    public String description;
    public BufferedImage icon;

    public Item(String id, String name, String description, BufferedImage icon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
    }
}
