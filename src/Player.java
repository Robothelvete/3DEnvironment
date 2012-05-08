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
	
	
	public Player() {
		pos = new double[] { 0.0, 2.0, -10.0 };// TODO
		moving = new boolean[] {false, false, false, false};
	}

	/**
	 * Moves the player/camera in the given direction. 
	 */
	public void move(long timelasted, GameObject[] otherObjects) { //TODO: make this more pretty
		//walkingspeed is in other words how long he should have moved this frame
		double walkingspeed = speed * timelasted/1000000000;
		
		for (int i = 0; i < 4; i++) {
			if (moving[i]) {
				int direction = i;
				if (direction % 2 == 0) {
					int forback = 1;
					if (direction > 0)
						forback = -1;

					pos[0] += Math.sin(yrot) * walkingspeed * forback;//movement on X axis
					pos[2] += Math.cos(yrot) * walkingspeed * forback;//movement on Z axis
				} else {
					int leftright = -1;
					if (direction > 1)
						leftright = 1;

					pos[0] += Math.cos(yrot) * walkingspeed * leftright;
					pos[2] -= Math.sin(yrot) * walkingspeed * leftright; // negative because I've switched the axles
				}
			}
		}
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
}
