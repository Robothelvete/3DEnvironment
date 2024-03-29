import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 * Holds all information about player, camera position and angle and such
 * 
 * @author Robert
 * 
 */
public class Player {
	private double[] pos;
	// as it will not be necessary to rotate along the z-axis (which would be
	// akin to walking the walls and ceiling), only these two are needed
	private double xrot;
	private double yrot;
	private static final double speed = 10.0;
	private static final double mouseSense = 0.001 * Math.PI;
	private boolean[] moving;
	private static final double buffer = 1.3;
	private MoveableObject heldObject;
	private double objectBuffer;
	
	public Player() {
		pos = new double[] {0, 2, -10 };// TODO
		moving = new boolean[] {false, false, false, false};
	}

	/**
	 * Moves the player/camera in the given direction. 
	 */
	public void move(long timelasted, GameObject[] otherObjects) { //TODO: make this more pretty
		//walkingspeed is in other words how long he should have moved this frame
		double walkingspeed = speed * timelasted/1000000000;

		double[] newpos = new double[]{pos[0], pos[1], pos[2]};
		
		for (int i = 0; i < 4; i++) {
			if (moving[i]) {
				int direction = i;
				if (direction % 2 == 0) {
					int forback = 1;
					if (direction > 0)
						forback = -1;

					newpos[0] += Math.sin(yrot) * walkingspeed * forback;//movement on X axis
					newpos[2] += Math.cos(yrot) * walkingspeed * forback;//movement on Z axis
				} else {
					int leftright = -1;
					if (direction > 1)
						leftright = 1;

					newpos[0] += Math.cos(yrot) * walkingspeed * leftright;
					newpos[2] -= Math.sin(yrot) * walkingspeed * leftright; // negative because I've switched the axles
				}
			}
		}
		
		//now that we have calculated the new position we try to move into, it's time for collision detection
		//this is slightly different from collision detection for objects, because we don't want our player to bounce off walls now, do we?
		//let's start by checking if we're even near any object (in any objects bounding box)
		
		//Note: for the player, a buffer of at least buffer units from any object is necessary, otherwise it will LOOK like you're inside the object when in fact you're just very close
		for (int i = 0; i < otherObjects.length; i++) {
			GameObject curobj = otherObjects[i];
			//Check if we're in the vicinity on the X axis
			double[] deltaX = curobj.deltaX();
			if (newpos[0] >= deltaX[0] - buffer && newpos[0] <= deltaX[1] + buffer) {
				//Then the Z axis
				double[] deltaZ = curobj.deltaZ();
				if (newpos[2] >= deltaZ[0] - buffer && newpos[2] <= deltaZ[1] + buffer) {
					//And finally the Y axis, because this is the axis most likely to give a positive
					double[] deltaY = curobj.deltaY();
					if (newpos[1] >= deltaY[0] - buffer && newpos[1] <= deltaY[1] + buffer) {
						//Ok, so we're in this objects interaction box, time to do a more precise detection
						double[] collNormal = curobj.collisionNormal(pos, newpos, buffer);
						if (collNormal != null) {
							if (curobj instanceof MoveableObject) {
								((MoveableObject) curobj).addMovement(new double[] {-collNormal[0], -collNormal[1], -collNormal[2]});
								//If it's a moveableObject, get that one started in the opposite direction instead
							} else {
								//slide along the wall by nullifing the movement by the normal
								newpos[0] = newpos[0] + Math.abs(newpos[0] - pos[0]) * collNormal[0];
								newpos[1] = newpos[1] + Math.abs(newpos[1] - pos[1]) * collNormal[1];
								newpos[2] = newpos[2] + Math.abs(newpos[2] - pos[2]) * collNormal[2];
							}
						}
						
					}
				}
			}
		}
		
		if(heldObject != null && (newpos[0] != pos[0] || newpos[1] != pos[1] || newpos[2] != pos[2])) {			
			heldObject.moveTo(new double[] {newpos[0] + Math.sin(yrot) * objectBuffer, newpos[1] + Math.sin(xrot) * objectBuffer, newpos[2] + Math.cos(yrot) * objectBuffer});
		}
		
		pos = newpos;
		
		
		
	}
	
	/**TODO:
	 * only for debugging purposes, remove before realase
	 */
	public void printpos() {
		System.out.println(pos[0] + ", " + pos[1] + ", " + pos[2]);
	}

	/**
	 * Move in this direction until it's stopped
	 * 
	 * @param direction
	 */
	public void startMoving(int direction) {
		moving[direction] = true;
	}

	/**
	 * Stop moving in this direction
	 * 
	 * @param direction
	 */
	public void stopMoving(int direction) {
		moving[direction] = false;
	}

	/**
	 * Sends the info of position and viewing angle to OpenGL engine
	 * 
	 * @param gl
	 */
	public void render(GL2 gl, GLU glu) {

		glu.gluLookAt(pos[0], pos[1], pos[2], // the camera position
				pos[0] + Math.sin(yrot), pos[1] + Math.sin(xrot), pos[2] + Math.cos(yrot), // give angle relative to position
				0, 1, 0); // we don't tilt our heads

	}

	/**
	 * Changes the camera angle
	 * 
	 * @param deltaX
	 * @param deltaY
	 */
	public void changeView(int deltaX, int deltaY) {
		yrot += deltaX * mouseSense;

		double possibleX = xrot + deltaY * mouseSense;
		if (Math.abs(possibleX) < Math.PI / 2) {
			xrot += deltaY * mouseSense;
		}
	}
	
	/**
	 * While the mousebutton is held, drag the object in front of you
	 * @param deltaX
	 * @param deltaY
	 */
	public void dragObject(int deltaX, int deltaY, int totalX, int totalY) {
		changeView(deltaX, deltaY);
		if(heldObject != null) {			
			heldObject.moveTo(new double[] {pos[0] + Math.sin(yrot) * objectBuffer, pos[1] + Math.sin(xrot) * objectBuffer, pos[2] + Math.cos(yrot) * objectBuffer});
			//System.out.println((double) deltaX/totalX);
			double rotation =  (double)deltaX/ (double)totalX;
			//System.out.println( yrot * Math.PI * 2.0);
			heldObject.rotate(new double[] {0, rotation * Math.PI * 2.0, 0});
			//heldObject.rotate(new double[] {deltaX/(Math.PI*2), deltaY/(Math.PI*2), deltaX/(Math.PI*2) });
			//heldObject.rotate(new double[] {0,0.01,0});
		}
	}
	
	/**
	 * Grabs hold of the object in front of you 
	 * @param gameObjects
	 */
	public void grabObject(MoveableObject[] gameObjects) {
		if (heldObject == null) {
			double[] handpos = new double[] {pos[0] + Math.sin(yrot) * 3, pos[1] + Math.sin(xrot) * 3, pos[2] + Math.cos(yrot) * 3};
			for (int i = 0; i < gameObjects.length; i++) {
				MoveableObject curobj = gameObjects[i]; //all moveable objects are gameobjects
				
				// Check if we're in the vicinity on the X axis
				double[] deltaX = curobj.deltaX();
				if (handpos[0] >= deltaX[0] && handpos[0] <= deltaX[1]) {
					// Then the Z axis
					double[] deltaZ = curobj.deltaZ();
					if (handpos[2] >= deltaZ[0] && handpos[2] <= deltaZ[1]) {
						// And finally the Y axis, because this is the axis most likely to give a positive
						double[] deltaY = curobj.deltaY();
						if (handpos[1] >= deltaY[0] && handpos[1] <= deltaY[1]) {
							//take the first object we find
							heldObject = curobj;
							heldObject.startMoving(new double[]{0,0,0}); //Stop moving the object
							heldObject.startRotating(new double[]{0,0,0}); //and stop rotation
							objectBuffer = Math.abs(deltaX[0] - deltaX[1]) * 2;
							
							return;
						}
					}
				}
			}
		}
	}
	
	public boolean isHoldingObject(MoveableObject obj) {
		if (heldObject != null && obj.equals(heldObject)) {
			return true;
		}
		return false;
	}

	/**
	 * Releases the object that was held
	 */
	public void realeseObject() {
		heldObject = null;
	}
}
