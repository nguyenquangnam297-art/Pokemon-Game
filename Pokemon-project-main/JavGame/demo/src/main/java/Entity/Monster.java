package Entity;

import java.util.ArrayList;
import Battle.Skills.Skill;

public class Monster extends Entity {
    private String name;
    private int level;

    private int maxHP;
    private int currentHP;

    private int attack;
    private int defense;
    private int battleSpeed;
    private ArrayList<Skill> skills;

    public Monster(String name, int level, int maxHP, int currentHP, int attack, int defense, int battleSpeed) {
        this.name = name;
        this.level = level;
        this.maxHP = maxHP;
        this.currentHP = currentHP;
        if (currentHP < 0) {
            this.currentHP = 0;
        } else if (currentHP > maxHP) {
            this.currentHP = maxHP;
        } else {
            this.currentHP = currentHP;
        }
        this.attack = attack;
        this.defense = defense;
        this.battleSpeed = battleSpeed;
        skills = new ArrayList<>();
    }
    public void addSkill(Skill skill) {
        skills.add(skill);
    }
    public ArrayList<Skill> getSkills() {
        return skills;
    }
    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getBattleSpeed() {
        return battleSpeed;
    }
    public void takeDamage(int damage) {
        if(damage < 0) return;
        currentHP -= damage;
        if(currentHP < 0) {
            currentHP = 0;
        }
    }
    public void heal(int amount) {
        if (amount < 0) return;
        currentHP += amount;
        if (currentHP > maxHP) {
            currentHP = maxHP;
        }
    }
    public boolean isAlive() {
        return currentHP > 0;
    }
}
