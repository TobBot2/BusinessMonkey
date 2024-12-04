package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * Control for coin objects that will be picked up by the player
 * 
 * @author Trevor Black & Liam Finn & Samuel Muzac
 */
public class CoinPickupControl extends AbstractControl {
    
    final int worth = 1;
    private final AudioNode coinPickupSound;
    
    public CoinPickupControl(AssetManager assetManager) {
        // Load the coin pickup sound
        coinPickupSound = new AudioNode(assetManager, "Sounds/coin_pickup.wav", AudioData.DataType.Buffer);
        coinPickupSound.setPositional(false); // Non-positional since it's a UI-like sound
        coinPickupSound.setLooping(false);    // Ensure it plays once per pickup
        coinPickupSound.setVolume(1);        // Adjust volume if needed
    }

    public int pickup() {
        if (coinPickupSound != null) { coinPickupSound.playInstance();}
        spatial.removeFromParent();    
        return worth;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        spatial.rotate(0, 0.05f, 0);  // spin coin
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) { }
}
