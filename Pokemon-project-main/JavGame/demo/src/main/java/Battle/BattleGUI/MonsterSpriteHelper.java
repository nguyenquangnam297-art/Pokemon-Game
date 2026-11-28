package Battle.BattleGUI;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class MonsterSpriteHelper {

    private static final int SHEET_COLS = 4;
    private static final int SHEET_ROWS = 2;
    private static final String CONTROLLER_KEY = "monsterSpriteController";


    public static void applyStaticFrame(ImageView view, Image img) {
        stopAnimation(view);
        view.setImage(img);
        if (img == null || img.getWidth() <= 0 || img.getHeight() <= 0) {
            return;
        }

        int[] grid = detectGrid(img);
        double frameWidth = img.getWidth() / grid[0];
        double frameHeight = img.getHeight() / grid[1];
        view.setViewport(new Rectangle2D(0.0, 0.0, frameWidth, frameHeight));
    }


    public static void setupAnimatedSprite(ImageView view, Image img) {
        stopAnimation(view);
        view.setImage(img);
        if (img == null || img.getWidth() <= 0 || img.getHeight() <= 0) {
            return;
        }

        int[] grid = detectGrid(img);
        int cols = grid[0];
        int rows = grid[1];
        double frameWidth = img.getWidth() / cols;
        double frameHeight = img.getHeight() / rows;

        view.setViewport(new Rectangle2D(0.0, 0.0, frameWidth, frameHeight));

        if (cols > 1) {
            SpriteAnimationController controller = new SpriteAnimationController(
                view, cols, rows, frameWidth, frameHeight
            );
            view.getProperties().put(CONTROLLER_KEY, controller);
            controller.start();
        }
    }

    public static void playAttackPose(Node targetView, double durationMillis) {
        if (!(targetView instanceof ImageView)) {
            return;
        }
        Object obj = targetView.getProperties().get(CONTROLLER_KEY);
        if (obj instanceof SpriteAnimationController) {
            ((SpriteAnimationController) obj).triggerAttackRow(durationMillis);
        }
    }

    public static void stopAnimation(ImageView view) {
        if (view == null) {
            return;
        }
        Object obj = view.getProperties().remove(CONTROLLER_KEY);
        if (obj instanceof SpriteAnimationController) {
            ((SpriteAnimationController) obj).stop();
        }
    }

    private static int[] detectGrid(Image img) {
        double w = img.getWidth();
        double h = img.getHeight();
        if (w > h * 1.5) {
            return new int[] { SHEET_COLS, SHEET_ROWS };
        }
        return new int[] { 1, 1 };
    }

    private static class SpriteAnimationController {
        private final ImageView view;
        private final int cols;
        private final int rows;
        private final double frameWidth;
        private final double frameHeight;
        private final Timeline loopTimeline;
        private PauseTransition attackResetTimer;
        private int currentCol = 0;
        private int currentRow = 0;

        public SpriteAnimationController(
                ImageView view,
                int cols,
                int rows,
                double frameWidth,
                double frameHeight) {
            this.view = view;
            this.cols = cols;
            this.rows = rows;
            this.frameWidth = frameWidth;
            this.frameHeight = frameHeight;

            this.loopTimeline = new Timeline(new KeyFrame(Duration.millis(180), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    currentCol = (currentCol + 1) % SpriteAnimationController.this.cols;
                    updateViewport();
                }
            }));
            this.loopTimeline.setCycleCount(Timeline.INDEFINITE);
        }

        public void start() {
            loopTimeline.play();
        }

        public void stop() {
            loopTimeline.stop();
            if (attackResetTimer != null) {
                attackResetTimer.stop();
            }
        }

        public void triggerAttackRow(double durationMillis) {
            if (rows < 2) {
                return;
            }
            if (attackResetTimer != null) {
                attackResetTimer.stop();
            }
            currentRow = 1;
            currentCol = 0;
            updateViewport();

            attackResetTimer = new PauseTransition(Duration.millis(durationMillis));
            attackResetTimer.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    currentRow = 0;
                    currentCol = 0;
                    updateViewport();
                }
            });
            attackResetTimer.play();
        }

        private void updateViewport() {
            view.setViewport(new Rectangle2D(
                currentCol * frameWidth,
                currentRow * frameHeight,
                frameWidth,
                frameHeight
            ));
        }
    }
}