package Battle.BattleGUI;

import Battle.Skills.Skill;
import Data.Inventory;
import Data.InventorySlot;
import Entity.Monster;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import java.util.function.Consumer;

public class SkillControlPanel extends HBox {
    private List<Button> currentButtons = new ArrayList<>();
    private int selectedIndex = 0;
    private int menuState = 0; // 0: Root, 1: Attack, 2: Item
    
    private Monster p1;
    private Inventory inventory;
    private Consumer<Skill> onSkillSelected;
    private Consumer<String> onItemSelected;
    private Runnable onFullscreenToggle;

    public SkillControlPanel(Monster p1, Inventory inventory, Consumer<Skill> onSkillSelected, Consumer<String> onItemSelected, Runnable onFullscreenToggle) {
        super(20);
        this.p1 = p1;
        this.inventory = inventory;
        this.onSkillSelected = onSkillSelected;
        this.onItemSelected = onItemSelected;
        this.onFullscreenToggle = onFullscreenToggle;
        
        this.setDisable(true); 
        drawMainMenu();
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

        Button btnItem = new Button("VẬT PHẨM");
        btnItem.setPrefSize(130, 70);
        btnItem.setFocusTraversable(false);
        btnItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                drawItemMenu();
            }
        });
        
        if (inventory.getSlots().isEmpty()) {
            btnItem.setDisable(true); 
            btnItem.setText("HẾT ĐỒ");
        }

        currentButtons.add(btnAttack);
        currentButtons.add(btnItem);
        this.getChildren().addAll(btnAttack, btnItem);
        updateFocus();
    }

    private void drawAttackMenu() {
        menuState = 1;
        selectedIndex = 0;
        this.getChildren().clear();
        currentButtons.clear();

        // 1. Thêm các nút kỹ năng
        for (int i = 0; i < 4; i++) {
            Skill skill = p1.getSkill(i);
            if (skill != null) {
                Button btn = new Button(skill.getName() + "\n(" + skill.getType() + ")");
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
        
        // 2. Thêm nút QUAY LẠI ở cuối
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

        // 1. Thêm các nút vật phẩm
        for (int i = 0; i < inventory.getSlots().size(); i++) {
            InventorySlot slot = inventory.getSlots().get(i);
            
            String btnText = "";
            if (slot.itemId.equals("HEAL_POTION")) { btnText = "Hồi Máu (x" + slot.quantity + ")"; }
            else if (slot.itemId.equals("ATK_BUFF")) { btnText = "Sức Mạnh (x" + slot.quantity + ")"; }
            else if (slot.itemId.equals("SPD_BUFF")) { btnText = "Tốc Độ (x" + slot.quantity + ")"; }

            Button btn = new Button(btnText);
            btn.setPrefSize(130, 70);
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

        // 2. Thêm nút QUAY LẠI ở cuối
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

    public void setupKeyboard(Scene scene) {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode code = event.getCode();

                if (code == KeyCode.F11) {
                    if (onFullscreenToggle != null) { 
                        onFullscreenToggle.run(); 
                    }
                    return;
                }

                if (isDisabled() == false) {
                    // ESC hoặc BACKSPACE vẫn có tác dụng quay lại
                    if (code == KeyCode.ESCAPE || code == KeyCode.BACK_SPACE) {
                        if (menuState != 0) {
                            drawMainMenu();
                        }
                    } 
                    else if (code == KeyCode.LEFT || code == KeyCode.A) {
                        if (currentButtons.size() > 0) {
                            selectedIndex = (selectedIndex - 1 + currentButtons.size()) % currentButtons.size();
                            updateFocus();
                        }
                    } 
                    else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                        if (currentButtons.size() > 0) {
                            selectedIndex = (selectedIndex + 1) % currentButtons.size();
                            updateFocus();
                        }
                    } 
                    else if (code == KeyCode.ENTER || code == KeyCode.SPACE) {
                        if (currentButtons.size() > 0) {
                            Button btn = currentButtons.get(selectedIndex);
                            if (btn.isDisabled() == false) {
                                btn.fire();
                            }
                        }
                    }
                }
            }
        });
    }

    private void updateFocus() {
        for (int i = 0; i < currentButtons.size(); i++) {
            Button btn = currentButtons.get(i);
            if (i == selectedIndex) {
                btn.setStyle("-fx-font-size: 16px; -fx-cursor: hand; -fx-text-alignment: center; "
                           + "-fx-border-color: #cc0000; -fx-border-width: 3px; "
                           + "-fx-background-color: #ffcccc; -fx-scale-x: 1.05; -fx-scale-y: 1.05;");
            } else {
                btn.setStyle("-fx-font-size: 16px; -fx-cursor: hand; -fx-text-alignment: center; "
                           + "-fx-border-color: transparent; -fx-border-width: 3px; "
                           + "-fx-background-color: #e0e0e0; -fx-scale-x: 1.0; -fx-scale-y: 1.0;");
            }
        }
    }
}