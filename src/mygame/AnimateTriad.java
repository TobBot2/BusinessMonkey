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
import java.util.List;
import java.util.ArrayList;


/**
 * Main class of Business Monkey game
 * 
 * @author Trevor Black & Liam Finn & Samuel Muzac
 */
public class AnimateTriad {
//    
//    private AnimControl control;
//    private AnimChannel channel;
//   
    
    private List<AnimControl> controls = new ArrayList<>();
    private List<AnimChannel> channels = new ArrayList<>();
    private List<Spatial> triad = new ArrayList<>();
    private AssetManager assetManager;
    


    public AnimateTriad(AssetManager aM) {
        this.assetManager = aM;
       
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
        }
    }

    // Method to play a specific animation
    public void playAnimationAll(String animationName, float speed) {
        for (AnimChannel channel: channels) {
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
        for (AnimChannel channel: channels) {
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
