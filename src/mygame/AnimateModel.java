package mygame;

import com.jme3.scene.Spatial;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.scene.Node;

/**
 * Main class of Business Monkey game
 * 
 * @author Trevor Black & Liam Finn & Samuel Muzac
 */
public class AnimateModel {
    
    private AnimControl control;
    private AnimChannel channel;
    private final Node player;

    public AnimateModel(Spatial modelCurrent) {
        Spatial model = modelCurrent;
        player = (Node) model;
        control = model.getControl(AnimControl.class);

        if (control != null) {
            // Create a new animation channel
            channel = control.createChannel();
        }
    }

    // Method to play a specific animation
    public void playAnimation(String animationName, float speed) {
        if (channel != null) {
            channel.setAnim(animationName);
            channel.setSpeed(speed);
        }
    }

    public void stopAnimation() {
        if (channel != null) channel.reset(true);
    }

    // Method to get the model (if needed for attaching to the scene)
    public Spatial getModel() { return control.getSpatial(); }

    public Node getPlayer() { return player; }
}