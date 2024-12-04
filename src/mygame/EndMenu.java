/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

public class EndMenu extends AbstractAppState {
    private SimpleApplication app;
    private Node guiNode;
    private BitmapText endText;
    private AppStateManager stateManager;
    private final Main mainApp;
    
    public EndMenu(Main mainApp) { this.mainApp = mainApp; }

    public void initialize(AppStateManager stateManager, SimpleApplication app) {
        super.initialize(stateManager, app);
        this.stateManager = stateManager;
        this.app = app;
        this.guiNode = app.getGuiNode();

        // Display the end menu
        if (guiNode != null) {
            setupEndMenu();
        }
        // Add keybinding for restarting the game
       app.getInputManager().addMapping("Restart", new KeyTrigger(KeyInput.KEY_R));
       app.getInputManager().addListener(actionListener, "Restart");
    }

    @Override
    public void cleanup() {
        if (guiNode != null) {
            guiNode.detachAllChildren();
            // Remove the menu and keybindings when this state is detached
            app.getInputManager().deleteMapping("Restart");
            app.getInputManager().removeListener(actionListener);
        }
        super.cleanup(); 
    }

    private void setupEndMenu() {
        // Clear previous menus
        guiNode.detachAllChildren();

        // Add end menu text
        BitmapFont font = stateManager.getApplication().getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        endText = new BitmapText(font, false);
        endText.setSize(font.getCharSet().getRenderedSize());
        endText.setText("Game Over\nPress 'R' to Restart\nPress 'Esc' to Exit");
        endText.setColor(ColorRGBA.White);
        endText.setLocalTranslation(
                app.getCamera().getWidth() / 2 - endText.getLineWidth() / 2,
                app.getCamera().getHeight() / 2 + endText.getLineHeight(),
                0
        );
        guiNode.attachChild(endText);
        app.getInputManager().addMapping("Restart", new KeyTrigger(KeyInput.KEY_R));
        app.getInputManager().addListener(actionListener, "Restart");
    }

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (!isPressed) return; // Only act on key release
            switch (name) {
                case "Restart" -> {
                    app.getInputManager().deleteMapping("Restart");
                    restartGame();
                }
                case "Exit" -> app.stop();
            }
        }
    };

   private void restartGame() {
    // Detach all active states and reset the game
    stateManager.detach(this); // Remove EndMenu

    // Optionally, clear other states to ensure a clean slate
    GameRunningAppState gameState = stateManager.getState(GameRunningAppState.class);
    if (gameState != null) {
        stateManager.detach(gameState);
    }

    // Attach a fresh instance of the game state
    GameRunningAppState newGameState = new GameRunningAppState(mainApp);
    stateManager.attach(newGameState);
    }

}
