package engineTester;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
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
		
		
		RawModel model = OBJLoader.loadObjModel("stall", loader);
		RawModel playerModel = OBJLoader.loadObjModel("person", loader);
		
		TexturedModel staticModel  = new TexturedModel(model, new ModelTexture(loader.loadTexture("stallTexture")));
		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),
				new ModelTexture(loader.loadTexture("grassTexture")));
		TexturedModel playerTModel = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("white")));
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		ModelTexture texture = staticModel.getTexture();
		texture.setShineDamper(10);
		texture.setReflectivity(1);
		
		ArrayList<Entity> allEntity = new ArrayList<Entity>();
		Player player = new Player(playerTModel, new Vector3f(0,0,-50), 0, 0, 0, 1);
		Entity entity = new Entity(staticModel, new Vector3f(0,0,-50),0,0,0,1);
		Entity herbe = new Entity(grass, new Vector3f(0,0,-40), 0, 0, 0, 1);
		allEntity.add(herbe);
		allEntity.add(entity);
		
		
		
		Light light = new Light(new Vector3f(3000,2000,2000), new Vector3f(1,1,1));
		
		Terrain terrain = new Terrain(0,-1,loader,texturePack, blendMap, "heightMap");
		
		Camera camera = new Camera(player);
		
		MasterRenderer renderer = new MasterRenderer();
		
		while(!Display.isCloseRequested()){
			entity.increaseRotation(0, 1, 0);
			camera.move();
			player.move(terrain);
			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			for(Entity object:allEntity){
				renderer.processEntity(object);
			}
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}
		
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
