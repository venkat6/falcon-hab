package atmosphere;

public class AtmosphereState implements Comparable<AtmosphereState> {
	
	public double pressure;
	public double altitude;
	public double temp;
	public double dewPoint;
	public double windDir;
	public double windSpeed;
	
	public AtmosphereState(double p, double a, double t, double dp, double dir, double speed) {
		pressure = p;
		altitude = a;
		temp = t;
		dewPoint = dp;
		windDir = dir;
		windSpeed = speed;
	}
	
	@Override
	public int compareTo(AtmosphereState x) {
		if(altitude < x.altitude) {
			return -1;
		} else if(altitude > x.altitude) {
			return 1;
		}
		return 0;
	}

}

