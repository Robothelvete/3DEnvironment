import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLLightingFunc;

/**
 * A 3-dimensional wall that has an actual thickness, unlike Wall that is 2D
 * @author Robert
 *
 */
public class ThickWall implements GameObject {
	private double[][] corners;
	private double[] color;
	private double angle;
	
	public ThickWall(double[] first, double [] second, double[] third, double thickness, double[] color) {
		corners = new double[8][3]; //Eight corners in three dimensions
		corners[0] = first;
		corners[1] = second;
		corners[2] = third;
		
		//calculate where the fourth one is.
		corners[3] = new double[]{third[0] + (first[0] - second[0]), third[1] + (first[1] - second[1]), third[2] + (first[2] - second[2]) };
		
		//calculate the other ones from the thickness of the wall
		double deltaX = corners[0][0] - corners[3][0];
		double deltaZ = corners[0][2] - corners[3][2];
		double angle;
		//Stand back, I'm gonna try trigonometry
		if (deltaX == 0) {
			angle = Math.PI/2;
		}
		else {
			angle = Math.atan(deltaZ/deltaX);
		}
		
		double offsetX = Math.sin(angle) * thickness;
		double offsetZ = Math.cos(angle) * thickness;
		for (int i = 4; i < 8; i++) {
			corners[i][0] = corners[i-4][0] - offsetX;
			corners[i][2] = corners[i-4][2] + offsetZ;
			corners[i][1] = corners[i-4][1]; //TODO: later, open up for sloping walls/hills
		}
		
		this.angle = angle;
		this.color = color;
	}
	
	@Override
	public void draw(GL2 gl) {
		
		gl.glBegin(GL2.GL_QUADS);
			//set "material" of wall, or rather the light reflection behaviour 
			float[] rgba = new float[] {(float)color[0],(float)color[1],(float)color[2], 1.0f};
			gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE, rgba, 0);
	        gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_SPECULAR, rgba, 0);
	        gl.glMaterialf(GL.GL_FRONT, GLLightingFunc.GL_SHININESS, 0.3f);
	        
	        //A normal is fed to opengl BEFORE the actual plane is fed into it
	        gl.glNormal3d(Math.sin(angle), 0, Math.cos(angle));
	        
			//draw the two parallell long lines
			for(int i = 0; i < 8; i++) {
				gl.glVertex3d(corners[i][0], corners[i][1], corners[i][2]);
				if(i == 3) {
					gl.glNormal3d(- Math.sin(angle), 0, -Math.cos(angle));
				}
			}
			
			
			//0 1 5 4, 2 3 7 6, 1 5 6 2 <-- these are the corners visited, in that order, to make these final planes
			
			//The two umm... "sides"
			gl.glNormal3d(-Math.cos(angle), 0, -Math.sin(angle));
			for (int i = 0; i < 3; i += 2) {
				gl.glVertex3d(corners[i][0], corners[i][1], corners[i][2]);
				gl.glVertex3d(corners[i + 1][0], corners[i + 1][1], corners[i + 1][2]);
				gl.glVertex3d(corners[i + 5][0], corners[i + 5][1], corners[i + 5][2]);
				gl.glVertex3d(corners[i + 4][0], corners[i + 4][1], corners[i + 4][2]);
				
				gl.glNormal3d(Math.cos(angle), 0, Math.sin(angle));
			}
			
			//and lastly, the top. (a wall is placed on the ground, shut up)
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
		if(corners[0][0] < corners[4][0]) {
			minmax[0] = corners[0][0];
			minmax[1] = corners[7][0];
		}
		else {
			minmax[0] = corners[4][0];
			minmax[1] = corners[3][0];
		}
		return minmax;
	}

	@Override
	public double[] deltaY() {
		double[] minmax = new double[2];
		minmax[0] = corners[0][1];//TODO: change this as well when walls can slope
		minmax[1] = corners[1][1];
		return minmax;
	}

	@Override
	public double[] deltaZ() {
		double[] minmax = new double[2];
		if(corners[0][2] < corners[4][2]) {
			minmax[0] = corners[0][2];
			minmax[1] = corners[7][2];
		}
		else {
			minmax[0] = corners[4][2];
			minmax[1] = corners[3][2];
		}
		return minmax;	
	}

	/*@Override
	public boolean rotate(double x, double y, double z, double degrees) {
		return false;
	}

	@Override
	public boolean move(double x, double y, double z) {
		return false;
	}*/

}
