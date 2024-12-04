package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
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
import java.util.Random;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioData;

/**
 *
 * AppState in charge of running the demo level
 * 
 * @author Trevor Black & Liam Finn & Samuel Muzac
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
    private CameraNode camNode;
    private Node rootNode;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private InputManager inputManager;
    
    // reused variables
    private final Ray ray = new Ray();
    private static final Box boxMesh = new Box(Vector3f.ZERO, 1, 1, 1);
    private static final Cylinder cylinderMesh = new Cylinder(10, 10, 1, 1, true);
    private int app_width, app_height;
    
    private BulletAppState bulletAppState;
    
    // player variables
    private Node playerNode;
    private PlayerPhysControl playerControl;
    
    private Geometry healthBar;
    private final float max_health = 10;
    private float health = max_health;
    private float hb_width, hb_height;
    
    private final Vector3f walkDirection = new Vector3f(0,0,0);
    private final Vector3f viewDirection = new Vector3f(0,0,1);
    private boolean moveLeft = false, moveRight = false, forward = false, backward = false;
    private float speed=30;
    
    private Spatial playerGun;
    private float shootRate = .2f;
    private float shootTimer = 0f;

    private int coinsCollected = 0;
    private AnimateTriad animateModel;
    
    //damage system
    private BoundingBox playerBox;
    private BoundingBox demoHurtBox;
    private float fireDamageCooldown = 0.3f;  // Cooldown period in seconds (1 second)
    private float timeSinceLastDamage = 0.0f;
    
    //fog
    private FilterPostProcessor fpp;
    private FogFilter fogFilter;
    // Define the bounds where fog should be active
    private final float fogStartX = -20f;
    private final float fogEndX = -180f;
    private final float fogStartY = -50f;
    private final float fogEndY = 50f;
    private final float fogStartZ = 110f;
    private final float fogEndZ = -10f;


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
        app_width = this.app.getContext().getSettings().getWidth();
        app_height = this.app.getContext().getSettings().getHeight();
        hb_width = app_width / 8;
        hb_height = app_height / 20;
        
//        // key mappings
        initKeyMappings();
//        //player
        initPlayer();
//        //geometry
        initGeometry();
//        //lighting
        setupLights();
        //models
        createAndAnimateModels();
//        //camera
        initCamera();
        
        
        //sky
        Spatial mySky = assetManager.loadModel("Scenes/mySky.j3o");
        rootNode.attachChild(mySky);
        
        //fire
        ParticleEmitter fireEmitter = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        Material fireMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        rootNode.attachChild(fireEmitter);
        fireMat.setTexture("Texture",assetManager.loadTexture("Effects/flame.png"));
        fireEmitter.setMaterial(fireMat);
        fireEmitter.setImagesX(2);
        fireEmitter.setImagesY(2);
        fireEmitter.setSelectRandomImage(true);
        fireEmitter.setRandomAngle(true);
        fireEmitter.setStartColor(new ColorRGBA(1f, 1f, .5f, 1f));
        fireEmitter.setEndColor(new ColorRGBA(1f, 0f, 0f, 0f));
        fireEmitter.setGravity(0,0,0);
        fireEmitter.getParticleInfluencer().setVelocityVariation(0.2f);
        fireEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0,5f,0));
        fireEmitter.setLowLife(0.5f);
        fireEmitter.setHighLife(10f);
        fireEmitter.setStartSize(25f);
        fireEmitter.setEndSize(0.5f);
        fireEmitter.setLocalTranslation(new Vector3f(-54f, 110f, -90f));
        //fire with damage
        ParticleEmitter fireEmitter2 = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
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
        
        fpp = new FilterPostProcessor(assetManager);
        this.app.getViewPort().addProcessor(fpp);
        //Initialize the FogFilter and add it to the FilterPostProcesor.
        fogFilter = new FogFilter();
        fogFilter.setFogDistance(155);
        fogFilter.setFogDensity(2.0f);
        fogFilter.setFogColor(new ColorRGBA(0.1f, 0.1f, 0.1f, 0.1f));
        fpp.addFilter(fogFilter);

        // crosshair
        Geometry c = new Geometry("crosshair", boxMesh);
        Material crosshairMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        crosshairMaterial.setColor("Color", ColorRGBA.White);
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
        
        //Ambient Sound
        AudioNode ambientSound = new AudioNode(assetManager, "Sounds/city-ambience.wav", AudioData.DataType.Stream);
        ambientSound.setPositional(false); // Sound is everywhere, not localized
        ambientSound.setLooping(true);    // Play continuously
        ambientSound.setVolume(0.5f);     // Adjust volume
        rootNode.attachChild(ambientSound);
        ambientSound.play(); 
        
    }

    private void initKeyMappings() {
        this.inputManager.addMapping(MAPPING_SHOOT, TRIGGER_SHOOT);
        this.inputManager.addMapping(MAPPING_PICKUP, TRIGGER_PICKUP);
        this.inputManager.addListener(analogListener, new String[]{MAPPING_SHOOT});
        this.inputManager.addListener(actionListener, new String[]{MAPPING_PICKUP});
    }
        
            // initialize all static geometry, and group them in common nodes (ground, buildings, cars)
    private void initGeometry() {
        Node ground = new Node("Ground");
        ground.attachChild(createBox("floor", new Vector3f(0, -2, 0), new Vector3f(250, 1, 250), ColorRGBA.DarkGray));
        ground.attachChild(createBox("sidewalk1", new Vector3f(-91, -1, -19), new Vector3f(80, .5f, 185), ColorRGBA.Gray));
        ground.attachChild(createBox("sidewalk2", new Vector3f(128, -1, -19), new Vector3f(80, .5f, 185), ColorRGBA.Gray));
        
        Node buildings = new Node("Buildings");
        buildings.attachChild(createBuilding("building1", new Vector3f(-74, -20, 75), new Vector3f(23, 36, 23), ColorRGBA.Brown));
        buildings.attachChild(createBuilding("building2", new Vector3f(-70, -20, 26), new Vector3f(26, 48, 26), ColorRGBA.Brown));
        buildings.attachChild(createBuilding("building3", new Vector3f(-98, -20, -13), new Vector3f(51, 34, 13), ColorRGBA.Brown));
        buildings.attachChild(createBuilding("building1a", new Vector3f(-150, -20, 75), new Vector3f(40, 36, 26), ColorRGBA.Brown));
        buildings.attachChild(createBuilding("building2a", new Vector3f(-130, -20, 36), new Vector3f(29, 48, 21), ColorRGBA.Brown));
        buildings.attachChild(createBuilding("building3a", new Vector3f(-170, -20, 0), new Vector3f(51, 34, 13), ColorRGBA.Brown));
        buildings.attachChild(createBuilding("building5", new Vector3f(-92, -20, -146), new Vector3f(20, 23, 20), ColorRGBA.Brown));
        buildings.attachChild(createBuilding("building6", new Vector3f(95, -20, -94), new Vector3f(25, 64, 31), ColorRGBA.Brown));
        buildings.attachChild(createBuilding("building7", new Vector3f(123, -20, -6), new Vector3f(43, 115, 57), ColorRGBA.LightGray));
        buildings.attachChild(createBuilding("building8", new Vector3f(134, -20, 64), new Vector3f(51, 34, 13), ColorRGBA.Brown));
        buildings.attachChild(createBuilding("building9", new Vector3f(113, -20, 100), new Vector3f(23, 36, 23), ColorRGBA.Brown));

    
        Node cars = new Node("Cars");
        float rot = (float) (Math.PI / 2);
        cars.attachChild(createCar("car1", new Vector3f(-5, 0, 0), new Vector3f(6, 6, 12), rot, ColorRGBA.Blue));
        cars.attachChild(createCar("car1", new Vector3f(-5, 0, -37), new Vector3f(6, 6, 12), rot, ColorRGBA.Blue));
        cars.attachChild(createCar("car1", new Vector3f(-5, 0, -74), new Vector3f(6, 6, 12), rot, ColorRGBA.Blue));
        cars.attachChild(createCar("car1", new Vector3f(-5, 0, 116), new Vector3f(6, 6, 12), rot, ColorRGBA.Blue));
        cars.attachChild(createCar("car1", new Vector3f(38, 0, -63), new Vector3f(6, 6, 12), -rot, ColorRGBA.Blue));
        cars.attachChild(createCar("car1", new Vector3f(38, 0, 30), new Vector3f(6, 6, 12), -rot, ColorRGBA.Blue));
        cars.attachChild(createCar("car1", new Vector3f(38, 0, 65), new Vector3f(6, 6, 12), -rot, ColorRGBA.Blue));
        cars.attachChild(createCar("car1", new Vector3f(-133, 0, 58), new Vector3f(10, 10, 15), rot, ColorRGBA.Blue));

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
    
    private void initPlayer() {
        this.bulletAppState = new BulletAppState();
        this.stateManager.attach(this.bulletAppState);
        RigidBodyControl scenePhy = new RigidBodyControl(0f);
        this.rootNode.addControl(scenePhy);
        this.bulletAppState.getPhysicsSpace().add(this.rootNode);
        
        this.playerNode = new Node("Player");
        this.bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0,-100f, 0)); // GRAVITY
        this.playerNode.setLocalTranslation(new Vector3f(0, 6, 0));
        rootNode.attachChild(this.playerNode);
        
        this.playerControl = new PlayerPhysControl(1.5f, 4, 40f);
        this.playerControl.setJumpForce(new Vector3f(0, 1000, 0)); // JUMP FORCE
        
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
    
    // adds general lighting (ambient, sun, some points)
    private void setupLights() {
        // add lighting
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(.1f));
        rootNode.addLight(ambient);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1,0,-2));
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        // Point light (can be added near a character or object)
        PointLight pointLight = new PointLight();
        pointLight.setPosition(new Vector3f(38, 3, 30));  // Position the light in space
        pointLight.setColor(ColorRGBA.Blue);  // Set light color (white here)
        pointLight.setRadius(100f);  // Set the radius of the light
        app.getRootNode().addLight(pointLight);  // Add point light to the root node
        
        PointLight pointLight2 = new PointLight();
        pointLight.setPosition(new Vector3f(38, 3, -60));  // Position the light in space
        pointLight.setColor(ColorRGBA.Blue);  // Set light color (white here)
        pointLight.setRadius(100f);  // Set the radius of the light
        app.getRootNode().addLight(pointLight2);  // Add point light to the root node
    }
    
    private void initCamera() {
        camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(0, 4f, 0)); // Set camera height relative to player
        playerNode.attachChild(camNode);
        
        // gun model
        LoadModel lm = new LoadModel(assetManager);
        playerGun = lm.load("Textures/Gun/m4.j3o");
        playerGun.setLocalScale(Vector3f.UNIT_XYZ.mult(10));
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", ColorRGBA.Gray);
        mat.setColor("Ambient", ColorRGBA.Gray);
        
        Node gunNode = new Node("Gun Node");
        gunNode.attachChild(playerGun);
        
        gunNode.setMaterial(mat);
//        gunNode.rotate(0, (float)Math.toRadians(-90), 0);
        gunNode.move(-.5f, -.5f, 2);
        gunNode.scale(1);
        
        this.camNode.attachChild(gunNode);
        
//        float aspect = (float) cam.getWidth() / (float) cam.getHeight();
//        // Define the field of view (FOV)
//        float fov = 45f; // Field of view angle in degrees
//
//        // Calculate the frustum size at the near plane based on the FOV and aspect ratio
//        float top = (float) Math.tan(Math.toRadians(fov / 2)) * 1f;
//        float bottom = -top;
//        float right = top * aspect;
//        float left = -right;
//        
//        cam.setFrustumLeft(left);
//        cam.setFrustumRight(right);
//        cam.setFrustumTop(top);
//        cam.setFrustumBottom(bottom);
//        cam.setFrustumNear(1f);
//        cam.setFrustumFar(1000f);

        inputManager.addMapping("MouseMoveLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("MouseMoveRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("MouseMoveUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("MouseMoveDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addListener(analogListener, "MouseMoveLeft", "MouseMoveRight", "MouseMoveUp", "MouseMoveDown");
    }
    
    private void createAndAnimateModels() {
        loadMonkeyTriad();
    }
    
    // loads array of monkeys into scene to animate default idle
    private void loadMonkeyTriad() {
        //Monkey Enemies
        Node monkeyTriadNode = new Node("MonkeyTriad");
        for (int i = 0; i < 3; i++) {
            
            LoadModel loadModel = new LoadModel(assetManager);
            Spatial mymodel = loadModel.load("Textures/MonkeyEnemy/Jaime.j3o");
            animateModel = new AnimateTriad(assetManager);
            animateModel.createInstance(mymodel);
            
            mymodel.addControl(new ExplodeCarControl());
            
            mymodel.setLocalTranslation(i + 20, 0, 0);
            mymodel.setLocalScale(1f);
            mymodel.rotate(0, (float) Math.PI, 0);
            
            monkeyTriadNode.attachChild(mymodel);

            // idle by default
            animateModel.playAnimationAll("Idle", 1.0f);
        }
        
        rootNode.attachChild(monkeyTriadNode);
    }
    
    @Override
    public void update(float tpf) {
        cameraUpdate(tpf);
        playerUpdate(tpf);
        healthUpdate();
        
        shootTimer -= tpf;
        
        Vector3f position = playerNode.getWorldTranslation();
        String formattedPosition = String.format("Player position: (%.2f, %.2f, %.2f)", 
            position.x, position.y, position.z);
//        System.out.println(formattedPosition);
        
        //fire damage
        timeSinceLastDamage += tpf;
        if (playerBox.intersects(demoHurtBox)) {
            if (timeSinceLastDamage >= fireDamageCooldown) {
                health--;
                // Reset the timer
                timeSinceLastDamage = 0.0f;
            }
        }
        
        //fog
        boolean isInFogArea = position.x >= fogEndX && position.x <= fogStartX &&
                       position.y >= fogStartY && position.y <= fogEndY &&
                       position.z >= fogEndZ && position.z <= fogStartZ;
        if (isInFogArea) {
            // Player is in the fog area, enable fog with red tint
            if (fogFilter.getFogDensity() < 4.0f) {
                fogFilter.setFogDensity(fogFilter.getFogDensity() + 0.05f);  // Adjust density as needed
            }
        } else {
            if (fogFilter.getFogDensity() > 0.1f) {
                fogFilter.setFogDensity(fogFilter.getFogDensity() - 0.05f);  // Adjust density as needed
            }
        }
        
        if (PlayerPhysControl.coinsCollected > 5) {
            endGame_win();
        }
    }
    
    // updates camera location and rotation each frame
    private void cameraUpdate(float tpf) {
//        CameraNode camNode = new CameraNode("CamNode",cam);
//        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
//        camNode.setLocalTranslation(new Vector3f(0, 4, -6));
//        Quaternion quat = new Quaternion();
//        quat.lookAt(Vector3f.UNIT_Z,Vector3f.UNIT_Y);
//        camNode.setLocalRotation(quat);
//        playerNode.attachChild(camNode);
//        camNode.setEnabled(true);
//        this.app.getFlyByCamera().setEnabled(false);
    }
    
    // updates player location and rotation each frame
    private void playerUpdate(float tpf) {
        //update position
        playerBox.setCenter(playerNode.getWorldTranslation());
        // Get current forward and left vectors of the playerNode:
        Vector3f modelForwardDir = playerNode.getWorldRotation().mult(Vector3f.UNIT_Z);
        Vector3f modelLeftDir = playerNode.getWorldRotation().mult(Vector3f.UNIT_X);
        // Determine the change in direction
        walkDirection.set(0, 0, 0);
        if (forward) {
            walkDirection.addLocal(modelForwardDir.mult(speed));
        }
        if (backward) {
            walkDirection.addLocal(modelForwardDir.mult(speed).negate());
        }
        if (moveLeft) {
            Quaternion rotateL = new Quaternion().fromAngleAxis(FastMath.PI * tpf, Vector3f.UNIT_Y);
            walkDirection.addLocal(modelLeftDir.mult(speed));
        }
        if (moveRight) {
            walkDirection.addLocal(modelLeftDir.mult(speed).negate());
        }
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
            health = -1;
            endGame_lose();
        }
    }
    
    // attempt to shoot a car located at the center of the screen, damaging it.
    private void shoot() {
        if (shootTimer > 0) {
            return;
        }
        
        shootTimer = shootRate;
        
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
            fireEmitter.setStartColor(new ColorRGBA(1f, 1f, .5f, 1f));
            fireEmitter.setEndColor(new ColorRGBA(1f, 0f, 0f, 0f));
            fireEmitter.setGravity(0,0,0);
            fireEmitter.getParticleInfluencer().setVelocityVariation(0.2f);
            fireEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0,5f,0));
            fireEmitter.setLowLife(0.5f);
            fireEmitter.setHighLife(10f);
            fireEmitter.setStartSize(1f);
            fireEmitter.setEndSize(0.5f);
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
    
    // attempt to pick up a coin located at the center of the screen, and add its value to the player's coinsCollected
    private void pickup() {
//        CollisionResults results = new CollisionResults();
//        
//        // set ray to center of first person perspective and shoot ray
//        ray.setOrigin(cam.getLocation());
//        ray.setDirection(cam.getDirection());
//        rootNode.collideWith(ray, results);
//        
//        if (results.size() > 0) {
//            // if hit...
//            Geometry target = results.getClosestCollision().getGeometry();
//            CoinPickupControl coinPickupControl = target.getControl(CoinPickupControl.class); 
//            if (coinPickupControl != null) {
//                // if hit a coin which can be picked up...
//                coinsCollected += coinPickupControl.pickup();
//         
//                
//                System.out.println("Picked up coin! " + coinsCollected);
//                if (coinsCollected >= 2) {
//                    endGame_win();
//                }
//            }
//        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        app.getRootNode().detachAllChildren();
        app.getGuiNode().detachAllChildren();
        
        speed = 1;
        
        
    }
        
    // map input to shoot
    private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float intensity, float tpf) {
            switch (name) {
                case MAPPING_SHOOT:
                    shoot();
                    break;
                case "MouseMoveLeft":
                    playerNode.rotate(0, intensity, 0); // Rotate player left around Y-axis
                    break;
                case "MouseMoveRight":
                    playerNode.rotate(0, -intensity, 0); // Rotate player right around Y-axis
                    break;
                case "MouseMoveUp":
                    camNode.rotate(-intensity, 0, 0); // Tilt up
                    break;
                case "MouseMoveDown":
                    camNode.rotate(intensity, 0, 0); // Tilt down
                    break;
                default:
                    break;
            }
        }
    };
    
    // map input to pick up items (and damage player)
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals(MAPPING_PICKUP) && !isPressed) {
                pickup();
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
            case "Jump" -> playerControl.jump();
            default -> {
            }
        }
    } 
    
    // creates a box for GUI use (no shading)
    private Geometry createGuiBox(String name, Vector3f loc, Vector3f scale, ColorRGBA color) {
        Geometry geom = new Geometry(name, boxMesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); // unshaded for GUI
        mat.setColor("Color", color);
        
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);  // Use alpha blending
        mat.getAdditionalRenderState().setDepthWrite(false);
        
        geom.setMaterial(mat);
        geom.setLocalTranslation(loc);
        geom.setLocalScale(scale);
                
        return geom;
    }
    
    // helper function to quickly create a box geometry with a given name, location, scale, and color. Uses phong shading
    private Node createBox(String name, Vector3f loc, Vector3f scale, ColorRGBA color) {
        Node node = new Node(name);
        Geometry geom = new Geometry(name + "_geom", boxMesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Diffuse", color);
        mat.setColor("Ambient", color.mult(0.3f));

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
        
        LoadModel lm = new LoadModel(assetManager);
        Spatial myModel = lm.load("Textures/Vehicles/car" + carIndex + ".j3o");
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
    private Node createBuilding(String name, Vector3f loc, Vector3f scale, ColorRGBA color) {
        LoadModel lm = new LoadModel(assetManager);
        Spatial building = lm.load("Textures/Buildings/ResBuilding.j3o");
        building.setLocalTranslation(loc);
        building.setLocalScale(scale.normalize().mult(5));
        return (Node) building;
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
    
    // create red tint over screen
    private void you_died() {
        Vector3f origin = new Vector3f(app_width / 2, app_height / 2, 0);  // Center position
        Box screenBox = new Box(app_width / 2, app_height / 2, 1);  // Width, height, depth
        Geometry redTint = new Geometry("RedTint", screenBox);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(1, 0, 0, 0.2f));  // Red with 20% opacity
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);  // Enable transparency
        redTint.setMaterial(mat);
        redTint.setLocalTranslation(origin);

        this.app.getGuiNode().detachAllChildren();
        this.app.getGuiNode().attachChild(redTint);
        speed = 0;
    }

    private void endGame_lose() {
        you_died();
        stateManager.detach(this);
        EndMenu endMenu = new EndMenu();
        this.app.getGuiNode().detachAllChildren();
        stateManager.attach(endMenu);
    }
    
    private void endGame_win() {
            //player wins, end game
        stateManager.detach(this);
        EndMenu endMenu = new EndMenu();
        this.app.getGuiNode().detachAllChildren();
        stateManager.attach(endMenu);
        
    }
    
    
}
