package Battle.BattleGUI;

import Battle.BattleManager;
import Battle.Skills.Skill;
import Data.InventorySlot;
import Entity.Monster;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class SkillControlPanel extends HBox {
    private List<Button> currentButtons = new ArrayList<Button>();
    private int selectedIndex = 0;
    private int menuState = 0; 

    private Monster p1;
    private BattleManager battleManager;
    private Consumer<Skill> onSkillSelected;
    private Consumer<String> onItemSelected;
    private Consumer<String> onBallSelected;
    private Runnable onSwitchRequested;
    private boolean canSwitch = true;

    public SkillControlPanel(
            Monster p1,
            BattleManager battleManager,
            Consumer<Skill> onSkillSelected,
            Consumer<String> onItemSelected,
            Consumer<String> onBallSelected,
            Runnable onSwitchRequested) {
        super(16);
        this.p1 = p1;
        this.battleManager = battleManager;
        this.onSkillSelected = onSkillSelected;
        this.onItemSelected = onItemSelected;
        this.onBallSelected = onBallSelected;
        this.onSwitchRequested = onSwitchRequested;

        this.setDisable(true);
        drawMainMenu();
    }

    public void setMonster(Monster newP1) {
        this.p1 = newP1;
    }

    public void setCanSwitch(boolean canSwitch) {
        this.canSwitch = canSwitch;
    }

    public void drawMainMenu() {
        menuState = 0;
        selectedIndex = 0;
        this.getChildren().clear();
        currentButtons.clear();

        Button btnAttack = new Button("TẤN CÔNG");
        btnAttack.setPrefSize(130, 70);
        btnAttack.setFocusTraversable(false);
        btnAttack.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                drawAttackMenu();
            }
        });

        int totalItems = battleManager.getTotalBattleItemCount();
        String itemBtnLabel;
        if (totalItems > 0) {
            itemBtnLabel = "VẬT PHẨM";
        } else {
            itemBtnLabel = "HẾT ĐỒ";
        }
        Button btnItem = new Button(itemBtnLabel);
        btnItem.setPrefSize(130, 70);
        btnItem.setFocusTraversable(false);
        btnItem.setDisable(totalItems <= 0);
        btnItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                drawItemMenu();
            }
        });

        Button btnSwitch = new Button("ĐỔI QUÁI");
        btnSwitch.setPrefSize(130, 70);
        btnSwitch.setFocusTraversable(false);
        btnSwitch.setDisable(!canSwitch);
        btnSwitch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (onSwitchRequested != null) {
                    onSwitchRequested.run();
                }
            }
        });

        currentButtons.add(btnAttack);
        currentButtons.add(btnItem);
        currentButtons.add(btnSwitch);
        this.getChildren().addAll(btnAttack, btnItem, btnSwitch);


        if (battleManager.canCaptureEnemy()) {
            int totalBalls = battleManager.getTotalBallCount();
            int chance = battleManager.calculateCaptureChance(battleManager.getDefaultBallId());

            String captureBtnLabel;
            if (totalBalls > 0) {
                captureBtnLabel = "BẮT QUÁI\n(" + chance + "% | x" + totalBalls + ")";
            } else {
                captureBtnLabel = "HẾT BÓNG (0)";
            }

            Button btnCapture = new Button(captureBtnLabel);
            btnCapture.setPrefSize(155, 70);
            btnCapture.setFocusTraversable(false);
            btnCapture.setDisable(totalBalls <= 0);
            btnCapture.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    List<InventorySlot> balls = battleManager.getAvailableBallSlots();
                    if (balls.size() == 1) {
                        onBallSelected.accept(balls.get(0).itemId);
                    } else if (balls.size() > 1) {
                        drawBallMenu(balls);
                    }
                }
            });

            currentButtons.add(btnCapture);
            this.getChildren().add(btnCapture);
        }

        updateFocus();
    }

    private void drawAttackMenu() {
        menuState = 1;
        selectedIndex = 0;
        this.getChildren().clear();
        currentButtons.clear();

        for (int i = 0; i < 4; i++) {
            final Skill skill = p1.getSkill(i);
            if (skill != null) {
                Button btn = new Button(skill.getName());
                btn.setPrefSize(130, 70);
                btn.setFocusTraversable(false);
                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        onSkillSelected.accept(skill);
                    }
                });
                currentButtons.add(btn);
                this.getChildren().add(btn);
            }
        }

        Button btnBack = new Button("QUAY LẠI");
        btnBack.setPrefSize(150, 70);
        btnBack.setFocusTraversable(false);
        btnBack.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                drawMainMenu();
            }
        });
        currentButtons.add(btnBack);
        this.getChildren().add(btnBack);

        updateFocus();
    }

    private void drawItemMenu() {
        menuState = 2;
        selectedIndex = 0;
        this.getChildren().clear();
        currentButtons.clear();

        List<InventorySlot> itemSlots = battleManager.getAvailableBattleItemSlots();
        for (int i = 0; i < itemSlots.size(); i++) {
            InventorySlot slot = itemSlots.get(i);
            String btnText = getDisplayItemName(slot.itemId) + " (x" + slot.quantity + ")";

            Button btn = new Button(btnText);
            btn.setPrefSize(145, 70);
            btn.setFocusTraversable(false);

            final String finalItemId = slot.itemId;
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    onItemSelected.accept(finalItemId);
                }
            });

            currentButtons.add(btn);
            this.getChildren().add(btn);
        }

        Button btnBack = new Button("QUAY LẠI");
        btnBack.setPrefSize(130, 70);
        btnBack.setFocusTraversable(false);
        btnBack.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                drawMainMenu();
            }
        });
        currentButtons.add(btnBack);
        this.getChildren().add(btnBack);

        updateFocus();
    }

    private void drawBallMenu(List<InventorySlot> ballSlots) {
        menuState = 3;
        selectedIndex = 0;
        this.getChildren().clear();
        currentButtons.clear();

        for (int i = 0; i < ballSlots.size(); i++) {
            InventorySlot slot = ballSlots.get(i);
            int chance = battleManager.calculateCaptureChance(slot.itemId);
            String btnText = getDisplayItemName(slot.itemId) + "\n(" + chance + "% | x" + slot.quantity + ")";

            Button btn = new Button(btnText);
            btn.setPrefSize(155, 70);
            btn.setFocusTraversable(false);

            final String finalBallId = slot.itemId;
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    onBallSelected.accept(finalBallId);
                }
            });

            currentButtons.add(btn);
            this.getChildren().add(btn);
        }

        Button btnBack = new Button("QUAY LẠI");
        btnBack.setPrefSize(130, 70);
        btnBack.setFocusTraversable(false);
        btnBack.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                drawMainMenu();
            }
        });
        currentButtons.add(btnBack);
        this.getChildren().add(btnBack);

        updateFocus();
    }

    private String getDisplayItemName(String itemId) {
        if (itemId == null) {
            return "";
        }
        String id = itemId.trim().toLowerCase();
        if (id.equals("heal_potion")) {
            return "Hồi Máu";
        } else if (id.equals("potion")) {
            return "Potion";
        } else if (id.equals("super potion")) {
            return "Super Potion";
        } else if (id.equals("hyper potion")) {
            return "Hyper Potion";
        } else if (id.equals("atk_buff") || id.equals("x attack")) {
            return "Sức Mạnh";
        } else if (id.equals("spd_buff")) {
            return "Tốc Độ";
        } else if (id.equals("x defense")) {
            return "Phòng Thủ";
        } else if (id.equals("pokeball")) {
            return "Poke Ball";
        } else if (id.equals("great ball")) {
            return "Great Ball";
        } else if (id.equals("ultra ball")) {
            return "Ultra Ball";
        }
        return itemId;
    }

    public void enableAndFocus() {
        this.setDisable(false);
        drawMainMenu();
    }

    public void resetAndDisable() {
        this.setDisable(true);
        drawMainMenu();
        selectedIndex = -1;
        updateFocus();
    }

    public void handleBackKey() {
        if (menuState != 0) {
            drawMainMenu();
        }
    }

    public void moveSelection(int delta) {
        if (!currentButtons.isEmpty()) {
            int size = currentButtons.size();
            selectedIndex = (selectedIndex + delta + size) % size;
            updateFocus();
        }
    }

    public void triggerSelectedButton() {
        if (selectedIndex >= 0 && selectedIndex < currentButtons.size()) {
            Button btn = currentButtons.get(selectedIndex);
            if (!btn.isDisabled()) {
                btn.fire();
            }
        }
    }

    private void updateFocus() {
        for (int i = 0; i < currentButtons.size(); i++) {
            Button btn = currentButtons.get(i);
            if (i == selectedIndex) {
                btn.setStyle("-fx-font-size: 15px; -fx-cursor: hand; -fx-text-alignment: center; "
                    + "-fx-border-color: #cc0000; -fx-border-width: 3px; "
                    + "-fx-background-color: #ffcccc; -fx-scale-x: 1.05; -fx-scale-y: 1.05;");
            } else {
                btn.setStyle("-fx-font-size: 15px; -fx-cursor: hand; -fx-text-alignment: center; "
                    + "-fx-border-color: transparent; -fx-border-width: 3px; "
                    + "-fx-background-color: #e0e0e0; -fx-scale-x: 1.0; -fx-scale-y: 1.0;");
            }
        }
    }
}