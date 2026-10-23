package Battle.BattleGUI;

import Entity.Monster;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Component gom nhóm Hình ảnh, Tên và Thanh máu của 1 Monster.
 */
public class MonsterStatusView extends VBox {
    private Monster monster;
    private ProgressBar hpBar;
    private Label nameLabel;

    public MonsterStatusView(Monster monster, boolean isEnemy) {
        super(10);
        this.monster = monster;
        
        hpBar = new ProgressBar(1.0);
        hpBar.setStyle("-fx-accent: #28c828;");
        hpBar.setPrefSize(400, 25);

        nameLabel = new Label();
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");

        ImageView imageView = new ImageView();
        try {
            imageView.setImage(new Image(getClass().getResourceAsStream(monster.getImagePath())));
            imageView.setFitHeight(250);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {}

        if (isEnemy) {
            // Đối thủ (P2): Tên -> Máu -> Ảnh (Lật ngược)
            imageView.setScaleX(-1);
            this.setAlignment(Pos.CENTER_RIGHT);
            this.getChildren().addAll(nameLabel, hpBar, imageView);
        } else {
            // Người chơi (P1): Ảnh -> Tên -> Máu
            this.setAlignment(Pos.CENTER_LEFT);
            this.getChildren().addAll(imageView, nameLabel, hpBar);
        }
        
        updateHp();
    }

    public void updateHp() {
        nameLabel.setText(monster.getName() + " (HP: " + monster.getCurrentHp() + "/" + monster.getMaxHp() + ")");
        hpBar.setProgress((double) monster.getCurrentHp() / monster.getMaxHp());
    }
}