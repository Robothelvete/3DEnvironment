import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

public class Sphere implements MoveableObject {
	private GLU glu;
	private double[] pos;
	private double radius;
	private double[] color;
	private int slices = 32;
	private int stacks = 32;
	private double[] moveVector;
	private double elasticity;
	private double friction = 0.4;
	private boolean atground = false;
	
	
	public Sphere(GLU glu, double[] startpos, double radius, double[] color, double elast) {
		this.glu = glu;
		pos = startpos;
		this.radius = radius;
		this.color = color;
		elasticity = elast;
		
		moveVector = new double[3];
	}

	@Override
	public void draw(GL2 gl) {

		gl.glPushMatrix();
		gl.glTranslated(pos[0], pos[1], pos[2]);

		float[] rgba = new float[] { (float) color[0], (float) color[1], (float) color[2], 1.0f };
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE, rgba, 0);
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_SPECULAR, rgba, 0);
		gl.glMaterialf(GL.GL_FRONT, GLLightingFunc.GL_SHININESS, 0.3f);

		GLUquadric sphere = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(sphere, GLU.GLU_FILL);
		glu.gluQuadricNormals(sphere, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(sphere, GLU.GLU_OUTSIDE);
		glu.gluSphere(sphere, radius, slices, stacks);
		glu.gluDeleteQuadric(sphere);

		gl.glPopMatrix();
	}

	@Override
	public double[] deltaX() {
		return new double[] { pos[0] - radius, pos[0] + radius };
	}

	@Override
	public double[] deltaY() {
		return new double[] { pos[1] - radius, pos[1] + radius };
	}

	@Override
	public double[] deltaZ() {
		return new double[] { pos[2] - radius, pos[2] + radius };
	}

	@Override
	public double[] collisionNormal(double[] startpoint, double[] endpoint) {
		return Helpers.vectorFromPoints(endpoint, pos);
		
	}

	@Override
	public double[] collisionNormal(double[] startpoint, double[] endpoint, double buffersize) {
		return collisionNormal(startpoint, endpoint); //it's all the same
	}

	@Override
	public void takeAction(long timetaken, GameObject[] gameObjects) {
		double dist = (double) timetaken / 1000000000;
		double[] newpos = new double[] { pos[0], pos[1], pos[2] };

		newpos[0] += dist * moveVector[0];
		newpos[1] += dist * moveVector[1];
		newpos[2] += dist * moveVector[2];

		// check collisions
		for (int i = 0; i < gameObjects.length; i++) {
			if (gameObjects[i].hashCode() != this.hashCode()) {
				GameObject curobj = gameObjects[i];
				// Check if we're in the vicinity on the X axis
				double[] deltaX = curobj.deltaX();
				if (newpos[0] >= deltaX[0] - radius && newpos[0] <= deltaX[1] + radius) {
					// Then the Z axis
					double[] deltaZ = curobj.deltaZ();
					if (newpos[2] >= deltaZ[0] - radius && newpos[2] <= deltaZ[1] + radius) {
						// And finally the Y axis, because this is the axis most likely to give a positive
						double[] deltaY = curobj.deltaY();
						if (newpos[1] >= deltaY[0] - radius && newpos[1] <= deltaY[1] + radius) {
							// Ok, so we're in this objects interaction box, time to do a more precise detection
							double[] checkerpos = new double[3];
							for (int j = 0; j < 3; j++) { 
								checkerpos[j] = newpos[j] + Math.signum(moveVector[j]) * radius;
							}
							
							double[] collNormal = curobj.collisionNormal(pos, checkerpos);
							if (collNormal != null) {
								//bounce
								double[] newmov = Helpers.bounceVector(moveVector, collNormal, elasticity);
								
								for (int j = 0; j < 3; j++) { 
									newpos[j] += collNormal[j] * dist * -moveVector[j];
									//while we're looping, apply friction
									
									//newmov[j] -= newmov[j] * (1 - collNormal[j]) * friction;
									//newmov[j] = Helpers.round(newmov[j],8);
								}
								//System.out.println(moveVector[0] + ", " + moveVector[1]+ ", " + moveVector[2]);
								moveVector = newmov;
								//System.out.println(moveVector[0] + ", " + moveVector[1]+ ", " + moveVector[2]);
								//System.out.println("");
								//set it at the edge
								
								
								if (collNormal[1] == 1 && moveVector[1] < 0.6 && moveVector[1] > 0 ) {
									atground = true;
									moveVector[1] = 0;
								}
							}
						}
					}
				}
			}
		}
		if (atground) {
			for (int j = 0; j < 3; j++) {
				//moveVector[j] = moveVector[j] * friction;
				moveVector[j] -= Math.signum(moveVector[j]) * friction;
			}
		}
		pos = newpos;
	}

	@Override
	public void startMoving(double[] movingVector) {
		atground = false;
		moveVector = movingVector;
	}

	@Override
	public void startRotating(double[] rotationVector) {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveTo(double[] pos) {
		atground = false;
		moveVector = Helpers.vectorFromPoints(this.pos, pos);
		this.pos = pos;
	}

	@Override
	public void rotate(double[] amount) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void applyGravity(long timetaken, double grav) {
		if (!atground) {
			double dist = (double) timetaken / 1000000000;
			moveVector[1] -= dist * grav;
		}
	}
	
	@Override
	public void addMovement(double[] movingVector) {
		atground = false;
		for (int i = 0; i < 3; i++) {
			moveVector[i] += movingVector[i];
		}
	}

}
