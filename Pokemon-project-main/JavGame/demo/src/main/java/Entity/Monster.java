package Entity;
import java.util.ArrayList;

import Battle.Skills.Skill;

public class Monster {
    private String name, type;
    private int maxHp, currentHp, attack, defense, speed;
    private ArrayList<Skill> moves = new ArrayList<>();
    private String imagePath;
    
    // Biến trạng thái tạm thời (Buffs)
    private int atkBuffTurns = 0;
    private int spdBuffTurns = 0;
    
    public Monster(String name, String type, int maxHp, int attack, int defense, int speed, String imagePath) {
        this.name = name; 
        this.type = type; 
        this.maxHp = maxHp;
        this.currentHp = maxHp; 
        this.attack = attack; 
        this.defense = defense; 
        this.speed = speed;
        this.imagePath = imagePath;
        
        // Mặc định luôn có đòn đánh thường
        this.addSkill(new Skill("Tackle", 40, "NORMAL"));
    }

    public void addSkill(Skill move) {
        if (moves.size() < 4) {
            moves.add(move);
        }
    }

    // Đã thay thế toán tử 3 ngôi (? :) bằng if-else
    public Skill getSkill(int i) {
        if (i >= 0 && i < moves.size()) {
            return moves.get(i);
        } else {
            return null;
        }
    }

    public void takeDamage(int damage) {
        this.currentHp = Math.max(0, this.currentHp - damage);
    }
    
    public void heal(int amount) {
        this.currentHp += amount;
        if (this.currentHp > this.maxHp) {
            this.currentHp = this.maxHp;
        }
    }

    public boolean isFainted() { 
        return currentHp <= 0; 
    }
    
    // Logic đếm lùi thời gian Buff
    public void tickBuffs() {
        if (atkBuffTurns > 0) { atkBuffTurns--; }
        if (spdBuffTurns > 0) { spdBuffTurns--; }
    }
    
    public int getAtkBuffTurns() { return atkBuffTurns; }
    public void applyAtkBuff(int turns) { this.atkBuffTurns = turns; }
    
    public int getSpdBuffTurns() { return spdBuffTurns; }
    public void applySpdBuff(int turns) { this.spdBuffTurns = turns; }
    
    // Getters
    public String getName() { return name; }
    public String getType() { return type; }
    public int getMaxHp() { return maxHp; }
    public int getCurrentHp() { return currentHp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getSpeed() { return speed; }
    public String getImagePath() { return imagePath; }

    public Monster copyForBattle() {
        Monster copy = new Monster(name, type, maxHp, attack, defense, speed, imagePath);
        copy.moves.clear();
        for (Skill skill : moves) {
            copy.addSkill(new Skill(skill.getName(), skill.getBasePower(), skill.getType()));
        }
        return copy;
    }
}