package Entity.NPC;

import java.util.ArrayList;

import Entity.Monster;

public class TrainerNPC extends NPC {
    private ArrayList<Monster> monsters;
    public TrainerNPC(String name,ArrayList<Monster> monsters, String ... dialogues) {
        super(name,"trainer", dialogues);
        this.monsters = new ArrayList<>();
        this.monsters.addAll(monsters);
    }
    public ArrayList<Monster> getMonsters() {
        return monsters;
    }
    public void challengePlayer() {}
}
