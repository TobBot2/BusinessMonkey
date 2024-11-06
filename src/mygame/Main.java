package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;

/**
 * Main class of Business Monkey game
 * 
 * @author Trevor Black & Liam Finn
 */
public class Main extends SimpleApplication {
    
    private AnimateTriad animateModel;

    public static void main(String[] args) {
        Main app = new Main();
        
        // set app settings
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Business Monkey.");
        settings.setResolution(1600,1000);
        
        //app.setDisplayFps(false);
        //app.setDisplayStatView(false);
        app.setSettings(settings);
        app.setShowSettings(false);
        
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // remove debug key mappings
        inputManager.deleteMapping(INPUT_MAPPING_CAMERA_POS); // Key_C
        inputManager.deleteMapping(INPUT_MAPPING_MEMORY); // Key_M
        
       
        
        // initialize main app state
        GameRunningAppState gameRunningAppState = new GameRunningAppState();
        stateManager.attach(gameRunningAppState);
        
        
        
        createAndAnimateModels();
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)));
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
        
        
        
    }
    
    private void createAndAnimateModels() {
        loadMoneyTriad();
        
    }
    
    private void loadMoneyTriad() {
        //Monkey Enemies
        for (int i = 0; i < 3; i++) {
            
            LoadModel loadModel = new LoadModel(assetManager);
            Spatial mymodel = loadModel.load(
                "Textures/MonkeyEnemy/Jaime.j3o");
            animateModel = new AnimateTriad(assetManager);
            animateModel.createInstance(mymodel);

            
            
            mymodel.setLocalTranslation(i + 20, 0, 0);
            mymodel.setLocalScale(1f);
            mymodel.rotate(0, (float) Math.PI, 0);
           
            
            rootNode.attachChild(mymodel);

            animateModel.playAnimationAll("Idle", 1.0f);

        }  
        
    }

    @Override
    public void simpleUpdate(float tpf) { }

    @Override
    public void simpleRender(RenderManager rm) { }

    

    
}
