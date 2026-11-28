package Battle.BattleGUI;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class SpriteEffectHelper {

    public static void playTargetSpriteEffect(
            final Node defenderView,
            final String[] candidatePaths,
            final boolean isP1Attacking,
            final double durationMillis) {

        playStyledTargetSpriteEffect(
            defenderView,
            candidatePaths,
            isP1Attacking,
            durationMillis,
            1.0,
            Color.WHITE,
            0.0
        );
    }

    public static void playStyledTargetSpriteEffect(
            final Node defenderView,
            final String[] candidatePaths,
            final boolean isP1Attacking,
            final double durationMillis,
            final double sizeMultiplier,
            final Color glowColor,
            final double hueShift) {

        final Pane effectLayer = findEffectPane(defenderView);
        if (effectLayer == null || defenderView.getScene() == null) {
            return;
        }

        Image spriteImg = BattleAssetLoader.loadImage(candidatePaths);
        if (spriteImg == null || spriteImg.isError() || spriteImg.getWidth() <= 0 || spriteImg.getHeight() <= 0) {
            return;
        }

        Bounds defLocal = defenderView.getBoundsInLocal();
        Point2D defScene = defenderView.localToScene(defLocal.getCenterX(), defLocal.getCenterY());
        Point2D defPos = effectLayer.sceneToLocal(defScene);

        double baseSize = Math.min(defLocal.getWidth(), defLocal.getHeight()) * 0.85 * sizeMultiplier;
        if (baseSize < 150.0) {
            baseSize = 150.0;
        }

        final ImageView effectView = new ImageView(spriteImg);
        effectView.setMouseTransparent(true);
        effectView.setPreserveRatio(true);
        effectView.setEffect(buildSpriteEffect(glowColor, hueShift));

        if (!isP1Attacking) {
            effectView.setScaleX(-1.0);
        }

        double imgW = spriteImg.getWidth();
        double imgH = spriteImg.getHeight();

        if (imgW > imgH) {
            int frameCount = detectHorizontalFrameCount(imgW, imgH);
            final double frameWidth = imgW / frameCount;
            final double frameHeight = imgH;

            effectView.setViewport(new Rectangle2D(0.0, 0.0, frameWidth, frameHeight));
            effectView.setFitWidth(baseSize);
            effectView.setFitHeight(baseSize);
            effectView.setLayoutX(defPos.getX() - baseSize / 2.0);
            effectView.setLayoutY(defPos.getY() - baseSize / 2.0);

            effectLayer.getChildren().add(effectView);

            Timeline spriteTimeline = new Timeline();
            double stepMillis = durationMillis / frameCount;

            for (int i = 0; i < frameCount; i++) {
                final int frameIndex = i;
                KeyFrame kf = new KeyFrame(Duration.millis(i * stepMillis), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        effectView.setViewport(new Rectangle2D(
                            frameIndex * frameWidth, 0.0, frameWidth, frameHeight
                        ));
                    }
                });
                spriteTimeline.getKeyFrames().add(kf);
            }

            KeyFrame endFrame = new KeyFrame(Duration.millis(durationMillis), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    effectLayer.getChildren().remove(effectView);
                }
            });
            spriteTimeline.getKeyFrames().add(endFrame);
            spriteTimeline.play();

        } else {
            double drawW = baseSize;
            double drawH = baseSize;
            if (imgH > imgW) {
                drawW = baseSize * (imgW / imgH);
            }

            effectView.setFitWidth(drawW);
            effectView.setFitHeight(drawH);
            effectView.setLayoutX(defPos.getX() - drawW / 2.0);
            effectView.setLayoutY(defPos.getY() - drawH / 2.0);

            effectLayer.getChildren().add(effectView);

            ScaleTransition slashScale = new ScaleTransition(Duration.millis(durationMillis), effectView);
            double dirSign = 1.0;
            if (!isP1Attacking) {
                dirSign = -1.0;
            }
            slashScale.setFromX(dirSign * 0.75);
            slashScale.setFromY(0.75);
            slashScale.setToX(dirSign * 1.2);
            slashScale.setToY(1.2);

            FadeTransition slashFade = new FadeTransition(Duration.millis(durationMillis), effectView);
            slashFade.setFromValue(1.0);
            slashFade.setToValue(0.0);

            ParallelTransition singleAnim = new ParallelTransition(slashScale, slashFade);
            singleAnim.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    effectLayer.getChildren().remove(effectView);
                }
            });
            singleAnim.play();
        }
    }

    public static int detectHorizontalFrameCount(double imgW, double imgH) {
        if (imgH <= 0 || imgW <= imgH) {
            return 1;
        }
        int count = (int) Math.round(imgW / imgH);
        if (count < 1) {
            return 1;
        }
        return count;
    }

    public static Effect buildSpriteEffect(Color glowColor, double hueShift) {
        DropShadow glow = new DropShadow();
        if (glowColor != null) {
            glow.setColor(glowColor);
        } else {
            glow.setColor(Color.WHITE);
        }
        glow.setRadius(18.0);
        glow.setSpread(0.3);

        if (Math.abs(hueShift) > 0.001) {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setHue(hueShift);
            colorAdjust.setSaturation(0.25);
            colorAdjust.setBrightness(0.15);
            colorAdjust.setInput(glow);
            return colorAdjust;
        }
        return glow;
    }

    public static Timeline createHitShakeTimeline(
            final Node defenderView,
            final Effect hitEffect,
            final Effect originalEffect,
            final Runnable onImpactStart) {

        return new Timeline(
            new KeyFrame(Duration.millis(0), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (onImpactStart != null) {
                        onImpactStart.run();
                    }
                    defenderView.setTranslateX(-14.0);
                    defenderView.setEffect(hitEffect);
                }
            }),
            new KeyFrame(Duration.millis(55), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    defenderView.setTranslateX(14.0);
                    defenderView.setEffect(originalEffect);
                }
            }),
            new KeyFrame(Duration.millis(110), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    defenderView.setTranslateX(-10.0);
                    defenderView.setEffect(hitEffect);
                }
            }),
            new KeyFrame(Duration.millis(165), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    defenderView.setTranslateX(10.0);
                    defenderView.setEffect(originalEffect);
                }
            }),
            new KeyFrame(Duration.millis(220), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    defenderView.setTranslateX(0.0);
                    defenderView.setEffect(originalEffect);
                }
            })
        );
    }

    public static Pane findEffectPane(Node node) {
        Parent current = node.getParent();
        Pane fallback = null;
        while (current != null) {
            if (current instanceof AnchorPane) {
                return (AnchorPane) current;
            }
            if (current instanceof Pane && fallback == null) {
                fallback = (Pane) current;
            }
            current = current.getParent();
        }
        return fallback;
    }
}