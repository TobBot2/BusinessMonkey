/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.scene.Spatial;

/**
 *
 * @author slmuz
 */
public class PlayerHealth {
    private float hp;
    private final Spatial player;
    private final GameRunningAppState mas;
    
    public PlayerHealth(float hp, Spatial spatial, GameRunningAppState mastate) {
        this.hp = hp;
        this.player = spatial;
        this.mas = mastate;
    }
    
    public float getHp() {
        return hp;
    }
    
    public void takeDamage(int damage) {
//        hp -= damage;
        mas.playerTakeDamage(damage);
//        if (hp <= 0) {
//            hp = 0;
//            onDeath();
//        }
    }
    
    protected void onDeath() {
        player.removeFromParent();
    }
    
    
    
}
