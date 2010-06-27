package falcon.helpers;
//TODO Az and El calculations

import falcon.components.datatypes.Location;
import falcon.components.datatypes.Position;

public class AzElCalc {

//	public static double calcAz(Location base, Location target) {
//		double bLat = base.getLatitude();
//		double bLon = base.getLongitude();
//		double tLat = target.getLatitude();
//		double tLon = target.getLongitude();
//	}
	
//	public static double calcEl(Position base, Position target) {
//		double bLat = base.getLatitude();
//		double bLon = base.getLongitude();
//		double bAlt = base.getAltitude();
//		double tLat = base.getLatitude();
//		double tLon = base.getLongitude();
//		double tAlt = base.getAltitude();
//	}
	
	public static double length(double[] x) {
		double length = 0;
		for(int i = 0; i < x.length; i++) {
			length += x[i] * x[i];
		}
		return Math.sqrt(length);
	}
	
}
