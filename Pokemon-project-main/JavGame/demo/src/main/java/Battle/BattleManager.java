package Battle;

import Battle.Skills.Skill;
import Battle.Skills.TypeChart;
import Data.Inventory;
import Data.InventorySlot;
import Entity.Monster;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleManager {

    public enum BattleState {
        CONTINUE,
        PLAYER_WIN,
        PLAYER_LOSE,
        FORCE_SWITCH,
        MONSTER_CAPTURED
    }

    private List<Monster> playerTeam;
    private Monster p1, p2;
    private Inventory inventory;
    private BattleEnvironment environment;
    private CaptureSystem captureSystem;
    private int p1Gauge = 0, p2Gauge = 0;
    private final int MAX_GAUGE = 1000;
    private Random rand = new Random();
    private int actionCount = 1;

    //Constructors dùng mặc định test
    public BattleManager(Monster p1, Monster p2) {
        this(p1, p2, BattleEnvironment.FOREST, null);
    }

    public BattleManager(Monster p1, Monster p2, BattleEnvironment environment) {
        this(p1, p2, environment, null);
    }

    public BattleManager(List<Monster> playerTeam, Monster p2) {
        this(playerTeam, p2, BattleEnvironment.FOREST, null);
    }

    public BattleManager(List<Monster> playerTeam, Monster p2, BattleEnvironment environment) {
        this(playerTeam, p2, environment, null);
    }

    // --- Constructors nhận túi đồ thật từ bên ngoài ---
    public BattleManager(Monster p1, Monster p2, BattleEnvironment environment, Inventory inventory) {
        this.playerTeam = new ArrayList<Monster>();
        Monster clonedP1 = cloneMonster(p1);
        this.playerTeam.add(clonedP1);
        this.p1 = clonedP1;
        this.p2 = cloneMonster(p2);
        this.captureSystem = new CaptureSystem();

        if (inventory != null) {
            this.inventory = inventory;
        } else {
            this.inventory = createDefaultTestInventory();
        }

        if (environment != null) {
            this.environment = environment;
        } else {
            this.environment = BattleEnvironment.FOREST;
        }
    }

    public BattleManager(List<Monster> playerTeam, Monster p2, BattleEnvironment environment, Inventory inventory) {
        this.playerTeam = new ArrayList<Monster>();
        if (playerTeam != null) {
            for (int i = 0; i < playerTeam.size(); i++) {
                Monster member = playerTeam.get(i);
                if (member != null) {
                    this.playerTeam.add(cloneMonster(member));
                }
            }
        }
        this.p1 = getFirstAliveMonster(this.playerTeam);
        this.p2 = cloneMonster(p2);
        this.captureSystem = new CaptureSystem();

        if (inventory != null) {
            this.inventory = inventory;
        } else {
            this.inventory = createDefaultTestInventory();
        }

        if (environment != null) {
            this.environment = environment;
        } else {
            this.environment = BattleEnvironment.FOREST;
        }
    }

    private Inventory createDefaultTestInventory() {
        List<InventorySlot> slots = new ArrayList<InventorySlot>();
        slots.add(new InventorySlot("pokeball", 3));
        slots.add(new InventorySlot("HEAL_POTION", 3));
        slots.add(new InventorySlot("ATK_BUFF", 3));
        slots.add(new InventorySlot("SPD_BUFF", 3));
        return new Inventory(slots);
    }

    private Monster cloneMonster(Monster source) {
        if (source == null) {
            return null;
        }

        Monster copy = new Monster(
            source.getName(),
            source.getType(),
            source.getMaxHp(),
            source.getAttack(),
            source.getDefense(),
            source.getSpeed(),
            source.getImagePath()
        );

        for (int i = 0; i < 4; i++) {
            Skill s = source.getSkill(i);
            if (s != null) {
                copy.addSkill(new Skill(s.getName(), s.getBasePower(), s.getType(), s.isRanged()));
            }
        }

        int missingHp = source.getMaxHp() - source.getCurrentHp();
        if (missingHp > 0) {
            copy.takeDamage(missingHp);
        }

        if (source.getAtkBuffTurns() > 0) {
            copy.applyAtkBuff(source.getAtkBuffTurns());
        }
        if (source.getSpdBuffTurns() > 0) {
            copy.applySpdBuff(source.getSpdBuffTurns());
        }

        return copy;
    }


    public boolean isBallItem(String itemId) {
        return BattleItemHandler.isBallItem(itemId);
    }

    public boolean isBattleUsableItem(String itemId) {
        return BattleItemHandler.isBattleUsableItem(itemId);
    }

    public int getTotalBallCount() {
        return BattleItemHandler.getTotalBallCount(inventory);
    }

    public int getTotalBattleItemCount() {
        return BattleItemHandler.getTotalBattleItemCount(inventory);
    }

    public List<InventorySlot> getAvailableBallSlots() {
        return BattleItemHandler.getAvailableBallSlots(inventory);
    }

    public List<InventorySlot> getAvailableBattleItemSlots() {
        return BattleItemHandler.getAvailableBattleItemSlots(inventory);
    }

    public String getDefaultBallId() {
        return BattleItemHandler.getDefaultBallId(inventory);
    }

    // --- Chuyển tiếp hệ thống bắt quái sang CaptureSystem ---

    public boolean canCaptureEnemy() {
        return captureSystem.canCaptureEnemy(p2);
    }

    public int calculateCaptureChance(String ballId) {
        return captureSystem.calculateCaptureChance(p2, actionCount, ballId);
    }

    public Boolean attemptCapture(String ballId) {
        return captureSystem.attemptCapture(p2, actionCount, inventory, ballId);
    }

    public void finishFailedCaptureTurn() {
        p1.tickBuffs();
        consumeP1Gauge();
        incrementActionCount();
    }

    public boolean isMonsterCaptured() {
        return captureSystem.isMonsterCaptured();
    }

    public String getCapturedMonsterName() {
        return captureSystem.getCapturedMonsterName();
    }

   

    public BattleEnvironment getEnvironment() {
        return environment;
    }

    public void setEnvironment(BattleEnvironment environment) {
        if (environment != null) {
            this.environment = environment;
        }
    }

    private Monster getFirstAliveMonster(List<Monster> team) {
        for (int i = 0; i < team.size(); i++) {
            if (!team.get(i).isFainted()) {
                return team.get(i);
            }
        }
        return team.get(0);
    }

    public boolean hasAliveMonsterInTeam() {
        for (int i = 0; i < playerTeam.size(); i++) {
            if (!playerTeam.get(i).isFainted()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSwitchableMonster() {
        for (int i = 0; i < playerTeam.size(); i++) {
            Monster m = playerTeam.get(i);
            if (m != p1 && !m.isFainted()) {
                return true;
            }
        }
        return false;
    }

    public List<Monster> getPlayerTeam() {
        return playerTeam;
    }

    public Monster getP1() {
        return p1;
    }

    public Monster getP2() {
        return p2;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setP1(Monster newP1) {
        this.p1 = newP1;
    }



    public boolean tick() {
        if (captureSystem.isMonsterCaptured() || p1.isFainted() || p2.isFainted()) {
            return false;
        }

        int p1Speed = p1.getSpeed();
        if (p1.getSpdBuffTurns() > 0) {
            p1Speed = (int) (p1Speed * 1.3);
        }
        p1Gauge += p1Speed;

        int p2Speed = p2.getSpeed();
        if (p2.getSpdBuffTurns() > 0) {
            p2Speed = (int) (p2Speed * 1.3);
        }
        p2Gauge += p2Speed;

        return p1Gauge >= MAX_GAUGE || p2Gauge >= MAX_GAUGE;
    }

    public boolean isP1Turn() {
        return p1Gauge >= p2Gauge;
    }

    public void consumeP1Gauge() {
        p1Gauge -= MAX_GAUGE;
    }

    public void consumeP2Gauge() {
        p2Gauge -= MAX_GAUGE;
    }

    public void resetP1Gauge() {
        this.p1Gauge = 0;
    }

    public int getActionCount() {
        return actionCount;
    }

    public void incrementActionCount() {
        actionCount++;
    }

    public Skill getRandomP2Skill() {
        int validSkills = 0;
        for (int i = 0; i < 4; i++) {
            if (p2.getSkill(i) != null) {
                validSkills++;
            }
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
        if (p1.getSpdBuffTurns() > 0) {
            p1Speed = (int) (p1Speed * 1.3);
        }

        int p2Speed = p2.getSpeed();
        if (p2.getSpdBuffTurns() > 0) {
            p2Speed = (int) (p2Speed * 1.3);
        }

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



    public String executeSkill(Monster attacker, Monster defender, Skill skill) {
        if (skill == null) {
            return "";
        }

        int damage = DamageCalculator.calculateDamage(attacker, defender, skill);
        defender.takeDamage(damage);

        String attackerName;
        if (attacker == p1) {
            attackerName = "Bạn";
        } else {
            attackerName = "Đối thủ";
        }
        String popupMsg = "Lượt " + actionCount + "\n" + attackerName + " dùng chiêu " + skill.getName() + "!";

        int effect = TypeChart.getEffectiveness(skill.getType(), defender.getType());
        if (effect == 1) {
            popupMsg += "\n(Siêu hiệu quả!)";
        } else if (effect == -1) {
            popupMsg += "\n(Không mấy hiệu quả...)";
        }

        incrementActionCount();
        return popupMsg;
    }

    public void finishPlayerSkillTurn() {
        p1.tickBuffs();
        consumeP1Gauge();
    }

    public void finishEnemySkillTurn() {
        p2.tickBuffs();
        consumeP2Gauge();
    }

    public String useItem(String itemId) {
        String msg = BattleItemHandler.useItem(p1, inventory, itemId);
        if (msg == null) {
            return null;
        }

        p1.tickBuffs();
        consumeP1Gauge();
        incrementActionCount();
        return msg;
    }

    public void completeSwitchTurn(Monster oldMonster, boolean forced) {
        if (forced) {
            resetP1Gauge();
        } else {
            oldMonster.tickBuffs();
            consumeP1Gauge();
            incrementActionCount();
        }
    }

    public BattleState evaluateBattleState() {
        if (captureSystem.isMonsterCaptured()) {
            return BattleState.MONSTER_CAPTURED;
        }
        if (p2.isFainted()) {
            return BattleState.PLAYER_WIN;
        }
        if (p1.isFainted()) {
            if (hasAliveMonsterInTeam()) {
                return BattleState.FORCE_SWITCH;
            }
            return BattleState.PLAYER_LOSE;
        }
        return BattleState.CONTINUE;
    }
}