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
	 * The smallest and largest values on the X axis for this object. 
	 */
	public double[] deltaX();
	
	/**
	 * The smallest and largest values on the Y axis for this object
	 */
	public double[] deltaY();
	
	/**
	 * The smallest and largest values on the Z axis for this object
	 */
	public double[] deltaZ();
	
}
