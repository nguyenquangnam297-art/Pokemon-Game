package Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    //TuanDepTrai
    public boolean fPressed;
    @Override
    public void keyTyped(java.awt.event.KeyEvent e) {
        // Not used, but cannot remove
    }

    @Override
    public void keyPressed(java.awt.event.KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_W){
            upPressed = true;
        }
        if(code == KeyEvent.VK_A){
            leftPressed = true;
        }
        if(code == KeyEvent.VK_S){
            downPressed = true;
        }
        if(code == KeyEvent.VK_D){
            rightPressed = true;
        }
        //TuanDepTrai
        if(code == KeyEvent.VK_F){
            fPressed = true;
        }
    }

    @Override
    public void keyReleased(java.awt.event.KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_W){
            upPressed = false;
        }
        if(code == KeyEvent.VK_A){
            leftPressed = false;
        }
        if(code == KeyEvent.VK_S){
            downPressed = false;
        }
        if(code == KeyEvent.VK_D){
            rightPressed = false;
        }
        //TuanDepTrai
        if(code == KeyEvent.VK_F){
            fPressed = false;
        }
    }
    
}
