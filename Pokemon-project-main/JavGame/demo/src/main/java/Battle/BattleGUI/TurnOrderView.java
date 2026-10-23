package Battle.BattleGUI;

import Entity.Monster;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Component hiển thị danh sách dự đoán lượt.
 */
public class TurnOrderView extends VBox {

    public TurnOrderView() {
        super(10);
        this.setAlignment(Pos.TOP_CENTER);
    }

    public void update(Monster[] upcoming, Monster p2) {
        this.getChildren().clear();
        
        Label title = new Label("Thứ tự lượt:");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;");
        this.getChildren().add(title);

        for (int i = 0; i < upcoming.length; i++) {
            ImageView img = new ImageView();
            try {
                img.setImage(new Image(getClass().getResourceAsStream(upcoming[i].getImagePath())));
            } catch (Exception e) {}

            int size;
            String borderColor;
            String borderWidth;
            
            if (i == 0) {
                size = 70;
                borderColor = "#cc0000"; 
                borderWidth = "3";
            } else {
                size = 45;
                borderColor = "#aaaaaa"; 
                borderWidth = "1";
            }
            
            img.setFitHeight(size);
            img.setFitWidth(size); 
            
            if (upcoming[i] == p2) {
                img.setScaleX(-1); 
            }
            
            StackPane frame = new StackPane(img);
            frame.setStyle("-fx-border-color: " + borderColor + 
                           "; -fx-border-width: " + borderWidth + 
                           "; -fx-background-color: white; -fx-padding: 3; -fx-background-radius: 5; -fx-border-radius: 5;");
            
            this.getChildren().add(frame);
        }
    }
}