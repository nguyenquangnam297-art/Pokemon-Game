package Event;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import Data.Item;
import Data.ItemDatabase;
import Data.PlayerData;
import Main.KeyHandler;

import java.util.ArrayList;
import java.util.List;

public class ShopHandler {

    private final PlayerData playerData;
    private final List<String> shopItems = new ArrayList<>();
    private boolean shopOpen = false;
    private int selectedItemIndex = 0;
    private String shopMessage = "";
    private boolean sellingMode = false;
    private boolean keyLocked = false;
    private int visibleItemCount = 3;
    private int scrollOffset = 0;

    public ShopHandler(PlayerData playerData) {
        this.playerData = playerData;

        ItemDatabase.load();

        shopItems.add("potion");
        shopItems.add("super potion");
        shopItems.add("hyper potion");
        shopItems.add("antidote");
        shopItems.add("revive");
        shopItems.add("pokeball");
        shopItems.add("great ball");
        shopItems.add("ultra ball");
        shopItems.add("x attack");
        shopItems.add("x defense");
        shopItems.add("ether");
    }

    public void open() {
        shopOpen = true;
        sellingMode = false;
        selectedItemIndex = 0;
        shopMessage = "";
        keyLocked = false;
    }

    public void close() {
        shopOpen = false;
    }

    public boolean isOpen() {
        return shopOpen;
    }

    public boolean buyItem(String itemId) {
        if (!shopItems.contains(itemId)) {
            return false;
        }

        Item item = ItemDatabase.get(itemId);

        if (item == null) {
            return false;
        }

        if (!playerData.spendMoney(item.buyprice)) {
            return false;
        }

        playerData.getInventory().addItem(itemId, 1);

        return true;
    }
    public boolean sellItem(String itemId) {
        Item item = ItemDatabase.get(itemId);

        if (item == null) {
            return false;
        }

        if (!playerData.getInventory().removeItem(itemId, 1)) {
            return false;
        }

        playerData.addMoney(item.sellprice);
        return true;
    }
    private boolean isAnyShopKeyPressed(KeyHandler keyH) {
        return keyH.upPressed
                || keyH.downPressed
                || keyH.leftPressed
                || keyH.rightPressed
                || keyH.enterPressed
                || keyH.escapePressed;
    }

    private int getCurrentItemCount() {
        if(sellingMode) {
            return playerData.getInventory().getSlots().size();
        }

        return shopItems.size();
    }

    private String getSelectedItemId() {
        if(selectedItemIndex < 0 || selectedItemIndex >= getCurrentItemCount()) {
            return null;
        }

        if (sellingMode) {
            return playerData.getInventory().getSlots().get(selectedItemIndex).itemId;
        }

        return shopItems.get(selectedItemIndex);
    }

    public void update(KeyHandler keyH) {
        if (!shopOpen) {
            return;
        }

        if (keyLocked) {
            if (!isAnyShopKeyPressed(keyH)) {
                keyLocked = false;
            }
            return;
        }

        if (keyH.escapePressed) {
            close();
            keyH.escapePressed = false;
            keyLocked = true;
            return;
        }

        if (keyH.leftPressed) {
            sellingMode = false;
            selectedItemIndex = 0;
            shopMessage = "";
            keyLocked = true;
            return;
        }

        if (keyH.rightPressed) {
            sellingMode = true;
            selectedItemIndex = 0;
            shopMessage = "";
            keyLocked = true;
            return;
        }

        if (keyH.upPressed) {
            selectedItemIndex--;
            keyLocked = true;
        }

        else if (keyH.downPressed) {
            selectedItemIndex++;
            keyLocked = true;
        }

        else if (keyH.enterPressed) {
            String itemId = getSelectedItemId();

            if (itemId == null) {
                shopMessage = sellingMode
                        ? "Ban khong co vat pham de ban!"
                        : "Khong co vat pham trong shop!";
            } else {
                boolean success;

                if (sellingMode) {
                    success = sellItem(itemId);
                } else {
                    success = buyItem(itemId);
                }

                Item item = ItemDatabase.get(itemId);

                if (success) {
                    shopMessage = sellingMode
                            ? "Da ban " + item.name + " thanh cong!"
                            : "Da mua " + item.name + " thanh cong!";
                } else {
                    shopMessage = sellingMode
                            ? "Khong the ban vat pham!"
                            : "Khong du tien de mua vat pham!";
                }
            }

            keyLocked = true;
        }

        int itemCount = getCurrentItemCount();

        if (itemCount == 0) {
            selectedItemIndex = 0;
            scrollOffset = 0;
        } else {
            // Không cho phép chọn vượt quá số vật phẩm
            if (selectedItemIndex < 0) {
                selectedItemIndex = itemCount - 1;
            }

            if (selectedItemIndex >= itemCount) {
                selectedItemIndex = 0;
            }

            // Chuyển sang trang tiếp theo
            if (selectedItemIndex >= scrollOffset + visibleItemCount) {
                scrollOffset += visibleItemCount;
            }

            // Chuyển về trang trước
            if (selectedItemIndex < scrollOffset) {
                scrollOffset -= visibleItemCount;
            }

            // Đảm bảo scrollOffset không vượt quá giới hạn
            scrollOffset = Math.max(0, Math.min(scrollOffset, ((itemCount - 1) / visibleItemCount) * visibleItemCount
            ));
        }
    }


    public void draw(GraphicsContext graphics, int screenWidth, int screenHeight) {
        if (!shopOpen) {
            return;
        }

        // 1. Lớp nền tối phía sau Shop
        graphics.setFill(Color.rgb(0, 0, 0, 0.55));
        graphics.fillRect(0, 0, screenWidth, screenHeight);

        // 2. Khung cửa hàng
        double boxWidth = 540;
        double boxHeight = 420;

        double boxX = (screenWidth - boxWidth) / 2;
        double boxY = (screenHeight - boxHeight) / 2;

        // Nền màu trắng ngà
        graphics.setFill(Color.rgb(248, 244, 220));
        graphics.fillRect(boxX, boxY, boxWidth, boxHeight);

        // Viền ngoài
        graphics.setStroke(Color.rgb(35, 55, 100));
        graphics.setLineWidth(5);
        graphics.strokeRect(boxX, boxY, boxWidth, boxHeight);

        // Viền trong
        graphics.setStroke(Color.rgb(120, 135, 155));
        graphics.setLineWidth(2);
        graphics.strokeRect(
                boxX + 7, boxY + 7,
                boxWidth - 14, boxHeight - 14
        );

        // 3. Tiêu đề
        graphics.setFill(Color.rgb(35, 55, 100));
        graphics.setFont(Font.font("Monospaced", 25));
        graphics.fillText("POKEMON SHOP", boxX + 25, boxY + 42);

        // Tiền người chơi
        graphics.setFill(Color.rgb(40, 40, 40));
        graphics.setFont(Font.font("Monospaced", 16));
        graphics.fillText(
                "MONEY: " + playerData.getMoney() + " G",
                boxX + 320,
                boxY + 40
        );

        // 4. Hai tab BUY và SELL
        double tabY = boxY + 65;
        double tabWidth = 110;
        double tabHeight = 35;

        // Tab BUY
        graphics.setFill(!sellingMode
                ? Color.rgb(65, 115, 165)
                : Color.rgb(215, 220, 220));

        graphics.fillRect(boxX + 25, tabY, tabWidth, tabHeight);

        graphics.setStroke(Color.rgb(35, 55, 100));
        graphics.setLineWidth(2);
        graphics.strokeRect(boxX + 25, tabY, tabWidth, tabHeight);

        graphics.setFill(!sellingMode ? Color.WHITE : Color.BLACK);
        graphics.setFont(Font.font("Monospaced", 16));
        graphics.fillText("BUY", boxX + 60, tabY + 23);

        // Tab SELL
        graphics.setFill(sellingMode
                ? Color.rgb(65, 115, 165)
                : Color.rgb(215, 220, 220));

        graphics.fillRect(boxX + 140, tabY, tabWidth, tabHeight);
        graphics.strokeRect(boxX + 140, tabY, tabWidth, tabHeight);

        graphics.setFill(sellingMode ? Color.WHITE : Color.BLACK);
        graphics.fillText("SELL", boxX + 170, tabY + 23);

        // 5. Đường phân cách
        graphics.setStroke(Color.rgb(100, 115, 140));
        graphics.setLineWidth(2);
        graphics.strokeLine(
                boxX + 20, boxY + 115,
                boxX + boxWidth - 20, boxY + 115
        );

        // 6. Tiêu đề danh sách
        graphics.setFill(Color.rgb(35, 55, 100));
        graphics.setFont(Font.font("Monospaced", 16));

        graphics.fillText(
                sellingMode ? "YOUR ITEMS" : "ITEMS FOR SALE",
                boxX + 25,
                boxY + 145
        );

        graphics.setFont(Font.font("Monospaced", 13));

        if (sellingMode) {
            graphics.fillText("QTY", boxX + 340, boxY + 145);
        }

        graphics.fillText("PRICE", boxX + 420, boxY + 145);

        // 7. Danh sách vật phẩm
        double itemY = boxY + 180;
        int itemCount = getCurrentItemCount();

// Chỉ vẽ số lượng vật phẩm nằm trong vùng hiển thị
        int endIndex = Math.min(
                scrollOffset + visibleItemCount,
                itemCount
        );

        for (int i = scrollOffset; i < endIndex; i++) {

            String itemId;

            if (sellingMode) {
                itemId = playerData.getInventory()
                        .getSlots()
                        .get(i).itemId;
            } else {
                itemId = shopItems.get(i);
            }

            Item item = ItemDatabase.get(itemId);

            if (item == null) {
                continue;
            }

            // Tô sáng vật phẩm đang được chọn
            if (i == selectedItemIndex) {
                graphics.setFill(Color.rgb(190, 210, 225));
                graphics.fillRect(
                        boxX + 18,
                        itemY - 22,
                        boxWidth - 36,
                        32
                );

                graphics.setFill(Color.rgb(30, 65, 130));
                graphics.setFont(Font.font("Monospaced", 17));
                graphics.fillText("▶", boxX + 28, itemY);
            }

            // Tên vật phẩm
            graphics.setFill(Color.rgb(35, 35, 35));
            graphics.setFont(Font.font("Monospaced", 16));
            graphics.fillText(item.name, boxX + 65, itemY);

            // Số lượng khi bán
            if (sellingMode) {
                int quantity = playerData.getInventory()
                        .getSlots()
                        .get(i).quantity;

                graphics.fillText(
                        String.valueOf(quantity),
                        boxX + 350,
                        itemY
                );
            }

            // Giá mua hoặc giá bán
            int price = sellingMode
                    ? item.sellprice
                    : item.buyprice;

            graphics.fillText(
                    price + " G",
                    boxX + 420,
                    itemY
            );

            itemY += 40;
        }
        // Chỉ báo còn vật phẩm phía trên
        if (scrollOffset > 0) {
            graphics.setFill(Color.rgb(35, 55, 100));
            graphics.setFont(Font.font("Monospaced", 13));
            graphics.fillText("▲", boxX + boxWidth - 35, boxY + 165);
        }

        // Chỉ báo còn vật phẩm phía dưới
        if (endIndex < itemCount) {
            graphics.setFill(Color.rgb(35, 55, 100));
            graphics.setFont(Font.font("Monospaced", 13));
            graphics.fillText("▼", boxX + boxWidth - 35, boxY + 275);
        }

        // 8. Trường hợp danh sách trống
        if (itemCount == 0) {
            graphics.setFill(Color.GRAY);
            graphics.setFont(Font.font("Monospaced", 15));

            graphics.fillText(
                    sellingMode
                            ? "You have no items to sell."
                            : "No items available.",
                    boxX + 45,
                    boxY + 195
            );
        }

        // 9. Thông tin vật phẩm đang chọn
        graphics.setStroke(Color.rgb(100, 115, 140));
        graphics.setLineWidth(2);
        graphics.strokeLine(
                boxX + 20, boxY + 295,
                boxX + boxWidth - 20, boxY + 295
        );

        if (itemCount > 0) {
            String selectedId = getSelectedItemId();
            Item selectedItem = ItemDatabase.get(selectedId);

            if (selectedItem != null) {
                graphics.setFill(Color.rgb(35, 55, 100));
                graphics.setFont(Font.font("Monospaced", 14));

                graphics.fillText(
                        selectedItem.description,
                        boxX + 25,
                        boxY + 320
                );
            }
        }

        // 10. Thông báo giao dịch
        if (shopMessage != null && !shopMessage.isEmpty()) {
            graphics.setFill(Color.rgb(180, 90, 30));
            graphics.setFont(Font.font("Monospaced", 13));

            graphics.fillText(
                    shopMessage,
                    boxX + 25,
                    boxY + 355
            );
        }

        // 11. Hướng dẫn điều khiển
        graphics.setFill(Color.rgb(35, 55, 100));
        graphics.setFont(Font.font("Monospaced", 12));

        graphics.fillText(
                "W/S: SELECT    A/D: CHANGE TAB",
                boxX + 25,
                boxY + 385
        );

        graphics.fillText(
                "ENTER: CONFIRM    ESC: EXIT",
                boxX + 25,
                boxY + 405
        );
    }

}