/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author Trevor Black, Liam Finn & Samuel Muzac
 */
public class PlayerPhysControl extends BetterCharacterControl implements PhysicsCollisionListener {
    
    public static int coinsCollected = 0;
    private final GameRunningAppState mas;
    
    //double jump
    private int jumpCount = 0;
    private int maxJumps = 2;

    public PlayerPhysControl(float f, int i, float f0, GameRunningAppState mas) {
        super(f, i, f0);
        this.mas = mas;
    }
    
    public int getCoins() {
        return coinsCollected;
    }
    
    public void specialJump() {
        if (jumpCount < maxJumps) {
            // Apply upward impulse for jump
            jumpCount++;
            
            jump = true;
        }
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
            var ctrl = coin.getControl(CoinPickupControl.class);
            if (ctrl != null) {
                coinsCollected += ctrl.pickup();
            }
            
            coin.removeFromParent();
            System.out.println("Coin collected: " + coinsCollected);
        } else if ("MonkeyTriad".equals(parentA) && "Player".equals(nodeB.getName()) || "MonkeyTriad".equals(parentB) && "Player".equals(nodeA.getName())) {
            
            //player loses health 
            mas.playerTakeDamage(2);
            System.out.println("Player hit by enemy! Health -2");
            
        } else if ("MonkeyBall".equals(nodeA.getName()) || "MonkeyBall".equals(nodeB.getName())) {
            Spatial monkeyBall = "MonkeyBall".equals(nodeA.getName()) ? nodeA : nodeB;
            Spatial other = "MonkeyBall".equals(nodeA.getName()) ? nodeB : nodeA;
            
//            if (other.getName().equals("wall") || other.getName().equals("floor")) {
//            }else {
//                System.out.println("Monkeyball hit something: " + other.getName());
//            }
            
            if (monkeyBall.getControl(RigidBodyControl.class) != null) {
                RigidBodyControl ballPhys = monkeyBall.getControl(RigidBodyControl.class);
                ballPhys.getPhysicsSpace().remove(ballPhys);
                monkeyBall.removeControl(ballPhys);
            }
            monkeyBall.removeFromParent();
            
            if (other.getName().equals("Player")) {
                System.out.println("hit player...");
                     mas.playerTakeDamage(2);
                    System.out.println("took damage! Hp - 2");
            }
        } else {
//            System.out.println("unhandled collision: " + nodeA.getName() + ", " + nodeB.getName());
        }
        
        if (nodeA.getName().equals("Player") || nodeB.getName().equals("Player")) {
            jumpCount = 0;
            
            this.setJumpForce(event.getNormalWorldOnB().add(Vector3f.UNIT_Y.mult(2f)).normalize().mult(1500));
        }
    }
}
