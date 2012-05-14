import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLLightingFunc;

/**
 * A 2-dimensional rectangle
 * @author Robert
 *
 */
public class Wall implements GameObject {
	private double[][] corners;
	private double[] color;
	private double[] normal;
	private double[] angles;
	
	//note to self: three points (corners) are needed 
	public Wall(double[] first, double [] second, double[] third, double[] color) {
		corners = new double[4][3]; //Four corners in three dimensions
		corners[0] = first;
		corners[1] = second;
		corners[2] = third;
		
		//calculate where the last one is.
		corners[3] = new double[]{third[0] + (first[0] - second[0]), third[1] + (first[1] - second[1]), third[2] + (first[2] - second[2]) };
		
		this.color = color;
		
		normal = Helpers.normalize(Helpers.crossProduct(Helpers.vectorFromPoints(corners[0], corners[2]), Helpers.vectorFromPoints(corners[0], corners[1])));
		
		double deltaX = corners[0][0] - corners[2][0];
		double deltaZ = corners[2][2] - corners[0][2];
		double deltaY = corners[2][1] - corners[0][1];
		
		angles = new double[3];
		angles[0] = Math.atan2(deltaZ, deltaX);
		angles[1] = Math.abs(Math.atan2(deltaX, deltaY));
		
	
	}
	
	@Override
	public void draw(GL2 gl) {
		gl.glBegin(GL2.GL_QUADS);
			//color, or rather, light reflection
			float[] rgba = new float[] {(float)color[0],(float)color[1],(float)color[2],1.0f};
			gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT, rgba, 0);
	        gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_SPECULAR, rgba, 0);
	        gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_DIFFUSE, rgba, 0);
	        gl.glMaterialf(GL.GL_FRONT, GLLightingFunc.GL_SHININESS, 0.5f);
        
	        gl.glNormal3d(0, 1, 0);//TODO: change here so that it's not only a floor
			for(int i = 0; i < 4; i++) {
				gl.glVertex3d(corners[i][0], corners[i][1], corners[i][2]);
			}
			
		gl.glEnd();
	}

	@Override
	public double[] deltaX() {
		double[] minmax = new double[2];
		minmax[0] = corners[0][0];
		minmax[1] = corners[2][0];
		return minmax;
	}

	@Override
	public double[] deltaY() {
		double[] minmax = new double[2];
		//minmax[0] = corners[0][1];
		minmax[0] = -10.0;//TODO fix this ugly hack
		minmax[1] = corners[2][1];
		return minmax;
	}

	@Override
	public double[] deltaZ() {
		double[] minmax = new double[2];
		minmax[0] = corners[0][2];
		minmax[1] = corners[2][2];
		return minmax;
	}

	@Override
	public double[] collisionNormal(double[] startpoint, double[] endpoint) {
		/*if (endpoint[1] < 0) {
			return new double[]{0,1,0};
		}*/
		
		double distXend = corners[1][0] - endpoint[0];
		double distZend = endpoint[2] - corners[1][2];
		double distYend = corners[1][1] - endpoint[1];
		boolean overorunder = false;
		//System.out.println(Math.atan2(distXend, distYend) + ", " + angles[1]); 
		if (Math.atan2(distXend, distYend) < angles[1]) {
			overorunder = true;
		}
		
		double distXstart = corners[1][0] - startpoint[0];
		double distZstart = startpoint[2] - corners[1][2];
		double distYstart = corners[1][1] - startpoint[1];
		
		//System.out.println(Math.atan2(distXstart, distYstart) + ", " + angles[1]);
		if (Math.atan2(distXstart, distYstart) > angles[1] && overorunder) {
			return normal;
		}
		
		return null;
		
	}

	@Override
	public double[] collisionNormal(double[] startpoint, double[] endpoint, double buffersize) {
		return null;
	}

	
}
