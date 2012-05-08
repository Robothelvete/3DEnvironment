import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class CollisionTester {
	private ThickWall wall;
	private ThickWall wall2;
	@Before
	public void setUp() throws Exception {
		wall = new ThickWall(new double[]{10,0,42}, new double[]{10,10,42}, new double[]{-40,10,42}, 2, new double[]{0.1,0,0.8});
		wall2 = new ThickWall(new double[]{10,0,0}, new double[]{10,10,0}, new double[]{10,10,40}, 2, new double[]{0.0,0.5,0.2});
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testCollisionNormal() {
		//fail("Not yet implemented"); // TODO
		double[] test = wall.collisionNormal(new double[]{0,5,39}, new double[]{0,5,41});
		assertEquals(test, new double[]{-0.0,0.0,-1.0});
		
		double[] test2 = wall2.collisionNormal(new double[]{7,5,10}, new double[]{9,5,10});
		assertEquals(test2, new double[]{-1.0,0.0,-0.0});
		
	}

}
