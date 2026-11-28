package Battle.BattleGUI;

import Battle.Skills.Skill;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class BattleAnimator {

    public static void playOrbSendOutAnimation(
            final Node throwerView,
            final Node targetView,
            final Runnable onFinished) {
        CaptureOrbAnimator.playOrbSendOutAnimation(throwerView, targetView, onFinished);
    }

    public static void playCaptureAnimation(
            final Node throwerView,
            final Node targetView,
            final boolean captureSuccess,
            final Runnable onFinished) {
        CaptureOrbAnimator.playCaptureAnimation(throwerView, targetView, captureSuccess, onFinished);
    }

    public static void playSendOutAnimation(
            final Node targetView,
            final Runnable onFinished) {

        targetView.setScaleX(0.1);
        targetView.setScaleY(0.1);
        targetView.setOpacity(0.0);

        ScaleTransition expand = new ScaleTransition(Duration.millis(250), targetView);
        expand.setFromX(0.1);
        expand.setFromY(0.1);
        expand.setToX(1.0);
        expand.setToY(1.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), targetView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        ParallelTransition sendOut = new ParallelTransition(expand, fadeIn);
        sendOut.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                targetView.setScaleX(1.0);
                targetView.setScaleY(1.0);
                targetView.setOpacity(1.0);
                if (onFinished != null) {
                    onFinished.run();
                }
            }
        });

        sendOut.play();
    }

    public static void playSwitchAnimation(
            final Node targetView,
            final Runnable onMidpoint,
            final Runnable onFinished) {

        targetView.setScaleX(1.0);
        targetView.setScaleY(1.0);
        targetView.setOpacity(1.0);

        ScaleTransition shrink = new ScaleTransition(Duration.millis(200), targetView);
        shrink.setFromX(1.0);
        shrink.setFromY(1.0);
        shrink.setToX(0.1);
        shrink.setToY(0.1);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), targetView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        ParallelTransition recallPhase = new ParallelTransition(shrink, fadeOut);
        recallPhase.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (onMidpoint != null) {
                    onMidpoint.run();
                }
            }
        });

        ScaleTransition expand = new ScaleTransition(Duration.millis(220), targetView);
        expand.setFromX(0.1);
        expand.setFromY(0.1);
        expand.setToX(1.0);
        expand.setToY(1.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(220), targetView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        ParallelTransition sendOutPhase = new ParallelTransition(expand, fadeIn);

        SequentialTransition fullSwitch = new SequentialTransition(recallPhase, sendOutPhase);
        fullSwitch.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                targetView.setScaleX(1.0);
                targetView.setScaleY(1.0);
                targetView.setOpacity(1.0);
                if (onFinished != null) {
                    onFinished.run();
                }
            }
        });

        fullSwitch.play();
    }

    public static void playSkillAnimation(
            final Node attackerView,
            final Node defenderView,
            final Skill skill,
            boolean isP1Attacking,
            final Runnable onHit,
            final Runnable onFinished) {

        MonsterSpriteHelper.playAttackPose(attackerView, 650.0);

        if (skill != null && skill.isRanged()) {
            RangedAttackAnimator.play(attackerView, defenderView, skill, isP1Attacking, onHit, onFinished);
        } else {
            MeleeAttackAnimator.play(attackerView, defenderView, isP1Attacking, onHit, onFinished);
        }
    }

    public static void playBasicAttack(
            final Node attackerView,
            final Node defenderView,
            boolean isP1Attacking,
            final Runnable onHit,
            final Runnable onFinished) {

        MonsterSpriteHelper.playAttackPose(attackerView, 650.0);
        MeleeAttackAnimator.play(attackerView, defenderView, isP1Attacking, onHit, onFinished);
    }



    static void playTargetSpriteEffect(
            Node defenderView,
            String[] candidatePaths,
            boolean isP1Attacking,
            double durationMillis) {
        SpriteEffectHelper.playTargetSpriteEffect(defenderView, candidatePaths, isP1Attacking, durationMillis);
    }

    static void playStyledTargetSpriteEffect(
            Node defenderView,
            String[] candidatePaths,
            boolean isP1Attacking,
            double durationMillis,
            double sizeMultiplier,
            Color glowColor,
            double hueShift) {
        SpriteEffectHelper.playStyledTargetSpriteEffect(
            defenderView, candidatePaths, isP1Attacking, durationMillis, sizeMultiplier, glowColor, hueShift
        );
    }

    static String[] buildAttackCandidatePaths(String fileName) {
        return BattleAssetLoader.buildAttackCandidatePaths(fileName);
    }

    static Image loadAttackSpriteByName(String fileName) {
        return BattleAssetLoader.loadAttackSpriteByName(fileName);
    }

    static int detectHorizontalFrameCount(double imgW, double imgH) {
        return SpriteEffectHelper.detectHorizontalFrameCount(imgW, imgH);
    }

    static Effect buildSpriteEffect(Color glowColor, double hueShift) {
        return SpriteEffectHelper.buildSpriteEffect(glowColor, hueShift);
    }

    static Timeline createHitShakeTimeline(
            Node defenderView,
            Effect hitEffect,
            Effect originalEffect,
            Runnable onImpactStart) {
        return SpriteEffectHelper.createHitShakeTimeline(defenderView, hitEffect, originalEffect, onImpactStart);
    }

    static Pane findEffectPane(Node node) {
        return SpriteEffectHelper.findEffectPane(node);
    }
}