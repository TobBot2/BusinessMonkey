package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author Trevor Black & Liam Finn
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        
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
        inputManager.deleteMapping(INPUT_MAPPING_CAMERA_POS); // Key_C
        inputManager.deleteMapping(INPUT_MAPPING_MEMORY); // Key_M
        
        GameRunningAppState gameRunningAppState = new GameRunningAppState();
        stateManager.attach(gameRunningAppState);
    }

    @Override
    public void simpleUpdate(float tpf) { }

    @Override
    public void simpleRender(RenderManager rm) { }
}
