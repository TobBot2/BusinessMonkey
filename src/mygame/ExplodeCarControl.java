/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Trevor Black & Liam Finn
 */
public class ExplodeCarControl extends AbstractControl {
    
    float health = 50;
    
    float rotateOnHitAmount = .2f;
    
    public void damage(float amount) {
        health -= amount;        
        
        // rotate randomly on hit
        spatial.rotate(randomAngle(rotateOnHitAmount),randomAngle(rotateOnHitAmount),randomAngle(rotateOnHitAmount));
        
        // after being hit too many times, destroy self
        if (health <= 0) {
            spatial.removeFromParent();
        }
    }
    
    private float randomAngle(float intensity) { return (float)(Math.random() - .5f) * intensity; }

    @Override
    protected void controlUpdate(float tpf) { }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) { }
    
}
