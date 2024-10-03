/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * Control for cars that will take damage and get destroyed after being depleted of health
 * 
 * @author Trevor Black & Liam Finn
 */
public class ExplodeCarControl extends AbstractControl {
    
    float health = 50;
    
    // intensity of rotation on hit
    float rotateOnHitAmount = .2f;
    
    // reduce health by damage, and destroy if it's health is too low
    public void damage(float amount) {
        health -= amount;        
        
        // rotate randomly on hit
        spatial.rotate(randAroundZero(rotateOnHitAmount),randAroundZero(rotateOnHitAmount),randAroundZero(rotateOnHitAmount));
        
        // after being hit too many times, destroy self
        if (health <= 0) {
            spatial.removeFromParent();
        }
    }
    
    // custom random function for outputting value [-intensity/2, intensity/2]
    private float randAroundZero(float intensity) { return (float)(Math.random() - .5f) * intensity; }

    @Override
    protected void controlUpdate(float tpf) { }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) { }
    
}
