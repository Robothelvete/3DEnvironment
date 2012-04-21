import javax.media.opengl.GL2;


/**
 * Lowest common denominator for all objects in the "game", all shall implement this interface
 * @author Robert
 *
 */
public interface GameObject {
	/**
	 * Renders the object to the screen
	 */
	public void draw(GL2 gl);
	
	/**
	 * Rotates the object
	 * @param x
	 * @param y
	 * @param z
	 * @param degrees
	 * @return False if object cannot rotate so
	 */
	//TODO: I feel like I should change the parameters here to something better....
	public boolean rotate(float x, float y,float z, float degrees);
	
	/**
	 * Moves the object
	 * @param x
	 * @param y
	 * @param z
	 * @return False if object cannot be moved so
	 */
	public boolean move(float x, float y, float z);
}
