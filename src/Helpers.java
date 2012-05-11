
public abstract class Helpers {
	/**
	 * Calculates the new movement vector after bounce against a normal
	 * 
	 * @param v
	 *            The movement vector
	 * @param n
	 *            The normal to the surface we bounce against
	 * @param elasticity
	 *            The energy left after impact, must be < 1
	 * @return
	 */
	public static double[] bounceVector(double[] v, double[] n, double elasticity) {
		double dp = dotProduct(v, n);
		double[] u = new double[] { n[0] * dp, n[1] * dp, n[2] * dp };

		double[] w = vectorFromPoints(v, u); // Even though these two are technically vectors already, it does the same
												// thing: w = v-u

		return vectorFromPoints(w, new double[] { u[0] * elasticity, u[1] * elasticity, u[2] * elasticity });
	}

	/**
	 * Calculates the dot product for two vectors
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	public static double dotProduct(double[] one, double[] two) {
		double returnvalue = 0;
		for (int i = 0; i < one.length; i++) {
			returnvalue += one[i] * two[i];
		}

		return returnvalue;
	}
	
	/**
	 * Find the angle between two vectors
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double vectorAngle(double[] v1, double[] v2) {
		return Math.acos(dotProduct(v1, v2) / (vectorLength(v1) * vectorLength(v2)));
	}

	public static double vectorLength(double[] v) {
		double sum = 0;
		for (int i = 0; i < v.length; i++) {
			sum += v[i] * v[i];
		}
		return Math.sqrt(sum);
	}
	
	public static double[] normalize(double v[]) {
		double length = vectorLength(v);
		double[] v2 = new double[3];
		for (int i = 0; i < 3; i++) {
			v2[i] = v[i]/length;
		}
		return v2;
	}
	
	/**
	 * The cross product of two vectors
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double[] crossProduct(double[] v1, double[] v2) {
		return new double[] {
				v1[1] * v2[2] - v1[2] * v2[1],
				v1[2] * v2[0] - v1[0] * v2[2],
				v1[0] * v2[1] - v1[1] * v2[0]
		};
	}

	/**
	 * Calculates a vector from a given point to another by (add - sub)
	 * 
	 * @param add
	 * @param sub
	 * @return
	 */
	public static double[] vectorFromPoints(double[] add, double[] sub) {
		return new double[] {
				add[0] - sub[0],
				add[1] - sub[1],
				add[2] - sub[2] };
	}
	
	public static double round(double v, int decimalplaces) {
		//int tmp = (int)( v * 100000000);
		int roundness = 10 ^ decimalplaces;
		double tmp = v * roundness;
		tmp = Math.round(tmp);
		return tmp/roundness;
	}
	
	public static double floor(double v, int decimalplaces) {
		//int tmp = (int)( v * 100000000);
		int roundness = 10 ^ decimalplaces;
		double tmp = v * roundness;
		return tmp/roundness;
	}
}
