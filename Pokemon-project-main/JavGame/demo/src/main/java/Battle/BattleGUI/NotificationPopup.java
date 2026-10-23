package Battle.BattleGUI;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/*
Thông báo trận đấu.
 */
public class NotificationPopup extends HBox {
    private Label popupLabel;

    public NotificationPopup() {
        popupLabel = new Label();
        popupLabel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); "
                          + "-fx-text-fill: white; "
                          + "-fx-font-size: 18px; "
                          + "-fx-font-weight: bold; "
                          + "-fx-padding: 15 30; "
                          + "-fx-background-radius: 20; "
                          + "-fx-text-alignment: center;");
        popupLabel.setAlignment(Pos.CENTER);

        this.getChildren().add(popupLabel);
        this.setAlignment(Pos.CENTER);
        this.setMouseTransparent(true); // Không chặn nhấp chuột
    }

    public void showMessage(String message) {
        popupLabel.setText(message);
    }
}