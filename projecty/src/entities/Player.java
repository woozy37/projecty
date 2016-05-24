package entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.Terrain;

public class Player extends Entity {
	
	private static final float RUN_SPEED = 20;
	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -200;
	private static final float JUMP_POWER = 55;
	
	private static final float TERRAIN_HEIGHT = 0;
	
	
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	
	private boolean isInAir = false;
	
	private Terrain[][] bigTerrain;


	public Player(TexturedModel model, List<Terrain> terrains, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		
		bigTerrain = new Terrain[terrains.size()][terrains.size()];
		
		for(Terrain terrain:terrains){
			bigTerrain[(int) (terrain.getX()/Terrain.SIZE)][(int) (terrain.getZ()/Terrain.SIZE)] = terrain;
		}
		
		
		
	}
	
	public void move(){
		
		checkInputs();
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		Terrain terrain = currentTerrain(super.getPosition().x, super.getPosition().z);
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if(super.getPosition().y<terrainHeight){
			isInAir = false;
			upwardsSpeed = 0;
			super.getPosition().y = terrainHeight;
		}
		
	}
	
	private Terrain currentTerrain(float worldX, float worldZ) {
		int x = (int) (worldX / Terrain.SIZE);
		int z = (int) (worldZ / Terrain.SIZE);
		return bigTerrain[x][z];
	}

	private void jump(){
		if(!isInAir){
		this.upwardsSpeed = JUMP_POWER;}
		isInAir = true;
	}
	
	private void checkInputs(){
		if(Keyboard.isKeyDown(Keyboard.KEY_Z)){
			this.currentSpeed = RUN_SPEED;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			this.currentSpeed = -RUN_SPEED;
		}
		else {
			this.currentSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
			this.currentSpeed *= 10;
		}
		else {
			this.currentSpeed *= 1;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			this.currentTurnSpeed = -TURN_SPEED;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_Q)){
			this.currentTurnSpeed = TURN_SPEED;
		}
		else {
			this.currentTurnSpeed = 0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			jump();
		}
	}

}
