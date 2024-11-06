package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;

/**
 *
 * AppState in charge of running the demo level
 * 
 * @author Trevor Black & Liam Finn
 */
public class GameRunningAppState extends AbstractAppState implements ActionListener{
    // input/key mappings
    private final static Trigger TRIGGER_SHOOT = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private final static Trigger TRIGGER_PICKUP = new KeyTrigger(KeyInput.KEY_SPACE);

    private final static String MAPPING_SHOOT = "Shoot";
    private final static String MAPPING_PICKUP = "Pickup";
    
    // application variables
    private SimpleApplication app;
    private Camera cam;
    private Node rootNode;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private InputManager inputManager;
    
    // reused variables
    private Ray ray = new Ray();
    private static final Box boxMesh = new Box(Vector3f.ZERO, 1, 1, 1);
    private static final Cylinder cylinderMesh = new Cylinder(10, 10, 1, 1, true);
    
    // player variables
    private Node playerNode;
    private BetterCharacterControl playerControl;
    private Vector3f walkDirection = new Vector3f(0,0,0);
    private Vector3f viewDirection = new Vector3f(0,0,1);
    private boolean rotateLeft = false, rotateRight = false, forward = false, backward = false;
    private float speed=8;

    private int coinsCollected = 0;
    
    @Override
    public void update(float tpf) {
        CameraNode camNode = new CameraNode("CamNode",cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(0, 4, -6));
        Quaternion quat = new Quaternion();
        quat.lookAt(Vector3f.UNIT_Z,Vector3f.UNIT_Y);
        camNode.setLocalRotation(quat);
        playerNode.attachChild(camNode);
        camNode.setEnabled(true);
        this.app.getFlyByCamera().setEnabled(false);
        
        // Get current forward and left vectors of the playerNode:
        Vector3f modelForwardDir = playerNode.getWorldRotation().mult(Vector3f.UNIT_Z);
        Vector3f modelLeftDir = playerNode.getWorldRotation().mult(Vector3f.UNIT_X);
        // Determine the change in direction
        walkDirection.set(0, 0, 0);
        if (forward) {
            walkDirection.addLocal(modelForwardDir.mult(speed));
        } else if (backward) {
            walkDirection.addLocal(modelForwardDir.mult(speed).negate());
        }
        playerControl.setWalkDirection(walkDirection); // walk!
        // Determine the change in rotation
        if (rotateLeft) {
            Quaternion rotateL = new Quaternion().fromAngleAxis(FastMath.PI * tpf, Vector3f.UNIT_Y);
            rotateL.multLocal(viewDirection);
        } else if (rotateRight) {
            Quaternion rotateR = new Quaternion().fromAngleAxis(-FastMath.PI * tpf, Vector3f.UNIT_Y);
            rotateR.multLocal(viewDirection);
        }
        playerControl.setViewDirection(viewDirection); // turn!
    }
    
    // attempt to shoot a car located at the center of the screen, damaging it.
    private void shoot() {
        CollisionResults results = new CollisionResults();
        
        // set ray to center of first person perspective and shoot ray
        ray.setOrigin(cam.getLocation());
        ray.setDirection(cam.getDirection());
        rootNode.collideWith(ray, results);
        
        if (results.size() > 0) {
            // if hit...
            // get parent because the geometry is a child of the car node that has the ExplodeCarControl class
            Node target = results.getClosestCollision().getGeometry().getParent();
            ExplodeCarControl explodeCarControl = target.getControl(ExplodeCarControl.class); 
            if (explodeCarControl != null) {
                // if hit a car which can explode...
                explodeCarControl.damage(1f);
            }
        }
    }
    
    // attempt to pick up a coin located at the center of the screen, and add its value to the player's coinsCollected
    private void pickup() {
        CollisionResults results = new CollisionResults();
        
        // set ray to center of first person perspective and shoot ray
        ray.setOrigin(cam.getLocation());
        ray.setDirection(cam.getDirection());
        rootNode.collideWith(ray, results);
        
        if (results.size() > 0) {
            // if hit...
            Geometry target = results.getClosestCollision().getGeometry();
            CoinPickupControl coinPickupControl = target.getControl(CoinPickupControl.class); 
            if (coinPickupControl != null) {
                // if hit a coin which can be picked up...
                coinsCollected += coinPickupControl.pickup();
                
                System.out.println("Picked up coin! " + coinsCollected);
            }
        }
    }
    
    @Override
    public void cleanup() {}
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        // app settings/variables
        this.app = (SimpleApplication) app;
        this.cam = this.app.getCamera();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        
        this.app.getFlyByCamera().setMoveSpeed(50f);
        
        // key mappings
        this.inputManager.addMapping(MAPPING_SHOOT, TRIGGER_SHOOT);
        this.inputManager.addMapping(MAPPING_PICKUP, TRIGGER_PICKUP);
        this.inputManager.addListener(analogListener, new String[]{MAPPING_SHOOT});
        this.inputManager.addListener(actionListener, new String[]{MAPPING_PICKUP});
        
        // crosshair
        Geometry c = createBox("crosshair", Vector3f.ZERO, new Vector3f(2, 2, 2), ColorRGBA.White);
        c.setLocalTranslation(this.app.getContext().getSettings().getWidth() / 2, this.app.getContext().getSettings().getHeight() / 2, 0);
        this.app.getGuiNode().attachChild(c); // attach to 2D user interface
        
        initGeometry();
        initPlayer();
    }
    
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float intensity, float tpf) {
            if (name.equals(MAPPING_SHOOT)) {
                shoot();
            }
        }
    };
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals(MAPPING_PICKUP) && !isPressed) {
                pickup();
            }
        }
    };
    
    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        if (binding.equals("Rotate Left")) { rotateLeft = isPressed; }
        else if (binding.equals("Rotate Right")) { rotateRight = isPressed; }
        else if (binding.equals("Forward")) { forward = isPressed; }
        else if (binding.equals("Back")) { backward = isPressed; }
        else if (binding.equals("Jump")) { playerControl.jump(); }
    }
    
    private void initPlayer() {
        // physics
        BulletAppState bulletAppState = new BulletAppState();
        this.stateManager.attach(bulletAppState);
        RigidBodyControl scenePhy = new RigidBodyControl(0f);
        this.rootNode.addControl(scenePhy);
        bulletAppState.getPhysicsSpace().add(this.rootNode);
        
        this.playerNode = new Node("Player");
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0,-9.81f, 0));
        this.playerNode.setLocalTranslation(new Vector3f(0, 6, 0));
        rootNode.attachChild(this.playerNode);
        
        this.playerControl = new BetterCharacterControl(1.5f, 4, 30f);
        this.playerControl.setJumpForce(new Vector3f(0, 300, 0));
        this.playerControl.setGravity(new Vector3f(0, -10, 0));
        
        this.playerNode.addControl(this.playerControl);
        bulletAppState.getPhysicsSpace().add(this.playerControl);
        
        // remap controls
        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Back", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Rotate Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Rotate Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Rotate Left", "Rotate Right");
        inputManager.addListener(this, "Forward", "Back", "Jump");
    }
    
    // initialize all static geometry, and group them in common nodes (ground, buildings, cars)
    private void initGeometry() {
        Node ground = new Node("Ground");
        ground.attachChild(createBox("floor", new Vector3f(0, -2, 0), new Vector3f(250, 1, 250), ColorRGBA.DarkGray));
        ground.attachChild(createBox("sidewalk1", new Vector3f(-91, -1, -19), new Vector3f(80, .5f, 185), ColorRGBA.Gray));
        ground.attachChild(createBox("sidewalk2", new Vector3f(128, -1, -19), new Vector3f(80, .5f, 185), ColorRGBA.Gray));
        
        Node buildings = new Node("Buildings");
        buildings.attachChild(createBox("building1", new Vector3f(-74, 36, 75), new Vector3f(23, 36, 23), ColorRGBA.Brown));
        buildings.attachChild(createBox("building2", new Vector3f(-70, 48, 26), new Vector3f(26, 48, 26), ColorRGBA.Brown));
        buildings.attachChild(createBox("building3", new Vector3f(-98, 34, -13), new Vector3f(51, 34, 13), ColorRGBA.Brown));
        buildings.attachChild(createBox("building4", new Vector3f(-77, 81, -101), new Vector3f(42, 81, 42), ColorRGBA.LightGray));
        buildings.attachChild(createBox("building5", new Vector3f(-92, 23, -146), new Vector3f(20, 23, 20), ColorRGBA.Brown));
        buildings.attachChild(createBox("building6", new Vector3f(95, 64, -94), new Vector3f(25, 64, 31), ColorRGBA.Brown));
        buildings.attachChild(createBox("building7", new Vector3f(123, 115, -6), new Vector3f(43, 115, 57), ColorRGBA.LightGray));
        buildings.attachChild(createBox("building8", new Vector3f(134, 34, 64), new Vector3f(51, 34, 13), ColorRGBA.Brown));
        buildings.attachChild(createBox("building9", new Vector3f(113, 36, 100), new Vector3f(23, 36, 23), ColorRGBA.Brown));
    
        Node cars = new Node("Cars");
        cars.attachChild(createCar("car1", new Vector3f(-5, 3.5f, 0), new Vector3f(3, 3, 6), 0, ColorRGBA.Blue));
        cars.attachChild(createCar("car2", new Vector3f(-5, 3.5f, -37), new Vector3f(3, 3, 6), 0, ColorRGBA.Blue));
        cars.attachChild(createCar("car3", new Vector3f(-5, 3.5f, -74), new Vector3f(3, 3, 6), 0, ColorRGBA.Blue));
        cars.attachChild(createCar("car4", new Vector3f(-5, 3.5f, 116), new Vector3f(3, 3, 6), 0, ColorRGBA.Blue));
        cars.attachChild(createCar("car5", new Vector3f(38, 3.5f, -63), new Vector3f(3, 3, 6), (float)Math.PI, ColorRGBA.Blue));
        cars.attachChild(createCar("car6", new Vector3f(38, 3.5f, 30), new Vector3f(3, 3, 6), (float)Math.PI, ColorRGBA.Blue));
        cars.attachChild(createCar("car7", new Vector3f(38, 3.5f, 65), new Vector3f(3, 3, 6), (float)Math.PI, ColorRGBA.Blue));
        
        Node coins = new Node("Coins");
        coins.attachChild(createCoin("coin1", new Vector3f(-5, 3.5f, 0)));
        coins.attachChild(createCoin("coin2", new Vector3f(-5, 3.5f, -37)));
        coins.attachChild(createCoin("coin3", new Vector3f(-5, 3.5f, -74)));
        coins.attachChild(createCoin("coin4", new Vector3f(-5, 3.5f, 116)));
        coins.attachChild(createCoin("coin5", new Vector3f(38, 3.5f, -63)));
        coins.attachChild(createCoin("coin6", new Vector3f(38, 3.5f, 30)));
        coins.attachChild(createCoin("coin7", new Vector3f(38, 3.5f, 65)));
        
        rootNode.attachChild(ground);
        rootNode.attachChild(buildings);
        rootNode.attachChild(cars);
        rootNode.attachChild(coins);
    }
    
    // helper function to quickly create a box geometry with a given name, location, scale, and color
    private Geometry createBox(String name, Vector3f loc, Vector3f scale, ColorRGBA color) {
        Geometry geom = new Geometry(name, boxMesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geom.setMaterial(mat);
        geom.setLocalTranslation(loc);
        geom.setLocalScale(scale);
                
        return geom;
    }
    
    // helper function to quickly create a car (similar to createBox, setting name, location, scale, color, with addition of rotation).
    // The scale determines the size of the main body of the car
    private Node createCar(String name, Vector3f loc, Vector3f scale, float rot, ColorRGBA color) {
        Node car = new Node(name); 
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        
        Geometry body = new Geometry(name + "_body", boxMesh);
        body.setMaterial(mat);
        body.setLocalScale(scale);
        
        Geometry front = new Geometry(name + "_front", boxMesh);
        front.setMaterial(mat);
        front.setLocalTranslation(0, -scale.y / 2.f, scale.z * 1.33f);
        front.setLocalScale(scale.mult(1, .5f, .33f));
                
        // create body and front of car as separate objects, children to the parent car node
        car.attachChild(body);
        car.attachChild(front);
        
        // move the car node and rotate it
        car.setLocalTranslation(loc);
        car.rotate(0, rot, 0);
        
        // add explode controller
        car.addControl(new ExplodeCarControl());
        
        return car;
    }
    
    // creates pickup-able coin at a given location with the given name
    private Geometry createCoin(String name, Vector3f loc) {
        Geometry coin = new Geometry(name, cylinderMesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Yellow);
        coin.setMaterial(mat);
        coin.setLocalTranslation(loc);
        coin.setLocalScale(1, 1, .1f);
        
        coin.addControl(new CoinPickupControl());
        
        return coin;
    }
}
