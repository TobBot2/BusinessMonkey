package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

/**
 * Main class of Business Monkey game
 * 
 * @author Trevor Black & Liam Finn & Samuel Muzac
 */
public class AnimateModel {
    
    private AnimControl control;
    private AnimChannel channel;
    private Node player;

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
        if (channel != null) {
            channel.reset(true);
        }
    }

    // Method to get the model (if needed for attaching to the scene)
    public Spatial getModel() {
        return control.getSpatial();
    }

    public Node getPlayer() {
        return player;
    }
}



//    public static void main(String[] args) {
//        AnimateModel app = new AnimateModel();
//        
        // set app settings
//        AppSettings settings = new AppSettings(true);
//        settings.setTitle("Business Monkey.");
//        settings.setResolution(1600,1000);
//        
//        //app.setDisplayFps(false);
//        //app.setDisplayStatView(false);
//        app.setSettings(settings);
//        app.setShowSettings(false);
//        
//        app.start();
    

//    @Override
//    public void simpleInitApp() {
//        player = (Node)
//    }
//
//    @Override
//    public void simpleUpdate(float tpf) { }
//
//    @Override
//    public void simpleRender(RenderManager rm) { }
//}
