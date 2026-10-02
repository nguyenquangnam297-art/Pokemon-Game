package Event;

import Entity.NPC.NPC;

public class DialogueEvent extends Event {
    private NPC npc;
    private int dialogueIndex;

    public DialogueEvent(NPC npc) {
        super();
        this.npc = npc;
    }
    public NPC getNpc () {
        return npc;
    }
    public String startDialogue() {
        start();

        if (npc.getDialogues().isEmpty()) {
            return "";
        }

        return npc.getDialogues().get(dialogueIndex);
    }
    public String nextDialogue() {
        dialogueIndex++;

        if (dialogueIndex >= npc.getDialogues().size()) {
            endDialogue();
            return "";
        }

        return npc.getDialogues().get(dialogueIndex);
    }
    public void endDialogue() {
        complete();
    }
}
