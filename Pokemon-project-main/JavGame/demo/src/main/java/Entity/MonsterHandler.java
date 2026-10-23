package Entity;

import Battle.Skills.Skill;
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

    private void createMonsters() {
        // --- Pikachu ---
        Monster pikachu = new Monster("Pikachu", "ELECTRIC", 2300, 55, 40, 90,"/MonsterSprites/pikachu.png");
        pikachu.addSkill(new Skill("Quick Attack", 40, "NORMAL"));
        pikachu.addSkill(new Skill("Thunderbolt", 90, "ELECTRIC"));
        pikachu.addSkill(new Skill("Iron Tail", 75, "STEEL"));
        allMonsters.add(pikachu);
        tierAMonsters.add(pikachu);

        // --- Charmander ---
        Monster charmander = new Monster("Charmander", "FIRE", 2600, 52, 43, 65,"/MonsterSprites/charmander.png");
        charmander.addSkill(new Skill("Scratch", 40, "NORMAL"));
        charmander.addSkill(new Skill("Ember", 40, "FIRE"));
        charmander.addSkill(new Skill("Flamethrower", 90, "FIRE"));
        allMonsters.add(charmander);
        tierBMonsters.add(charmander);

        // --- Squirtle ---
        Monster squirtle = new Monster("Squirtle", "WATER", 2900, 48, 65, 43,"/MonsterSprites/squirtle.png");
        squirtle.addSkill(new Skill("Tackle", 40, "NORMAL"));
        squirtle.addSkill(new Skill("Water Gun", 40, "WATER"));
        squirtle.addSkill(new Skill("Hydro Pump", 110, "WATER"));
        allMonsters.add(squirtle);
        tierCMonsters.add(squirtle);

        // --- Mewtwo ---
        Monster mewtwo = new Monster("Mewtwo", "PSYCHIC", 3500, 90, 80, 95,"/MonsterSprites/mewtwo.png");
        mewtwo.addSkill(new Skill("Psychic", 90, "PSYCHIC"));
        mewtwo.addSkill(new Skill("Swift", 60, "NORMAL"));
        allMonsters.add(mewtwo);
        tierSMonsters.add(mewtwo);
    }

    // Hàm lấy Monster theo tên
    public Monster getMonsterByName(String name) {
        for (Monster m : allMonsters) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        System.out.println("Lỗi: Không tìm thấy " + name + " trong MonsterHandler!");
        Monster fallback = new Monster("MissingNo", "NORMAL", 50, 10, 10, 10,"/MonsterSprites/pikachu.png");
        fallback.addSkill(new Skill("Struggle", 50, "NORMAL"));
        return fallback;
    }

    // Getters cho các danh sách Tier
    public ArrayList<Monster> getAllMonsters() { return allMonsters; }
    public ArrayList<Monster> getTierCMonsters() { return tierCMonsters; }
    public ArrayList<Monster> getTierBMonsters() { return tierBMonsters; }
    public ArrayList<Monster> getTierAMonsters() { return tierAMonsters; }
    public ArrayList<Monster> getTierSMonsters() { return tierSMonsters; }
}