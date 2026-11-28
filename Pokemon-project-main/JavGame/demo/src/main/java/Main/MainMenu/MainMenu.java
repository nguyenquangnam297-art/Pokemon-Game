package Main.MainMenu;

import Main.GamePanel;
import Main.KeyHandler;
import javafx.scene.canvas.GraphicsContext;

public class MainMenu {
    private final MainMenuUI ui;

    public MainMenu(GamePanel gamePanel, KeyHandler keyHandler) {
        this.ui = new MainMenuUI(gamePanel, keyHandler);

        gamePanel.setOnMouseClicked(event -> {
            if (gamePanel.gameState == gamePanel.titleState) {
                ui.handleMouseClick(event.getX(), event.getY());
            }
        });
        gamePanel.setOnMouseMoved(event -> {
            if (gamePanel.gameState == gamePanel.titleState) {
                ui.handleMouseMove(event.getX(), event.getY());
            }
        });
    }

    public void update() {
        ui.update();
    }

    public void draw(GraphicsContext graphics) {
        ui.draw(graphics);
    }
}
