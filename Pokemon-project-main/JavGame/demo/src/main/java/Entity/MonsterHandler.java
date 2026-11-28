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
        Monster atrox = new Monster("Atrox", "POISON", 3000, 70, 60, 80, "/MonsterSprites/Atrox.png");
        atrox.addSkill(new Skill("Venom Fang", 65, "POISON"));
        atrox.addSkill(new Skill("Coil Strike", 75, "DRAGON"));
        atrox.addSkill(new Skill("Toxic Crash", 90, "POISON", true));
        atrox.addSkill(new Skill("Tail Sweep", 55, "NORMAL"));
        addMonster(atrox, tierAMonsters);

        Monster charmadillo = new Monster("Charmadillo", "FIRE", 3200, 80, 75, 55, "/MonsterSprites/Charmadillo.png");
        charmadillo.addSkill(new Skill("Ember", 45, "FIRE", true));
        charmadillo.addSkill(new Skill("Flame Burst", 85, "FIRE", true));
        charmadillo.addSkill(new Skill("Magma Pound", 70, "GROUND"));
        charmadillo.addSkill(new Skill("Shell Bash", 50, "NORMAL"));
        addMonster(charmadillo, tierAMonsters);

        Monster finiette = new Monster("Finiette", "WATER", 2800, 65, 55, 90, "/MonsterSprites/Finiette.png");
        finiette.addSkill(new Skill("Water Gun", 45, "WATER", true));
        finiette.addSkill(new Skill("Tidal Wave", 80, "WATER", true));
        finiette.addSkill(new Skill("Ice Shard", 60, "ICE", true));
        finiette.addSkill(new Skill("Fin Strike", 55, "NORMAL"));
        addMonster(finiette, tierBMonsters);

        Monster pluma = new Monster("Pluma", "GRASS", 3500, 75, 90, 55, "/MonsterSprites/Pluma.png");
        pluma.addSkill(new Skill("Vine Whip", 50, "GRASS"));
        pluma.addSkill(new Skill("Leaf Storm", 85, "GRASS", true));
        pluma.addSkill(new Skill("Razor Leaf", 65, "GRASS", true));
        pluma.addSkill(new Skill("Crushing Seed", 70, "NORMAL"));
        addMonster(pluma, tierBMonsters);

        Monster plumette = new Monster("Plumette", "GRASS", 2300, 45, 45, 75, "/MonsterSprites/Plumette.png");
        plumette.addSkill(new Skill("Leaf Blade", 60, "GRASS"));
        plumette.addSkill(new Skill("Razor Leaf", 55, "GRASS", true));
        plumette.addSkill(new Skill("Vine Whip", 50, "GRASS"));
        plumette.addSkill(new Skill("Petal Dash", 65, "NORMAL"));
        addMonster(plumette, tierCMonsters);

        Monster pouch = new Monster("Pouch", "NORMAL", 2700, 55, 65, 35, "/MonsterSprites/Pouch.png");
        pouch.addSkill(new Skill("Tackle", 40, "NORMAL"));
        pouch.addSkill(new Skill("Heavy Slam", 65, "NORMAL"));
        pouch.addSkill(new Skill("Body Press", 75, "GROUND"));
        pouch.addSkill(new Skill("Mega Bump", 85, "NORMAL"));
        addMonster(pouch, tierCMonsters);

        Monster sparchu = new Monster("Sparchu", "ELECTRIC", 2500, 75, 45, 100, "/MonsterSprites/Sparchu.png");
        sparchu.addSkill(new Skill("Spark", 55, "ELECTRIC", true));
        sparchu.addSkill(new Skill("Thunderbolt", 90, "ELECTRIC", true));
        sparchu.addSkill(new Skill("Volt Rush", 70, "ELECTRIC"));
        sparchu.addSkill(new Skill("Quick Strike", 45, "NORMAL"));
        addMonster(sparchu, tierSMonsters);
    }

    private void addMonster(Monster monster, ArrayList<Monster> tier) {
        allMonsters.add(monster);
        tier.add(monster);
    }

    // Hàm lấy Monster theo tên
    public Monster getMonsterByName(String name) {
        for (Monster m : allMonsters) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        System.out.println("Lỗi: Không tìm thấy " + name + " trong MonsterHandler!");
        Monster fallback = new Monster("MissingNo", "NORMAL", 50, 10, 10, 10, "/MonsterSprites/Atrox.png");
        fallback.addSkill(new Skill("Struggle", 50, "NORMAL"));
        fallback.addSkill(new Skill("Tackle", 40, "NORMAL"));
        fallback.addSkill(new Skill("Glitch", 60, "DRAGON", true));
        fallback.addSkill(new Skill("Error Slam", 70, "NORMAL"));
        return fallback;
    }

    // Getters cho các danh sách Tier
    public ArrayList<Monster> getAllMonsters() { return allMonsters; }
    public ArrayList<Monster> getTierCMonsters() { return tierCMonsters; }
    public ArrayList<Monster> getTierBMonsters() { return tierBMonsters; }
    public ArrayList<Monster> getTierAMonsters() { return tierAMonsters; }
    public ArrayList<Monster> getTierSMonsters() { return tierSMonsters; }
}