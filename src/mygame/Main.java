package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;

/**
 * Main class of Business Monkey game
 * 
 * @author Trevor Black & Liam Finn & Samuel Muzac
 */
public class Main extends SimpleApplication {
        
    private GameRunningAppState gs;
    private StartMenu startMenu;
    private WinScreenState winScreen;
    private LoseScreenState loseScreen;
    
    public static void main(String[] args) {
        Main app = new Main();
        
        // set app settings
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Business Monkey.");
        settings.setResolution(1200,750);
        
        //app.setDisplayFps(false);
        //app.setDisplayStatView(false);
        app.setSettings(settings);
        app.setShowSettings(false);
        
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // remove debug key mappings
        inputManager.clearMappings();
        inputManager.addMapping("Quit", new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addListener(actionListener, "Quit");

                
        startMenu = new StartMenu(this);
        stateManager.attach(startMenu);  // Pass the Main instance
        
//        DirectionalLight sun = new DirectionalLight();
//        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)));
//        sun.setColor(ColorRGBA.White);
//        rootNode.addLight(sun);
    }
    
    public void startGame() {
        if (gs != null) {
            gs.cleanup();
            stateManager.detach(gs);
        }
         
        gs = new GameRunningAppState(this);
        stateManager.attach(gs);
    }
    
    public void resetGame() {
        if (gs != null) {
            gs.cleanup();
            stateManager.detach(gs);
            gs = null;
        }
        
        if (winScreen != null) { //we won so reset from here
            stateManager.detach(winScreen);
            winScreen = null;
        }

        if (loseScreen != null) { //we lost so reset from here
            stateManager.detach(loseScreen);
            loseScreen = null;
        }
        
        startMenu = new StartMenu(this);
        stateManager.attach(startMenu);        
    }
    
    public void endGame(boolean win) {
         if (gs != null) {
            gs.cleanup();
            stateManager.detach(gs);
        }
         
        if (win) {
           winScreen = new WinScreenState(this);
           stateManager.attach(winScreen);
        } else {
           loseScreen = new LoseScreenState(this);
           stateManager.attach(loseScreen);
        }
    }
    
    @Override
    public void simpleUpdate(float tpf) { }

    @Override
    public void simpleRender(RenderManager rm) { }
    
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("Quit") && isPressed) {
                stop(); // Quit the application
            }
        }
    };


}