package models;

public class RawModel {
	
	private int vaoId;
	private int vertexCount;
	
	public RawModel (int vaoID, int vertexCount){
		this.vaoId = vaoID;
		this.vertexCount = vertexCount;
	}

	public int getVaoId() {
		return vaoId;
	}

	public void setVaoId(int vaoId) {
		this.vaoId = vaoId;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public void setVertexCount(int vertexCount) {
		this.vertexCount = vertexCount;
	}

}
