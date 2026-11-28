package Battle;

import Battle.BattleGUI.BattleAssetLoader;
import Battle.BattleGUI.BattleInputHandler;
import Battle.BattleGUI.BattleIntroAnimator;
import Battle.BattleGUI.MonsterStatusView;
import Battle.BattleGUI.NotificationPopup;
import Battle.BattleGUI.PartySwitchOverlay;
import Battle.BattleGUI.SkillControlPanel;
import Battle.BattleGUI.TurnOrderView;
import Battle.Skills.Skill;
import Data.Inventory;
import Entity.Monster;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class FightScreen {

    private BattleManager battleManager;
    private BattleController battleController;

    // --- Constructors dùng mặc định test (tự khởi tạo 3 bóng & 3 mỗi loại item) ---
    public FightScreen(Monster p1, Monster p2) {
        this.battleManager = new BattleManager(p1, p2);
    }

    public FightScreen(Monster p1, Monster p2, BattleEnvironment environment) {
        this.battleManager = new BattleManager(p1, p2, environment);
    }

    public FightScreen(List<Monster> playerTeam, Monster p2) {
        this.battleManager = new BattleManager(playerTeam, p2);
    }

    public FightScreen(List<Monster> playerTeam, Monster p2, BattleEnvironment environment) {
        this.battleManager = new BattleManager(playerTeam, p2, environment);
    }

    // --- Constructors nhận túi đồ thật (Inventory) từ bên ngoài ---
    public FightScreen(Monster p1, Monster p2, Inventory inventory) {
        this.battleManager = new BattleManager(p1, p2, BattleEnvironment.FOREST, inventory);
    }

    public FightScreen(Monster p1, Monster p2, BattleEnvironment environment, Inventory inventory) {
        this.battleManager = new BattleManager(p1, p2, environment, inventory);
    }

    public FightScreen(List<Monster> playerTeam, Monster p2, Inventory inventory) {
        this.battleManager = new BattleManager(playerTeam, p2, BattleEnvironment.FOREST, inventory);
    }

    public FightScreen(List<Monster> playerTeam, Monster p2, BattleEnvironment environment, Inventory inventory) {
        this.battleManager = new BattleManager(playerTeam, p2, environment, inventory);
    }

    public BattleManager getBattleManager() {
        return battleManager;
    }

    public void start(Stage primaryStage) {
        start(primaryStage, null, null, null);
    }

    public void start(final Stage primaryStage, Runnable onBattleFinished) {
        start(primaryStage, onBattleFinished, null, null);
    }

    public void start(
            final Stage primaryStage,
            Runnable onBattleFinished,
            Consumer<Boolean> onPlayerBattleFinished) {
        start(primaryStage, onBattleFinished, onPlayerBattleFinished, null);
    }

    public void start(
            final Stage primaryStage,
            BiConsumer<Boolean, String> onCaptureFinished) {
        start(primaryStage, null, null, onCaptureFinished);
    }

    public void start(
            final Stage primaryStage,
            Runnable onBattleFinished,
            Consumer<Boolean> onPlayerBattleFinished,
            BiConsumer<Boolean, String> onCaptureFinished) {

        Runnable finishCallback = new Runnable() {
            @Override
            public void run() {
                if (onBattleFinished != null) {
                    onBattleFinished.run();
                }

                if (onPlayerBattleFinished != null) {
                    BattleManager.BattleState state = battleManager.evaluateBattleState();
                    if (state == BattleManager.BattleState.PLAYER_WIN) {
                        onPlayerBattleFinished.accept(true);
                    } else if (state == BattleManager.BattleState.PLAYER_LOSE) {
                        onPlayerBattleFinished.accept(false);
                    }
                }

                if (onCaptureFinished != null) {
                    onCaptureFinished.accept(
                        battleManager.isMonsterCaptured(),
                        battleManager.getCapturedMonsterName()
                    );
                }
            }
        };

        BattleEnvironment env = battleManager.getEnvironment();

        AnchorPane gameRoot = new AnchorPane();
        gameRoot.setMinSize(1300, 800);
        gameRoot.setMaxSize(1300, 800);
        gameRoot.setPrefSize(1300, 800);
        gameRoot.setStyle("-fx-background-color: " + env.getFallbackHexColor() + ";");

        ImageView backgroundView = createBackgroundView(env);
        if (backgroundView != null) {
            gameRoot.getChildren().add(backgroundView);
        }

        // Lớp giao diện mở đầu (nằm ngay trên ảnh nền và dưới chân 2 quái)
        BattleIntroAnimator introAnimator = new BattleIntroAnimator(gameRoot, battleManager.getPlayerTeam());

        MonsterStatusView p1View = new MonsterStatusView(battleManager.getP1(), false);
        MonsterStatusView p2View = new MonsterStatusView(battleManager.getP2(), true);
        TurnOrderView turnOrderBox = new TurnOrderView();
        NotificationPopup popupBox = new NotificationPopup();

        PartySwitchOverlay partyOverlay = new PartySwitchOverlay(
            new Consumer<Monster>() {
                @Override
                public void accept(Monster selectedMonster) {
                    battleController.handlePokemonSwitch(selectedMonster);
                }
            },
            new Runnable() {
                @Override
                public void run() {
                    battleController.handleSwitchCancel();
                }
            }
        );

        SkillControlPanel skillPanelBox = new SkillControlPanel(
            battleManager.getP1(),
            battleManager,
            new Consumer<Skill>() {
                @Override
                public void accept(Skill skill) {
                    battleController.handlePlayerSkillSelect(skill);
                }
            },
            new Consumer<String>() {
                @Override
                public void accept(String itemId) {
                    battleController.handleItemSelect(itemId);
                }
            },
            new Consumer<String>() {
                @Override
                public void accept(String ballId) {
                    battleController.handleBallSelect(ballId);
                }
            },
            new Runnable() {
                @Override
                public void run() {
                    battleController.openSwitchOverlay(false);
                }
            }
        );
        skillPanelBox.setCanSwitch(battleManager.hasSwitchableMonster());

        setupLayoutAnchors(p1View, p2View, skillPanelBox, turnOrderBox, popupBox, partyOverlay);
        gameRoot.getChildren().addAll(p2View, p1View, skillPanelBox, turnOrderBox, popupBox, partyOverlay);

        battleController = new BattleController(
            battleManager,
            p1View,
            p2View,
            turnOrderBox,
            popupBox,
            skillPanelBox,
            partyOverlay,
            introAnimator,
            finishCallback
        );

        StackPane mainRoot = new StackPane(gameRoot);
        mainRoot.setStyle("-fx-background-color: #111111;");

        Scene scene = new Scene(mainRoot, 1040, 640);
        NumberBinding scaleBinding = Bindings.min(
            scene.widthProperty().divide(1300.0),
            scene.heightProperty().divide(800.0)
        );
        gameRoot.scaleXProperty().bind(scaleBinding);
        gameRoot.scaleYProperty().bind(scaleBinding);

        BattleInputHandler.attach(
            scene,
            skillPanelBox,
            partyOverlay,
            new BooleanSupplier() {
                @Override
                public boolean getAsBoolean() {
                    return battleController.trySkipIntro();
                }
            },
            new Runnable() {
                @Override
                public void run() {
                    primaryStage.setFullScreen(!primaryStage.isFullScreen());
                }
            }
        );
        mainRoot.setFocusTraversable(true);

        primaryStage.setTitle("Monster Battle System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setFullScreenExitHint("");
        primaryStage.show();
        mainRoot.requestFocus();

        battleController.startBattle();
    }

    private ImageView createBackgroundView(BattleEnvironment env) {
        String fileName = env.getFileName();
        Image bgImage = BattleAssetLoader.loadImage(
            "/BattleBackgrounds/" + fileName,
            "/backgrounds/" + fileName,
            "/" + fileName,
            "graphics/backgrounds/" + fileName,
            "src/main/resources/BattleBackgrounds/" + fileName,
            "src/main/resources/backgrounds/" + fileName
        );
        if (bgImage == null || bgImage.isError() || bgImage.getWidth() <= 0) {
            return null;
        }

        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(1300);
        bgView.setFitHeight(800);
        bgView.setPreserveRatio(false);
        bgView.setSmooth(false);
        bgView.setMouseTransparent(true);
        AnchorPane.setTopAnchor(bgView, 0.0);
        AnchorPane.setLeftAnchor(bgView, 0.0);
        return bgView;
    }

    private void setupLayoutAnchors(
            MonsterStatusView p1View,
            MonsterStatusView p2View,
            SkillControlPanel skillPanelBox,
            TurnOrderView turnOrderBox,
            NotificationPopup popupBox,
            PartySwitchOverlay partyOverlay) {

        AnchorPane.setBottomAnchor(p1View, 50.0);
        AnchorPane.setLeftAnchor(p1View, 50.0);
        AnchorPane.setTopAnchor(p2View, 50.0);
        AnchorPane.setRightAnchor(p2View, 50.0);
        AnchorPane.setBottomAnchor(skillPanelBox, 50.0);
        AnchorPane.setRightAnchor(skillPanelBox, 50.0);
        AnchorPane.setTopAnchor(turnOrderBox, 20.0);
        AnchorPane.setLeftAnchor(turnOrderBox, 20.0);
        AnchorPane.setTopAnchor(popupBox, 50.0);
        AnchorPane.setLeftAnchor(popupBox, 0.0);
        AnchorPane.setRightAnchor(popupBox, 0.0);

        AnchorPane.setTopAnchor(partyOverlay, 0.0);
        AnchorPane.setBottomAnchor(partyOverlay, 0.0);
        AnchorPane.setLeftAnchor(partyOverlay, 0.0);
        AnchorPane.setRightAnchor(partyOverlay, 0.0);
    }
}