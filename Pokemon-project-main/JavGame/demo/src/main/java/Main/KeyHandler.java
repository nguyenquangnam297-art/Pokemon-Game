package Main;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class KeyHandler {
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean fPressed;

    public void attachTo(Scene scene) {
        scene.setOnKeyPressed(event -> setPressed(event.getCode(), true));
        scene.setOnKeyReleased(event -> setPressed(event.getCode(), false));
    }

    private void setPressed(KeyCode code, boolean pressed) {
        switch (code) {
            case W -> upPressed = pressed;
            case A -> leftPressed = pressed;
            case S -> downPressed = pressed;
            case D -> rightPressed = pressed;
            case F -> fPressed = pressed;
            default -> {
            }
        }
    }
}
