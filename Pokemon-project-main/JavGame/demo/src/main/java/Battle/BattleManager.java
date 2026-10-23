package Battle;

import java.util.Random;

import Battle.Skills.Skill;
import Entity.Monster;

public class BattleManager {
    private Monster p1, p2;
    private int p1Gauge = 0, p2Gauge = 0;
    private final int MAX_GAUGE = 1000;
    private Random rand = new Random();
    private int actionCount = 1;

    public BattleManager(Monster p1, Monster p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public boolean tick() {
        if (p1.isFainted() || p2.isFainted()) {
            return false;
        }
        
        // Tính toán tốc độ thực tế (Có tính cả Buff)
        int p1Speed = p1.getSpeed();
        if (p1.getSpdBuffTurns() > 0) { p1Speed = (int) (p1Speed * 1.3); }
        p1Gauge += p1Speed;
        
        int p2Speed = p2.getSpeed();
        if (p2.getSpdBuffTurns() > 0) { p2Speed = (int) (p2Speed * 1.3); }
        p2Gauge += p2Speed;
        
        if (p1Gauge >= MAX_GAUGE || p2Gauge >= MAX_GAUGE) {
            return true; 
        }
        return false;
    }

    public boolean isP1Turn() { return p1Gauge >= p2Gauge; }
    public void consumeP1Gauge() { p1Gauge -= MAX_GAUGE; }
    public void consumeP2Gauge() { p2Gauge -= MAX_GAUGE; }
    public int getActionCount() { return actionCount; }
    public void incrementActionCount() { actionCount++; }

    public Skill getRandomP2Skill() {
        int validSkills = 0;
        for (int i = 0; i < 4; i++) {
            if (p2.getSkill(i) != null) validSkills++;
        }
        
        int randomLimit;
        if (validSkills > 0) {
            randomLimit = validSkills;
        } else {
            randomLimit = 1;
        }
        return p2.getSkill(rand.nextInt(randomLimit));
    }

    public Monster[] getNextTurns(int count) {
        Monster[] turns = new Monster[count];
        int simP1Gauge = p1Gauge;
        int simP2Gauge = p2Gauge;
        
        int p1Speed = p1.getSpeed();
        if (p1.getSpdBuffTurns() > 0) { p1Speed = (int) (p1Speed * 1.3); }
        
        int p2Speed = p2.getSpeed();
        if (p2.getSpdBuffTurns() > 0) { p2Speed = (int) (p2Speed * 1.3); }
        
        for (int i = 0; i < count; i++) {
            while (simP1Gauge < MAX_GAUGE && simP2Gauge < MAX_GAUGE) {
                simP1Gauge += p1Speed;
                simP2Gauge += p2Speed;
            }
            
            if (simP1Gauge >= simP2Gauge) {
                turns[i] = p1;
                simP1Gauge -= MAX_GAUGE;
            } else {
                turns[i] = p2;
                simP2Gauge -= MAX_GAUGE;
            }
        }
        return turns;
    }
}