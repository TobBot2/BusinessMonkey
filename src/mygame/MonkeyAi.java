package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Sphere;
import java.util.Random;

/**
 *
 * Shoot at intervals towards player
 * 
 * @author Trevor Black & Liam Finn & Samuel Muzac
 */
public class MonkeyAi extends AbstractControl {
    
    Spatial player;
    Node rootNode;
    BulletAppState bulletAppState;
    AssetManager assetManager;
    
    Mesh ballMesh = new Sphere(8, 16, 1);
    
    float fireTimer = 0f;
    float fireRate = 3f;
    
    public MonkeyAi(Spatial player, BulletAppState bulletAppState, AssetManager assetManager, Node rootNode) {
        Random rand = new Random();
        
        // desynchronize firing of monkeys
        fireTimer = rand.nextFloat(fireRate);
        fireRate += rand.nextFloat(.2f);
        
        this.player = player;
        this.bulletAppState = bulletAppState;
        this.assetManager = assetManager;
        this.rootNode = rootNode;
    }
    
    private void shoot() {
        Vector3f playerPosition = player.getWorldTranslation().add(Vector3f.UNIT_Y.mult(2f));
        Vector3f shootOrigin = this.getSpatial().getWorldTranslation().add(Vector3f.UNIT_Y.mult(10f));
        Vector3f playerDirection = (playerPosition.subtract(shootOrigin)).normalize(); // point from shoot origin to player
        this.rootNode.attachChild(createBall(shootOrigin.add(playerDirection.mult(5f)), playerDirection.mult(100f)));
    }

    // helper function to quickly create a sphere geometry with a given name, location, scale, and color. Uses phong shading
    private Node createBall(Vector3f loc, Vector3f velocity) {
        String name = "MonkeyBall";
        ColorRGBA color = ColorRGBA.Red;
        
        Node node = new Node(name);
        Geometry geom = new Geometry(name + "_geom", ballMesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Diffuse", color);
        mat.setColor("Ambient", color.mult(0.3f));
        geom.setMaterial(mat);
        geom.setLocalTranslation(loc);
        geom.setLocalScale(Vector3f.UNIT_XYZ.mult(.5f));
        node.attachChild(geom);
        
        RigidBodyControl rbControl = new RigidBodyControl(1f);
        node.addControl(rbControl);
        bulletAppState.getPhysicsSpace().add(rbControl);
        rbControl.setGravity(Vector3f.ZERO);
        rbControl.setLinearVelocity(velocity);
                
        return node;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        fireTimer -= tpf;
        
        if (fireTimer <= 0) {
            shoot();
            
            fireTimer = fireRate;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) { }
}
