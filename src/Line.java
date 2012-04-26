import javax.media.opengl.GL2;

/**
 * A vector in 3D space
 * @author Robert
 *
 */
public class Line implements GameObject {
	private double[][] points;
	private double[] color;
	public Line(double[] start, double[] end, double[] color) {
		points = new double[2][3];
		
		points[0] = start;
		points[1] = end;
		this.color = color;
	}
	@Override
	public void draw(GL2 gl) {
		gl.glBegin(GL2.GL_LINES);
			gl.glColor3dv(color, 0);
			gl.glVertex3d(points[0][0], points[0][1], points[0][2]);
			gl.glVertex3d(points[1][0], points[1][1], points[1][2]);
		gl.glEnd();
	}

	@Override
	public boolean rotate(double x, double y, double z, double degrees) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean move(double x, double y, double z) {
		// TODO Auto-generated method stub
		return false;
	}

}
