import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLLightingFunc;

/**
 * A box with six sides (eight corners) that can move, rotate and interact with the environment
 * @author Robert
 *
 */
public class Box implements GameObject, MoveableObject {
	private double[][] corners;
	private double[] color;
	private double[] centerpoint;
	private double xrot;
	private double yrot;
	private double zrot;
	private double maxlength;
	private double[] rotVector;
	private double[] moveVector;
	/**
	 * Construct a box at a certain place with a certain color
	 * @param halfmeasurements - The dimensions of the box, divided by half
	 * @param startpos - the centerpoint of the box at the startingposition
	 * @param color 
	 */
	public Box(double[] halfmeasurements, double[] startpos, double[] color) {
		corners = new double[8][3];
		//first the bottom square, drawn 
		corners[0] = new double[]{startpos[0] - halfmeasurements[0], startpos[1] - halfmeasurements[1], startpos[2] - halfmeasurements[2]};
		corners[1] = new double[]{startpos[0] + halfmeasurements[0], startpos[1] - halfmeasurements[1], startpos[2] - halfmeasurements[2]};
		corners[2] = new double[]{startpos[0] + halfmeasurements[0], startpos[1] - halfmeasurements[1], startpos[2] + halfmeasurements[2]};
		corners[3] = new double[]{startpos[0] - halfmeasurements[0], startpos[1] - halfmeasurements[1], startpos[2] + halfmeasurements[2]};
		
		//then the top one
		corners[4] = new double[]{startpos[0] - halfmeasurements[0], startpos[1] + halfmeasurements[1], startpos[2] - halfmeasurements[2]};
		corners[5] = new double[]{startpos[0] + halfmeasurements[0], startpos[1] + halfmeasurements[1], startpos[2] - halfmeasurements[2]};
		corners[6] = new double[]{startpos[0] + halfmeasurements[0], startpos[1] + halfmeasurements[1], startpos[2] + halfmeasurements[2]};
		corners[7] = new double[]{startpos[0] - halfmeasurements[0], startpos[1] + halfmeasurements[1], startpos[2] + halfmeasurements[2]};
		
		centerpoint = startpos;
		
		this.color = color; //why did I decide on american spelling? annoys the shit out of me...
		
		xrot = 0.0;
		yrot = 0.0;
		zrot = 0.0;
		
		maxlength = 0;
		for(int i= 0; i < halfmeasurements.length; i++) {
			if(halfmeasurements[i] > maxlength) {
				maxlength = halfmeasurements[i];
			}
		}
		
	}
	
	@Override
	public void draw(GL2 gl) {
		gl.glBegin(GL2.GL_QUADS);
			//set "material" of wall, or rather the light reflection behaviour 
			float[] rgba = new float[] {(float)color[0],(float)color[1],(float)color[2], 1.0f};
			gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE, rgba, 0);
	        gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_SPECULAR, rgba, 0);
	        gl.glMaterialf(GL.GL_FRONT, GLLightingFunc.GL_SHININESS, 0.3f);
	        
	        
	        gl.glNormal3d(-Math.sin(zrot),Math.sin(xrot) -Math.cos(zrot) , -Math.sin(xrot));
	        
	        for(int i = 0; i < 8; i++) {
				gl.glVertex3dv(corners[i], 0);
				if(i == 3) {
					gl.glNormal3d(Math.sin(zrot), Math.cos(zrot) - Math.sin(xrot), Math.sin(xrot));
					//gl.glNormal3d(- Math.sin(angle), 0, -Math.cos(angle));
				}
			}
	        
	        gl.glNormal3d(Math.sin(yrot), -Math.sin(xrot), Math.sin(xrot) - Math.cos(yrot));
	        for (int i = 0; i < 3; i += 2) {
				gl.glVertex3dv(corners[i], 0);
				gl.glVertex3dv(corners[i + 1], 0);
				gl.glVertex3dv(corners[i + 5], 0);
				gl.glVertex3dv(corners[i + 4], 0);
				
				gl.glNormal3d(-Math.sin(yrot), Math.sin(xrot), Math.cos(yrot) - Math.sin(xrot));
				//gl.glNormal3d(Math.cos(angle), 0, Math.sin(angle));
			}
	        
	        gl.glNormal3d(Math.sin(zrot) - Math.cos(yrot), Math.sin(zrot), -Math.sin(yrot));
	        
	        gl.glVertex3dv(corners[0], 0);
			gl.glVertex3dv(corners[4], 0);
			gl.glVertex3dv(corners[7], 0);
			gl.glVertex3dv(corners[3], 0);
	        
			gl.glNormal3d(Math.cos(yrot) - Math.sin(zrot), -Math.sin(zrot), Math.sin(yrot));
			
	        gl.glVertex3dv(corners[1], 0);
			gl.glVertex3dv(corners[5], 0);
			gl.glVertex3dv(corners[6], 0);
			gl.glVertex3dv(corners[2], 0);
		
        
        gl.glEnd();
	}

	@Override
	public double[] deltaX() {
		return new double[]{centerpoint[0] - maxlength, centerpoint[0] + maxlength};
	}

	@Override
	public double[] deltaY() {
		return new double[]{centerpoint[1] - maxlength, centerpoint[1] + maxlength};
	}

	@Override
	public double[] deltaZ() {
		return new double[]{centerpoint[2] - maxlength, centerpoint[2] + maxlength};
	}

	@Override
	public double[] collisionNormal(double[] startpoint, double[] endpoint) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] collisionNormal(double[] startpoint, double[] endpoint, double buffersize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rotate(long timelasted, GameObject[] otherObjects) {
		xrot += rotVector[0] * timelasted/1000000000;
		yrot += rotVector[1] * timelasted/1000000000;
		zrot += rotVector[2] * timelasted/1000000000;
	}

	@Override
	public void move(long timelasted, GameObject[] otherObjects) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startMoving(double[] movingVector) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startRotating(double[] rotationVector) {
		rotVector = rotationVector;
	}



	
}
