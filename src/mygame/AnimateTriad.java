package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

import java.util.ArrayList;
import java.util.List;


/**
 * Main class of Business Monkey game
 * 
 * @author Trevor Black & Liam Finn & Samuel Muzac
 */

public class AnimateTriad {
    

    private List<AnimControl> controls = new ArrayList<>();
    private List<AnimChannel> channels = new ArrayList<>();
    private List<Spatial> triad = new ArrayList<>();
    private AssetManager assetManager;
    private Vector3f playerPosition;

    public AnimateTriad(AssetManager aM) {
        this.assetManager = aM;
    }

    public void setPlayerPosition(Vector3f playerPosition) {
        this.playerPosition = playerPosition;
    }

    public void createInstance(Spatial modelCurrent) {
        Spatial model = modelCurrent;
        AnimControl control = model.getControl(AnimControl.class);

        if (control != null) {
            // Create a new animation channel
            AnimChannel channel = control.createChannel();
            triad.add(model);
            controls.add(control);
            channels.add(channel);

            // Add movement control
            model.addControl(new MoveTowardsPlayerControl(playerPosition));
        }
    }

    public void playAnimationAll(String animationName, float speed) {
        for (AnimChannel channel : channels) {
            if (channel != null) {
                channel.setAnim(animationName);
                channel.setSpeed(speed);
            }
        }
    }

    public void playAnimationSingle(int index, String animationName, float speed) {
        if (index >= 0 && index < channels.size()) {
            AnimChannel channel = channels.get(index);
            if (channel != null) {
                channel.setAnim(animationName);
                channel.setSpeed(speed);
            }
        }
    }

    public void stopAnimationAll() {
        for (AnimChannel channel : channels) {
            if (channel != null) {
                channel.reset(true);
            }
        }
    }

    public void stopAnimationSingle(int index) {
        if (index >= 0 && index < channels.size()) {
            AnimChannel channel = channels.get(index);
            if (channel != null) {
                channel.reset(true);
            }
        }
    }

    public List<Spatial> getTriadMonkeys() {
        return triad;
    }

    private static class MoveTowardsPlayerControl extends AbstractControl {
        private Vector3f target;
        private float speed = 2.0f;

        public MoveTowardsPlayerControl(Vector3f targetPosition) {
            this.target = targetPosition;
        }

        @Override
        protected void controlUpdate(float tpf) {
            if (spatial != null && target != null) {
                // Calculate the direction towards the target
                Vector3f currentPos = spatial.getWorldTranslation();
                Vector3f direction = target.subtract(currentPos).normalize();

                // Move the spatial towards the target
                spatial.move(direction.mult(speed * tpf));
            }
        }

        @Override
        protected void controlRender(com.jme3.renderer.RenderManager rm, com.jme3.renderer.ViewPort vp) {
            // Not used in this example
        }

        public void setTarget(Vector3f target) {
            this.target = target;
        }
    }
}
