package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;

/**
 *
 * AppState in charge of running the demo level
 * 
 * @author Trevor Black & Liam Finn
 */
public class GameRunningAppState extends AbstractAppState {
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
    
    // reused variables
    private Ray ray = new Ray();
    private static final Box boxMesh = new Box(Vector3f.ZERO, 1, 1, 1);
    private static final Cylinder cylinderMesh = new Cylinder(10, 10, 1, 1, true);
    
    private int coinsCollected = 0;
    
    @Override
    public void update(float tpf) { }
    
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
        
        this.app.getFlyByCamera().setMoveSpeed(50f);
        
        // key mappings
        this.app.getInputManager().addMapping(MAPPING_SHOOT, TRIGGER_SHOOT);
        this.app.getInputManager().addMapping(MAPPING_PICKUP, TRIGGER_PICKUP);
        this.app.getInputManager().addListener(analogListener, new String[]{MAPPING_SHOOT});
        this.app.getInputManager().addListener(actionListener, new String[]{MAPPING_PICKUP});
        
        // crosshair
        Geometry c = createBox("crosshair", Vector3f.ZERO, new Vector3f(2, 2, 2), ColorRGBA.White);
        c.setLocalTranslation(this.app.getContext().getSettings().getWidth() / 2, this.app.getContext().getSettings().getHeight() / 2, 0);
        this.app.getGuiNode().attachChild(c); // attach to 2D user interface
        
        initGeometry();
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
