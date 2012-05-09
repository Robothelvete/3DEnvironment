import java.awt.AWTException;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.Animator;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

/**
 * Main "game" engine with setup and the main loop
 * 
 * @author Robert
 * 
 */
public class GameEngine implements GLEventListener, KeyListener, MouseMotionListener {
	static GLU glu = new GLU();
	static GLCanvas canvas = new GLCanvas();
	static Frame frame = new Frame("Gameframe");
	static Animator animator = new Animator(canvas);
	private GameObject[] gameObjects;
	private Lightsource[] lightsources;
	private MoveableObject[] moveableObjects;
	private Player player;
	//for centering mouse
	private static int centerX;
	private static int centerY;
	private Robot robot;
	private long lastNanoTime;
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case (KeyEvent.VK_ESCAPE):
			exit();
			break;	
		case (KeyEvent.VK_W):
			player.startMoving(0);
			break;
		case (KeyEvent.VK_D):
			player.startMoving(1);
			break;
		case (KeyEvent.VK_S):
			player.startMoving(2);
			break;
		case (KeyEvent.VK_A):
			player.startMoving(3);
			break;
		}
	}

	public GameEngine() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			exit();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case (KeyEvent.VK_W):
			player.stopMoving(0);
			break;
		case (KeyEvent.VK_D):
			player.stopMoving(1);
			break;
		case (KeyEvent.VK_S):
			player.stopMoving(2);
			break;
		case (KeyEvent.VK_A):
			player.stopMoving(3);
			break;
		case (KeyEvent.VK_C):
			player.printpos();
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		//never seems to fire, don't know why	
	}

	@Override
	// TODO: check to see what this does in detail
	public void init(GLAutoDrawable gLDrawable) {
		GL2 gl = gLDrawable.getGL().getGL2();
		gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepth(1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		
		//Let there be light!
		for (int i = 0; i < lightsources.length; i++) {
			lightsources[i].init(gl);
		}
		gl.glEnable(GLLightingFunc.GL_LIGHTING);
		
		// add keyboard input listener
		((Component) gLDrawable).addKeyListener(this);

		// add mouse motion input listener
		((Component) gLDrawable).addMouseMotionListener(this);
		// center mouse
		robot.mouseMove(centerX, centerY);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	/**
	 * Main game loop - automagically called from Animator
	 */
	public void display(GLAutoDrawable gLDrawable) {
		final GL2 gl = gLDrawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		if (lastNanoTime == 0) {
			lastNanoTime = System.nanoTime(); //initialize
		}
		long timeLasted = System.nanoTime() - lastNanoTime;
		lastNanoTime = System.nanoTime();//reset time until next uh.... time
		
		player.move(timeLasted, gameObjects); //update position and render camera
		player.render(gl, glu);
		
		//place out the lights
		for (int i = 0; i < lightsources.length; i++) {
			lightsources[i].draw(gl);
		}
		
		//move all objects
		for (int i = 0; i < moveableObjects.length; i++) {
			moveableObjects[i].move(timeLasted, gameObjects);
			moveableObjects[i].rotate(timeLasted, gameObjects);
		}
		
		// Render all objects
		for (int i = 0; i < gameObjects.length; i++) {
			gameObjects[i].draw(gl);
		}
		
		
	}

	@Override
	// TODO: check to see what this does in detail
	public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
		GL2 gl = gLDrawable.getGL().getGL2();
		if (height <= 0) {
			height = 1;
		}
		float h = (float) width / (float) height;
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(50.0f, h, 1.0, 1000.0);
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	/**
	 * Exactly what it sounds like
	 */
	public static void exit() {
		animator.stop();
		frame.dispose();
		System.exit(0);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GameEngine ge = new GameEngine();
		canvas.addGLEventListener(ge);
		frame.add(canvas);
		frame.setUndecorated(true);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
		
		//hide cursor
		Toolkit t = Toolkit.getDefaultToolkit();
		java.awt.Image i = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_ARGB);
	    Cursor noCursor = t.createCustomCursor(i, new java.awt.Point(0, 0), "none"); 
		frame.setCursor(noCursor);

		ge.setupFromFile();
		frame.setVisible(true);

		centerX = frame.getSize().width / 2;
		centerY = frame.getSize().height / 2;

		animator.start(); // Start main game loop`
		canvas.requestFocus();
	}

	/**
	 * Sets up all game objects from a file
	 * TODO: clean this mess up
	 */
	public void setupFromFile() {
		BufferedReader br;

		try {
			br = new BufferedReader(new FileReader("resources/gameobjects.txt"));
		} catch (FileNotFoundException e) {
			System.err.println("Couldn't find file \"gameobjects.txt\"");
			exit();
			return; // compiler fluff
		}

		try {
			// First line says how many objects there are in total
			gameObjects = new GameObject[Integer.parseInt(br.readLine())];
			//the second tells us how many of them are movable
			moveableObjects = new MoveableObject[Integer.parseInt(br.readLine())];
			
			String line = br.readLine();
			int counter = 0;
			int movecounter = 0;
			while (line != null) {
				if (!line.startsWith("#")) { //ignore comments
					String[] allinfo = line.split(" ");

					switch (allinfo[0]) {
					case "wall":
						gameObjects[counter] = new Wall(parseDoubleArrays(allinfo[1]), parseDoubleArrays(allinfo[2]),
								parseDoubleArrays(allinfo[3]), parseDoubleArrays(allinfo[4]));
						break;// <-- that took me over an hour of bugtracking to remember
					case "line":
						gameObjects[counter] = new Line(parseDoubleArrays(allinfo[1]), parseDoubleArrays(allinfo[2]),
								parseDoubleArrays(allinfo[3]));
						break;
					case "thickwall":
						gameObjects[counter] = new ThickWall(parseDoubleArrays(allinfo[1]), parseDoubleArrays(allinfo[2]),
								parseDoubleArrays(allinfo[3]), Double.parseDouble(allinfo[4]), parseDoubleArrays(allinfo[5]));
						break;
					case "box":
						Box tmp = new Box(parseDoubleArrays(allinfo[1]), parseDoubleArrays(allinfo[2]),
								parseDoubleArrays(allinfo[3]));
						gameObjects[counter] = tmp;
						moveableObjects[movecounter] = tmp;
						tmp.startRotating(new double[]{1, 1, 1});
						movecounter++;
						break;
					}
					counter++;
				}
				line = br.readLine();
			}

			// TODO: set start position of player
			player = new Player();

		} catch (IOException e) {
			try {
				br.close();
			} catch (IOException e1) {
			}
			System.err.println("Couldn't read \"gameobjects.txt\"");
			exit();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// Well, you're on your own from here boy
			}
		}
		
		
		//now read in the lights
		try {
			br = new BufferedReader(new FileReader("resources/lightsources.txt"));
		} catch (FileNotFoundException e) {
			System.err.println("Couldn't find file \"lightsources.txt\"");
			exit();
			return; // compiler fluff
		}
		
		try {
			lightsources = new Lightsource[Integer.parseInt(br.readLine())];
			String line = br.readLine();
			int counter = 0;
			while (line != null) {
				if (!line.startsWith("#")) { //ignore comments
					String[] allinfo = line.split(" ");
					lightsources[counter] = new Lightsource(counter, parseFloatArrays(allinfo[0]), parseFloatArrays(allinfo[1]), parseFloatArrays(allinfo[2]));
					counter++;
				}
				line = br.readLine();

			}
		} catch (IOException e) {
		try {
			br.close();
		} catch (IOException e1) {
		}
		System.err.println("Couldn't read \"lightsources.txt\"");
		exit();
	} finally {
		try {
			br.close();
		} catch (IOException e) {
			// Well, you're on your own from here boy
		}
	}
	}

	/**
	 * Parses a comma-separated list of doubles as an array of such
	 * @param source
	 * @return
	 */
	public double[] parseDoubleArrays(String source) {
		String[] points = source.split(",");
		double[] arr = new double[points.length]; 

		for (int i = 0; i < points.length; i++) {
			arr[i] = Double.parseDouble(points[i]);
		}
		
		return arr;
	}

	/**
	 * Parses a comma-separated list of floats as an array of such
	 * @param source
	 * @return
	 */
	public float[] parseFloatArrays(String source) {
		String[] points = source.split(",");
		float[] arr = new float[points.length]; 
		
		for (int i = 0; i < points.length; i++) {
			arr[i] = Float.parseFloat(points[i]);
		}
		return arr;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		player.changeView(centerX - e.getX(), centerY - e.getY());
		robot.mouseMove(centerX, centerY);
	}
}
