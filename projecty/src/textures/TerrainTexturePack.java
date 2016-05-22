package textures;

public class TerrainTexturePack {
	
	private TerrainTexture backgroudTexture;
	private TerrainTexture rTexture;
	private TerrainTexture gTexture;
	private TerrainTexture bTexture;
	
	public TerrainTexturePack(TerrainTexture backgroudTexture, TerrainTexture rTexture, TerrainTexture gTexture,
			TerrainTexture bTexture) {
		super();
		this.backgroudTexture = backgroudTexture;
		this.rTexture = rTexture;
		this.gTexture = gTexture;
		this.bTexture = bTexture;
	}

	public TerrainTexture getBackgroudTexture() {
		return backgroudTexture;
	}

	public TerrainTexture getrTexture() {
		return rTexture;
	}

	public TerrainTexture getgTexture() {
		return gTexture;
	}

	public TerrainTexture getbTexture() {
		return bTexture;
	}
	
	

}
