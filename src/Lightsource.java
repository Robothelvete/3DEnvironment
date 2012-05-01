import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLLightingFunc;


/**
 * Class control a lightsource
 * @author Robert
 *
 */
public class Lightsource {
	private float[] pos;
    private float[] lightColor;
    private float[] ambientColor;
    private int lightnum;
    
	public Lightsource(int lightnum, float[] positions, float[] color, float[] ambcolor) {
		this.lightnum = lightnum + 16384; //the enum for lights start at this value
		pos = positions;
		lightColor = color;
		ambientColor = ambcolor;
	}
	
	/**
	 * Renders the lightsource to the environment
	 * @param gl
	 */
	public void draw(GL2 gl) {
		gl.glLightfv(lightnum, GLLightingFunc.GL_POSITION, pos, 0);
		//Since opengl itself keeps track of it's lightsources, and it has already been initialized,
		//as long as we don't need to change the light itself, only the position needs to be fed in to opengl again
		//so it can place it out correctly in accordance with the players updated position
	}
	
	public void init(GL2 gl) {
		gl.glLightfv(lightnum, GLLightingFunc.GL_SPECULAR, lightColor, 0);
		gl.glLightfv(lightnum, GLLightingFunc.GL_POSITION, pos, 0);
		gl.glEnable(lightnum);
	}
}
