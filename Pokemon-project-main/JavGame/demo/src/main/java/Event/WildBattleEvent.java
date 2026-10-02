package Event;

import Entity.Monster;

public class WildBattleEvent extends Event {
    private Monster wildMonster;

    public WildBattleEvent(Monster wildMonster) {
        super();
        this.wildMonster = wildMonster;
    }

    public Monster getWildMonster() {
        return wildMonster;
    }
    public void startBattle() {
        start();
        System.out.println("Wild " + wildMonster.getName() + " in combat");
        //hoạt động của battleClass
    }

    public void endBattle() {
        complete();
        //hoạt động của battleClass
    }
}
