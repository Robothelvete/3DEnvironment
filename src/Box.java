import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLLightingFunc;

/**
 * A box with six sides (eight corners) that can move, rotate and interact with the environment
 * 
 * @author Robert
 * 
 */
public class Box implements GameObject, MoveableObject {
	private double[][] corners;
	private double[] color;
	private double[] centerpoint;
	private double maxlength;
	private double[] rotVector;
	private double[] moveVector;
	// private double[] radii;
	private double[][] normals;

	/**
	 * Construct a box at a certain place with a certain color
	 * 
	 * @param halfmeasurements
	 *            - The dimensions of the box, divided by half
	 * @param startpos
	 *            - the centerpoint of the box at the startingposition
	 * @param color
	 */
	public Box(double[] halfmeasurements, double[] startpos, double[] color) {
		corners = new double[8][3];
		// first the bottom square, drawn
		corners[0] = new double[] { startpos[0] - halfmeasurements[0], startpos[1] - halfmeasurements[1],
				startpos[2] - halfmeasurements[2] };
		corners[1] = new double[] { startpos[0] + halfmeasurements[0], startpos[1] - halfmeasurements[1],
				startpos[2] - halfmeasurements[2] };
		corners[2] = new double[] { startpos[0] + halfmeasurements[0], startpos[1] - halfmeasurements[1],
				startpos[2] + halfmeasurements[2] };
		corners[3] = new double[] { startpos[0] - halfmeasurements[0], startpos[1] - halfmeasurements[1],
				startpos[2] + halfmeasurements[2] };

		// then the top one
		corners[4] = new double[] { startpos[0] - halfmeasurements[0], startpos[1] + halfmeasurements[1],
				startpos[2] - halfmeasurements[2] };
		corners[5] = new double[] { startpos[0] + halfmeasurements[0], startpos[1] + halfmeasurements[1],
				startpos[2] - halfmeasurements[2] };
		corners[6] = new double[] { startpos[0] + halfmeasurements[0], startpos[1] + halfmeasurements[1],
				startpos[2] + halfmeasurements[2] };
		corners[7] = new double[] { startpos[0] - halfmeasurements[0], startpos[1] + halfmeasurements[1],
				startpos[2] + halfmeasurements[2] };

		centerpoint = startpos;

		this.color = color; // why did I decide on american spelling? annoys the shit out of me...

		maxlength = 0;
		for (int i = 0; i < halfmeasurements.length; i++) {
			if (halfmeasurements[i] > maxlength) {
				maxlength = halfmeasurements[i];
			}
		}

		normals = new double[6][3];
		updateNormals(); // set smoe normals in case we don't rotate from the start

		rotVector = new double[3];
		moveVector = new double[3];
	}

	@Override
	public void draw(GL2 gl) {

		gl.glBegin(GL2.GL_QUADS);
		// set "material" of wall, or rather the light reflection behaviour
		float[] rgba = new float[] { (float) color[0], (float) color[1], (float) color[2], 1.0f };
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE, rgba, 0);
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_SPECULAR, rgba, 0);
		gl.glMaterialf(GL.GL_FRONT, GLLightingFunc.GL_SHININESS, 0.3f);

		gl.glNormal3dv(normals[0], 0);

		for (int i = 0; i < 8; i++) {
			gl.glVertex3dv(corners[i], 0);
			if (i == 3) {
				gl.glNormal3dv(normals[1], 0);
			}
		}

		gl.glNormal3dv(normals[2], 0);

		for (int i = 0; i < 3; i += 2) {
			gl.glVertex3dv(corners[i], 0);
			gl.glVertex3dv(corners[i + 1], 0);
			gl.glVertex3dv(corners[i + 5], 0);
			gl.glVertex3dv(corners[i + 4], 0);

			gl.glNormal3dv(normals[3], 0);
		}

		gl.glNormal3dv(normals[4], 0);

		gl.glVertex3dv(corners[0], 0);
		gl.glVertex3dv(corners[4], 0);
		gl.glVertex3dv(corners[7], 0);
		gl.glVertex3dv(corners[3], 0);

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
		// We take three points from the surface, make two vectors and cross them to get the surface normal
		// The three points are three of the corners ending this surface. So if we let A, B and C be three corners
		// The order of them must be clockwise when viewed opposite the normal
		// (yes I know that's not usually the case, but since I accidentally have flipped the axles this is suddenly a
		// left-handed coordinate system, and this is the least of the problems that has caused me)
		// Anyway, given that, the formula for the surface normal is (B-A) x (C-A)

		normals[0] = crossProduct(vectorFromPoints(corners[1], corners[0]), vectorFromPoints(corners[2], corners[0]));
		normals[2] = crossProduct(vectorFromPoints(corners[4], corners[0]), vectorFromPoints(corners[5], corners[0]));
		normals[4] = crossProduct(vectorFromPoints(corners[3], corners[0]), vectorFromPoints(corners[7], corners[0]));

		// The odd numbered planes are always directly opposite an even numbered plane, so we just invert these
		for (int i = 1; i < normals.length; i += 2) {
			normals[i][0] = -normals[i - 1][0];
			normals[i][1] = -normals[i - 1][1];
			normals[i][2] = -normals[i - 1][2];
		}

	}

	/**
	 * The cross product of two vectors
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	private double[] crossProduct(double[] v1, double[] v2) {
		return new double[] {
				v1[1] * v2[2] - v1[2] * v2[1],
				v1[2] * v2[0] - v1[0] * v2[2],
				v1[0] * v2[1] - v1[1] * v2[0]
		};
	}

	/**
	 * Calculates a vector from a given point to another by (add - sub)
	 * 
	 * @param add
	 * @param sub
	 * @return
	 */
	private double[] vectorFromPoints(double[] add, double[] sub) {
		return new double[] {
				add[0] - sub[0],
				add[1] - sub[1],
				add[2] - sub[2] };
	}

	@Override
	public double[] deltaX() {
		return new double[] { centerpoint[0] - maxlength, centerpoint[0] + maxlength };
	}

	@Override
	public double[] deltaY() {
		return new double[] { centerpoint[1] - maxlength, centerpoint[1] + maxlength };
	}

	@Override
	public double[] deltaZ() {
		return new double[] { centerpoint[2] - maxlength, centerpoint[2] + maxlength };
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

	private void rotate(long timelasted) {
		double dist = timelasted / 1000000000;
		double xrot = rotVector[0] * dist;
		double yrot = rotVector[1] * dist;
		double zrot = rotVector[2] * dist;

		// Nothing to rotate, no need to do anything
		if (xrot == 0 && yrot == 0 && zrot == 0) {
			return;
		}

		// we rotate around centerpoint, so transform the positions for origin at the centerpos
		for (int i = 0; i < corners.length; i++) {
			for (int j = 0; j < 3; j++) {
				corners[i][j] -= centerpoint[j];
			}
		}

		// ok so this is how it works:
		// A rotation matrix for rotation in that dimension is applied to all corners. These matrices rotate the corner
		// (which is a point, or a vector from origin (which as you remember is set to centerpos)) around the origin.
		// We only do this calculation if there's something to actually rotate
		// These basic rotation matrices are taken from the wikipedia page for rotation matrix
		// (http://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations)
		// The order in which we rotate doesn't matter (for more info, read some linear algebra)

		if (xrot != 0) {
			// The rotation matrix
			double[][] Rx = new double[][] {
					{ 1, 0, 0 },
					{ 0, Math.cos(xrot), -Math.sin(xrot) },
					{ 0, Math.sin(xrot), Math.cos(xrot) }
			};
			// apply to all corners
			for (int i = 0; i < corners.length; i++) {
				corners[i][0] = Rx[0][0] * corners[i][0] + Rx[0][1] * corners[i][1] + Rx[0][2] * corners[i][2];
				corners[i][1] = Rx[1][0] * corners[i][0] + Rx[1][1] * corners[i][1] + Rx[1][2] * corners[i][2];
				corners[i][2] = Rx[2][0] * corners[i][0] + Rx[2][1] * corners[i][1] + Rx[2][2] * corners[i][2];
			}
		}

		if (yrot != 0) {
			double[][] Ry = new double[][] {
					{ Math.cos(yrot), 0, Math.sin(yrot) },
					{ 0, 1, 0 },
					{ -Math.sin(yrot), 0, Math.cos(yrot) }
			};

			for (int i = 0; i < corners.length; i++) {
				corners[i][0] = Ry[0][0] * corners[i][0] + Ry[0][1] * corners[i][1] + Ry[0][2] * corners[i][2];
				corners[i][1] = Ry[1][0] * corners[i][0] + Ry[1][1] * corners[i][1] + Ry[1][2] * corners[i][2];
				corners[i][2] = Ry[2][0] * corners[i][0] + Ry[2][1] * corners[i][1] + Ry[2][2] * corners[i][2];
			}
		}

		if (zrot != 0) {
			double[][] Rz = new double[][] {
					{ Math.cos(zrot), -Math.sin(zrot), 0 },
					{ Math.sin(zrot), Math.cos(zrot), 0 },
					{ 0, 0, 1 }
			};

			for (int i = 0; i < corners.length; i++) {
				corners[i][0] = Rz[0][0] * corners[i][0] + Rz[0][1] * corners[i][1] + Rz[0][2] * corners[i][2];
				corners[i][1] = Rz[1][0] * corners[i][0] + Rz[1][1] * corners[i][1] + Rz[1][2] * corners[i][2];
				corners[i][2] = Rz[2][0] * corners[i][0] + Rz[2][1] * corners[i][1] + Rz[2][2] * corners[i][2];
			}
		}

		// Now that we've done some rotations, it's time to recalculate the normals to each surface
		updateNormals();

		// Normalise them back to original position
		for (int i = 0; i < corners.length; i++) {
			for (int j = 0; j < 3; j++) {
				corners[i][j] += centerpoint[j];
			}
		}

	}

	private void move(long timelasted) {
		double dist = timelasted / 1000000000;
		/*
		 * double[] newpos = new double[]{centerpoint[0], centerpoint[1], centerpoint[2]};
		 * 
		 * newpos[0] += dist * moveVector[0]; newpos[1] += dist * moveVector[1]; newpos[2] += dist * moveVector[2];
		 * 
		 * centerpoint = newpos;
		 */
		centerpoint[0] += dist * moveVector[0];
		centerpoint[1] += dist * moveVector[1];
		centerpoint[2] += dist * moveVector[2];

	}

	@Override
	public void startMoving(double[] movingVector) {
		moveVector = movingVector;
	}

	@Override
	public void startRotating(double[] rotationVector) {
		rotVector = rotationVector;
	}

	@Override
	public void takeAction(long timelasted, GameObject[] gameObjects) {
		double dist = (double)timelasted /1000000000;

		double[] newpos = new double[] { centerpoint[0], centerpoint[1], centerpoint[2] };

		newpos[0] += dist * moveVector[0];
		newpos[1] += dist * moveVector[1];
		newpos[2] += dist * moveVector[2];

		double xrot = rotVector[0] * dist;
		double yrot = rotVector[1] * dist;
		double zrot = rotVector[2] * dist;
		
		// store these in a separate array, that way we can get the movement vector for each corner when we collision
		// detect
		double[][] newcorners = new double[8][3];
		System.arraycopy(corners, 0, newcorners, 0, corners.length);
		
		// Nothing to rotate, but we still might have to move every corner
		if (xrot == 0 && yrot == 0 && zrot == 0) {
			
			// move if we are moving
			if (moveVector[0] != 0 || moveVector[1] != 0 || moveVector[2] != 0) {

				// add delta(position) to every corner to move them in accordance to the movement vectors
				for (int i = 0; i < newcorners.length; i++) {
					for (int j = 0; j < 3; j++) {
						newcorners[i][j] += newpos[j] - centerpoint[j];
					}
				}
			}
		}
		else {
			
			// we rotate around centerpoint, so transform the positions for origin at the centerpos
			for (int i = 0; i < newcorners.length; i++) {
				for (int j = 0; j < 3; j++) {
					newcorners[i][j] -= centerpoint[j];
				}
			}

			// ok so this is how it works:
			// A rotation matrix for rotation in that dimension is applied to all newcorners. These matrices rotate the
			// corner
			// (which is a point, or a vector from origin (which as you remember is set to centerpos)) around the
			// origin.
			// We only do this calculation if there's something to actually rotate
			// These basic rotation matrices are taken from the wikipedia page for rotation matrix
			// (http://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations)
			// The order in which we rotate doesn't matter (for more info, read some linear algebra)

			if (xrot != 0) {
				// The rotation matrix
				double[][] Rx = new double[][] {
						{ 1, 0, 0 },
						{ 0, Math.cos(xrot), -Math.sin(xrot) },
						{ 0, Math.sin(xrot), Math.cos(xrot) }
				};
				// apply to all newcorners
				for (int i = 0; i < newcorners.length; i++) {
					newcorners[i][0] = Rx[0][0] * newcorners[i][0] + Rx[0][1] * newcorners[i][1] + Rx[0][2]
							* newcorners[i][2];
					newcorners[i][1] = Rx[1][0] * newcorners[i][0] + Rx[1][1] * newcorners[i][1] + Rx[1][2]
							* newcorners[i][2];
					newcorners[i][2] = Rx[2][0] * newcorners[i][0] + Rx[2][1] * newcorners[i][1] + Rx[2][2]
							* newcorners[i][2];
				}
			}

			if (yrot != 0) {
				double[][] Ry = new double[][] {
						{ Math.cos(yrot), 0, Math.sin(yrot) },
						{ 0, 1, 0 },
						{ -Math.sin(yrot), 0, Math.cos(yrot) }
				};

				for (int i = 0; i < newcorners.length; i++) {
					newcorners[i][0] = Ry[0][0] * newcorners[i][0] + Ry[0][1] * newcorners[i][1] + Ry[0][2]
							* newcorners[i][2];
					newcorners[i][1] = Ry[1][0] * newcorners[i][0] + Ry[1][1] * newcorners[i][1] + Ry[1][2]
							* newcorners[i][2];
					newcorners[i][2] = Ry[2][0] * newcorners[i][0] + Ry[2][1] * newcorners[i][1] + Ry[2][2]
							* newcorners[i][2];
				}
			}

			if (zrot != 0) {
				double[][] Rz = new double[][] {
						{ Math.cos(zrot), -Math.sin(zrot), 0 },
						{ Math.sin(zrot), Math.cos(zrot), 0 },
						{ 0, 0, 1 }
				};

				for (int i = 0; i < newcorners.length; i++) {
					newcorners[i][0] = Rz[0][0] * newcorners[i][0] + Rz[0][1] * newcorners[i][1] + Rz[0][2]
							* newcorners[i][2];
					newcorners[i][1] = Rz[1][0] * newcorners[i][0] + Rz[1][1] * newcorners[i][1] + Rz[1][2]
							* newcorners[i][2];
					newcorners[i][2] = Rz[2][0] * newcorners[i][0] + Rz[2][1] * newcorners[i][1] + Rz[2][2]
							* newcorners[i][2];
				}
			}

			// Now that we've done some rotations, it's time to recalculate the normals to each surface
			updateNormals();

			// Normalise them back to original position (or the new position, it has changed)
			for (int i = 0; i < newcorners.length; i++) {
				for (int j = 0; j < 3; j++) {
					newcorners[i][j] += newpos[j];
				}
			}

		}

		// TODO: Check for collisions
		for (int i = 0; i < gameObjects.length; i++) {
			//No need to check collisions against yourself now is there? (that would be rather fun to watch though)
			if (gameObjects[i].hashCode() == this.hashCode()) {
				//i++;
			}
			else {
				GameObject curobj = gameObjects[i];
				for (int j = 0; j < corners.length; j++) {
					double[] corner = newcorners[j];
					// Check if we're in the vicinity on the X axis
					double[] deltaX = curobj.deltaX();
					if (corner[0] >= deltaX[0] && corner[0] <= deltaX[1]) {
						// Then the Z axis
						double[] deltaZ = curobj.deltaZ();
						if (corner[2] >= deltaZ[0] && corner[2] <= deltaZ[1]) {
							// And finally the Y axis, because this is the axis most likely to give a positive
							double[] deltaY = curobj.deltaY();
							if (corner[1] >= deltaY[0] && corner[1] <= deltaY[1]) {
								// Ok, so we're in this objects interaction box, time to do a more precise detection
								double[] collNormal = curobj.collisionNormal(corners[j], corner);
								if (collNormal != null) {
									//Ok so this corner has hit something
								}
							}
						}
					}
				}
			}
		}

		centerpoint = newpos;// set the new centerpoint to where we have moved
		corners = newcorners;
	}

	/*
	 * private double largest(double a, double b) { if(a>b) return a; else return b; }
	 */

}
