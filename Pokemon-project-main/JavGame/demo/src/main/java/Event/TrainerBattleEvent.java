package Event;

import Entity.NPC.TrainerNPC;

public class TrainerBattleEvent extends Event {
    private TrainerNPC trainerNPC;

    public TrainerBattleEvent(TrainerNPC trainerNPC) {
        super();
        this.trainerNPC = trainerNPC;
    }

    public TrainerNPC getTrainerNPC() {
        return trainerNPC;
    }
    public void startBatlle() {
        start();
        //hoạt động của classBattle
    }
    public void endBattle() {
        complete();
        //hoạt động của classBattle
    }
}
