package Battle;

import Battle.BattleGUI.BattleAnimator;
import Battle.BattleGUI.BattleIntroAnimator;
import Battle.BattleGUI.MonsterStatusView;
import Battle.BattleGUI.NotificationPopup;
import Battle.BattleGUI.PartySwitchOverlay;
import Battle.BattleGUI.SkillControlPanel;
import Battle.BattleGUI.TurnOrderView;
import Battle.BattleManager.BattleState;
import Battle.Skills.Skill;
import Entity.Monster;
import Main.MusicHandler;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class BattleController {

    private static final double MESSAGE_DELAY_SECONDS = 0.5;

    private BattleManager battleManager;
    private MonsterStatusView p1View, p2View;
    private TurnOrderView turnOrderBox;
    private NotificationPopup popupBox;
    private SkillControlPanel skillPanelBox;
    private PartySwitchOverlay partyOverlay;
    private BattleIntroAnimator introAnimator;
    private Runnable onBattleFinished;
    private final MusicHandler musicHandler = MusicHandler.getInstance();

    private Timeline battleLoop;
    private boolean battleFinished = false;
    private boolean isForcedSwitch = false;
    private boolean isInitialSelection = false;

    public BattleController(
            BattleManager battleManager,
            MonsterStatusView p1View,
            MonsterStatusView p2View,
            TurnOrderView turnOrderBox,
            NotificationPopup popupBox,
            SkillControlPanel skillPanelBox,
            PartySwitchOverlay partyOverlay,
            BattleIntroAnimator introAnimator,
            Runnable onBattleFinished) {
        this.battleManager = battleManager;
        this.p1View = p1View;
        this.p2View = p2View;
        this.turnOrderBox = turnOrderBox;
        this.popupBox = popupBox;
        this.skillPanelBox = skillPanelBox;
        this.partyOverlay = partyOverlay;
        this.introAnimator = introAnimator;
        this.onBattleFinished = onBattleFinished;
    }

    public boolean trySkipIntro() {
        if (introAnimator != null) {
            return introAnimator.skipCurrentStep();
        }
        return false;
    }

    public void startBattle() {
        battleFinished = false;
        musicHandler.playMusic("/BackgroundMusic/Battle_music.mp3");

        battleLoop = new Timeline(new KeyFrame(Duration.millis(50), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                processNextTick();
            }
        }));
        battleLoop.setCycleCount(Timeline.INDEFINITE);

        p1View.setVisible(false);
        p1View.setStatusHudVisible(false);
        p2View.setStatusHudVisible(false);
        turnOrderBox.setVisible(false);
        popupBox.setVisible(false);
        skillPanelBox.setVisible(false);
        skillPanelBox.resetAndDisable();

        if (introAnimator != null) {
            introAnimator.playWildEncounterEntrance(
                p2View,
                battleManager.getP2().getName(),
                new Runnable() {
                    @Override
                    public void run() {
                        openInitialMonsterSelection();
                    }
                }
            );
        } else {
            openInitialMonsterSelection();
        }
    }

    private void openInitialMonsterSelection() {
        if (battleManager.hasAliveMonsterInTeam()) {
            isInitialSelection = true;
            isForcedSwitch = true;
            partyOverlay.openInitialSelection(battleManager.getPlayerTeam());
        } else {
            beginBattleIntroAndLoop();
        }
    }

    private void beginBattleIntroAndLoop() {
        if (introAnimator != null) {
            introAnimator.cleanupIntroOverlays();
        }

        p1View.setVisible(true);
        p1View.setStatusHudVisible(true);
        p2View.setVisible(true);
        p2View.setStatusHudVisible(true);
        turnOrderBox.setVisible(true);
        popupBox.setVisible(true);
        skillPanelBox.setVisible(true);

        updateHpViews();
        updateTurnOrderUI();

        popupBox.showMessage("TRẬN ĐẤU BẮT ĐẦU!\n"
            + battleManager.getP1().getName() + " VS " + battleManager.getP2().getName());

        PauseTransition introDelay = new PauseTransition(Duration.seconds(MESSAGE_DELAY_SECONDS));
        introDelay.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!battleFinished) {
                    battleLoop.play();
                }
            }
        });
        introDelay.play();
    }

    private void processNextTick() {
        if (battleManager.tick()) {
            if (battleManager.isP1Turn()) {
                battleLoop.pause();
                skillPanelBox.setCanSwitch(battleManager.hasSwitchableMonster());
                skillPanelBox.enableAndFocus();
                popupBox.showMessage("Lượt " + battleManager.getActionCount() + "\nĐến lượt bạn!");
            } else {
                battleLoop.pause();
                popupBox.showMessage("Lượt " + battleManager.getActionCount() + "\nĐến lượt đối thủ...");
                executeEnemyTurn();
                return;
            }
        }

        updateHpViews();
        checkBattleOutcome();
    }

    private void executeEnemyTurn() {
        PauseTransition delay = new PauseTransition(Duration.seconds(MESSAGE_DELAY_SECONDS));
        delay.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final Skill randomSkill = battleManager.getRandomP2Skill();

                BattleAnimator.playSkillAnimation(
                    p2View.getImageView(),
                    p1View.getImageView(),
                    randomSkill,
                    false,
                    new Runnable() {
                        @Override
                        public void run() {
                            String msg = battleManager.executeSkill(
                                battleManager.getP2(),
                                battleManager.getP1(),
                                randomSkill
                            );
                            popupBox.showMessage(msg);
                            updateHpViews();
                        }
                    },
                    new Runnable() {
                        @Override
                        public void run() {
                            battleManager.finishEnemySkillTurn();
                            updateHpViews();
                            updateTurnOrderUI();
                            resumeBattleAfterDelay();
                        }
                    }
                );
            }
        });
        delay.play();
    }

    public void handlePlayerSkillSelect(final Skill skill) {
        skillPanelBox.resetAndDisable();

        BattleAnimator.playSkillAnimation(
            p1View.getImageView(),
            p2View.getImageView(),
            skill,
            true,
            new Runnable() {
                @Override
                public void run() {
                    String msg = battleManager.executeSkill(
                        battleManager.getP1(),
                        battleManager.getP2(),
                        skill
                    );
                    popupBox.showMessage(msg);
                    updateHpViews();
                }
            },
            new Runnable() {
                @Override
                public void run() {
                    battleManager.finishPlayerSkillTurn();
                    updateHpViews();
                    updateTurnOrderUI();
                    resumeBattleAfterDelay();
                }
            }
        );
    }

    public void handleBallSelect(String ballId) {
        if (!battleManager.canCaptureEnemy()) {
            return;
        }

        final int chance = battleManager.calculateCaptureChance(ballId);
        final Boolean captureResult = battleManager.attemptCapture(ballId);
        if (captureResult == null) {
            return;
        }

        final boolean success = captureResult.booleanValue();
        final String targetName = battleManager.getP2().getName();
        final int remainingBalls = battleManager.getTotalBallCount();

        skillPanelBox.resetAndDisable();
        popupBox.showMessage("Lượt " + battleManager.getActionCount()
            + "\nBạn tung cầu thu phục " + targetName + "! (" + chance + "%)");

        BattleAnimator.playCaptureAnimation(
            p1View.getImageView(),
            p2View.getImageView(),
            success,
            new Runnable() {
                @Override
                public void run() {
                    if (success) {
                        checkBattleOutcome();
                    } else {
                        battleManager.finishFailedCaptureTurn();
                        updateHpViews();
                        popupBox.showMessage(targetName + " đã thoát ra khỏi cầu! (Còn "
                            + remainingBalls + " bóng)\n(Mất 1 lượt)");
                        updateTurnOrderUI();
                        resumeBattleAfterDelay();
                    }
                }
            }
        );
    }

    public void handleItemSelect(String itemId) {
        String msg = battleManager.useItem(itemId);
        if (msg == null) {
            return;
        }

        popupBox.showMessage(msg);
        updateTurnOrderUI();
        skillPanelBox.resetAndDisable();
        updateHpViews();
        resumeBattleAfterDelay();
    }

    public void openSwitchOverlay(boolean forced) {
        this.isForcedSwitch = forced;
        skillPanelBox.setDisable(true);
        partyOverlay.open(battleManager.getPlayerTeam(), battleManager.getP1(), forced);
    }

    public void handleSwitchCancel() {
        skillPanelBox.setCanSwitch(battleManager.hasSwitchableMonster());
        skillPanelBox.enableAndFocus();
    }

    public void handlePokemonSwitch(final Monster selectedMonster) {
        skillPanelBox.resetAndDisable();

        if (isInitialSelection) {
            isInitialSelection = false;
            isForcedSwitch = false;

            battleManager.setP1(selectedMonster);
            p1View.setMonster(selectedMonster);
            p1View.getImageView().setOpacity(0.0);
            p1View.getImageView().setScaleX(0.05);
            p1View.getImageView().setScaleY(0.05);
            p1View.setVisible(true);

            skillPanelBox.setMonster(selectedMonster);
            skillPanelBox.setCanSwitch(battleManager.hasSwitchableMonster());

            if (introAnimator != null) {
                introAnimator.playPlayerRetreatAndSendOut(
                    p1View,
                    selectedMonster.getName(),
                    new Runnable() {
                        @Override
                        public void run() {
                            beginBattleIntroAndLoop();
                        }
                    }
                );
            } else {
                p1View.getImageView().setOpacity(1.0);
                p1View.getImageView().setScaleX(1.0);
                p1View.getImageView().setScaleY(1.0);
                beginBattleIntroAndLoop();
            }
            return;
        }

        final Monster oldMonster = battleManager.getP1();
        final boolean forced = this.isForcedSwitch;

        if (forced) {
            popupBox.showMessage("Bạn tung " + selectedMonster.getName() + " ra trận!");
        } else {
            popupBox.showMessage("Thu hồi " + oldMonster.getName() + "!\nLên nào, " + selectedMonster.getName() + "!");
        }

        battleManager.setP1(selectedMonster);
        skillPanelBox.setMonster(selectedMonster);
        skillPanelBox.setCanSwitch(battleManager.hasSwitchableMonster());

        BattleAnimator.playSwitchAnimation(
            p1View.getImageView(),
            new Runnable() {
                @Override
                public void run() {
                    p1View.setMonster(selectedMonster);
                }
            },
            new Runnable() {
                @Override
                public void run() {
                    battleManager.completeSwitchTurn(oldMonster, forced);
                    updateHpViews();
                    updateTurnOrderUI();
                    resumeBattleAfterDelay();
                }
            }
        );
    }

    private void resumeBattleAfterDelay() {
        PauseTransition readDelay = new PauseTransition(Duration.seconds(MESSAGE_DELAY_SECONDS));
        readDelay.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                checkBattleOutcome();
                if (!battleFinished && !isEitherFainted()) {
                    battleLoop.play();
                }
            }
        });
        readDelay.play();
    }

    private void checkBattleOutcome() {
        if (battleFinished) {
            return;
        }

        BattleState state = battleManager.evaluateBattleState();

        if (state == BattleState.MONSTER_CAPTURED) {
            battleFinished = true;
            String capturedName = battleManager.getCapturedMonsterName();
            popupBox.showMessage("THU PHỤC THÀNH CÔNG!\nBạn đã bắt được " + capturedName.toUpperCase() + "!");
            battleLoop.stop();
            skillPanelBox.setDisable(true);
            scheduleBattleEnd();
        } else if (state == BattleState.PLAYER_WIN) {
            battleFinished = true;
            popupBox.showMessage(battleManager.getP1().getName().toUpperCase() + " GIÀNH CHIẾN THẮNG!");
            battleLoop.stop();
            skillPanelBox.setDisable(true);
            scheduleBattleEnd();
        } else if (state == BattleState.FORCE_SWITCH) {
            battleLoop.pause();
            skillPanelBox.resetAndDisable();
            popupBox.showMessage(battleManager.getP1().getName() + " đã gục ngã!\nHãy chọn Pokémon tiếp theo!");
            openSwitchOverlay(true);
        } else if (state == BattleState.PLAYER_LOSE) {
            battleFinished = true;
            popupBox.showMessage(battleManager.getP2().getName().toUpperCase() + " GIÀNH CHIẾN THẮNG!");
            battleLoop.stop();
            scheduleBattleEnd();
        }
    }

    private void scheduleBattleEnd() {
        PauseTransition returnDelay = new PauseTransition(Duration.seconds(1.5));
        returnDelay.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (onBattleFinished != null) {
                    onBattleFinished.run();
                }
            }
        });
        returnDelay.play();
    }

    private void updateHpViews() {
        p1View.updateHp();
        p2View.updateHp();
    }

    private void updateTurnOrderUI() {
        Monster[] upcoming = battleManager.getNextTurns(5);
        turnOrderBox.update(upcoming, battleManager.getP2());
    }

    private boolean isEitherFainted() {
        return battleManager.getP1().isFainted() || battleManager.getP2().isFainted();
    }
}