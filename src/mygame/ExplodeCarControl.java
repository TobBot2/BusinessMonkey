/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * Control for cars that will take damage and get destroyed after being depleted of health
 * Inaccurate name now. It's been changed to kill the monkeys.
 * 
 * @author Trevor Black & Liam Finn & Samuel Muzac
 */
public class ExplodeCarControl extends AbstractControl {
    
    float health = 1;
    private Vector3f playerPosition;
    private float speed = 2.0f; // Movement speed
    private int attackDamage = 2;
    private float attackCooldown = 2.0f; // Time between attacks
    private float attackTimer = 0;
    
    PlayerHealth playerHealth;
    
    public ExplodeCarControl(PlayerHealth pH, Vector3f position) {
        this.playerHealth = pH;
        this.playerPosition = position;
    }
    
    
    // intensity of rotation on hit
    float rotateOnHitAmount = .2f;
    
    // reduce health by damage, and destroy if it's health is too low
    public void damage(float amount) {
//        this.playerHealth.takeDamage(amount);
        health -= amount;
        
        // rotate randomly on hit
//        spatial.rotate(randAroundZero(rotateOnHitAmount),randAroundZero(rotateOnHitAmount),randAroundZero(rotateOnHitAmount));
        
        // after being hit too many times, destroy self
        if (playerHealth.getHp() <= 0) {
            playerHealth.onDeath();
//            spatial.removeFromParent();
        }
        if (health <= 0) {
            System.out.println("Enemy defeated");
            spatial.removeFromParent();
        }
    }
    
    // custom random function for outputting value [-intensity/2, intensity/2]
    private float randAroundZero(float intensity) { return (float)(Math.random() - .5f) * intensity; }

    //TODO: DOESN'T WORK YET
    @Override
    protected void controlUpdate(float tpf) {
        if (spatial != null) {
            // Move towards the player
            Vector3f direction = playerPosition.subtract(spatial.getWorldTranslation()).normalize();
            spatial.move(direction.mult(speed * tpf));

            // Check if close enough to attack
            float distance = spatial.getWorldTranslation().distance(playerPosition);
            if (distance < 1.5f) { // Attack range
                attackTimer += tpf;
                if (attackTimer >= attackCooldown) {
                    attackPlayer();
                    attackTimer = 0; // Reset attack timer
                }
            }
        }
    }
    
    private void attackPlayer() {
        playerHealth.takeDamage(attackDamage);
        System.out.println("Player takes " + attackDamage + " damage! HP: " + playerHealth.getHp());
    }
    

    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) { }
    
}
