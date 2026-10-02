package Event;

import java.util.ArrayList;
import java.util.Random;

import Entity.Monster;
import Entity.Player;

public class GrassEvent extends Event {
    private int x; // x của góc bên trên trái
    private int y; // y của góc bên trên trái
    private int width;
    private int height;
    private double encounterRate;
    private ArrayList<Monster> wildMonsters;

    //Khai bao tam thoi, phai bo
    private int encounterCooldown = 0;
    private WildBattleEvent currentWildBattle = null;
    private Player player = null;

    public GrassEvent(int x, int y, int width, int height, double encounterRate,  ArrayList<Monster> wildMonsters) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.encounterRate = encounterRate;
        this.wildMonsters = new ArrayList<>();
        this.wildMonsters.addAll(wildMonsters);
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getEncounterRate() {
        return encounterRate;
    }
    public boolean isPlayerInGrass(Player player) {
        if (player == null) {
            return false;
        }
        return player.x >= x &&
                player.x <= x + width &&
                player.y >= y &&
                player.y <= y + height;
    }

    //temp
    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean shouldTriggerEncounter() {
        return Math.random() < encounterRate;
    }

    public Monster getRandomMonster() {
        if (wildMonsters.isEmpty()) {
            return null;
        }
        Random rd = new Random();
        int i = rd.nextInt(wildMonsters.size());
        return wildMonsters.get(i);
    }

    

    public void update() {
        if (player == null) {
            return;
        }

        // Giảm thời gian cooldown
        if (encounterCooldown > 0) {
            encounterCooldown--;
        }
        // Nếu trận battle đã kết thúc thì cho phép tạo trận mới
        if (currentWildBattle != null && currentWildBattle.isCompleted()) {
            currentWildBattle = null;
        }
        // Nếu chưa có trận battle
        if (currentWildBattle == null) {

            // Kiểm tra Player có ở trong cỏ không
            if (this.isPlayerInGrass(player)) {

                // Chỉ kiểm tra encounter khi cooldown = 0
                if (encounterCooldown == 0) {

                    // Có gặp Pokémon hay không?
                    if (this.shouldTriggerEncounter()) {

                        // Chọn Pokémon ngẫu nhiên
                        Monster monster = this.getRandomMonster();

                        if (monster != null) {

                            // Tạo WildBattleEvent
                            currentWildBattle = new WildBattleEvent(monster);

                            // Bắt đầu event
                            currentWildBattle.startBattle();

                            // In ra để kiểm tra
                            System.out.println(
                                    "Wild "
                                            + monster.getName()
                                            + " appeared!"
                            );

                            // Chờ khoảng 1 giây
                            encounterCooldown = 60;
                        }
                    }
                }
            }
        }
    }
}
