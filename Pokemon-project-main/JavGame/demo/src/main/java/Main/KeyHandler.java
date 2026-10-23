package Main;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class KeyHandler {
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean fPressed;
    public boolean enterPressed, escapePressed;

    public void attachTo(Scene scene) {
        scene.setOnKeyPressed(event -> setPressed(event.getCode(), true));
        scene.setOnKeyReleased(event -> setPressed(event.getCode(), false));
    }

    public void clearPressedKeys() {
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
        fPressed = false;
        enterPressed = false;
        escapePressed = false;
    }

    private void setPressed(KeyCode code, boolean pressed) {
        switch (code) {
            case W -> upPressed = pressed;
            case A -> leftPressed = pressed;
            case S -> downPressed = pressed;
            case D -> rightPressed = pressed;
            case F -> fPressed = pressed;
            case ENTER, SPACE -> enterPressed = pressed;
            case ESCAPE, E -> escapePressed = pressed;
            default -> {
            }
        }
    }
}
