import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLLightingFunc;

/**
 * A 3-dimensional wall that has an actual thickness, unlike Wall that is 2D
 * 
 * @author Robert
 * 
 */
public class ThickWall implements GameObject {
	private double[][] corners;
	private double[] color;
	private double angle;

	public ThickWall(double[] first, double[] second, double[] third, double thickness, double[] color) {
		corners = new double[8][3]; // Eight corners in three dimensions
		corners[0] = first;
		corners[1] = second;
		corners[2] = third;

		// calculate where the fourth one is.
		corners[3] = new double[] { third[0] + (first[0] - second[0]), third[1] + (first[1] - second[1]), third[2] + (first[2] - second[2]) };

		// calculate the other ones from the thickness of the wall
		double deltaX = corners[0][0] - corners[3][0];
		double deltaZ = corners[3][2] - corners[0][2];
		double angle = Math.atan2(deltaZ, deltaX);
		// Stand back, I'm gonna try trigonometry

		double offsetX = Math.sin(angle) * thickness;
		double offsetZ = Math.cos(angle) * thickness;
		for (int i = 4; i < 8; i++) {
			corners[i][0] = corners[i - 4][0] - offsetX;
			corners[i][2] = corners[i - 4][2] - offsetZ;
			corners[i][1] = corners[i - 4][1]; // TODO: later, open up for sloping walls/hills
		}

		this.angle = angle;
		this.color = color;
	}

	@Override
	public void draw(GL2 gl) {

		gl.glBegin(GL2.GL_QUADS);
		// set "material" of wall, or rather the light reflection behaviour
		float[] rgba = new float[] { (float) color[0], (float) color[1], (float) color[2], 1.0f };
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE, rgba, 0);
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_SPECULAR, rgba, 0);
		gl.glMaterialf(GL.GL_FRONT, GLLightingFunc.GL_SHININESS, 0.3f);

		// A normal is fed to opengl BEFORE the actual plane is fed into it
		gl.glNormal3d(Math.sin(angle), 0, Math.cos(angle));

		// draw the two parallell long lines
		for (int i = 0; i < 8; i++) {
			gl.glVertex3d(corners[i][0], corners[i][1], corners[i][2]);
			if (i == 3) {
				gl.glNormal3d(-Math.sin(angle), 0, -Math.cos(angle));
			}
		}

		// 0 1 5 4, 2 3 7 6, 1 5 6 2 <-- these are the corners visited, in that order, to make these final planes

		// The two umm... "sides"
		gl.glNormal3d(-Math.cos(angle), 0, -Math.sin(angle));
		for (int i = 0; i < 3; i += 2) {
			gl.glVertex3d(corners[i][0], corners[i][1], corners[i][2]);
			gl.glVertex3d(corners[i + 1][0], corners[i + 1][1], corners[i + 1][2]);
			gl.glVertex3d(corners[i + 5][0], corners[i + 5][1], corners[i + 5][2]);
			gl.glVertex3d(corners[i + 4][0], corners[i + 4][1], corners[i + 4][2]);

			gl.glNormal3d(Math.cos(angle), 0, Math.sin(angle));
		}

		// and lastly, the top. (a wall is placed on the ground, shut up)
		gl.glNormal3d(0, 1, 0);

		gl.glVertex3d(corners[1][0], corners[1][1], corners[1][2]);
		gl.glVertex3d(corners[5][0], corners[5][1], corners[5][2]);
		gl.glVertex3d(corners[6][0], corners[6][1], corners[6][2]);
		gl.glVertex3d(corners[2][0], corners[2][1], corners[2][2]);

		gl.glEnd();
	}

	@Override
	public double[] deltaX() {
		double[] minmax = new double[2];

		if (corners[0][0] < corners[7][0]) {
			minmax[0] = corners[0][0];
			minmax[1] = corners[7][0];
		} else {
			minmax[0] = corners[7][0];
			minmax[1] = corners[0][0];
		}

		return minmax;
	}

	@Override
	public double[] deltaY() {
		double[] minmax = new double[2];
		minmax[0] = corners[0][1];// TODO: change this as well when walls can slope
		minmax[1] = corners[1][1];
		return minmax;
	}

	@Override
	public double[] deltaZ() {
		double[] minmax = new double[2];

		if (corners[0][2] < corners[7][2]) {
			minmax[0] = corners[0][2];
			minmax[1] = corners[7][2];
		} else {
			minmax[0] = corners[7][2];
			minmax[1] = corners[0][2];
		}
		return minmax;
	}

	@Override
	public double[] collisionNormal(double[] startpoint, double[] endpoint) {
		// compare angles!
		// if angle to endpoint from corner4 is larger than from corner4 to corner7
		// and if angle to endpoint from corner0 is smaller than from corner0 to corner3, then endpoint is inside the wall
		// after that, check the angle from corner0 (or corner4, doesn't matter) to startpoint. If it's smaller than from that corner to endpoint, the plane starting from corner4 is the colliding plane
		// if it's larger, it's the other one

		// TODO: check the other sides of this wall as well. Also, can't this create a bug since the angle would probably be different, or am I simply paranoid? Investigate!

		double distX4 = corners[4][0] - endpoint[0];
		double distZ4 = endpoint[2] - corners[4][2];

		if (Math.atan2(distZ4, distX4) < angle) {
			return null;
		}

		double distX0 = corners[0][0] - endpoint[0];
		double distZ0 = endpoint[2] - corners[0][2];

		if (Math.atan2(distZ0, distX0) > angle) {
			return null;
		}

		// ok, if we got this far, we know that endpoint is inside this wall
		// but from what side are we approaching?
		if (Math.atan2(distZ4, distX4) > Math.atan2(startpoint[2] - corners[4][2], corners[4][0] - startpoint[0])) {
			// if endpoint angle is larger than startpoint angle, we're approaching the 'right side' wall, the one starting at corners[4]
			return new double[] { -Math.sin(angle), 0, -Math.cos(angle) };
		} else {
			// we're approaching it from the other wall, the one starting at corners[0]
			return new double[] { Math.sin(angle), 0, Math.cos(angle) };
		}

	}

	@Override
	public double[] collisionNormal(double[] startpoint, double[] endpoint, double buffersize) {
		// TODO: make this waaaaay more efficient, perhaps caching the buffered corners?

		// Make this wall buffersize units bigger
		corners[4][0] -= buffersize;
		corners[4][2] -= buffersize;
		corners[0][0] += buffersize;
		corners[0][2] += buffersize;

		// detect the collision
		double[] normal = collisionNormal(startpoint, endpoint);

		// reset it back to the way it was
		corners[4][0] += buffersize;
		corners[4][2] += buffersize;
		corners[0][0] -= buffersize;
		corners[0][2] -= buffersize;

		return normal;
	}

}