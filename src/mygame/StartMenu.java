/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
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
public class StartMenu extends AbstractAppState implements ActionListener{
    
    private Node guiNode;
    private BitmapText startText;
    private boolean isMenuActive = true;
    private final Main mainApp;
    private AppStateManager stateManager;
    
    public StartMenu(Main mainApp) {
        this.mainApp = mainApp;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.stateManager = stateManager;
        
        guiNode = new Node("GUI");
        ((SimpleApplication) app).getGuiNode().attachChild(guiNode);
        
        //Background
        Geometry background = new Geometry("Background", new Quad(app.getCamera().getWidth(), app.getCamera().getHeight()));
        Material bgMaterial = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        bgMaterial.setColor("Color", ColorRGBA.DarkGray); // Change to any color you like
        background.setMaterial(bgMaterial);
        background.setLocalTranslation(0, 0, -1); // Position slightly behind GUI elements
        guiNode.attachChild(background);
        
        BitmapFont font = stateManager.getApplication().getAssetManager().loadFont("Interface/Fonts/Default.fnt");

        
        // Load Title
        BitmapText title = new BitmapText(font, false);
        title.setSize(font.getCharSet().getRenderedSize() * 2);
        title.setText("Buisness Monkey"); // Set text
        title.setLocalTranslation(
                app.getCamera().getWidth() / 2 - title.getLineWidth() / 2,
                app.getCamera().getHeight() / 2 + title.getLineHeight() * 2,
                0
        );
        guiNode.attachChild(title);


        
        // Load font
        startText = new BitmapText(font, false);
        startText.setSize(font.getCharSet().getRenderedSize());
        startText.setText("Press Enter to Start"); // Set text
        startText.setLocalTranslation(
                app.getCamera().getWidth() / 2 - startText.getLineWidth() / 2,
                title.getLocalTranslation().y - title.getLineHeight() * 2f,
                0
        );
        guiNode.attachChild(startText);
        
        // Description Text
        BitmapText descriptionText = new BitmapText(font, false);
        descriptionText.setSize(font.getCharSet().getRenderedSize() * 0.75f); // Slightly smaller font
        descriptionText.setText("Use WASD to move and the mouse to look around.");
        descriptionText.setLocalTranslation(
                app.getCamera().getWidth() / 2 - descriptionText.getLineWidth() / 2,
                startText.getLocalTranslation().y - startText.getLineHeight() * 1.5f,
                0
        );
        guiNode.attachChild(descriptionText);

        // Additional Instructions
        BitmapText extraInstructions = new BitmapText(font, false);
        extraInstructions.setSize(font.getCharSet().getRenderedSize() * 0.75f);
        extraInstructions.setText("Collect all the coins and survive to win");
        extraInstructions.setLocalTranslation(
                app.getCamera().getWidth() / 2 - extraInstructions.getLineWidth() / 2,
                descriptionText.getLocalTranslation().y - descriptionText.getLineHeight() * 1.5f,
                0
        );
        guiNode.attachChild(extraInstructions);
        
        InputManager inputManager = stateManager.getApplication().getInputManager();
        inputManager.addMapping("StartGame", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, "StartGame");
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        guiNode.removeFromParent();
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (!isPressed && name.equals("StartGame")) {
            stateManager.getApplication().getInputManager().deleteMapping("StartGame");
            System.out.println("Game started");
            isMenuActive = false;
            mainApp.startGame();
            stateManager.detach(this);
        }
    }
    
    @Override
    public void update(float tpf) { }
}
