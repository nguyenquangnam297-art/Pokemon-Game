package Battle.BattleGUI;

import java.util.function.BooleanSupplier;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class BattleInputHandler {

    public static void attach(
            Scene scene,
            final SkillControlPanel skillPanelBox,
            final PartySwitchOverlay partyOverlay,
            final BooleanSupplier onIntroSkip,
            final Runnable onFullscreenToggle) {

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode code = event.getCode();

                if (code == KeyCode.F11) {
                    if (onFullscreenToggle != null) {
                        onFullscreenToggle.run();
                    }
                    return;
                }

                // skip intro
                if ((code == KeyCode.ENTER || code == KeyCode.SPACE) && onIntroSkip != null) {
                    if (onIntroSkip.getAsBoolean()) {
                        event.consume();
                        return;
                    }
                }

                if (partyOverlay != null && partyOverlay.isVisible()) {
                    partyOverlay.handleKeyPress(event);
                    return;
                }

                if (skillPanelBox != null && !skillPanelBox.isDisabled()) {
                    if (code == KeyCode.ESCAPE || code == KeyCode.BACK_SPACE) {
                        skillPanelBox.handleBackKey();
                    } else if (code == KeyCode.LEFT || code == KeyCode.A) {
                        skillPanelBox.moveSelection(-1);
                    } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                        skillPanelBox.moveSelection(1);
                    } else if (code == KeyCode.ENTER || code == KeyCode.SPACE) {
                        skillPanelBox.triggerSelectedButton();
                    }
                }
            }
        });
    }
}