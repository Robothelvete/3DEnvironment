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

/**
* Checks if a point collides with an object, and returns a normal to the surface to which it just collided if that is the case
* @param startpoint The point we originated from, so we know which surface it was collided into
* @param endpoint The point with which to check collision
* @return A normal (vector in 3D) to the surface which the point just collided into, or if no collision was detected: null
*/
public double[] collisionNormal(double[] startpoint, double[] endpoint);

public double[] collisionNormal(double[] startpoint, double[] endpoint, double buffersize);
}