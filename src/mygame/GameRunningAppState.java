package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 *
 * @author trevo
 */
public class GameRunningAppState extends AbstractAppState {
    private SimpleApplication app;
    private Camera cam;
    private Node rootNode;
    private AssetManager assetManager;
    
    private Ray ray = new Ray();
    private static Box mesh = new Box(Vector3f.ZERO, 1, 1, 1);
    
    @Override
    public void update(float tpf) {
        CollisionResults results = new CollisionResults();
        ray.setOrigin(cam.getLocation());
        ray.setDirection(cam.getDirection());
        rootNode.collideWith(ray, results);
        if (results.size() > 0) {
            Geometry target = results.getClosestCollision().getGeometry();
//            if (target.getControl(CubeChaserControl.class) != null) {
//                
//            }
        }
    }
    @Override
    public void cleanup() {}
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.cam = this.app.getCamera();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        
        this.app.getFlyByCamera().setMoveSpeed(50f);
        
        initGeometry();
    }
    
    private void initGeometry() {
        rootNode.attachChild(createBox("floor", new Vector3f(0, -2, 0), new Vector3f(250, 1, 250), ColorRGBA.DarkGray));
        rootNode.attachChild(createBox("sidewalk1", new Vector3f(-91, -1, -19), new Vector3f(80, .5f, 185), ColorRGBA.Gray));
        rootNode.attachChild(createBox("sidewalk2", new Vector3f(128, -1, -19), new Vector3f(80, .5f, 185), ColorRGBA.Gray));
        
        rootNode.attachChild(createBox("building1", new Vector3f(-74, 36, 75), new Vector3f(23, 36, 23), ColorRGBA.Brown));
        rootNode.attachChild(createBox("building2", new Vector3f(-70, 48, 26), new Vector3f(26, 48, 26), ColorRGBA.Brown));
        rootNode.attachChild(createBox("building3", new Vector3f(-98, 34, -13), new Vector3f(51, 34, 13), ColorRGBA.Brown));
        rootNode.attachChild(createBox("building4", new Vector3f(-77, 81, -101), new Vector3f(42, 81, 42), ColorRGBA.LightGray));
        rootNode.attachChild(createBox("building5", new Vector3f(-92, 23, -146), new Vector3f(20, 23, 20), ColorRGBA.Brown));
        rootNode.attachChild(createBox("building6", new Vector3f(95, 64, -94), new Vector3f(25, 64, 31), ColorRGBA.Brown));
        rootNode.attachChild(createBox("building7", new Vector3f(123, 115, -6), new Vector3f(43, 115, 57), ColorRGBA.LightGray));
        rootNode.attachChild(createBox("building8", new Vector3f(134, 34, 64), new Vector3f(51, 34, 13), ColorRGBA.Brown));
        rootNode.attachChild(createBox("building9", new Vector3f(113, 36, 100), new Vector3f(23, 36, 23), ColorRGBA.Brown));
    
        rootNode.attachChild(createCar("car1", new Vector3f(-5, 3.5f, 0), new Vector3f(3, 3, 6), 0, ColorRGBA.Blue));
        rootNode.attachChild(createCar("car2", new Vector3f(-5, 3.5f, -37), new Vector3f(3, 3, 6), 0, ColorRGBA.Blue));
        rootNode.attachChild(createCar("car3", new Vector3f(-5, 3.5f, -74), new Vector3f(3, 3, 6), 0, ColorRGBA.Blue));
        rootNode.attachChild(createCar("car4", new Vector3f(-5, 3.5f, 116), new Vector3f(3, 3, 6), 0, ColorRGBA.Blue));
        rootNode.attachChild(createCar("car5", new Vector3f(38, 3.5f, -63), new Vector3f(3, 3, 6), (float)Math.PI, ColorRGBA.Blue));
        rootNode.attachChild(createCar("car6", new Vector3f(38, 3.5f, 30), new Vector3f(3, 3, 6), (float)Math.PI, ColorRGBA.Blue));
        rootNode.attachChild(createCar("car7", new Vector3f(38, 3.5f, 65), new Vector3f(3, 3, 6), (float)Math.PI, ColorRGBA.Blue));
    }
    
    private Geometry createBox(String name, Vector3f loc, Vector3f scale, ColorRGBA color) {
        Geometry geom = new Geometry(name, mesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geom.setMaterial(mat);
        geom.setLocalTranslation(loc);
        geom.setLocalScale(scale);
        return geom;
    }
    
    private Node createCar(String name, Vector3f loc, Vector3f scale, float rot, ColorRGBA color) {
        Node car = new Node(name); 
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        
        Geometry body = new Geometry(name + "_body", mesh);
        body.setMaterial(mat);
        body.setLocalScale(scale);
        
        Geometry front = new Geometry(name + "_front", mesh);
        front.setMaterial(mat);
        front.setLocalTranslation(0, -scale.y / 2.f, scale.z * 1.33f);
        front.setLocalScale(scale.mult(1, .5f, .33f));
                
        car.attachChild(body);
        car.attachChild(front);
        
        car.setLocalTranslation(loc);
        car.rotate(0, rot, 0);
        
        return car;
    }
}
