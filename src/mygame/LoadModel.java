package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.asset.AssetManager;

/**
 * LoadModel class of Business Monkey game
 * 
 * @author Trevor Black & Liam Finn
 */
public class LoadModel {
    
    private final AssetManager assetManager;
    
    public LoadModel(AssetManager assetManager) {
        this.assetManager = assetManager;
    }
    
    public Spatial load(String modelPath) {
        Spatial model = assetManager.loadModel(modelPath);
        return model;
    }
}
    
    
    
//    public LoadModel() {
//        
//    }
//
//    public static void main(String[] args) {
//        LoadModel app = new LoadModel();
//        
//        // set app settings
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
//    }
//
//    @Override
//    public void simpleInitApp() {
//        Spatial mymodel = assetManager.loadModel(
//            "Textures/MonkeyUser/Jaime.j3o");
//        rootNode.attachChild(mymodel);
//        DirectionalLight sun = new DirectionalLight();
//        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)));
//        sun.setColor(ColorRGBA.White);
//        rootNode.addLight(sun);
//    }
//
//    @Override
//    public void simpleUpdate(float tpf) { }
//
//    @Override
//    public void simpleRender(RenderManager rm) { }


