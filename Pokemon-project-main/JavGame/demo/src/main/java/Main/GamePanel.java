package Main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import Entity.MonsterHandler;
import Entity.NPC.NPCHandler;
import Entity.Player;
import Event.GrassEvent;
import Map.TileHandler;

public class GamePanel extends JPanel implements Runnable {
    
    final int originalTileSize = 16;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; // 48x48
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    final int screenHeight = tileSize * maxScreenRow; // 576 pixels


    int FPS = 60;

    KeyHandler keyH = new KeyHandler();
    TileHandler tileH = new TileHandler(this);
    Thread gameThread;
    Player player = new Player(this, keyH);
    MonsterHandler monsterHandler = new MonsterHandler();
    public NPCHandler npcHandler = new NPCHandler(player, keyH, monsterHandler);
    //vung co
    GrassEvent grassEvent = new GrassEvent(
            200,
            200,
            150,
            100,
            1,
            monsterHandler.getAllMonsters()
    );

    public GamePanel(){

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        //temp
        grassEvent.setPlayer(player);
    }
    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }
    @Override
    public void run() {
        double drawInterval = 1000000000/FPS; // 0.01666 seconds
        double delta = 0;
        double lastTime = System.nanoTime();
        double curTime;
        while(gameThread != null){
            curTime = System.nanoTime();
            delta += (curTime - lastTime) / drawInterval;
            if(delta >= 1){
                update();
                repaint();
                delta--;
            }
            lastTime = curTime;
        }
    }
    public void update(){
        player.update();
        npcHandler.update();
        //temp
        grassEvent.update();
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        tileH.draw(g2);

        //temp
        g2.setColor(Color.GREEN);
        g2.fillRect(
            grassEvent.getX(),
            grassEvent.getY(),
            grassEvent.getWidth(),
            grassEvent.getHeight()
        );


        player.draw(g2);
        npcHandler.draw(g2, tileSize);
        g2.dispose();
    }
}
