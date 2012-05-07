/**
 * All objects that can be moved, rotated and so forth. Objects that aren't static
 * @author Robert
 *
 */
public interface MoveableObject {
	/**
	 * Rotates the object on it's rotationvector, and checks for collisions
	 * @param timelasted - the time passed between the last and this frame
	 * @param otherObjects - the objects with which to check collisions agaisnt 
	 */
	public void rotate(long timelasted, GameObject[] otherObjects);
	
	/**
	 * Moves this object along it's movement vector, and checks for collisions
	 * @param timelasted - the time passed between the last and this frame
	 * @param otherObjects- the objects with which to check collisions agaisnt 
	 */
	public void move(long timelasted, GameObject[] otherObjects);
	
	/**
	 * Sets this object moving in the direction given by the vector
	 * @param movingVector
	 */
	public void startMoving(double[] movingVector);
	
	/**
	 * Starts rotating along this vector
	 * @param rotationVector
	 */
	public void startRotating(double[] rotationVector);
}
