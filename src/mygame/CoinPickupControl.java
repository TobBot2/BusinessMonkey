package mygame;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * Control for coin objects that will be picked up by the player
 * 
 * @author Trevor Black & Liam Finn & Samuel Muzac
 */
public class CoinPickupControl extends AbstractControl implements PhysicsCollisionListener {
    
    int worth = 5;

    public int pickup() {
        spatial.removeFromParent();
                
        return worth;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        // spin coin
        spatial.rotate(0, 0.05f, 0);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) { }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        if (event.getNodeB().getName().equals("Player") || event.getNodeA().getName().equals("Player")) {
            spatial.removeFromParent();
        }
    }
}
