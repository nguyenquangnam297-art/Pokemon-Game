package Entity;

import java.util.ArrayList;

public class MonsterHandler {
    private ArrayList<Monster> allMonsters;

    private ArrayList<Monster> tierCMonsters;
    private ArrayList<Monster> tierBMonsters;
    private ArrayList<Monster> tierAMonsters;
    private ArrayList<Monster> tierSMonsters;
    public MonsterHandler() {

        allMonsters = new ArrayList<>();

        tierCMonsters = new ArrayList<>();
        tierBMonsters = new ArrayList<>();
        tierAMonsters = new ArrayList<>();
        tierSMonsters = new ArrayList<>();

        createMonsters();
    }
    private void createMonsters(){
        Monster pikachu = new Monster(
                "Pikachu",
                5,
                100,
                100,
                20,
                10,
                15
        );
        allMonsters.add(pikachu);
        tierAMonsters.add(pikachu);

        Monster charmander = new Monster(
                "Charmander",
                5,
                100,
                100,
                22,
                9,
                12
        );
        allMonsters.add(charmander);
        tierBMonsters.add(charmander);

        Monster mewtwo = new Monster(
                "Mewtwo",
                50,
                180,
                180,
                90,
                80,
                95
        );
        allMonsters.add(mewtwo);
        tierAMonsters.add(mewtwo);
    };

    public ArrayList<Monster> getAllMonsters() {
        return allMonsters;
    }

    public ArrayList<Monster> getTierCMonsters() {
        return tierCMonsters;
    }

    public ArrayList<Monster> getTierBMonsters() {
        return tierBMonsters;
    }

    public ArrayList<Monster> getTierAMonsters() {
        return tierAMonsters;
    }

    public ArrayList<Monster> getTierSMonsters() {
        return tierSMonsters;
    }
}
