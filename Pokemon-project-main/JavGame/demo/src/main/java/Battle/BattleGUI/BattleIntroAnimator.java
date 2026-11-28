package Battle.BattleGUI;

import Entity.Monster;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class BattleIntroAnimator {

    private BattleIntroOverlay overlay;
    private boolean activeStepPlaying = false;
    private Runnable activeSkipAction = null;

    public BattleIntroAnimator(AnchorPane gameRoot, List<Monster> playerTeam) {
        this.overlay = new BattleIntroOverlay(gameRoot, playerTeam);
    }

    public void playWildEncounterEntrance(
            final MonsterStatusView p2View,
            final String enemyName,
            final Runnable onFinished) {

        final String fullMessage = "QUÁI HOANG DÃ " + enemyName.toUpperCase() + "\nxuất hiện!";

        overlay.prepareEntranceLayout();

        p2View.setVisible(true);
        p2View.setOpacity(1.0);
        p2View.setTranslateX(-620.0);

        TranslateTransition enemyPlatSlide = new TranslateTransition(Duration.millis(950), overlay.getEnemyPlatform());
        enemyPlatSlide.setFromX(-620.0);
        enemyPlatSlide.setToX(0.0);

        TranslateTransition enemyMonsterSlide = new TranslateTransition(Duration.millis(950), p2View);
        enemyMonsterSlide.setFromX(-620.0);
        enemyMonsterSlide.setToX(0.0);

        TranslateTransition playerPlatSlide = new TranslateTransition(Duration.millis(950), overlay.getPlayerPlatform());
        playerPlatSlide.setFromX(620.0);
        playerPlatSlide.setToX(0.0);

        TranslateTransition playerSpriteSlide = new TranslateTransition(Duration.millis(950), overlay.getPlayerBackView());
        playerSpriteSlide.setFromX(620.0);
        playerSpriteSlide.setToX(0.0);

        TranslateTransition enemyBarSlide = new TranslateTransition(Duration.millis(950), overlay.getEnemyBallBar());
        enemyBarSlide.setFromX(-560.0);
        enemyBarSlide.setToX(0.0);

        TranslateTransition playerBarSlide = new TranslateTransition(Duration.millis(950), overlay.getPlayerBallBar());
        playerBarSlide.setFromX(560.0);
        playerBarSlide.setToX(0.0);

        final ParallelTransition slideInAnim = new ParallelTransition(
            enemyPlatSlide,
            enemyMonsterSlide,
            playerPlatSlide,
            playerSpriteSlide,
            enemyBarSlide,
            playerBarSlide
        );

        final Timeline typewriterTimeline = new Timeline();
        int charCount = fullMessage.length();
        double stepMillis = 42.0;

        for (int i = 1; i <= charCount; i++) {
            final int endIndex = i;
            KeyFrame kf = new KeyFrame(Duration.millis(i * stepMillis), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    overlay.setDialogueText(fullMessage.substring(0, endIndex));
                }
            });
            typewriterTimeline.getKeyFrames().add(kf);
        }

        KeyFrame showCursorFrame = new KeyFrame(
            Duration.millis(charCount * stepMillis),
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    overlay.setCursorVisible(true);
                }
            }
        );
        typewriterTimeline.getKeyFrames().add(showCursorFrame);

        KeyFrame holdFrame = new KeyFrame(
            Duration.millis(charCount * stepMillis + 850.0),
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    completeEntranceStep(p2View, fullMessage, onFinished);
                }
            }
        );
        typewriterTimeline.getKeyFrames().add(holdFrame);

        activeStepPlaying = true;
        activeSkipAction = new Runnable() {
            @Override
            public void run() {
                slideInAnim.stop();
                typewriterTimeline.stop();
                completeEntranceStep(p2View, fullMessage, onFinished);
            }
        };

        slideInAnim.play();
        typewriterTimeline.play();
    }

    private void completeEntranceStep(
            MonsterStatusView p2View,
            String fullMessage,
            Runnable onFinished) {

        if (!activeStepPlaying) {
            return;
        }
        activeStepPlaying = false;
        activeSkipAction = null;

        overlay.snapToEntranceTarget();
        p2View.setTranslateX(0.0);
        overlay.setDialogueText(fullMessage);
        overlay.setCursorVisible(true);

        if (onFinished != null) {
            onFinished.run();
        }
    }


    public void playPlayerRetreatAndSendOut(
            final MonsterStatusView p1View,
            final String chosenMonsterName,
            final Runnable onFinished) {

        overlay.setCursorVisible(false);
        overlay.setDialogueText("Lên nào! " + chosenMonsterName.toUpperCase() + "!");

        TranslateTransition retreatSlide = new TranslateTransition(Duration.millis(500), overlay.getPlayerBackView());
        retreatSlide.setFromX(0.0);
        retreatSlide.setToX(-420.0);

        FadeTransition retreatFade = new FadeTransition(Duration.millis(500), overlay.getPlayerBackView());
        retreatFade.setFromValue(1.0);
        retreatFade.setToValue(0.0);

        TranslateTransition enemyBarOut = new TranslateTransition(Duration.millis(420), overlay.getEnemyBallBar());
        enemyBarOut.setFromX(0.0);
        enemyBarOut.setToX(-560.0);

        TranslateTransition playerBarOut = new TranslateTransition(Duration.millis(420), overlay.getPlayerBallBar());
        playerBarOut.setFromX(0.0);
        playerBarOut.setToX(560.0);

        final ParallelTransition retreatAnim = new ParallelTransition(
            retreatSlide,
            retreatFade,
            enemyBarOut,
            playerBarOut
        );

        activeStepPlaying = true;
        activeSkipAction = new Runnable() {
            @Override
            public void run() {
                retreatAnim.stop();
                cleanupIntroOverlays();

                Node monsterSprite = p1View.getImageView();
                monsterSprite.setScaleX(1.0);
                monsterSprite.setScaleY(1.0);
                monsterSprite.setOpacity(1.0);

                activeStepPlaying = false;
                activeSkipAction = null;
                if (onFinished != null) {
                    onFinished.run();
                }
            }
        };

        retreatAnim.play();

        CaptureOrbAnimator.playOrbSendOutAnimation(
            overlay.getPlayerBackView(),
            p1View.getImageView(),
            new Runnable() {
                @Override
                public void run() {
                    if (!activeStepPlaying) {
                        return;
                    }
                    activeStepPlaying = false;
                    activeSkipAction = null;
                    cleanupIntroOverlays();
                    if (onFinished != null) {
                        onFinished.run();
                    }
                }
            }
        );
    }

    public void cleanupIntroOverlays() {
        overlay.hideAll();
    }

    public boolean skipCurrentStep() {
        if (activeStepPlaying && activeSkipAction != null) {
            activeSkipAction.run();
            return true;
        }
        return false;
    }
}