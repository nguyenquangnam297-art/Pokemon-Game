package Battle.BattleGUI;

import Entity.Monster;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class TurnOrderView extends VBox {

    public TurnOrderView() {
        super(10);
        this.setAlignment(Pos.TOP_CENTER);
    }

    public void update(Monster[] upcoming, Monster p2) {
        this.getChildren().clear();

        Label title = new Label("Thứ tự lượt:");
        title.setStyle(
            "-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #ffffff; "
            + "-fx-background-color: rgba(20, 25, 35, 0.75); -fx-padding: 4 10 4 10; -fx-background-radius: 0;"
        );
        this.getChildren().add(title);

        for (int i = 0; i < upcoming.length; i++) {
            ImageView img = new ImageView();
            try {
                Image rawImg = new Image(getClass().getResourceAsStream(upcoming[i].getImagePath()));
                MonsterSpriteHelper.applyStaticFrame(img, rawImg);
            } catch (Exception e) {}

            boolean isEnemy = (upcoming[i] == p2);

            String bgColor;
            if (isEnemy) {
                bgColor = "#ef5350"; // Nền đỏ cho quái địch
            } else {
                bgColor = "#42a5f5"; // Nền xanh cho quái bản thân
            }

            int size;
            String borderColor;
            String borderWidth;

            if (i == 0) {
                size = 53;
                borderColor = "#ffd54f"; // Viền vàng nổi bật cho lượt hiện tại
                borderWidth = "3";
            } else {
                size = 53;
                if (isEnemy) {
                    borderColor = "#b71c1c";
                } else {
                    borderColor = "#0d47a1";
                }
                borderWidth = "2";
            }

            img.setFitHeight(size);
            img.setFitWidth(size);
            img.setPreserveRatio(true);

            if (isEnemy) {
                img.setScaleX(-1);
            }

            StackPane frame = new StackPane(img);
            frame.setStyle("-fx-border-color: " + borderColor
                + "; -fx-border-width: " + borderWidth
                + "; -fx-background-color: " + bgColor
                + "; -fx-padding: 3; -fx-background-radius: 0; -fx-border-radius: 0;");

            this.getChildren().add(frame);
        }
    }
}