/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mygame;

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
import com.jme3.scene.Node;
import mygame.Main;


/**
 *
 * @author slmuz
 */
public class StartMenu extends AbstractAppState implements ActionListener{
    
    private Node guiNode;
    private BitmapText startText;
    private boolean isMenuActive = true;
    private Main mainApp;
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
        
        // Load font
        BitmapFont font = stateManager.getApplication().getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        startText = new BitmapText(font, false);
        startText.setSize(font.getCharSet().getRenderedSize());
        startText.setText("Press Enter to Start"); // Set text
//        startText.setLocalTranslation(300, startText.getLineHeight() + 100, 0); // Position text
startText.setLocalTranslation(
                app.getCamera().getWidth() / 2 - startText.getLineWidth() / 2,
                app.getCamera().getHeight() / 2 + startText.getLineHeight(),
                0
        );
        guiNode.attachChild(startText);
        
        
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
            System.out.println("Game started");
            isMenuActive = false;
            mainApp.startGame();
            stateManager.detach(this);
        }
    }
    
    @Override
    public void update(float tpf) {
        if (!isMenuActive) {
            
        }
    }

    
    
}
