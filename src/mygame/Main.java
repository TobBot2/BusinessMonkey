package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import mygame.StartMenu;


/**
 * Main class of Business Monkey game
 * 
 * @author Trevor Black & Liam Finn & Samuel Muzac
 */
public class Main extends SimpleApplication {
        private AnimateTriad animateModel;
        private GameRunningAppState gs;
        private StartMenu startMenu;
        private WinScreenState winScreen;
        private LoseScreenState loseScreen;
        private AudioNode ambientSound;        

    
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
//        GameRunningAppState gameRunningAppState = new GameRunningAppState();
//        stateManager.attach(gameRunningAppState);
        
        startMenu = new StartMenu(this);
        stateManager.attach(startMenu);  // Pass the Main instance
        
        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)));
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
        
        startAudio();


        
        
    }
   

    @Override
    public void simpleUpdate(float tpf) { }

    @Override
    public void simpleRender(RenderManager rm) { }
    
    
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
        if (winScreen != null) {
        stateManager.detach(winScreen);
        winScreen = null;
         }

        if (loseScreen != null) {
            stateManager.detach(loseScreen);
            loseScreen = null;
        }
        
        startMenu = new StartMenu(this);
        stateManager.attach(startMenu);
        startAudio();
        
    }
    
    public void endGame(boolean win) {
         if (gs != null) {
            gs.cleanup();
            stateManager.detach(gs);
        }
        stopAudio();
         
        if (win) {
           System.out.println("Player won");
           winScreen = new WinScreenState(this);
           stateManager.attach(winScreen);
        } else {
           System.out.println("Player lost");
           loseScreen = new LoseScreenState(this);
           stateManager.attach(loseScreen);
        }
    }

    private void stopAudio() {
        if (ambientSound != null) {
            ambientSound.stop();
        }
    }
    
    private void startAudio() {
        //Ambient Sound
        ambientSound = new AudioNode(assetManager, "Sounds/city-ambience.wav", AudioData.DataType.Stream);
        ambientSound.setPositional(false); // Sound is everywhere, not localized
        ambientSound.setLooping(true);    // Play continuously
        ambientSound.setVolume(0.5f);     // Adjust volume
        rootNode.attachChild(ambientSound);
        ambientSound.play();
    }



}