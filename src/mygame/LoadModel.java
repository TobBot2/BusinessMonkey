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
    
    
   