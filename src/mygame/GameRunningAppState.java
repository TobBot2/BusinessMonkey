package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import java.util.Random;
import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.PointLightShadowRenderer;



/**
 *
 * AppState in charge of running the demo level
 * 
 * @author Trevor Black & Liam Finn & Samuel Muzac
 */
public class GameRunningAppState extends AbstractAppState implements ActionListener{
    
    // input/key mappings
    private final static Trigger TRIGGER_SHOOT = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private final static String MAPPING_SHOOT = "Shoot";
    
    // application variables
    private SimpleApplication app;
    private Camera cam;
    private CameraNode camNode;
    private Node rootNode;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private InputManager inputManager;
    private Main mainApp;
    private ViewPort viewPort;
    
    //Sound Nodes
    //private Node soundNode;
    //private AudioNode fireNode;
    private AudioNode ambientSound;        

    // reused variables
    private final Ray ray = new Ray();
    private static final Box boxMesh = new Box(Vector3f.ZERO, 1, 1, 1);
    private static final Cylinder cylinderMesh = new Cylinder(10, 10, 1, 1, true);
    private int app_width, app_height;
    
    private BulletAppState bulletAppState;
    
    // player variables
    private Node playerNode;
    private PlayerPhysControl playerControl;
    
    //health bar
    private Geometry healthBar;
    private float health;
    private final float max_health = 10;
    private float hb_width, hb_height;
    
    //movement stats
    private boolean moveLeft = false, moveRight = false, forward = false, backward = false;
    private float speed=100;
    
    //player gun
    private Spatial playerGun;
    private final float shootRate = .3f;
    private float shootTimer = 0f;

    //coins collected
    private final int coinsNeededToWin = 10;
    private AnimateTriad animateModel;
    
    //damage system
    private BoundingBox playerBox;
    private BoundingBox demoHurtBox;
    private final float fireDamageCooldown = 0.3f;  // Cooldown period in seconds (1 second)
    private float timeSinceLastDamage = 0.0f;
    
    //fog
    private ParticleEmitter fireEmitter2;
    private FilterPostProcessor fpp;
    private FogFilter fogFilter;
    private final float maxFogFloat = 3.0f;
    // Define the bounds where fog should be active
    private final float fogStartX = -50f, fogEndX = -300f, fogStartY = -10f, 
                        fogEndY = 20f, fogStartZ = 100, fogEndZ = -90;

    //damage?
    private Geometry redTint;
    private Material damageMat;
    private float opacity;
    private AudioNode hitSound1, hitSound2, hitSound3; 
    
//    private Geometry sliderBar;
//    private Geometry sliderThumb;
//    private boolean isDragging = false;
//    private float sliderMinX = 200;
//    private float sliderMaxX = 600;
//    private float sliderValue = 0.0f;
    private BitmapText positionText;
    final int SHADOWMAP_SIZE=1024;


    
    
    public GameRunningAppState(Main mainApp) { this.mainApp = mainApp; }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        //Initialize app settings/variables
        this.app = (SimpleApplication) app;
        this.cam = this.app.getCamera();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.viewPort = this.app.getViewPort();
        app_width = this.app.getContext().getSettings().getWidth();
        app_height = this.app.getContext().getSettings().getHeight();
        hb_width = app_width / 8;
        hb_height = app_height / 20;
        
        initKeyMappings();// key mappings
        initPlayer(); //player   
        initGeometry(); //geometry
        setupLights(); //lighting
        initEnemies(); //enimies
        initCamera(); //camera
        initUIElements(); //UI stuff 
        initFireElements(); //fire
        initFogElements(); //fog
        initSoundElements(); //sounds
//        initPositionText();
//        setupKeyMappings();
    }
    
    private void initKeyMappings() {
        this.inputManager.addMapping(MAPPING_SHOOT, TRIGGER_SHOOT);
        this.inputManager.addListener(analogListener, new String[]{MAPPING_SHOOT});
    }
    
    private void initPlayer() {
        this.bulletAppState = new BulletAppState();
        this.stateManager.attach(this.bulletAppState);
        RigidBodyControl scenePhy = new RigidBodyControl(0f);
        this.rootNode.addControl(scenePhy);
        this.bulletAppState.getPhysicsSpace().add(this.rootNode);
        
        this.playerNode = new Node("Player");
        this.bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0,-150f, 0)); // GRAVITY
        Vector3f playerPos = new Vector3f(19.332f, -0.960f, 237.932f);
        this.playerNode.setLocalTranslation(playerPos);
        Vector3f target = new Vector3f(0,6,0);
        this.playerNode.lookAt(target, Vector3f.UNIT_Y);
        rootNode.attachChild(this.playerNode);
                
        // initialize player physics/collision
        this.playerControl = new PlayerPhysControl(1.5f, 5, 40f, this);
        //this.playerControl.setJumpForce(new Vector3f(0, 1500, 0)); // JUMP FORCE now set in PlayerPhysControl

        this.health = max_health;
        
        this.playerNode.addControl(this.playerControl);
        this.bulletAppState.getPhysicsSpace().add(this.playerControl);
        this.bulletAppState.getPhysicsSpace().addCollisionListener((PhysicsCollisionListener) this.playerControl);

        // remap controls
        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Back", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Forward", "Back", "Left", "Right", "Jump");
                
        //init playerbox
        float playerWidth = 10f; // Example width, adjust as needed
        float playerHeight = 20f; // Example height
        float playerDepth = 10f; // Example depth
        playerBox = new BoundingBox(playerNode.getWorldTranslation(), playerWidth / 2, playerHeight / 2, playerDepth / 2);
        
    }
    
    // initialize all static geometry, and group them in common nodes (ground, buildings, cars)
    private void initGeometry() {
        
        //sky
        Spatial mySky = assetManager.loadModel("Scenes/mySky.j3o");
        rootNode.attachChild(mySky);
        
        //ground
        Node ground = new Node("Ground");
        ground.attachChild(createBox("road0", new Vector3f(0, -2, 250), new Vector3f(400, 1, 50), new ColorRGBA(0.1f, 0.1f, 0.1f, 1.0f)));
        ground.attachChild(createBox("road1", new Vector3f(0, -2, 0), new Vector3f(50, 1, 200), new ColorRGBA(0.1f, 0.1f, 0.1f, 1.0f)));
        ground.attachChild(createBox("block1L", new Vector3f(-170, -2, 110), new Vector3f(120, 1, 90), new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f)));
        ground.attachChild(createBox("roadbetweenblock1", new Vector3f(-170, -2, 0), new Vector3f(120, 1, 20), new ColorRGBA(0.1f, 0.1f, 0.1f, 1.0f)));
        ground.attachChild(createBox("block1R", new Vector3f(-170, -2, -110), new Vector3f(120, 1, 90), new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f)));
        ground.attachChild(createBox("block2", new Vector3f(250, -2, 0), new Vector3f(200, 1, 200), new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f)));
//        ground.attachChild(createBoxOldLighting("floor", new Vector3f(0, -2, 0), new Vector3f(250, 1, 250), ColorRGBA.White));
//        ground.attachChild(createBoxOldLighting("sidewalk1", new Vector3f(-91, 0, -19), new Vector3f(80, .5f, 185), ColorRGBA.Blue));
//        ground.attachChild(createBoxOldLighting("sidewalk2", new Vector3f(128, 0, -19), new Vector3f(80, .5f, 185), ColorRGBA.Blue));
            
        //boxes
        Node boxes = new Node("Boxes");
        Quaternion rotation = new Quaternion();
        Quaternion rotation2 = new Quaternion();
        Quaternion rotation3 = new Quaternion();
        rotation.fromAngleAxis(FastMath.PI / 3, Vector3f.UNIT_Y);
        rotation2.fromAngleAxis(- FastMath.PI / 4, Vector3f.UNIT_Y);
        rotation3.fromAngleAxis(FastMath.PI / 12, Vector3f.UNIT_X);
        ground.attachChild(createBoxRotate("block1", new Vector3f(200, 8, -12), new Vector3f(10, 50, 25), new ColorRGBA(0.8f, 0.3f, 0.1f, 1.0f), rotation));
        ground.attachChild(createBoxRotate("block1", new Vector3f(30, 8, 210), new Vector3f(10, 8, 25), new ColorRGBA(0.8f, 0.3f, 0.1f, 1.0f), rotation));
        ground.attachChild(createBoxRotate("block1", new Vector3f(-60, 8, 220), new Vector3f(10, 8, 25), new ColorRGBA(0.8f, 0.3f, 0.1f, 1.0f), rotation2));
        ground.attachChild(createBoxRotate("block1", new Vector3f(153, 1, 122), new Vector3f(10, 50, 10), new ColorRGBA(0.2f, 0.3f, 0.8f, 1.0f), rotation2));
        ground.attachChild(createBoxRotate("block1", new Vector3f(137, 13, 150), new Vector3f(10, 8, 25), new ColorRGBA(0.1f, 0.2f, 0.8f, 1.0f), rotation3));
        ground.attachChild(createBoxRotate("block1", new Vector3f(65, 0, 145), new Vector3f(2, 2, 2), new ColorRGBA(0.6f, 0.4f, 0.2f, 1.0f), rotation));

//        //buildings
        Node buildings = new Node("Buildings");
        //border buildings
        buildings.attachChild(createBuilding("building", new Vector3f(0, -50, 270), new Vector3f(23, 36, 23), ColorRGBA.White, true));
        buildings.attachChild(createBuilding("building", new Vector3f(50, -50, 270), new Vector3f(50, 49, 23), ColorRGBA.White, true));
        buildings.attachChild(createBuilding("building", new Vector3f(100, -50, 270), new Vector3f(68, 31, 23), ColorRGBA.White, true));
        buildings.attachChild(createBuilding("building", new Vector3f(150, -50, 270), new Vector3f(60, 59, 23), ColorRGBA.White, true));
        buildings.attachChild(createBuilding("building", new Vector3f(200, -50, 270), new Vector3f(78, 61, 23), ColorRGBA.White, true));
        buildings.attachChild(createBuilding("building", new Vector3f(-70, -50, 270), new Vector3f(40, 28, 23), ColorRGBA.White, true));
        buildings.attachChild(createBuilding("building", new Vector3f(-90, -30, 294), new Vector3f(60, 30, 23), ColorRGBA.White, true));
        buildings.attachChild(createBuilding("building", new Vector3f(-180, -50, 270), new Vector3f(40, 28, 23), ColorRGBA.White, true));
        buildings.attachChild(createBuilding("building", new Vector3f(-230, -50, 270), new Vector3f(40, 28, 23), ColorRGBA.White, true));

        buildings.attachChild(createBuilding("building", new Vector3f(150, -120, 50), new Vector3f(10, 6, 10), ColorRGBA.White, false));
        buildings.attachChild(createBuilding("building", new Vector3f(-220, -120, 90), new Vector3f(9, 8, 9), ColorRGBA.Brown, false));
        buildings.attachChild(createBuilding("building", new Vector3f(-310, -120, 150), new Vector3f(3, 7, 10), ColorRGBA.Brown, false));
        buildings.attachChild(createBuilding("building", new Vector3f(-130, -50, 32), new Vector3f(4, 7, 5), ColorRGBA.Brown, false));
        buildings.attachChild(createBuilding("building", new Vector3f(70, -120, -67), new Vector3f(6, 8, 5), ColorRGBA.Brown, false));
        buildings.attachChild(createBuilding("building", new Vector3f(-100, -140, -80), new Vector3f(9, 7, 3), ColorRGBA.Brown, false));
        buildings.attachChild(createBuilding("building", new Vector3f(-225, -20, -10), new Vector3f(3, 2, 3), ColorRGBA.Brown, false));
        buildings.attachChild(createBuilding("building", new Vector3f(-238, -40, 22), new Vector3f(1, 5, 1), ColorRGBA.Brown, false));
        buildings.attachChild(createBuilding("building", new Vector3f(-175, -40, -40), new Vector3f(3, 4, 3), ColorRGBA.Brown, false));
        buildings.attachChild(createBuilding("building", new Vector3f(-275, -40, -85), new Vector3f(3, 4, 3), ColorRGBA.Brown, false));
        buildings.attachChild(createBuilding("building", new Vector3f(-202, -40, -85), new Vector3f(5, 4, 1), ColorRGBA.Brown, false));
        buildings.attachChild(createBuilding("building", new Vector3f(-275, -40, -65), new Vector3f(1, 4, 7), ColorRGBA.Brown, false));
                buildings.attachChild(createBuilding("building", new Vector3f(-285, -40, -30), new Vector3f(1, 4, 7), ColorRGBA.Brown, false));

//        buildings.attachChild(createBuilding("building", new Vector3f(123, -20, -6), new Vector3f(43, 115, 57), ColorRGBA.LightGray));
//        buildings.attachChild(createBuilding("building", new Vector3f(134, -20, 64), new Vector3f(51, 34, 13), ColorRGBA.Brown));
//        buildings.attachChild(createBuilding("building", new Vector3f(113, -20, 100), new Vector3f(23, 36, 23), ColorRGBA.Brown));
//
//        //cars
        Node cars = new Node("Cars");
//        float rot = (float) (Math.PI / 2);
//        cars.attachChild(createCar("car1", new Vector3f(45, 0, -100), new Vector3f(40, 40, 40), rot, ColorRGBA.Blue));
//        cars.attachChild(createCar("car1", new Vector3f(45, 0, 0), new Vector3f(40, 40, 40), rot, ColorRGBA.Blue));
//        cars.attachChild(createCar("car1", new Vector3f(45, 0, -50), new Vector3f(40, 40, 40), rot, ColorRGBA.Blue));
//        cars.attachChild(createCar("car1", new Vector3f(45, 0, 150), new Vector3f(40, 40, 40), rot, ColorRGBA.Blue));
//        cars.attachChild(createCar("car1", new Vector3f(-45, 0, 100), new Vector3f(40, 40, 40), -rot, ColorRGBA.Blue));
//        cars.attachChild(createCar("car1", new Vector3f(-45, 0, 50), new Vector3f(40, 40, 40), -rot, ColorRGBA.Blue));
//        cars.attachChild(createCar("car1", new Vector3f(-45, 0, 0), new Vector3f(40, 40, 40), -rot, ColorRGBA.Blue));
//        cars.attachChild(createCar("car1", new Vector3f(-45, 0, -50), new Vector3f(40, 40, 40), rot, ColorRGBA.Blue));
        
        cars.attachChild(createCar("car1", new Vector3f(-30, 0, 260), new Vector3f(40, 40, 25), (float) -Math.PI / 4, ColorRGBA.Blue));

//
//        //coins
        Node coins = new Node("Coins");
        coins.attachChild(createCoin("coin1", new Vector3f(-95, 2, 280)));
        coins.attachChild(createCoin("coin1b", new Vector3f(-56, 2, 291)));
        coins.attachChild(createCoin("coin1c", new Vector3f(-63, 20, 220)));
        coins.attachChild(createCoin("coin1c", new Vector3f(-33, 2, 267)));
        coins.attachChild(createCoin("coin2", new Vector3f(151, 56, 120)));
        coins.attachChild(createCoin("coin3", new Vector3f(-278, 2f, 124)));
        coins.attachChild(createCoin("coin4", new Vector3f(-166, 2f, 90)));
        coins.attachChild(createCoin("coin5", new Vector3f(181, 2f, -4)));
        coins.attachChild(createCoin("coin6", new Vector3f(109, 2f, 228)));
        coins.attachChild(createCoin("coin7", new Vector3f(-223, 2, 10)));
//        coins.attachChild(createCoin("coin8", new Vector3f(38, 3.5f, 70)));
//        coins.attachChild(createCoin("coin9", new Vector3f(38, 3.5f, 90)));
//        coins.attachChild(createCoin("coin10", new Vector3f(38, 3.5f, 23)));
        initCoinText();
//        
//        //Invisible Walls to prevent User from falling
//        Node wallNode = new Node("Wall");
//        wallNode.attachChild(createWall("wall", new Vector3f(-250, 0, 0), new Vector3f(10, 20, 250)));  // Left wall
//        wallNode.attachChild(createWall("wall", new Vector3f(250, 0, 0), new Vector3f(10, 20, 250)));   // Right wall
//        wallNode.attachChild(createWall("wall", new Vector3f(0, 0, -250), new Vector3f(250, 20, 10)));   // Front wall
//        wallNode.attachChild(createWall("wall", new Vector3f(0, 0, 250), new Vector3f(250, 20, 10)));    // Back wall
//        
//        //attach all of the above to the scene

        //set shadows 
        rootNode.attachChild(ground);
        rootNode.attachChild(boxes);
        rootNode.attachChild(buildings);
        rootNode.attachChild(cars);
        rootNode.attachChild(coins);
//        rootNode.attachChild(wallNode);
    }
    
    // adds general lighting (ambient, sun, some points)
    private void setupLights() {
        // add lighting
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(.1f));
        rootNode.addLight(ambient);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1,-1,-1).normalizeLocal());
        sun.setColor(ColorRGBA.White.mult(0.3f));
        rootNode.addLight(sun);
        
        //Shadows
//        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(this.assetManager, SHADOWMAP_SIZE, 3);
//        dlsr.setLight(sun);
//        this.viewPort.addProcessor(dlsr);
//        
//        
//        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 3);
//        dlsf.setLight(sun);
//        dlsf.setEnabled(true);
//        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
//        fpp.addFilter(dlsf);
//        viewPort.addProcessor(fpp);

        // Point light (can be added near a character or object)
        PointLight pointLight = new PointLight();
        pointLight.setPosition(new Vector3f(66, 3, 156));  // Position the light in space
        pointLight.setColor(ColorRGBA.Yellow.mult(2.0f));  // Set light color (white here)
        pointLight.setRadius(100f);  // Set the radius of the light
        app.getRootNode().addLight(pointLight);  // Add point light to the root node
        
        PointLight pointLight2 = new PointLight();
        pointLight2.setPosition(new Vector3f(-12, 50, -39));  // Position the light in space
        pointLight2.setColor(ColorRGBA.Yellow.mult(100.0f));  // Set light color (white here)
        pointLight2.setRadius(50);  // Set the radius of the light
        app.getRootNode().addLight(pointLight2);  // Add point light to the root node
        
        PointLight pointLight3 = new PointLight();
        pointLight3.setPosition(new Vector3f(-241, 10, -57));  // Position the light in space
        pointLight3.setColor(ColorRGBA.Red.mult(10.0f));  // Set light color (white here)
        pointLight3.setRadius(100);  // Set the radius of the light
        app.getRootNode().addLight(pointLight3);  // Add point light to the root node
        
        
        //shadws for 3 point lights
        int shadowMapSize = 2048; // Adjust for quality
        PointLightShadowRenderer pointShadowRenderer = new PointLightShadowRenderer(assetManager, shadowMapSize);
        pointShadowRenderer.setLight(pointLight);  // Assign to first light
        viewPort.addProcessor(pointShadowRenderer);
        
        PointLightShadowRenderer pointShadowRenderer2 = new PointLightShadowRenderer(assetManager, shadowMapSize);
        pointShadowRenderer2.setLight(pointLight2);  // Assign to first light
        viewPort.addProcessor(pointShadowRenderer2);

        PointLightShadowRenderer pointShadowRenderer3 = new PointLightShadowRenderer(assetManager, shadowMapSize);
        pointShadowRenderer3.setLight(pointLight3);  // Assign to first light
        viewPort.addProcessor(pointShadowRenderer3);
    }
    
    private void initEnemies() {
        Quaternion rotationNorth = new Quaternion();
        Quaternion rotationWest = new Quaternion();
        Quaternion rotationSouth = new Quaternion();
        Quaternion rotationEast = new Quaternion();
        rotationWest.fromAngleAxis(FastMath.PI / 2, Vector3f.UNIT_Y);
        rotationNorth.fromAngleAxis(FastMath.PI, Vector3f.UNIT_Y);
        rotationEast.fromAngleAxis(- FastMath.PI / 2, Vector3f.UNIT_Y);
        rotationSouth.fromAngleAxis(0, Vector3f.UNIT_Y);
        loadMonkey(new Vector3f(84, 0, 125), rotationEast);
        loadMonkey(new Vector3f(75, 0, -34), rotationSouth);
        loadMonkey(new Vector3f(-10, 0, -34), rotationSouth);
        loadbabyMonkey(new Vector3f(-63, 0, 290), rotationNorth);       
        loadMonkey(new Vector3f(-100, 0, 220), rotationWest);
        loadMonkey(new Vector3f(-115, 0, 280), rotationNorth);       
        loadMonkey(new Vector3f(-30, 45, 280), rotationNorth);
        loadMonkey(new Vector3f(-145, 45, 280), rotationNorth);
        loadMonkey(new Vector3f(-117, 0, 80), rotationEast);
        loadMonkey(new Vector3f(-155, 0, 67), rotationSouth);
        loadMonkey(new Vector3f(-250, 0, 240), rotationWest);
        loadMonkey(new Vector3f(-256, 0, 29), rotationWest);
        loadMonkey(new Vector3f(-253, 0, -45), rotationSouth);

    }
    
    // loads array of monkeys into scene to animate default idle
    private void loadMonkey(Vector3f loc, Quaternion rot) {
        //Monkey Enemies
        Node monkeyTriadNode = new Node("MonkeyTriad");            
        Spatial mymodel = assetManager.loadModel("Textures/MonkeyEnemy/Jaime.j3o");
        Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
//        Texture normalMap = assetManager.loadTexture("Textures/MonkeyEnemy/NormalMap.png");
//        Texture diffuseMap = assetManager.loadTexture("Textures/MonkeyEnemy/diffuseMap.png");        
        TextureKey diffuseMap = new TextureKey("Textures/MonkeyEnemy/diffuseMap.jpg", false);
        material.setTexture("DiffuseMap",assetManager.loadTexture(diffuseMap));       
        // Set the normal map texture to the material
//        material.setTexture("NormalMap", normalMap);
//        material.setTexture("ColorMap", diffuseMap);
        mymodel.setMaterial(material);
        animateModel = new AnimateTriad(assetManager);
        animateModel.createInstance(mymodel);

        mymodel.addControl(new ExplodeCarControl(health, new Vector3f(0, 6, 0)));

        mymodel.setLocalTranslation(loc);
        mymodel.setLocalRotation(rot);
        mymodel.setLocalScale(8f);
        mymodel.rotate(0, (float) Math.PI, 0);


        monkeyTriadNode.attachChild(mymodel);

        // idle by default
        animateModel.playAnimationAll("Idle", 1.0f);

        //physics
        RigidBodyControl enemyControl = new RigidBodyControl(1.0f);
        mymodel.addControl(enemyControl);
        this.bulletAppState.getPhysicsSpace().add(enemyControl);

        // monkey shooting
        MonkeyAi ai = new MonkeyAi(playerNode, this.bulletAppState, assetManager, rootNode);
        mymodel.addControl(ai);     
        
        //shadow mode
        rootNode.attachChild(monkeyTriadNode);
    }
    
        private void loadbabyMonkey(Vector3f loc, Quaternion rot) {
        //Monkey Enemies
        Node monkeyTriadNode = new Node("MonkeyTriad");            
        Spatial mymodel = assetManager.loadModel("Textures/MonkeyEnemy/Jaime.j3o");
        Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
//        Texture normalMap = assetManager.loadTexture("Textures/MonkeyEnemy/NormalMap.png");
//        Texture diffuseMap = assetManager.loadTexture("Textures/MonkeyEnemy/diffuseMap.png");        
        TextureKey diffuseMap = new TextureKey("Textures/MonkeyEnemy/diffuseMap.jpg", false);
        material.setTexture("DiffuseMap",assetManager.loadTexture(diffuseMap));       
        // Set the normal map texture to the material
//        material.setTexture("NormalMap", normalMap);
//        material.setTexture("ColorMap", diffuseMap);
        mymodel.setMaterial(material);
//        animateModel = new AnimateTriad(assetManager);
//        animateModel.createInstance(mymodel);

        mymodel.addControl(new ExplodeCarControl(health, new Vector3f(0, 6, 0)));

        mymodel.setLocalTranslation(loc);
        mymodel.setLocalRotation(rot);
        mymodel.setLocalScale(2f);
        mymodel.rotate(0, (float) Math.PI, 0);


        monkeyTriadNode.attachChild(mymodel);

        // idle by default
        //animateModel.playAnimationAll("Idle", 1.0f);

        //physics
        RigidBodyControl enemyControl = new RigidBodyControl(1.0f);
        mymodel.addControl(enemyControl);
        this.bulletAppState.getPhysicsSpace().add(enemyControl);

        // monkey shooting
        MonkeyAi ai = new MonkeyAi(playerNode, this.bulletAppState, assetManager, rootNode);
        mymodel.addControl(ai); 
        
        
        rootNode.attachChild(monkeyTriadNode);
    }
    
    private void initCamera() {
        camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(0, 4f, 0)); // Set camera height relative to player
        playerNode.attachChild(camNode);
        camNode.getCamera().setFrustumNear(0.5f); // changes FOV (and makes gun not clip into camera)
        
        // gun model
        playerGun = assetManager.loadModel("Textures/Gun/m4.j3o");
        playerGun.setLocalScale(Vector3f.UNIT_XYZ.mult(10));
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", ColorRGBA.Gray);
        mat.setColor("Ambient", ColorRGBA.Gray);
        
        Node gunNode = new Node("Gun Node");
        gunNode.attachChild(playerGun);
        gunNode.setMaterial(mat);
        gunNode.move(-.5f, -.5f, 1);
        gunNode.scale(1);
        this.camNode.attachChild(gunNode);

        inputManager.addMapping("MouseMoveLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("MouseMoveRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("MouseMoveUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("MouseMoveDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addListener(analogListener, "MouseMoveLeft", "MouseMoveRight", "MouseMoveUp", "MouseMoveDown");
    }
    
    private void initUIElements() {
        // crosshair
        Geometry c = new Geometry("crosshair", boxMesh);
        Material crosshairMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        crosshairMaterial.setColor("Color", ColorRGBA.Red);
        c.setMaterial(crosshairMaterial);
        c.setLocalTranslation(app_width / 2, app_height / 2, 0);
        this.app.getGuiNode().attachChild(c); // attach to 2D user interface
        
        //health bar
        Vector3f healthBarPosition = new Vector3f(hb_width + 40, app_height * 9 / 10, 0);
        healthBar = new Geometry("health bar", boxMesh);
        Material healthBarMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        healthBarMaterial.setColor("Color", ColorRGBA.Red);
        healthBar.setMaterial(healthBarMaterial);
        healthBar.setLocalTranslation(healthBarPosition);
        healthBar.setLocalScale(hb_width, hb_height, 2);
        this.app.getGuiNode().attachChild(healthBar);
        
        //tint for taking hit
        Vector3f origin = new Vector3f(app_width / 2, app_height / 2, 0);  // Center position
        Box screenBox = new Box(app_width / 2, app_height / 2, 1);  // Width, height, depth
        redTint = new Geometry("RedTint", screenBox);
        damageMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        damageMat.setColor("Color", new ColorRGBA(1, 0, 0, 0.0f));  // Red with 20% opacity
        damageMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);  // Enable transparency
        redTint.setMaterial(damageMat);
        redTint.setLocalTranslation(origin);
        this.app.getGuiNode().attachChild(redTint);
    }
    
    private void initFireElements() {
        Material fireMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        //fire next to spawn car
        ParticleEmitter fireEmitter3 = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 15);
        rootNode.attachChild(fireEmitter3);
        fireMat.setTexture("Texture",assetManager.loadTexture("Effects/flame.png"));
        fireEmitter3.setMaterial(fireMat);
        fireEmitter3.setImagesX(2);
        fireEmitter3.setImagesY(2);
        fireEmitter3.setSelectRandomImage(true);
        fireEmitter3.setRandomAngle(true);
        fireEmitter3.setStartColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f));
        fireEmitter3.setEndColor(new ColorRGBA(0.1f, 0.1f, 0.1f, 0f));
        fireEmitter3.setGravity(0,0,0);
        fireEmitter3.getParticleInfluencer().setVelocityVariation(0.2f);
        fireEmitter3.getParticleInfluencer().setInitialVelocity(new Vector3f(0,5f,0));
        fireEmitter3.setLowLife(0.5f);
        fireEmitter3.setHighLife(3f);
        fireEmitter3.setStartSize(5f);
        fireEmitter3.setEndSize(0.5f);
        fireEmitter3.setLocalTranslation(new Vector3f(-25, 2, 267));
        //fire with damage
        fireEmitter2 = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        rootNode.attachChild(fireEmitter2);
        fireMat.setTexture("Texture",assetManager.loadTexture("Effects/flame.png"));
        fireEmitter2.setMaterial(fireMat);
        fireEmitter2.setImagesX(2);
        fireEmitter2.setImagesY(2);
        fireEmitter2.setSelectRandomImage(true);
        fireEmitter2.setRandomAngle(true);
        fireEmitter2.setStartColor(new ColorRGBA(1f, 1f, .5f, 1f));
        fireEmitter2.setEndColor(new ColorRGBA(1f, 0f, 0f, 0f));
        fireEmitter2.setGravity(0,0,0);
        fireEmitter2.getParticleInfluencer().setVelocityVariation(0.2f);
        fireEmitter2.getParticleInfluencer().setInitialVelocity(new Vector3f(0,5f,0));
        fireEmitter2.setLowLife(0.5f);
        fireEmitter2.setHighLife(10f);
        fireEmitter2.setStartSize(10f);
        fireEmitter2.setEndSize(0.5f);
        fireEmitter2.setLocalTranslation(new Vector3f(65f, 2f, 145f));
        demoHurtBox = new BoundingBox(fireEmitter2.getWorldTranslation(), 4, 10, 4);
    }
     
    private void initFogElements() {
        fpp = new FilterPostProcessor(assetManager);
        this.app.getViewPort().addProcessor(fpp);
        //Initialize the FogFilter and add it to the FilterPostProcesor.
        fogFilter = new FogFilter();
        fogFilter.setFogDistance(155);
        fogFilter.setFogDensity(0.1f);
        fogFilter.setFogColor(new ColorRGBA(0.1f, 0.1f, 0.1f, 0.1f));
        fpp.addFilter(fogFilter);
    }
    
    private void initSoundElements() {
        //ambience
        ambientSound = new AudioNode(assetManager, "Sounds/city-ambience.wav", AudioData.DataType.Stream);
        ambientSound.setPositional(false); // Sound is everywhere, not localized
        ambientSound.setLooping(true);    // Play continuously
        ambientSound.setVolume(0.5f);     // Adjust volume
        rootNode.attachChild(ambientSound);
        ambientSound.play();

        //hit Audio
        hitSound1 = new AudioNode(assetManager, "Sounds/ouch1.wav", AudioData.DataType.Stream);
        hitSound1.setPositional(false); // Sound is everywhere, not localized
        hitSound1.setLooping(false);    // Play continuously
        hitSound1.setVolume(0.6f);     // Adjust volume
        hitSound2 = new AudioNode(assetManager, "Sounds/ouch2.wav", AudioData.DataType.Stream);
        hitSound2.setPositional(false); // Sound is everywhere, not localized
        hitSound2.setLooping(false);    // Play continuously
        hitSound2.setVolume(0.6f);     // Adjust volume
        hitSound3 = new AudioNode(assetManager, "Sounds/ouch3.wav", AudioData.DataType.Stream);
        hitSound3.setPositional(false); // Sound is everywhere, not localized
        hitSound3.setLooping(false);    // Play continuously
        hitSound3.setVolume(0.6f);     // Adjust volume
        rootNode.attachChild(hitSound1); 
        rootNode.attachChild(hitSound2); 
        rootNode.attachChild(hitSound3); 
    }
        
    @Override
    public void update(float tpf) {
        playerUpdate(tpf);
        healthUpdate();
        animateModel.setPlayerPosition(playerNode.getWorldTranslation());
        Vector3f playerCoords = playerNode.getWorldTranslation();
        
//        System.out.println("Player Coordinates: " + playerCoords);
//        Vector3f camDirection = camNode.getLocalRotation().mult(Vector3f.UNIT_Z);
//         Print the camera's direction to the console
//        System.out.println("Camera Direction: " + camDirection);
        
        updateCoinText();
//        updateTextColorBasedOnDirection();

        //cooldown timers
        timeSinceLastDamage += tpf;
        shootTimer -= tpf;
        
        //fire damage
        if (playerBox.intersects(demoHurtBox)) {
            if (timeSinceLastDamage >= fireDamageCooldown) {
                playerTakeDamage(1);
                timeSinceLastDamage += tpf; //make it a little faster for fire damage
            }
        }
        
        //damage ui
        if (opacity > 0.0f) {
            float newOpacity = opacity - tpf;
            damageMat.setColor("Color", new ColorRGBA(1, 0, 0, newOpacity));  // Red with 20% opacity
            opacity = newOpacity;
        }
        
        //fog check
        Vector3f position = playerNode.getWorldTranslation();
        boolean isInFogArea = position.x >= fogEndX && position.x <= fogStartX &&
                       position.y >= fogStartY && position.y <= fogEndY &&
                       position.z >= fogEndZ && position.z <= fogStartZ;
        if (isInFogArea) {
            if (fogFilter.getFogDensity() < maxFogFloat) {
                fogFilter.setFogDensity(fogFilter.getFogDensity() + 0.1f);  // Adjust density as needed
            }
        } else {
            if (fogFilter.getFogDensity() > 0.1f) {
                fogFilter.setFogDensity(fogFilter.getFogDensity() - 0.1f); 
            }
        }
        //check if winConMet
        if (PlayerPhysControl.coinsCollected >= coinsNeededToWin) {
            PlayerPhysControl.coinsCollected = 0;
            endGame(true);        
        }
        
        // animate gun. Plug into desmos to see nature of function (note: shootTimer is .2 -> -inf after shot)
        float gunScale = Math.max(.9f, 1f + shootTimer / 2f);
        playerGun.setLocalScale(10, 10, gunScale * 10f);
    }
    
    // updates player location and rotation each frame
    private void playerUpdate(float tpf) {
        //update position
        playerBox.setCenter(playerNode.getWorldTranslation());
        // Get current forward and left vectors of the playerNode:
        Vector3f modelForwardDir = playerNode.getWorldRotation().mult(Vector3f.UNIT_Z);
        Vector3f modelLeftDir = playerNode.getWorldRotation().mult(Vector3f.UNIT_X);
        // Determine the change in direction
        Vector3f walkDirection = new Vector3f(0,0,0);
        if (forward) { walkDirection.addLocal(modelForwardDir.mult(speed)); }
        if (backward) {walkDirection.addLocal(modelForwardDir.mult(speed).negate()); }
        if (moveLeft) { walkDirection.addLocal(modelLeftDir.mult(speed)); }
        if (moveRight) { walkDirection.addLocal(modelLeftDir.mult(speed).negate()); }
        playerControl.setWalkDirection(walkDirection); // walk!
        playerControl.setViewDirection(modelForwardDir); // turn!
    }
    
    private void healthUpdate() {
        if (health > 0) {
            float healthPercentage = (float) health / max_health;
            healthBar.setLocalScale(hb_width * healthPercentage, hb_height, 2);
            float amountToShiftLeft = hb_width / max_health * (max_health - health);
            healthBar.setLocalTranslation(hb_width + 40 - amountToShiftLeft, this.app.getContext().getSettings().getHeight() * 9 / 10, 0);
        } else if (health == 0){
//            health = -1;
            endGame(false);
        }
    }
    
    public void playerTakeDamage(int amountOfDamage) {
        health -= amountOfDamage;
        Random rand = new Random();
        int randInd = rand.nextInt(3) + 1;
        switch (randInd) {
            case 1 -> hitSound1.play();
            case 2 -> hitSound2.play();
            default -> hitSound3.play();
        }
        // Reset the invuln timer
        timeSinceLastDamage = 0.0f;
        opacity = 0.2f;
        damageMat.setColor("Color", new ColorRGBA(1, 0, 0, 0.2f));  // Red with 20% opacity
    }
    
    // attempt to shoot a car located at the center of the screen, damaging it.
    private void shoot() {
        if (shootTimer > 0) return;
        shootTimer = shootRate; //taking shot so reset shooting cooldown
        
        CollisionResults results = new CollisionResults();        
        // set ray to center of first person perspective and shoot ray
        ray.setOrigin(cam.getLocation());
        ray.setDirection(cam.getDirection());
        rootNode.collideWith(ray, results);
        
        if (results.size() > 0) {
            // if hit...
            ParticleEmitter fireEmitter = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
            Material fireMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
            rootNode.attachChild(fireEmitter);
            fireMat.setTexture("Texture",assetManager.loadTexture("Effects/flame.png"));
            fireEmitter.setMaterial(fireMat);
            fireEmitter.setImagesX(2);
            fireEmitter.setImagesY(2);
            fireEmitter.setSelectRandomImage(true);
            fireEmitter.setRandomAngle(true);
            fireEmitter.setStartColor(new ColorRGBA(0.5f, 1f, 1f, 1f));
            fireEmitter.setEndColor(new ColorRGBA(0f, 0f, 1f, 0f));
            fireEmitter.setGravity(0,0,0);
            fireEmitter.getParticleInfluencer().setVelocityVariation(0.2f);
            fireEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0,5f,0));
            fireEmitter.setLowLife(0.5f);
            fireEmitter.setHighLife(1f);
            fireEmitter.setStartSize(0.5f);
            fireEmitter.setEndSize(0.25f);
            fireEmitter.setLocalTranslation(results.getClosestCollision().getContactPoint());
            
            // get parent because the geometry is a child of the car node that has the ExplodeCarControl class
            Spatial target = results.getClosestCollision().getGeometry().getParent();
            ExplodeCarControl explodeCarControl = target.getControl(ExplodeCarControl.class);
            if (explodeCarControl != null) {
                // if hit a car which can explode...
                explodeCarControl.damage(1f);
            }
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        if (this.app != null) { 
            app.getRootNode().detachAllChildren();
            app.getGuiNode().detachAllChildren();
//            rootNode.detachChild(soundNode);
//            fireNode.stop();
        }
        speed = 50;
    }
        
    // map input to shoot
    private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float intensity, float tpf) {
            switch (name) {
                case MAPPING_SHOOT:
                    System.out.println("shooting!");
                    shoot();
                    break;
                case "MouseMoveLeft":
                    playerNode.rotate(0, intensity, 0); // Rotate player left around Y-axis
                    break;
                case "MouseMoveRight":
                    playerNode.rotate(0, -intensity, 0); // Rotate player right around Y-axis
                    break;
                case "MouseMoveUp":
                    Vector3f camDirection = camNode.getLocalRotation().mult(Vector3f.UNIT_Z);
                    if (camDirection.y <= 0.9) {
                        camNode.rotate(-intensity, 0, 0);
                    }
                    break;
                case "MouseMoveDown":
                    Vector3f camDirectionAgain = camNode.getLocalRotation().mult(Vector3f.UNIT_Z);
                    if (camDirectionAgain.y >= -0.9) {
                        camNode.rotate(intensity, 0, 0);
                    }
                    break;
                default: {
                    
                    }
            }
           
        }
    };
    
    // map input to pick up items (and damage player)
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("Restart") && isPressed) {
                System.out.println("restarting the game");
                stateManager.attach(new StartMenu(mainApp));
                app.getGuiNode().detachAllChildren();    
            }
        }
    };
    
    // handle inputs to control player/camera
    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        switch (binding) {
            case "Left" -> moveLeft = isPressed;
            case "Right" -> moveRight = isPressed;
            case "Forward" -> forward = isPressed;
            case "Back" -> backward = isPressed;
            case "Jump" -> playerControl.specialJump();
            default -> {
            }
        }
    } 
        
    // helper function to quickly create a box geometry with a given name, location, scale, and color. Uses phong shading
    private Node createBox(String name, Vector3f loc, Vector3f scale, ColorRGBA color) {
        Node node = new Node(name);
        Geometry geom = new Geometry(name + "_geom", boxMesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);  // Ensure we use custom material colors
        mat.setColor("Diffuse", color);
        mat.setColor("Ambient", color.mult(0.3f));
        geom.setMaterial(mat);
        geom.setLocalTranslation(loc);
        geom.setLocalScale(scale);
        node.attachChild(geom);
        
        RigidBodyControl boxControl = new RigidBodyControl(0f);
        node.addControl(boxControl);
        bulletAppState.getPhysicsSpace().add(boxControl);
        
        //set shadow
        

                
        return node;
    }
    
        // helper function to quickly create a box geometry with a given name, location, scale, and color. Uses phong shading
    private Node createBoxRotate(String name, Vector3f loc, Vector3f scale, ColorRGBA color, Quaternion rotation) {
        Node node = new Node(name);
        Geometry geom = new Geometry(name + "_geom", boxMesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);  // Ensure we use custom material colors
        mat.setColor("Diffuse", color);
        mat.setColor("Ambient", color.mult(0.3f));
        geom.setMaterial(mat);
        geom.setLocalTranslation(loc);
        geom.setLocalScale(scale);
        node.attachChild(geom);
        geom.setLocalRotation(rotation);        
        RigidBodyControl boxControl = new RigidBodyControl(0f);
        node.addControl(boxControl);
        bulletAppState.getPhysicsSpace().add(boxControl);
                
        return node;
    }
    
        // helper function to quickly create a box geometry with a given name, location, scale, and color. Uses phong shading
    private Node createBoxOldLighting(String name, Vector3f loc, Vector3f scale, ColorRGBA color) {
        Node node = new Node(name);
        Geometry geom = new Geometry(name + "_geom", boxMesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geom.setMaterial(mat);
        geom.setLocalTranslation(loc);
        geom.setLocalScale(scale);
        node.attachChild(geom);
        
        RigidBodyControl boxControl = new RigidBodyControl(0f);
        node.addControl(boxControl);
        bulletAppState.getPhysicsSpace().add(boxControl);
                
        return node;
    }
    
    private Node createWall(String name, Vector3f loc, Vector3f scale) {
        ColorRGBA color = new ColorRGBA(0,0,0,0);
        Node node = new Node(name);
        Geometry geom = new Geometry(name + "_geom", boxMesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.getAdditionalRenderState().setDepthWrite(false);
        
        
        
        geom.setMaterial(mat);
        geom.setLocalTranslation(loc);
        geom.setLocalScale(scale);
        node.attachChild(geom);
        
        RigidBodyControl boxControl = new RigidBodyControl(0f);
        node.addControl(boxControl);
        bulletAppState.getPhysicsSpace().add(boxControl);
                
        return node;
    }
    
    // helper function to quickly create a car (similar to createBox, setting name, location, scale, color, with addition of rotation).
    // The scale determines the size of the main body of the car
    private Node createCar(String name, Vector3f loc, Vector3f scale, float rot, ColorRGBA color) {
        Node car = new Node(name);

        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Vehicles/texture-palette.png"));
        
        // load random model (car1, car2, car3)
        Random rand = new Random();
        int carIndex = rand.nextInt(3) + 1; // [0, 2] + 1 -> [1, 3]
        
        Spatial myModel = assetManager.loadModel("Textures/Vehicles/car" + carIndex + ".j3o");
        myModel.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        myModel.setLocalScale(Vector3f.UNIT_XYZ.mult(3));
        myModel.setMaterial(mat);
        myModel.rotate(0, (float)Math.toRadians(90), 0);
        myModel.move(0, 1, 0);
        
        SpotLight spot = new SpotLight();
        spot.setSpotRange(100);
        spot.setSpotOuterAngle(20 * FastMath.DEG_TO_RAD);
        spot.setSpotInnerAngle(15 * FastMath.DEG_TO_RAD);
        spot.setDirection(Vector3f.UNIT_Z);
        car.addLight(spot);
        


        // move the car node and rotate it
        car.attachChild(myModel);
        car.setLocalTranslation(loc);
        car.rotate(0, rot, 0);
        
        // add explode controller
//        car.addControl(new ExplodeCarControl());
        
        RigidBodyControl coinControl = new RigidBodyControl(0f); // Static coin
        car.addControl(coinControl);
        bulletAppState.getPhysicsSpace().add(coinControl);
        

        return car;
    }
    
    // loads building model into scene at location, scale, and color
    private Node createBuilding(String name, Vector3f loc, Vector3f scale, ColorRGBA color, boolean weirdscale) {
        // Load the model
        Spatial building = assetManager.loadModel("Textures/Buildings/ResBuilding.j3o");
        
        // Ensure the building is a Node
        Node buildingNode;
        if (building instanceof Node node) {
            buildingNode = node;
        } else {
            // Wrap the Spatial in a Node if it's not already one
            buildingNode = new Node(name);
            buildingNode.attachChild(building);
        }

        // Apply transformations
        buildingNode.setLocalTranslation(loc);
        if(weirdscale){ 
            buildingNode.setLocalScale(scale.normalize().mult(5));
        } else {
            buildingNode.setLocalScale(scale);
        }
        // Optionally apply color if the model supports it (e.g., via material)
        if (building instanceof Geometry geom) {
            Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            //mat.setColor("Color", color); // Ensure the material uses a color property
            geom.setMaterial(mat);
        } else {
            for (Spatial child : buildingNode.getChildren()) {
                if (child instanceof Geometry geom) {
                    Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
                    //mat.setColor("Color", color);
                    geom.setMaterial(mat);
                }
            }
        }
       
        RigidBodyControl buildingControl = new RigidBodyControl(0f);
        buildingNode.addControl(buildingControl);
        this.bulletAppState.getPhysicsSpace().add(buildingControl);
        
        buildingNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        
        return buildingNode;
    }

    // creates pickup-able coin at a given location with the given name
    private Geometry createCoin(String name, Vector3f loc) {
        Geometry coin = new Geometry(name, cylinderMesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", ColorRGBA.Yellow);
        mat.setColor("Ambient", ColorRGBA.Gray);
        coin.setMaterial(mat);
        coin.setLocalTranslation(loc);
        coin.setLocalScale(1, 1, .1f);
        
        coin.addControl(new CoinPickupControl(assetManager));
        
        RigidBodyControl coinControl = new RigidBodyControl(0f);
        coin.addControl(coinControl);
        this.bulletAppState.getPhysicsSpace().add(coinControl);
        

        return coin;
    }
    
    private void updateCoinText() {
        int coinsCollected = playerControl.getCoins();
        positionText.setText("Collected: " + coinsCollected + "/" + coinsNeededToWin);
      
    }
    
//TODO: Not important but change text of coins picked up display
//    private void updateTextColorBasedOnDirection() {
//        // Get the direction the player is facing
//        Vector3f direction = this.playerNode.getWorldRotation().getRotationColumn(2);
//        Vector3f position = this.playerNode.getWorldTranslation();
//        Ray ray_face = new Ray();
//        ray.setOrigin(position);
//        ray.setDirection(direction);
//        ray.setLimit(100f);
//        CollisionResults results = new CollisionResults();
//        this.rootNode.collideWith(ray_face, results);
//        
//        if (results.size() > 0) {
//            // The ray hit something
//            Spatial hitObject = results.getClosestCollision().getGeometry();
//
//            // If the hit object is a building (assumed to have a specific name or tag), set text to black
//            if (hitObject.getName().contains("building")) {
//                positionText.setColor(ColorRGBA.Black);
//            } else {
//                // If the player is looking at the sky or other dark areas, set text to white
//                positionText.setColor(ColorRGBA.White);
//            }
//        }
//    }
    
    private void initCoinText() {
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        positionText = new BitmapText(font, false);
        positionText.setSize(font.getCharSet().getRenderedSize());
        positionText.setLocalTranslation(10, cam.getHeight() - 10, 0); // Top-left corner
        positionText.setColor(ColorRGBA.Black);
        this.app.getGuiNode().attachChild(positionText);

        // Update the initial position text
        updateCoinText();
    }
    
    public void endGame(boolean win) {
        speed = 0;
        ambientSound.stop();
        mainApp.endGame(win);
    }
    
}
