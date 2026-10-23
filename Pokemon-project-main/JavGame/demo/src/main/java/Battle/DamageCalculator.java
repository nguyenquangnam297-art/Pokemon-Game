package Battle;
import Entity.Monster;
import Battle.Skills.*;

public class DamageCalculator {
    public static int calculateDamage(Monster attacker, Monster defender, Skill move) {
        if (move == null) {
            return 0;
        }
        int actualAtk = attacker.getAttack();
        // Áp dụng Buff Tấn công (tăng 20% sát thương)
        if (attacker.getAtkBuffTurns() > 0) {
            actualAtk = (int) (actualAtk * 1.2);
        }
        int actualDef = defender.getDefense();
        int effect = TypeChart.getEffectiveness(move.getType(), defender.getType());
        
        if (effect == 1) {
            actualAtk *= 2;
        } else if (effect == -1) {
            actualDef *= 2;
        }
        double delta = actualAtk - actualDef;
        double c = 50.0;
        double multiplier = 0.0;
        // Đã thay thế toán tử 3 ngôi (? :) bằng if-else
        if (delta >= 0) {
            multiplier = 1.0 + (delta / (delta + c)) * 2.0;
        } else {
            multiplier = c / (c - delta);
        }
        int damage = (int) (move.getBasePower() * multiplier * 5.0);
        if (damage < 1) {
            damage = 1;
        }
        return damage;
    }
}