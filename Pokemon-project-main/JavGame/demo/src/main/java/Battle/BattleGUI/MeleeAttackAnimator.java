package Battle.BattleGUI;

import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.util.Duration;

public class MeleeAttackAnimator {

    private static final String[] SCRATCH_SPRITE_PATHS = {
        "/AttackSprites/scratch.png",
        "/attacks/scratch.png",
        "/scratch.png"
    };

    public static void play(
            final Node attackerView,
            final Node defenderView,
            final boolean isP1Attacking,
            final Runnable onHit,
            final Runnable onFinished) {

        attackerView.setTranslateX(0.0);
        attackerView.setTranslateY(0.0);
        attackerView.setRotate(0.0);

        final double lungeX;
        final double lungeY;

        if (attackerView.getParent() != null && defenderView.getScene() != null) {
            Bounds defLocal = defenderView.getBoundsInLocal();
            Point2D defCenterInScene = defenderView.localToScene(defLocal.getCenterX(), defLocal.getCenterY());
            Point2D defCenterInParent = attackerView.getParent().sceneToLocal(defCenterInScene);

            Bounds attBounds = attackerView.getBoundsInParent();
            double attCenterX = attBounds.getCenterX();
            double attCenterY = attBounds.getCenterY();

            double dx = defCenterInParent.getX() - attCenterX;
            double dy = defCenterInParent.getY() - attCenterY;
            double distance = Math.hypot(dx, dy);

            double stopDistance = (attBounds.getWidth() + defLocal.getWidth()) * 0.35;
            if (stopDistance <= 0.0) {
                stopDistance = 150.0;
            }

            if (distance > stopDistance) {
                double ratio = (distance - stopDistance) / distance;
                lungeX = dx * ratio;
                lungeY = dy * ratio;
            } else {
                lungeX = 0.0;
                lungeY = 0.0;
            }
        } else {
            if (isP1Attacking) {
                lungeX = 780.0;
                lungeY = -240.0;
            } else {
                lungeX = -780.0;
                lungeY = 240.0;
            }
        }

        final double windUpAngle;
        final double strikeAngle;
        if (isP1Attacking) {
            windUpAngle = -20.0;
            strikeAngle = 25.0;
        } else {
            windUpAngle = 20.0;
            strikeAngle = -25.0;
        }

        final Effect originalEffect = defenderView.getEffect();
        final ColorAdjust whiteFlash = new ColorAdjust();
        whiteFlash.setBrightness(0.9);
        whiteFlash.setSaturation(-0.8);

        TranslateTransition lunge = new TranslateTransition(Duration.millis(400), attackerView);
        lunge.setFromX(0.0);
        lunge.setFromY(0.0);
        lunge.setToX(lungeX);
        lunge.setToY(lungeY);

        RotateTransition windUp = new RotateTransition(Duration.millis(110), attackerView);
        windUp.setFromAngle(0.0);
        windUp.setToAngle(windUpAngle);

        RotateTransition strike = new RotateTransition(Duration.millis(80), attackerView);
        strike.setFromAngle(windUpAngle);
        strike.setToAngle(strikeAngle);

        TranslateTransition recoil = new TranslateTransition(Duration.millis(400), attackerView);
        recoil.setFromX(lungeX);
        recoil.setFromY(lungeY);
        recoil.setToX(0.0);
        recoil.setToY(0.0);

        RotateTransition resetRotation = new RotateTransition(Duration.millis(150), attackerView);
        resetRotation.setFromAngle(strikeAngle);
        resetRotation.setToAngle(0.0);

        Timeline hitShake = BattleAnimator.createHitShakeTimeline(
            defenderView,
            whiteFlash,
            originalEffect,
            new Runnable() {
                @Override
                public void run() {
                    BattleAnimator.playTargetSpriteEffect(
                        defenderView,
                        SCRATCH_SPRITE_PATHS,
                        isP1Attacking,
                        220.0
                    );
                    if (onHit != null) {
                        onHit.run();
                    }
                }
            }
        );

        ParallelTransition impactPhase = new ParallelTransition(recoil, resetRotation, hitShake);

        SequentialTransition fullSequence = new SequentialTransition(lunge, windUp, strike, impactPhase);
        fullSequence.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                attackerView.setTranslateX(0.0);
                attackerView.setTranslateY(0.0);
                attackerView.setRotate(0.0);
                defenderView.setTranslateX(0.0);
                defenderView.setEffect(originalEffect);

                if (onFinished != null) {
                    onFinished.run();
                }
            }
        });

        fullSequence.play();
    }
}