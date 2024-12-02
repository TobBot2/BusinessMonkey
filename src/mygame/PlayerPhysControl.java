/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;

/**
 *
 * @author Trevor Black, Liam Finn & Samuel Muzac
 */
public class PlayerPhysControl extends BetterCharacterControl implements PhysicsCollisionListener {
    
    public int coinsCollected = 0;

    public PlayerPhysControl(float f, int i, float f0) {
        super(f, i, f0);
    }
    
    @Override
    public void collision(PhysicsCollisionEvent event) {
        // Get the colliding objects
        Spatial nodeA = event.getNodeA();
        Spatial nodeB = event.getNodeB();

        // Safety checks for null nodes
        if (nodeA == null || nodeB == null) {
            return;
        }

        // Get the parents (if they exist) of the colliding objects
        String parentA = nodeA.getParent() != null ? nodeA.getParent().getName() : "";
        String parentB = nodeB.getParent() != null ? nodeB.getParent().getName() : "";

        // Check if one of the nodes belongs to the "Coins" parent
        if ("Coins".equals(parentA) || "Coins".equals(parentB)) {
            // Identify the coin node
            Spatial coin = "Coins".equals(parentA) ? nodeA : nodeB;
            
            // Remove the physics control from the PhysicsSpace
            if (coin.getControl(RigidBodyControl.class) != null) {
                RigidBodyControl coinPhysics = coin.getControl(RigidBodyControl.class);
                coinPhysics.getPhysicsSpace().remove(coinPhysics); // Remove from PhysicsSpace
                coin.removeControl(coinPhysics); // Remove control from the node
            }

            // Remove the coin from the scene
            coin.removeFromParent();
            System.out.println("Coin collected!");
            coinsCollected++;
        }
    }
    
}
