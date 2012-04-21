import javax.media.opengl.GL2;


public class Wall implements GameObject {
	private float[][] corners;
	
	public Wall() {
		corners = new float[4][3]; //Four corners in three dimensions
		
	}
	
	@Override
	public void draw(GL2 gl) {
		// TODO Colors, validation?
		
		gl.glBegin(GL2.GL_QUADS);
		for(int i = 0; i < 4; i++) {
			gl.glVertex3f(corners[i][0], corners[i][1], corners[i][2]);
		}
		gl.glEnd();
	}

	@Override
	public boolean rotate(float x, float y, float z, float degrees) {
		return false; //At least until I for some reason decide you can move walls....
	}

	@Override
	public boolean move(float x, float y, float z) {
		return false; //At least until I for some reason decide you can move walls....
	}

}
