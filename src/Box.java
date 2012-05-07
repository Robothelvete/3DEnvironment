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
	//private double[] radii;
	private double[][] normals;
	private double[] dimensions;
	
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
		
		//radii = new double[]{largest(halfmeasurements[1], halfmeasurements[2]), largest(halfmeasurements[0], halfmeasurements[2]), largest(halfmeasurements[0], halfmeasurements[1])};
		
		dimensions = halfmeasurements;
		
		normals = new double[6][3];
		updateNormals();
		
	}
	
	@Override
	public void draw(GL2 gl) {
		gl.glBegin(GL2.GL_QUADS);
			//set "material" of wall, or rather the light reflection behaviour 
			float[] rgba = new float[] {(float)color[0],(float)color[1],(float)color[2], 1.0f};
			gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE, rgba, 0);
	        gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_SPECULAR, rgba, 0);
	        gl.glMaterialf(GL.GL_FRONT, GLLightingFunc.GL_SHININESS, 0.3f);
	        
	        
	        //gl.glNormal3d(-Math.sin(zrot),Math.sin(xrot) -Math.cos(zrot) , -Math.sin(xrot));
	        gl.glNormal3dv(normals[0], 0);
	        
	        for(int i = 0; i < 8; i++) {
				gl.glVertex3dv(corners[i], 0);
				if(i == 3) {
					//gl.glNormal3d(Math.sin(zrot), Math.cos(zrot) - Math.sin(xrot), Math.sin(xrot));
					gl.glNormal3dv(normals[1], 0);
				}
			}
	        
	        //gl.glNormal3d(Math.sin(yrot), -Math.sin(xrot), Math.sin(xrot) - Math.cos(yrot));
	        gl.glNormal3dv(normals[2], 0);
	        for (int i = 0; i < 3; i += 2) {
				gl.glVertex3dv(corners[i], 0);
				gl.glVertex3dv(corners[i + 1], 0);
				gl.glVertex3dv(corners[i + 5], 0);
				gl.glVertex3dv(corners[i + 4], 0);
				
				//gl.glNormal3d(-Math.sin(yrot), Math.sin(xrot), Math.cos(yrot) - Math.sin(xrot));
				gl.glNormal3dv(normals[3], 0);
			}
	        
	        //gl.glNormal3d(Math.sin(zrot) - Math.cos(yrot), Math.sin(zrot), -Math.sin(yrot));
	        gl.glNormal3dv(normals[4], 0);
	        
	        gl.glVertex3dv(corners[0], 0);
			gl.glVertex3dv(corners[4], 0);
			gl.glVertex3dv(corners[7], 0);
			gl.glVertex3dv(corners[3], 0);
	        
			//gl.glNormal3d(Math.cos(yrot) - Math.sin(zrot), -Math.sin(zrot), Math.sin(yrot));
			gl.glNormal3dv(normals[5], 0);
			
	        gl.glVertex3dv(corners[1], 0);
			gl.glVertex3dv(corners[5], 0);
			gl.glVertex3dv(corners[6], 0);
			gl.glVertex3dv(corners[2], 0);
		
        
        gl.glEnd();
	}
	/**
	 * recalculates every planes (sides) normal based on the current rotation status of the Box
	 */
	private void updateNormals() {
		normals[0] = new double[]{-Math.sin(zrot),Math.sin(xrot) -Math.cos(zrot) , -Math.sin(xrot)};
		normals[1] = new double[]{Math.sin(zrot), Math.cos(zrot) - Math.sin(xrot), Math.sin(xrot)};
		normals[2] = new double[]{Math.sin(yrot), -Math.sin(xrot), Math.sin(xrot) - Math.cos(yrot)};
		normals[3] = new double[]{-Math.sin(yrot), Math.sin(xrot), Math.cos(yrot) - Math.sin(xrot)};
		normals[4] = new double[]{Math.sin(zrot) - Math.cos(yrot), Math.sin(zrot), -Math.sin(yrot)};
		normals[5] = new double[]{Math.cos(yrot) - Math.sin(zrot), -Math.sin(zrot), Math.sin(yrot)};
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
		
		//double[] rotamount = new double[] {rotVector[0] * timelasted/1000000000, rotVector[1] * timelasted/1000000000,rotVector[2] * timelasted/1000000000};
		
		//recalculate corner points
		
		//WAIT! calculate this from the normals of each surface instead
		//A vector from the centerpoint to the corner can be calculated by adding together the normals of the three planes meeting in that particalar corner
		updateNormals();
		
		//corners[0] = normals[4] * dim[0] + normals[0] * dim[1] + normals[2] * dim[2]    
		corners[0] = addNormals(normals[4], normals[0], normals[2]);
		corners[1] = addNormals(normals[5], normals[0], normals[2]);
		corners[2] = addNormals(normals[5], normals[0], normals[3]);
		corners[3] = addNormals(normals[4], normals[0], normals[3]);
		corners[4] = addNormals(normals[4], normals[1], normals[2]);
		corners[5] = addNormals(normals[5], normals[1], normals[2]);
		corners[5] = addNormals(normals[5], normals[1], normals[3]);
		corners[6] = addNormals(normals[4], normals[1], normals[3]);
		//Note how the pattern of the normals correspond to the pattern of + and - of the corners being placed out in the constructors
		
		/*corners[0][0] = centerpoint[0] - (Math.cos(yrot) * radii[1]) - (Math.sin(zrot) * radii[2]) + (Math.sin(xrot) * radii[0]);
		corners[4][0] = centerpoint[4] - (Math.cos(yrot) * radii[1]) + (Math.sin(zrot) * radii[2]) - (Math.sin(xrot) * radii[0]);*/
		
	}

	@Override
	public void move(long timelasted, GameObject[] otherObjects) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Adds together the normals (3D vectors) from the centerpoint. 
	 * This places out the corner by knowing the direction of the normals, which when combined with that vectors length from the centerpoint
	 * gives an accurate position of the corner. It's very hard to explain this using just text....
	 * @param x The normal of the plane to which this corner cuts in the X dimension
	 * @param y The normal of the plane to which this corner cuts in the Y dimension
	 * @param z The normal of the plane to which this corner cuts in the Z dimension
	 * @return The new position calculated from this
	 */
	private double[] addNormals(double[] x, double[] y, double[] z) {
		double[] cornerposition = new double[3];
		for (int i = 0; i < 3; i++) {
			cornerposition[i] += x[i] * dimensions[0];
			cornerposition[i] += y[i] * dimensions[1];
			cornerposition[i] += z[i] * dimensions[2];
		}
		return cornerposition;
	}

	@Override
	public void startMoving(double[] movingVector) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startRotating(double[] rotationVector) {
		rotVector = rotationVector;
	}

	/*private double largest(double a, double b) {
		if(a>b) 
			return a;
		else return b;
	}*/
	
}
