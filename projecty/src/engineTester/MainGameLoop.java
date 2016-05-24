package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {
	
	private static int nbTree = 60;
	private static int nbGrass = 130;
	

	public static void main(String[] args) {
		
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		
		
		
		//*******************TERRAIN TEXTURE STUFF******************
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("sand"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("flower"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		//*******************************************************
		
		//Texture
		ModelTexture ferntexture = new ModelTexture(loader.loadTexture("fern"));
		ferntexture.setNumberOfRows(2);
		ModelTexture lamptexture = new ModelTexture(loader.loadTexture("lamp"));
		
		//RowModel
		RawModel model = OBJLoader.loadObjModel("stall", loader);
		RawModel playerModel = OBJLoader.loadObjModel("person", loader);
		RawModel treeObject = OBJLoader.loadObjModel("lowPolyTree", loader);
		RawModel treeNormalObject = OBJLoader.loadObjModel("tree", loader);
		RawModel fernObject = OBJLoader.loadObjModel("fern", loader);
		RawModel grassObject = OBJLoader.loadObjModel("grassModel", loader);
		RawModel lampObject = OBJLoader.loadObjModel("lamp", loader);
		
		//TexturedModel
		TexturedModel staticModel  = new TexturedModel(model, new ModelTexture(loader.loadTexture("stallTexture")));
		TexturedModel treeModel = new TexturedModel(treeObject, new ModelTexture(loader.loadTexture("lowPolyTree")));
		TexturedModel treeNormalModel = new TexturedModel(treeNormalObject, new ModelTexture(loader.loadTexture("tree")));
		TexturedModel fernModel = new TexturedModel(fernObject,ferntexture);
		TexturedModel grass = new TexturedModel(grassObject ,new ModelTexture(loader.loadTexture("grassTexture")));
		TexturedModel playerTModel = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("white")));
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		TexturedModel lampModel = new TexturedModel(lampObject, lamptexture);
		lampModel.getTexture().setUseFakeLighting(true);
		
		//EditTexture
		ModelTexture texture = staticModel.getTexture();
		texture.setShineDamper(10);
		texture.setReflectivity(1);
		
		//Terrain
		Terrain terrain = new Terrain(0,-1,loader,texturePack, blendMap, "heightMap");
		
		//Entity
		List<Entity> allEntity = new ArrayList<Entity>();
		for (int i = 0; i < nbTree; i++) {
			allEntity.add(new Entity(treeModel, getRandomPosition(terrain), 0, 0, 0, 1));
			allEntity.add(new Entity(treeNormalModel, getRandomPosition(terrain), 0, 0, 0, 14));
		}
		for (int i = 0; i < nbGrass; i++) {
			allEntity.add(new Entity(grass, getRandomPosition(terrain), 0, 0, 0, 1));
			allEntity.add(new Entity(fernModel, new Random().nextInt(4), getRandomPosition(terrain), 0, 0, 0, 1));
		}
		Player player = new Player(playerTModel, new Vector3f(0,0,-50), 0, 0, 0, 1);
		Entity entity = new Entity(staticModel, new Vector3f(0,0,-50),0,0,0,1);
		allEntity.add(entity);
		
		allEntity.add(new Entity(lampModel, new Vector3f(185,terrain.getHeightOfTerrain(185, -293),-293),0, 0, 0, 1));
		allEntity.add(new Entity(lampModel, new Vector3f(370,terrain.getHeightOfTerrain(370, -300),-300),0, 0, 0, 1));
		allEntity.add(new Entity(lampModel, new Vector3f(293,terrain.getHeightOfTerrain(293, -305),-305),0, 0, 0, 1));
		
		//Light
		Light light = new Light(new Vector3f(0,1000,-7000), new Vector3f(0.4f,0.4f,0.4f));
		List<Light> lights = new ArrayList<Light>();
		lights.add(light);
		lights.add(new Light(new Vector3f(185,10,-293),new Vector3f(2,0,0), new Vector3f(1,0.01f,0.002f)));
		lights.add(new Light(new Vector3f(370,17,-300),new Vector3f(0,2,2), new Vector3f(1,0.01f,0.002f)));
		lights.add(new Light(new Vector3f(293,7,-305),new Vector3f(2,2,0), new Vector3f(1,0.01f,0.002f)));
		
		//Camera
		Camera camera = new Camera(player);
		
		//GUI
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		GuiTexture gui = new GuiTexture(loader.loadTexture("socuwan"), new Vector2f(0.5f,0.5f), new Vector2f(0.25f,0.25f));
		//guis.add(gui);
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		MasterRenderer renderer = new MasterRenderer(loader);
		
		while(!Display.isCloseRequested()){
			entity.increaseRotation(0, 1, 0);
			camera.move();
			player.move(terrain);
			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			
			for(Entity entities:allEntity){
				renderer.processEntity(entities);
			}
			
			renderer.render(lights, camera);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
		}
		
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

	private static Vector3f getRandomPosition(Terrain terrain) {
		
		Random random = new Random();
		float xPosRandom = random.nextFloat() * 800;
		float zPosRandom = random.nextFloat() * -800;
		float yPosRandom = terrain.getHeightOfTerrain(xPosRandom, zPosRandom);
		
		return new Vector3f(xPosRandom,yPosRandom,zPosRandom);
	}

}
