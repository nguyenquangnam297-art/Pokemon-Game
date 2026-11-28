/*







CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO CẤM ĐỘNG VÀO 


Dung de test (Ko lien quan den code chinh cua game)




 */
package Battle;

import Entity.Monster;
import Entity.MonsterHandler;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.stage.Stage;

public class TestBattle {

    public static class FXRunner extends Application {
        @Override
        public void start(Stage primaryStage) {
            // Đổi môi trường
            BattleEnvironment environment = BattleEnvironment.FOREST;
            //BattleEnvironment environment = BattleEnvironment.ICE;
            //BattleEnvironment environment = BattleEnvironment.SAND;
            MonsterHandler handler = new MonsterHandler();
            List<Monster> myTeam = new ArrayList<Monster>();

            String[] selectedNames = {
                "Finiette",
                "Pluma",
                "Atrox",
                "Charmadillo"
            };

            for (int i = 0; i < selectedNames.length; i++) {
                Monster m = handler.getMonsterByName(selectedNames[i]);
                if (m != null) {
                    myTeam.add(m);
                }
            }

            Monster p2 = handler.getMonsterByName("Charmadillo");

            new FightScreen(myTeam, p2, environment).start(primaryStage);
        }
    }

    public static void main(String[] args) {
        Application.launch(FXRunner.class, args);
    }
}
