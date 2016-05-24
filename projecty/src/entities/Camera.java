package entities;



import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	private float distanceFromPlayer = 50;
	private float angleAroundPLayer = 0;
	
	private static float distanceMinPlayer = 20f;
	private static float distanceMaxPlayer = 300f;
	private static float pitchMin = 2;
	private static float pitchMax = 80;
	
	private Vector3f position = new Vector3f(0,1,0);
	private float pitch = 20;
	private float yaw = 0;
	private float roll;
	
	private Player player;
	public Camera(Player player){
		this.player = player;
	}
	
	public Camera(){}
	
	public void move(){
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		this.yaw = 180 - (player.getRotY() + angleAroundPLayer);
		
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	private void calculateCameraPosition(float horizDistance, float verticDistance){
		float theta = player.getRotY() + angleAroundPLayer;
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticDistance;
	}
	
	private float calculateHorizontalDistance(){
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance(){
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calculateZoom(){
		
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		if(distanceFromPlayer-zoomLevel<=distanceMaxPlayer && distanceFromPlayer-zoomLevel>=distanceMinPlayer){
		distanceFromPlayer -= zoomLevel;}
		else if (distanceFromPlayer-zoomLevel>distanceMaxPlayer){
			distanceFromPlayer = distanceMaxPlayer;
		}
		else {
			distanceFromPlayer = distanceMinPlayer;
		}
		
	}
	
	private void calculatePitch(){
		if(Mouse.isButtonDown(1)){
			float pitchChange = Mouse.getDY() * 0.1f;
			if(pitch-pitchChange<=pitchMax && pitch-pitchChange>=pitchMin){
			pitch -= pitchChange;}
			else if(pitch-pitchChange>pitchMax){
				pitch = pitchMax;
			}
			else {
				pitch = pitchMin;
			}
		}
	}
	
	private void calculateAngleAroundPlayer(){
		if(Mouse.isButtonDown(1)){
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundPLayer -= angleChange;
		}
	}
	
	
	

}
