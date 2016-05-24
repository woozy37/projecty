package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

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
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

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
		List<Terrain> terrains = new ArrayList<Terrain>();
		Terrain terrain = new Terrain(0,0,loader,texturePack, blendMap, "heightMap");
		Terrain terrain2 = new Terrain(0,1,loader,texturePack, blendMap, "heightMap");
		Terrain terrain3 = new Terrain(1,0,loader,texturePack, blendMap, "heightMap");
		Terrain terrain4 = new Terrain(1,1,loader,texturePack, blendMap, "heightMap");
		terrains.add(terrain);
		terrains.add(terrain2);
		terrains.add(terrain3);
		terrains.add(terrain4);
		
		//Entity
		List<Entity> allEntity = new ArrayList<Entity>();
		for (int i = 0; i < nbTree; i++) {
			allEntity.add(new Entity(treeModel, getRandomPosition(terrains), 0, 0, 0, 1));
			allEntity.add(new Entity(treeNormalModel, getRandomPosition(terrains), 0, 0, 0, 14));
		}
		for (int i = 0; i < nbGrass; i++) {
			allEntity.add(new Entity(grass, getRandomPosition(terrains), 0, 0, 0, 1));
			allEntity.add(new Entity(fernModel, new Random().nextInt(4), getRandomPosition(terrains), 0, 0, 0, 1));
		}
		
		Entity entity = new Entity(staticModel, new Vector3f(0,0,-50),0,0,0,1);
		allEntity.add(entity);
		
		
		Vector3f lampLight2 = getRandomPosition(terrains);
		Vector3f lampLight3 = getRandomPosition(terrains);
		
		
		Entity lamp2 = new Entity(lampModel,lampLight2,0, 0, 0, 1);
		Entity lamp3 = new Entity(lampModel, lampLight3,0, 0, 0, 1);
		
		
		allEntity.add(lamp2);
		allEntity.add(lamp3);
		
		//Light
		
		Light light = new Light(new Vector3f(3000,3000,-3000), new Vector3f(0.4f,0.4f,0.4f));
		Light light2 = new Light(new Vector3f(lampLight2.x,lampLight2.y + 15, lampLight2.z),new Vector3f(2,0,0), new Vector3f(1,0.01f,0.002f));
		Light light3 = new Light(new Vector3f(lampLight3.x,lampLight3.y + 15, lampLight3.z),new Vector3f(2,0,0), new Vector3f(1,0.01f,0.002f));
		
		List<Light> lights = new ArrayList<Light>();
		lights.add(light);
		lights.add(light2);
		lights.add(light3);
		
		//Player
		Player player = new Player(playerTModel, terrains, new Vector3f(0,0,-50), 0, 0, 0, 1);
		
		//Camera
		Camera camera = new Camera(player);
		
		
		
		
		MasterRenderer renderer = new MasterRenderer(loader);
		
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		
		//Water
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		WaterTile water = new WaterTile(300, 130, -2);
		waters.add(water);
		
		
		//GUI
				List<GuiTexture> guis = new ArrayList<GuiTexture>();
				GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		
		//MousePicker
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);
		
		while(!Display.isCloseRequested()){
			
			player.move();
			camera.move();
			picker.update();
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
//			Vector3f terrainPoint = picker.getCurrentTerrainPoint();
//			if(terrainPoint!=null){
//				//System.out.println(terrainPoint);
//				lamp.setPosition(terrainPoint);
//				light2.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y+15, terrainPoint.z));
//			}
			
			fbos.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(allEntity, terrains, lights, camera, new Vector4f(0, 1, 0, -water.getHeight()));
			camera.getPosition().y += distance;
			camera.invertPitch();
			renderer.processEntity(player);
			
			fbos.bindRefractionFrameBuffer();
			renderer.renderScene(allEntity, terrains, lights, camera, new Vector4f(0, -1, 0, water.getHeight()));
			
			
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			fbos.unbindCurrentFrameBuffer();
			renderer.processEntity(player);
			renderer.renderScene(allEntity, terrains, lights, camera, new Vector4f(0, 1, 0, 15));
			waterRenderer.render(waters, camera, light);
			renderer.processEntity(player);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
		}
		
		fbos.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

	private static Vector3f getRandomPosition(List<Terrain> terrains) {
		
		int totalTerrain = terrains.size();
		int idRandomTerrain = new Random().nextInt(totalTerrain);
		Terrain randomTerrain = terrains.get(idRandomTerrain);		
		
		Random random = new Random();
		float xPosRandom = (random.nextFloat() * 800) + randomTerrain.getX();
		float zPosRandom = (random.nextFloat() * 800) + randomTerrain.getZ();
		float yPosRandom = randomTerrain.getHeightOfTerrain(xPosRandom, zPosRandom);
		return new Vector3f(xPosRandom,yPosRandom,zPosRandom);
	}

}
