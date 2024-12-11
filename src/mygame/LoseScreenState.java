/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;


/**
 *
 * @author slmuz
 */
public class LoseScreenState extends AbstractAppState implements ActionListener{
    
    private Node guiNode;
    private BitmapText loseText;
    private final Main mainApp;
    private AppStateManager stateManager;
    
    public LoseScreenState(Main mainApp) {
        this.mainApp = mainApp;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.stateManager = stateManager;
        
//        // Background
//        Geometry background = new Geometry("Background", new Quad(app.getCamera().getWidth(), app.getCamera().getHeight()));
//        Material bgMaterial = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//        bgMaterial.setColor("Color", ColorRGBA.Red); // Change to any color you like
//        background.setMaterial(bgMaterial);
//        background.setLocalTranslation(0, 0, -1); // Position slightly behind GUI elements
//        guiNode.attachChild(background);
//        
        guiNode = ((Main) app).getGuiNode();
        
        BitmapFont font = stateManager.getApplication().getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        loseText = new BitmapText(font, false);
        loseText.setSize(font.getCharSet().getRenderedSize() * 2);
        loseText.setText("You Lost! Press R to restart or ESC to quit");
        loseText.setColor(ColorRGBA.White);
        loseText.setLocalTranslation(app.getContext().getSettings().getWidth() / 2 - loseText.getLineWidth() / 2,
                app.getContext().getSettings().getHeight() / 2 + loseText.getLineHeight(),
                0
        );
        guiNode.attachChild(loseText);   
        mainApp.getInputManager().addMapping("Restart", new KeyTrigger(KeyInput.KEY_R));
        mainApp.getInputManager().addListener(this, "Restart"); 
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        if (guiNode != null && loseText != null) {
            guiNode.detachChild(loseText);
        }   
        mainApp.getInputManager().deleteMapping("Reset");
        mainApp.getInputManager().removeListener(this);
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (isPressed && name.equals("Restart")) {
            System.out.println("Game reset");
            mainApp.resetGame();
        }
    }
}
