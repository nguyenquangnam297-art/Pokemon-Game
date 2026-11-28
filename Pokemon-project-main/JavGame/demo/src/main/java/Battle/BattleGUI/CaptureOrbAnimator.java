package Battle.BattleGUI;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class CaptureOrbAnimator {


    public static Group createCaptureOrb() {
        Circle outerAura = new Circle(26.0, Color.web("#ffd54f", 0.35));
        Circle orbBody = new Circle(18.0, Color.web("#e53935"));
        orbBody.setStroke(Color.WHITE);
        orbBody.setStrokeWidth(3.0);
        Circle orbCore = new Circle(6.0, Color.WHITE);

        DropShadow orbGlow = new DropShadow();
        orbGlow.setColor(Color.web("#ffca28"));
        orbGlow.setRadius(20.0);
        orbGlow.setSpread(0.35);

        Group orb = new Group(outerAura, orbBody, orbCore);
        orb.setEffect(orbGlow);
        orb.setMouseTransparent(true);
        return orb;
    }


    public static void playOrbSendOutAnimation(
            final Node throwerView,
            final Node targetView,
            final Runnable onFinished) {

        final Pane effectLayer = SpriteEffectHelper.findEffectPane(targetView);
        if (effectLayer == null || throwerView == null || throwerView.getScene() == null || targetView.getScene() == null) {
            targetView.setScaleX(1.0);
            targetView.setScaleY(1.0);
            targetView.setOpacity(1.0);
            if (onFinished != null) {
                onFinished.run();
            }
            return;
        }

        effectLayer.applyCss();
        effectLayer.layout();

        Bounds startLocal = throwerView.getBoundsInLocal();
        Point2D startScene = throwerView.localToScene(startLocal.getCenterX(), startLocal.getCenterY());
        Point2D startPos = effectLayer.sceneToLocal(startScene);

        Bounds targetLocal = targetView.getBoundsInLocal();
        Point2D targetScene = targetView.localToScene(targetLocal.getCenterX(), targetLocal.getCenterY());
        Point2D targetPos = effectLayer.sceneToLocal(targetScene);

        targetView.setScaleX(0.05);
        targetView.setScaleY(0.05);
        targetView.setOpacity(0.0);

        final Group sendOrb = createCaptureOrb();
        sendOrb.setLayoutX(startPos.getX());
        sendOrb.setLayoutY(startPos.getY());
        effectLayer.getChildren().add(sendOrb);

        double dx = targetPos.getX() - startPos.getX();
        double dy = targetPos.getY() - startPos.getY();

        // Pha 1: Phóng cầu tới tâm vị trí đứng của quái (550ms)
        TranslateTransition throwOrb = new TranslateTransition(Duration.millis(550), sendOrb);
        throwOrb.setFromX(0.0);
        throwOrb.setFromY(0.0);
        throwOrb.setToX(dx);
        throwOrb.setToY(dy);

        // Pha 2: Cầu lóe sáng tan biến và bung quái ra kích thước chuẩn (420ms)
        FadeTransition orbFade = new FadeTransition(Duration.millis(420), sendOrb);
        orbFade.setFromValue(1.0);
        orbFade.setToValue(0.0);

        ScaleTransition orbExpand = new ScaleTransition(Duration.millis(420), sendOrb);
        orbExpand.setFromX(1.0);
        orbExpand.setFromY(1.0);
        orbExpand.setToX(1.4);
        orbExpand.setToY(1.4);

        ScaleTransition monsterScale = new ScaleTransition(Duration.millis(420), targetView);
        monsterScale.setFromX(0.05);
        monsterScale.setFromY(0.05);
        monsterScale.setToX(1.0);
        monsterScale.setToY(1.0);

        FadeTransition monsterFade = new FadeTransition(Duration.millis(420), targetView);
        monsterFade.setFromValue(0.0);
        monsterFade.setToValue(1.0);

        ParallelTransition releasePhase = new ParallelTransition(orbFade, orbExpand, monsterScale, monsterFade);
        SequentialTransition fullAnim = new SequentialTransition(throwOrb, releasePhase);

        fullAnim.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                effectLayer.getChildren().remove(sendOrb);
                targetView.setScaleX(1.0);
                targetView.setScaleY(1.0);
                targetView.setOpacity(1.0);
                if (onFinished != null) {
                    onFinished.run();
                }
            }
        });

        fullAnim.play();
    }


    public static void playCaptureAnimation(
            final Node throwerView,
            final Node targetView,
            final boolean captureSuccess,
            final Runnable onFinished) {

        final Pane effectLayer = SpriteEffectHelper.findEffectPane(targetView);
        if (effectLayer == null || throwerView.getScene() == null || targetView.getScene() == null) {
            if (captureSuccess) {
                targetView.setOpacity(0.0);
            }
            if (onFinished != null) {
                onFinished.run();
            }
            return;
        }

        Bounds startLocal = throwerView.getBoundsInLocal();
        Point2D startScene = throwerView.localToScene(startLocal.getCenterX(), startLocal.getCenterY());
        Point2D startPos = effectLayer.sceneToLocal(startScene);

        Bounds targetLocal = targetView.getBoundsInLocal();
        Point2D targetScene = targetView.localToScene(targetLocal.getCenterX(), targetLocal.getCenterY());
        final Point2D targetPos = effectLayer.sceneToLocal(targetScene);

        final Group captureOrb = createCaptureOrb();
        captureOrb.setLayoutX(startPos.getX());
        captureOrb.setLayoutY(startPos.getY());

        effectLayer.getChildren().add(captureOrb);

        final double dx = targetPos.getX() - startPos.getX();
        double dy = targetPos.getY() - startPos.getY();
        TranslateTransition throwOrb = new TranslateTransition(Duration.millis(360), captureOrb);
        throwOrb.setFromX(0.0);
        throwOrb.setFromY(0.0);
        throwOrb.setToX(dx);
        throwOrb.setToY(dy);

        ScaleTransition absorbScale = new ScaleTransition(Duration.millis(260), targetView);
        absorbScale.setFromX(targetView.getScaleX());
        absorbScale.setFromY(1.0);
        double dirSign = 1.0;
        if (targetView.getScaleX() < 0) {
            dirSign = -1.0;
        }
        absorbScale.setToX(dirSign * 0.05);
        absorbScale.setToY(0.05);

        FadeTransition absorbFade = new FadeTransition(Duration.millis(260), targetView);
        absorbFade.setFromValue(1.0);
        absorbFade.setToValue(0.0);

        ParallelTransition absorbPhase = new ParallelTransition(absorbScale, absorbFade);

        Timeline wobbleTimeline = new Timeline(
            new KeyFrame(Duration.millis(0), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    captureOrb.setTranslateX(dx);
                }
            }),
            new KeyFrame(Duration.millis(110), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    captureOrb.setTranslateX(dx - 12.0);
                }
            }),
            new KeyFrame(Duration.millis(220), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    captureOrb.setTranslateX(dx + 12.0);
                }
            }),
            new KeyFrame(Duration.millis(330), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    captureOrb.setTranslateX(dx - 10.0);
                }
            }),
            new KeyFrame(Duration.millis(440), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    captureOrb.setTranslateX(dx + 10.0);
                }
            }),
            new KeyFrame(Duration.millis(550), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    captureOrb.setTranslateX(dx);
                }
            }),
            new KeyFrame(Duration.millis(660), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    captureOrb.setTranslateX(dx);
                }
            })
        );

        SequentialTransition captureSequence = new SequentialTransition(throwOrb, absorbPhase, wobbleTimeline);
        final double finalDirSign = dirSign;

        captureSequence.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (captureSuccess) {
                    FadeTransition orbFade = new FadeTransition(Duration.millis(250), captureOrb);
                    orbFade.setFromValue(1.0);
                    orbFade.setToValue(0.0);
                    orbFade.setOnFinished(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            effectLayer.getChildren().remove(captureOrb);
                            if (onFinished != null) {
                                onFinished.run();
                            }
                        }
                    });
                    orbFade.play();
                } else {
                    effectLayer.getChildren().remove(captureOrb);

                    ScaleTransition breakOutScale = new ScaleTransition(Duration.millis(220), targetView);
                    breakOutScale.setFromX(finalDirSign * 0.05);
                    breakOutScale.setFromY(0.05);
                    breakOutScale.setToX(finalDirSign * 1.0);
                    breakOutScale.setToY(1.0);

                    FadeTransition breakOutFade = new FadeTransition(Duration.millis(220), targetView);
                    breakOutFade.setFromValue(0.0);
                    breakOutFade.setToValue(1.0);

                    ParallelTransition breakOutPhase = new ParallelTransition(breakOutScale, breakOutFade);
                    breakOutPhase.setOnFinished(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            targetView.setScaleX(finalDirSign * 1.0);
                            targetView.setScaleY(1.0);
                            targetView.setOpacity(1.0);
                            if (onFinished != null) {
                                onFinished.run();
                            }
                        }
                    });
                    breakOutPhase.play();
                }
            }
        });

        captureSequence.play();
    }
}