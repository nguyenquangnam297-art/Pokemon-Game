package Battle.BattleGUI;

import Battle.Skills.Skill;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class RangedAttackAnimator {

    private static final double PROJECTILE_SIZE = 155.0;

    public static void play(
            final Node attackerView,
            final Node defenderView,
            final Skill skill,
            final boolean isP1Attacking,
            final Runnable onHit,
            final Runnable onFinished) {

        final Pane effectLayer = BattleAnimator.findEffectPane(attackerView);
        if (effectLayer == null || attackerView.getScene() == null || defenderView.getScene() == null) {
            MeleeAttackAnimator.play(attackerView, defenderView, isP1Attacking, onHit, onFinished);
            return;
        }

        final String spriteFileName = ElementStyleConfig.getAttackSpriteFileName(skill);
        final Image projectileImg = BattleAnimator.loadAttackSpriteByName(spriteFileName);
        if (projectileImg == null || projectileImg.isError() || projectileImg.getWidth() <= 0) {
            MeleeAttackAnimator.play(attackerView, defenderView, isP1Attacking, onHit, onFinished);
            return;
        }

        attackerView.setTranslateX(0.0);
        attackerView.setTranslateY(0.0);
        attackerView.setRotate(0.0);

        Color[] colors = ElementStyleConfig.getElementColors(skill);
        final Color auraColor = colors[1];
        final double hueShift = ElementStyleConfig.getElementHueShift(skill);

        int basePower = 50;
        if (skill != null) {
            basePower = skill.getBasePower();
        }
        final boolean isHeavySkill = basePower >= 80;


        Bounds attLocal = attackerView.getBoundsInLocal();
        Point2D attScene = attackerView.localToScene(attLocal.getCenterX(), attLocal.getCenterY());
        Point2D attPos = effectLayer.sceneToLocal(attScene);

        Bounds defLocal = defenderView.getBoundsInLocal();
        Point2D defScene = defenderView.localToScene(defLocal.getCenterX(), defLocal.getCenterY());
        final Point2D defPos = effectLayer.sceneToLocal(defScene);

        double offsetX = 65.0;
        if (!isP1Attacking) {
            offsetX = -65.0;
        }
        final double startX = attPos.getX() + offsetX;
        final double startY = attPos.getY() - 10.0;
        final double dx = defPos.getX() - startX;
        final double dy = defPos.getY() - startY;

        final double dirSign;
        final double aimAngle;
        if (isP1Attacking) {
            dirSign = 1.0;
            aimAngle = Math.toDegrees(Math.atan2(dy, dx));
        } else {
            dirSign = -1.0;
            aimAngle = Math.toDegrees(Math.atan2(-dy, -dx));
        }

        double drawSize = PROJECTILE_SIZE;
        if (isHeavySkill) {
            drawSize = PROJECTILE_SIZE * 1.2;
        }

        final int frameCount = BattleAnimator.detectHorizontalFrameCount(
            projectileImg.getWidth(), projectileImg.getHeight()
        );
        final double frameWidth = projectileImg.getWidth() / frameCount;
        final double frameHeight = projectileImg.getHeight();

        final ImageView projectileView = new ImageView(projectileImg);
        projectileView.setMouseTransparent(true);
        projectileView.setPreserveRatio(true);
        projectileView.setViewport(new Rectangle2D(0.0, 0.0, frameWidth, frameHeight));
        projectileView.setFitWidth(drawSize);
        projectileView.setFitHeight(drawSize);
        projectileView.setLayoutX(startX - drawSize / 2.0);
        projectileView.setLayoutY(startY - drawSize / 2.0);
        projectileView.setRotate(aimAngle);
        projectileView.setScaleX(dirSign * 0.2);
        projectileView.setScaleY(0.2);
        projectileView.setOpacity(0.0);
        projectileView.setEffect(BattleAnimator.buildSpriteEffect(auraColor, hueShift));

        effectLayer.getChildren().add(projectileView);

        final Timeline spriteFrameLoop = new Timeline();
        if (frameCount > 1) {
            final int[] currentFrame = new int[] { 0 };
            KeyFrame stepFrame = new KeyFrame(Duration.millis(65), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    currentFrame[0] = (currentFrame[0] + 1) % frameCount;
                    projectileView.setViewport(new Rectangle2D(
                        currentFrame[0] * frameWidth, 0.0, frameWidth, frameHeight
                    ));
                }
            });
            spriteFrameLoop.getKeyFrames().add(stepFrame);
            spriteFrameLoop.setCycleCount(Timeline.INDEFINITE);
            spriteFrameLoop.play();
        }

        final double windUpAngle;
        final double castAngle;
        if (isP1Attacking) {
            windUpAngle = -14.0;
            castAngle = 16.0;
        } else {
            windUpAngle = 14.0;
            castAngle = -16.0;
        }

        RotateTransition windUp = new RotateTransition(Duration.millis(200), attackerView);
        windUp.setFromAngle(0.0);
        windUp.setToAngle(windUpAngle);

        ScaleTransition chargeScale = new ScaleTransition(Duration.millis(200), projectileView);
        chargeScale.setFromX(dirSign * 0.2);
        chargeScale.setFromY(0.2);
        chargeScale.setToX(dirSign * 1.0);
        chargeScale.setToY(1.0);

        FadeTransition chargeFade = new FadeTransition(Duration.millis(140), projectileView);
        chargeFade.setFromValue(0.0);
        chargeFade.setToValue(1.0);

        ParallelTransition chargePhase = new ParallelTransition(windUp, chargeScale, chargeFade);

        RotateTransition castPose = new RotateTransition(Duration.millis(100), attackerView);
        castPose.setFromAngle(windUpAngle);
        castPose.setToAngle(castAngle);

        TranslateTransition flyTransition = new TranslateTransition(Duration.millis(360), projectileView);
        flyTransition.setFromX(0.0);
        flyTransition.setFromY(0.0);
        flyTransition.setToX(dx);
        flyTransition.setToY(dy);

        ScaleTransition flyPulse = new ScaleTransition(Duration.millis(360), projectileView);
        flyPulse.setFromX(dirSign * 1.0);
        flyPulse.setFromY(1.0);
        flyPulse.setToX(dirSign * 1.2);
        flyPulse.setToY(1.2);

        ParallelTransition launchPhase = new ParallelTransition(castPose, flyTransition, flyPulse);

        final Effect originalEffect = defenderView.getEffect();
        final ColorAdjust hitFlash = new ColorAdjust();
        hitFlash.setBrightness(0.75);
        DropShadow elementHitGlow = new DropShadow();
        elementHitGlow.setColor(auraColor);
        elementHitGlow.setRadius(35.0);
        elementHitGlow.setSpread(0.5);
        hitFlash.setInput(elementHitGlow);

        RotateTransition resetAttacker = new RotateTransition(Duration.millis(180), attackerView);
        resetAttacker.setFromAngle(castAngle);
        resetAttacker.setToAngle(0.0);

        Timeline hitShake = BattleAnimator.createHitShakeTimeline(
            defenderView,
            hitFlash,
            originalEffect,
            new Runnable() {
                @Override
                public void run() {
                    spriteFrameLoop.stop();
                    effectLayer.getChildren().remove(projectileView);

                    if (isHeavySkill) {
                        BattleAnimator.playStyledTargetSpriteEffect(
                            defenderView,
                            BattleAnimator.buildAttackCandidatePaths("explosion.png"),
                            isP1Attacking,
                            260.0,
                            1.15,
                            auraColor,
                            hueShift
                        );
                    } else {
                        BattleAnimator.playStyledTargetSpriteEffect(
                            defenderView,
                            BattleAnimator.buildAttackCandidatePaths(spriteFileName),
                            isP1Attacking,
                            230.0,
                            0.95,
                            auraColor,
                            hueShift
                        );
                    }

                    if (onHit != null) {
                        onHit.run();
                    }
                }
            }
        );

        ParallelTransition impactPhase = new ParallelTransition(hitShake, resetAttacker);

        SequentialTransition fullRangedSequence = new SequentialTransition(chargePhase, launchPhase, impactPhase);
        fullRangedSequence.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                spriteFrameLoop.stop();
                effectLayer.getChildren().remove(projectileView);
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

        fullRangedSequence.play();
    }
}