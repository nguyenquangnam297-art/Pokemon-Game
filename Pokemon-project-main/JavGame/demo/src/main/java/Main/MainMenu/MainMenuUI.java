package Main.MainMenu;

import Data.PlayerData;
import Data.SaveManager;
import Main.GamePanel;
import Main.KeyHandler;

import javafx.application.Platform;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class MainMenuUI {

    private static final int OPT_CONTINUE = 0;
    private static final int OPT_NEW_GAME = 1;
    private static final int OPT_QUIT = 2;

    private static final double BUTTON_WIDTH = 320;
    private static final double BUTTON_HEIGHT = 58;
    private static final double BUTTON_GAP = 14;

    private static final double CONFIRM_W = 420;
    private static final double CONFIRM_H = 190;
    private static final double CONFIRM_BTN_W = 150;
    private static final double CONFIRM_BTN_H = 46;

    private final GamePanel gp;
    private final KeyHandler keyH;

    private final String[] options = {"CONTINUE", "NEW GAME", "QUIT"};
    private final String[] confirmOptions = {"YES", "NO"};

    private PlayerData savedData;
    private int selectedIndex;
    private int inputCooldown = 0;

    private boolean confirming = false;
    private int confirmIndex = 1;

    public MainMenuUI(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        refresh();
    }

    public void refresh() {
        savedData = SaveManager.load();
        selectedIndex = savedData != null ? OPT_CONTINUE : OPT_NEW_GAME;
        confirming = false;
        confirmIndex = 1;
        inputCooldown = 0;
    }

    public void update() {
        if (inputCooldown > 0) {
            inputCooldown--;
        }

        if (confirming) {
            updateConfirm();
            return;
        }

        if (inputCooldown == 0) {
            if (keyH.upPressed) {
                moveSelection(-1);
                inputCooldown = 10;
            } else if (keyH.downPressed) {
                moveSelection(1);
                inputCooldown = 10;
            }
        }

        if (inputCooldown == 0 && keyH.enterPressed) {
            keyH.enterPressed = false;
            inputCooldown = 10;
            chooseOption();
        }
    }

    private void updateConfirm() {
        if (inputCooldown > 0) return;

        if (keyH.leftPressed || keyH.rightPressed
                || keyH.upPressed || keyH.downPressed) {
            confirmIndex = 1 - confirmIndex;
            inputCooldown = 10;
        } else if (keyH.escapePressed) {
            keyH.escapePressed = false;
            confirming = false;
            inputCooldown = 10;
        } else if (keyH.enterPressed) {
            keyH.enterPressed = false;
            confirmChosen();
        }
    }

    private void moveSelection(int direction) {
        int next = selectedIndex;
        do {
            next = (next + direction + options.length) % options.length;
        } while (isDisabled(next));
        selectedIndex = next;
    }

    private boolean isDisabled(int index) {
        return index == OPT_CONTINUE && savedData == null;
    }

    private void chooseOption() {
        switch (selectedIndex) {
            case OPT_CONTINUE -> continueGame();
            case OPT_NEW_GAME -> requestNewGame();
            case OPT_QUIT -> Platform.exit();
        }
    }

    private void requestNewGame() {
        if (savedData != null) {
            confirming = true;
            confirmIndex = 1;
        } else {
            gp.startNewGame();
        }
    }

    private void confirmChosen() {
        confirming = false;
        inputCooldown = 10;
        if (confirmIndex == 0) {
            gp.startNewGame();
        }
    }

    private void continueGame() {
        if (savedData != null) {
            gp.continueGame(savedData);
        }
    }


    public void handleMouseClick(double mouseX, double mouseY) {
        if (confirming) {
            int hit = confirmButtonAt(mouseX, mouseY);
            if (hit >= 0) {
                confirmIndex = hit;
                confirmChosen();
            }
            return;
        }

        int hit = optionAt(mouseX, mouseY);
        if (hit < 0 || isDisabled(hit)) {
            return;
        }
        selectedIndex = hit;
        chooseOption();
    }

    public void handleMouseMove(double mouseX, double mouseY) {
        if (confirming) {
            int hit = confirmButtonAt(mouseX, mouseY);
            if (hit >= 0) confirmIndex = hit;
            return;
        }

        int hit = optionAt(mouseX, mouseY);
        if (hit >= 0 && !isDisabled(hit)) {
            selectedIndex = hit;
        }
    }

    private int optionAt(double mouseX, double mouseY) {
        double x = (gp.screenWidth - BUTTON_WIDTH) / 2.0;
        for (int i = 0; i < options.length; i++) {
            double y = getButtonY(i);
            if (mouseX >= x && mouseX <= x + BUTTON_WIDTH
                    && mouseY >= y && mouseY <= y + BUTTON_HEIGHT) {
                return i;
            }
        }
        return -1;
    }

    private int confirmButtonAt(double mouseX, double mouseY) {
        for (int i = 0; i < confirmOptions.length; i++) {
            double x = getConfirmButtonX(i);
            double y = getConfirmButtonY();
            if (mouseX >= x && mouseX <= x + CONFIRM_BTN_W
                    && mouseY >= y && mouseY <= y + CONFIRM_BTN_H) {
                return i;
            }
        }
        return -1;
    }

    public void draw(GraphicsContext gc) {
        gc.save();

        gc.setFill(Color.rgb(20, 34, 48));
        gc.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        gc.setFill(Color.rgb(31, 75, 73));
        gc.fillOval(-120, 350, 350, 350);
        gc.setFill(Color.rgb(35, 58, 82));
        gc.fillOval(gp.screenWidth - 220, -100, 350, 350);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        gc.setFill(Color.WHITE);
        gc.fillText("POKEMON ADVENTURE", gp.screenWidth / 2.0, 125);

        gc.setFont(Font.font("Arial", 15));
        gc.setFill(Color.LIGHTGRAY);
        gc.fillText("Welcome to your adventure!", gp.screenWidth / 2.0, 170);

        for (int i = 0; i < options.length; i++) {
            drawButton(gc, i);
        }

        gc.setFont(Font.font("Arial", 13));
        gc.setFill(Color.LIGHTGRAY);
        gc.fillText("W/S to select - ENTER or click to confirm",
                gp.screenWidth / 2.0, gp.screenHeight - 35);

        if (confirming) {
            drawConfirm(gc);
        }

        gc.restore();
    }

    private void drawButton(GraphicsContext gc, int index) {
        double x = (gp.screenWidth - BUTTON_WIDTH) / 2.0;
        double y = getButtonY(index);

        boolean selected = index == selectedIndex && !confirming;
        boolean disabled = isDisabled(index);
        String subtitle = getSubtitle(index);

        if (disabled) {
            gc.setFill(Color.rgb(75, 80, 88));
        } else if (selected) {
            gc.setFill(Color.rgb(43, 135, 105));
        } else {
            gc.setFill(Color.rgb(48, 65, 82));
        }
        gc.fillRoundRect(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, 16, 16);

        gc.setLineWidth(selected ? 3 : 1);
        gc.setStroke(selected ? Color.WHITE : Color.rgb(110, 130, 145));
        gc.strokeRoundRect(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, 16, 16);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 19));
        gc.setFill(disabled ? Color.LIGHTGRAY : Color.WHITE);
        gc.fillText(options[index], gp.screenWidth / 2.0,
                subtitle != null ? y + 23 : y + BUTTON_HEIGHT / 2.0);

        if (subtitle != null) {
            gc.setFont(Font.font("Arial", 11));
            gc.setFill(Color.rgb(210, 210, 210));
            gc.fillText(subtitle, gp.screenWidth / 2.0, y + 43);
        }

        if (selected && !disabled) {
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            gc.setFill(Color.WHITE);
            gc.fillText(">", x + 35, y + BUTTON_HEIGHT / 2.0);
        }
    }

    private String getSubtitle(int index) {
        if (index != OPT_CONTINUE) return null;
        if (savedData == null) return "NO SAVE DATA";

        int level = Math.max(1, savedData.level);
        return "Lv " + level + "  -  " + savedData.getMoney() + " G";
    }

    private void drawConfirm(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.6));
        gc.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        double boxX = (gp.screenWidth - CONFIRM_W) / 2.0;
        double boxY = (gp.screenHeight - CONFIRM_H) / 2.0;

        gc.setFill(Color.rgb(28, 44, 60));
        gc.fillRoundRect(boxX, boxY, CONFIRM_W, CONFIRM_H, 20, 20);
        gc.setLineWidth(2);
        gc.setStroke(Color.WHITE);
        gc.strokeRoundRect(boxX, boxY, CONFIRM_W, CONFIRM_H, 20, 20);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        double cx = gp.screenWidth / 2.0;

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        gc.fillText("Start a new game?", cx, boxY + 36);

        gc.setFont(Font.font("Arial", 14));
        gc.setFill(Color.LIGHTGRAY);
        gc.fillText("Your saved game will be replaced", cx, boxY + 68);
        gc.fillText("the next time you save.", cx, boxY + 88);

        for (int i = 0; i < confirmOptions.length; i++) {
            boolean selected = i == confirmIndex;
            double bx = getConfirmButtonX(i);
            double by = getConfirmButtonY();

            gc.setFill(selected ? Color.rgb(43, 135, 105) : Color.rgb(48, 65, 82));
            gc.fillRoundRect(bx, by, CONFIRM_BTN_W, CONFIRM_BTN_H, 14, 14);
            gc.setLineWidth(selected ? 3 : 1);
            gc.setStroke(selected ? Color.WHITE : Color.rgb(110, 130, 145));
            gc.strokeRoundRect(bx, by, CONFIRM_BTN_W, CONFIRM_BTN_H, 14, 14);

            gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            gc.setFill(Color.WHITE);
            gc.fillText(confirmOptions[i], bx + CONFIRM_BTN_W / 2.0, by + CONFIRM_BTN_H / 2.0);
        }
    }

    private double getButtonY(int index) {
        double totalHeight = options.length * BUTTON_HEIGHT + (options.length - 1) * BUTTON_GAP;
        double startY = (gp.screenHeight - totalHeight) / 2.0 + 55;
        return startY + index * (BUTTON_HEIGHT + BUTTON_GAP);
    }

    private double getConfirmButtonX(int index) {
        double boxX = (gp.screenWidth - CONFIRM_W) / 2.0;
        return index == 0
                ? boxX + 40
                : boxX + CONFIRM_W - 40 - CONFIRM_BTN_W;
    }

    private double getConfirmButtonY() {
        double boxY = (gp.screenHeight - CONFIRM_H) / 2.0;
        return boxY + 120;
    }
}
