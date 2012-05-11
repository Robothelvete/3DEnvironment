/**
 * All objects that can be moved, rotated and so forth. Objects that aren't static
 * @author Robert
 *
 */
public interface MoveableObject extends GameObject {
	/**
	 * Rotates, moves and in general updates this box and checks for collisions
	 * @param gameObjects
	 */
	public void takeAction(long timetaken, GameObject[] gameObjects);
	
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
	
	/**
	 * Move the object to this position
	 * @param pos
	 */
	public void moveTo(double[] pos);
	
	/**
	 * Rotates the box this much
	 * @param amount
	 */
	public void rotate(double[] amount);
	
	/**
	 * Applies gravity
	 * @param grav
	 */
	public void applyGravity(long timetaken, double grav);
	
	/**
	 * Adds movement to already moving object
	 * @param movingVector
	 */
	public void addMovement(double[] movingVector);
}
