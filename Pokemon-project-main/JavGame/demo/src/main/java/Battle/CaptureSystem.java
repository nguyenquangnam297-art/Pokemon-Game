package Battle;

import Data.Inventory;
import Data.InventorySlot;
import Entity.Monster;
import java.util.Random;

public class CaptureSystem {

    private Random rand = new Random();
    private boolean monsterCaptured = false;
    private String capturedMonsterName = null;

    public boolean canCaptureEnemy(Monster p2) {
        if (p2 == null || p2.isFainted() || p2.getMaxHp() <= 0) {
            return false;
        }
        return p2.getCurrentHp() * 2 < p2.getMaxHp();
    }

    public int calculateCaptureChance(Monster p2, int actionCount, String ballId) {
        if (!canCaptureEnemy(p2)) {
            return 0;
        }

        double currentHpRatio = (double) p2.getCurrentHp() / p2.getMaxHp();
        double halfMaxHpRatio = 0.5;

        double hpWeaknessFactor = (halfMaxHpRatio - currentHpRatio) / halfMaxHpRatio;
        if (hpWeaknessFactor < 0.0) {
            hpWeaknessFactor = 0.0;
        }

        double turnBonus = Math.min(actionCount * 2.0, 25.0);

        double ballMultiplier = 1.0;
        if (ballId != null) {
            String normalized = ballId.trim().toLowerCase();
            if (normalized.equals("great ball")) {
                ballMultiplier = 1.25;
            } else if (normalized.equals("ultra ball")) {
                ballMultiplier = 1.5;
            }
        }

        double rawChance = (15.0 + (hpWeaknessFactor * 55.0) + turnBonus) * ballMultiplier;
        return (int) Math.round(Math.max(10.0, Math.min(95.0, rawChance)));
    }

    public Boolean attemptCapture(Monster p2, int actionCount, Inventory inventory, String ballId) {
        if (!canCaptureEnemy(p2)) {
            return null;
        }

        InventorySlot slot = BattleItemHandler.findSlotIgnoreCase(inventory, ballId);
        if (slot == null) {
            return null;
        }

        int chance = calculateCaptureChance(p2, actionCount, slot.itemId);
        if (!inventory.useItem(slot.itemId)) {
            return null;
        }

        int roll = rand.nextInt(100) + 1;
        boolean success = roll <= chance;

        if (success) {
            this.monsterCaptured = true;
            this.capturedMonsterName = p2.getName();
        }
        return Boolean.valueOf(success);
    }

    public boolean isMonsterCaptured() {
        return monsterCaptured;
    }

    public String getCapturedMonsterName() {
        return capturedMonsterName;
    }
}