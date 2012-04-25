import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

/**
 * Holds all information about player, camera position and angle and such
 * @author Robert
 *
 */
public class Player {
	private double[] pos;
	//angle is determined in opengl as a rotation around a vector in degrees
	//as it will not be necessary to rotate along the z-axis (which would be akin to walking the walls and ceiling), only these two are needed
	//we store these bad boys as radians however
	private double xrot; 
	private double yrot;
	private static final double speed = 0.2f;
	private static final double mouseSense = 0.002f * Math.PI;

	public Player(){
		pos = new double[]{0.0f, 2.0f, -10.0f};//TODO
	}
	
	/**
	 * Moves the player/camera in the given direction. 
	 * @param direction - int 0->3, 0 for forward, 1 for right and so on clockwise.
	 * @return Wether it was possible to move so - necessary?
	 */
	public boolean move(int direction) {
		//Why the fuck does OpenGL measure angle in degrees? (javas Math library deals only with radians....)
		
		if (direction % 2 == 0) {
			int forback = 1;
			if (direction > 0)
				forback = -1;
			
			pos[0] += Math.sin(yrot) * speed * forback;
			pos[2] += Math.cos(yrot) * speed * forback;
		}
		else {
			int leftright = -1;
			if (direction > 1)
				leftright = 1;
			
			pos[0] += Math.cos(yrot) * speed * leftright;
			pos[2] += Math.sin(yrot) * speed * leftright;
		}
		System.out.println(pos[0] + " " + pos[1] + " " + pos[2]);
		System.out.println(Math.sin(yrot) + " " + Math.cos(yrot));
		return true;
	}
	
	/**
	 * Sends the info of position and viewing angle to OpenGL engine
	 * @param gl
	 */
	public void render(GL2 gl, GLU glu) {
		//gl.glTranslated(-pos[0], -pos[1], -pos[2]);
		//gl.glRotated(270, 1.0f, 0.0f, 0.0f);
		//gl.glRotated(toDegrees(yrot), 0.0f, 1.0f, 0.0f);
//		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
//		gl.glLoadIdentity();
//		
//		glu.gluPerspective(45, 16/9, 1, 1000);
		
//		glu.gluLookAt(pos[0], pos[1], pos[2], 
//				pos[0] + Math.sin(yrot), pos[1] + Math.sin(xrot), pos[2],
//				0, 1, 0);
		glu.gluLookAt(pos[0], pos[1], pos[2], 
				//yrot, xrot, 0.0f,
				pos[0] + Math.sin(yrot), pos[1] + Math.sin(xrot), pos[2] + Math.cos(yrot),
				0, 1, 0);
//		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
//        gl.glLoadIdentity();
	}
	
	/**
	 * Changes the camera angle
	 * @param deltaX
	 * @param deltaY
	 */
	public void changeView(int deltaX, int deltaY) {
		yrot += deltaX * mouseSense;
		xrot += deltaY * mouseSense;
//		yrot += deltaX/5;
//		xrot += deltaY/5;
	}
	private double toDegrees(double rads) {
		return rads * 180 / Math.PI;
	}
/*	
	private double toRadians(double degs){
		
	}*/
}
