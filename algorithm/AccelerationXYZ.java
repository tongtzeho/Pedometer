package algorithm;

public class AccelerationXYZ {

	public static double[] calculate(double ax, double ay, double az, double gx, double gy, double gz) {
		double result[] = new double[6];
		double g = Math.sqrt(gx*gx+gy*gy+gz*gz);
		result[0] = gx/g;
		result[1] = gy/g;
		result[2] = gz/g;
		result[3] = ax*result[0]+ay*result[1]+az*result[2];
		result[4] = Math.sqrt(ax*ax+ay*ay+az*az-result[3]*result[3]);
		result[5] = Math.asin(result[2])*180/Math.PI;
		return result;
	}
}
