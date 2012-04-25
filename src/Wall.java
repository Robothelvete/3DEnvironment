import javax.media.opengl.GL2;


public class Wall implements GameObject {
	private double[][] corners;
	private double[] color;
	//note to self: three points (corners) are needed 
	public Wall(double[] first, double [] second, double[] third, double[] color) {
		corners = new double[4][3]; //Four corners in three dimensions
		corners[0] = first;
		corners[1] = second;
		corners[2] = third;
		
		//calculate where the last one is.
		corners[3] = new double[]{third[0] + (first[0] - second[0]), third[1] + (first[1] - second[1]), third[2] + (first[2] - second[2]) };
		
		this.color = color;
	}
	
	@Override
	public void draw(GL2 gl) {
		// TODO Colors, validation?
		
		gl.glBegin(GL2.GL_QUADS);
			gl.glColor3dv(color, 0);
			for(int i = 0; i < 4; i++) {
				gl.glVertex3d(corners[i][0], corners[i][1], corners[i][2]);
			}
		gl.glEnd();
	}

	@Override
	public boolean rotate(double x, double y, double z, double degrees) {
		return false; //At least until I for some reason decide you can move walls....
	}

	@Override
	public boolean move(double x, double y, double z) {
		return false; //At least until I for some reason decide you can move walls....
	}

}
