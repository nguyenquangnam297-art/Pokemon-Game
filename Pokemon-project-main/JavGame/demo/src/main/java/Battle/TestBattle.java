/*







CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO 


Dung de test (Ko lien quan den code chinh cua game)




 */
package Battle;

import Entity.Monster;
import Entity.MonsterHandler;
import javafx.application.Application;
import javafx.stage.Stage;

public class TestBattle { 

    public static class FXRunner extends Application {
        @Override
        public void start(Stage primaryStage) {
            MonsterHandler handler = new MonsterHandler();
            Monster p1 = handler.getMonsterByName("Pikachu");
            Monster p2 = handler.getMonsterByName("Charmander"); 
            
            new FightScreen(p1, p2).start(primaryStage);
        }
    }
    public static void main(String[] args) {
        Application.launch(FXRunner.class, args);
    }
}