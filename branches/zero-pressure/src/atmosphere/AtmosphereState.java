package atmosphere;

public class AtmosphereState implements Comparable<AtmosphereState> {
	
	public double pressure;
	public int altitude;
	public double temp;
	public double dewPoint;
	public int windDir;
	public double windSpeed;
	
	public AtmosphereState(double p, int a, double t, double dp, int dir, double speed) {
		pressure = p;
		altitude = a;
		temp = t;
		dewPoint = dp;
		windDir = dir;
		windSpeed = speed;
	}
	
	@Override
	public int compareTo(AtmosphereState x) {
		if(pressure < x.pressure) {
			return -1;
		} else if(pressure > x.pressure) {
			return 1;
		}
		return 0;
	}

}

