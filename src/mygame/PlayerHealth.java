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
    private Spatial player;
    
    public PlayerHealth(float hp, Spatial spatial) {
        this.hp = hp;
        this.player = spatial;
    }
    
    public float getHp() {
        return hp;
    }
    
    public void takeDamage(float damage) {
        hp -= damage;
        if (hp <= 0) {
            hp = 0;
            onDeath();
        }
    }
    
    protected void onDeath() {
        player.removeFromParent();
    }
    
    
    
}
