package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Trevor Black & Liam Finn
 */
public class CoinPickupControl extends AbstractControl {
    
    int worth = 5;

    public int pickup() {
        spatial.removeFromParent();
                
        return worth;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        spatial.rotate(0, 0.05f, 0);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) { }
    
}
