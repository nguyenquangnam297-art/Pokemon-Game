package Battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Battle.BattleGUI.MonsterStatusView;
import Battle.BattleGUI.NotificationPopup;
import Battle.BattleGUI.SkillControlPanel;
import Battle.BattleGUI.TurnOrderView;
import Battle.Skills.Skill;
import Battle.Skills.TypeChart;
import Data.Inventory;
import Data.InventorySlot;
import Entity.Monster;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FightScreen {

    private Monster p1, p2;
    private BattleManager battleManager;
    private Timeline battleLoop; 
    private Inventory inventory;
    private MonsterStatusView p1View, p2View;
    private TurnOrderView turnOrderBox;
    private NotificationPopup popupBox;
    private SkillControlPanel skillPanelBox;
    private Runnable onBattleFinished;
    private boolean battleFinished;

    public FightScreen(Monster p1, Monster p2) {
        this.p1 = p1; 
        this.p2 = p2;
        this.battleManager = new BattleManager(p1, p2);
    }

    public void start(Stage primaryStage) {
        start(primaryStage, () -> {});
    }

    public void start(Stage primaryStage, Runnable onBattleFinished) {
        this.onBattleFinished = Objects.requireNonNull(onBattleFinished);
        battleFinished = false;
        AnchorPane gameRoot = new AnchorPane();
        gameRoot.setMinSize(1300, 800);
        gameRoot.setMaxSize(1300, 800);
        gameRoot.setPrefSize(1300, 800);
        gameRoot.setStyle("-fx-background-color: #f4f4f4;");

        // 1. BẮT BUỘC PHẢI CÓ ĐOẠN NÀY ĐỂ NẠP DỮ LIỆU VÀO TÚI ĐỒ
        List<InventorySlot> slots = new ArrayList<>();
        slots.add(new InventorySlot("HEAL_POTION", 3));
        slots.add(new InventorySlot("ATK_BUFF", 3));
        slots.add(new InventorySlot("SPD_BUFF", 3));
        this.inventory = new Inventory(slots); // Gán dữ liệu thật vào biến toàn cục

        p1View = new MonsterStatusView(p1, false);
        p2View = new MonsterStatusView(p2, true);
        turnOrderBox = new TurnOrderView();
        popupBox = new NotificationPopup();
        
        // 2. Nạp túi đồ vào UI Panel
        skillPanelBox = new SkillControlPanel(p1, this.inventory,
            skill -> handlePlayerSkillSelect(skill), 
            itemId -> handleItemSelect(itemId),
            () -> primaryStage.setFullScreen(!primaryStage.isFullScreen()) 
        );
        
        // ... các code ghim tọa độ bên dưới giữ nguyên

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
        
        gameRoot.getChildren().addAll(p2View, p1View, skillPanelBox, turnOrderBox, popupBox);

        StackPane mainRoot = new StackPane(gameRoot);
        mainRoot.setStyle("-fx-background-color: #111111;");

        Scene scene = new Scene(mainRoot, 1300, 800);
        NumberBinding scaleBinding = Bindings.min(
            scene.widthProperty().divide(1300.0),
            scene.heightProperty().divide(800.0)
        );
        gameRoot.scaleXProperty().bind(scaleBinding);
        gameRoot.scaleYProperty().bind(scaleBinding);

        skillPanelBox.setupKeyboard(scene);
        mainRoot.setFocusTraversable(true);

        primaryStage.setTitle("Monster Battle System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true); 
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setFullScreenExitHint(""); 
        primaryStage.setFullScreen(true); 
        primaryStage.show();
        mainRoot.requestFocus();

        popupBox.showMessage("TRẬN ĐẤU BẮT ĐẦU!\n" + p1.getName() + " VS " + p2.getName());
        updateTurnOrderUI();
        startBattle();
    }

    private void updateTurnOrderUI() {
        Monster[] upcoming = battleManager.getNextTurns(5);
        turnOrderBox.update(upcoming, p2);
    }

    private void startBattle() {
        battleLoop = new Timeline(new KeyFrame(Duration.millis(50), e -> processNextTick()));
        battleLoop.setCycleCount(Timeline.INDEFINITE);
        battleLoop.play();
    }

    private void processNextTick() {
        if (battleManager.tick()) {
            
            if (battleManager.isP1Turn()) {
                battleLoop.pause(); 
                skillPanelBox.enableAndFocus();
                popupBox.showMessage("Lượt " + battleManager.getActionCount() + "\nĐến lượt bạn!");
                
            } else {
                battleLoop.pause(); 
                popupBox.showMessage("Lượt " + battleManager.getActionCount() + "\nĐến lượt đối thủ...");
                
                PauseTransition delay = new PauseTransition(Duration.seconds(1.2));
                delay.setOnFinished(event -> {
                    
                    Skill randomSkill = battleManager.getRandomP2Skill();
                    executeAction(p2, p1, randomSkill); 
                    
                    p2.tickBuffs(); // Trừ thời gian buff ở cuối lượt
                    
                    battleManager.consumeP2Gauge(); 
                    updateTurnOrderUI();  
                    p1View.updateHp();
                    p2View.updateHp();
                    checkWinner();
                    
                    if (!p1.isFainted() && !p2.isFainted()) {
                        battleLoop.play(); 
                    }
                });
                
                delay.play(); 
                return; 
            }
        }

        p1View.updateHp();
        p2View.updateHp();
        checkWinner();
    }

    private void handleItemSelect(String itemId) {
        // Thực thi trừ vật phẩm từ class Inventory
        boolean isUsed = this.inventory.useItem(itemId);
        
        if (isUsed == false) {
            return;
        }

        String itemName = "";
        
        if (itemId.equals("HEAL_POTION")) {
            int healAmount = p1.getMaxHp() * 10 / 100;
            p1.heal(healAmount); 
            
            itemName = "Thuốc Hồi Máu";
            popupBox.showMessage("Bạn đã dùng " + itemName + "!\n(Hồi " + healAmount + " HP)");
        } 
        else if (itemId.equals("ATK_BUFF")) {
            p1.applyAtkBuff(3); 
            itemName = "Thuốc Sức Mạnh";
            popupBox.showMessage("Bạn đã dùng " + itemName + "!\n(Tăng atk 2 lượt tới)");
        } 
        else if (itemId.equals("SPD_BUFF")) {
            p1.applySpdBuff(3);
            itemName = "Thuốc Tốc Độ";
            popupBox.showMessage("Bạn đã dùng " + itemName + "!\n(Tăng tốc trong 2 lượt tới)");
        }
        
        p1.tickBuffs(); 
        battleManager.consumeP1Gauge(); 
        battleManager.incrementActionCount(); 
        updateTurnOrderUI();
        
        skillPanelBox.resetAndDisable(); 
        
        p1View.updateHp();
        p2View.updateHp();
        checkWinner();
        
        if (!p1.isFainted() && !p2.isFainted()) {
            battleLoop.play(); 
        }
    }
    private void handlePlayerSkillSelect(Skill skill) {
        executeAction(p1, p2, skill);
        
        p1.tickBuffs(); // Trừ thời gian buff ở cuối lượt sau khi đánh xong
        
        battleManager.consumeP1Gauge(); 
        updateTurnOrderUI();
        
        skillPanelBox.resetAndDisable(); 
        
        p1View.updateHp();
        p2View.updateHp();
        checkWinner();
        
        if (!p1.isFainted() && !p2.isFainted()) {
            battleLoop.play(); 
        }
    }

    private void executeAction(Monster attacker, Monster defender, Skill skill) {
        if (skill == null) return;

        int damage = DamageCalculator.calculateDamage(attacker, defender, skill);
        defender.takeDamage(damage);

        String attackerName;
        if (attacker == p1) {
            attackerName = "Bạn";
        } else {
            attackerName = "Đối thủ";
        }
        
        String popupMsg = "Lượt " + battleManager.getActionCount() + "\n" + attackerName + " dùng chiêu " + skill.getName() + "!";
        
        int effect = TypeChart.getEffectiveness(skill.getType(), defender.getType());
        if (effect == 1) {
            popupMsg += "\n(Siêu hiệu quả!)";
        } else if (effect == -1) {
            popupMsg += "\n(Không mấy hiệu quả...)";
        }
        
        popupBox.showMessage(popupMsg);
        battleManager.incrementActionCount(); 
    }

    private void checkWinner() {
        if (!battleFinished && (p1.isFainted() || p2.isFainted())) {
            battleFinished = true;
            String winnerName;
            if (p1.isFainted()) {
                winnerName = p2.getName();
            } else {
                winnerName = p1.getName();
            }
            
            popupBox.showMessage(winnerName.toUpperCase() + " GIÀNH CHIẾN THẮNG!");
            battleLoop.stop(); 
            skillPanelBox.setDisable(true); 
            PauseTransition returnDelay = new PauseTransition(Duration.seconds(1.5));
            returnDelay.setOnFinished(event -> onBattleFinished.run());
            returnDelay.play();
        }
    }
}