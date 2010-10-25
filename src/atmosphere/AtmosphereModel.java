package atmosphere;

public class AtmosphereModel {
	
	protected int mStartTime;
	protected int mEndTime;
	protected double mLat;
	protected double mLon;
	protected double mResolution = 0.5;
	
	private AtmosphereProfile start;
	private AtmosphereProfile end;
	
	public AtmosphereModel(AtmosphereProfile startProfile, AtmosphereProfile endProfile) {
		start = startProfile;
		end = endProfile;
	}
	
	public boolean isValid(int time, double lat, double lon) {
		if((time >= mStartTime) && ((time <= mEndTime) || (mEndTime < 0))) {
			if((Math.abs(lat-mLat) <= mResolution) && (Math.abs(lon-mLon) <= mResolution)) {
				return true;
			}
		}
		return false;
	}
	
	public AtmosphereState getAtAltitude(double alt, int time) {
		AtmosphereState s = start.getAtAltitude(alt);
		AtmosphereState e = end.getAtAltitude(alt);
		double x = (time - mStartTime)/(mStartTime - mEndTime);
		double p = (e.pressure - s.pressure)*x + s.pressure;
		double t = (e.temp - s.temp)*x + s.temp;
		double dp = (e.dewPoint - s.dewPoint)*x + s.dewPoint;
		double speed = (e.windSpeed - s.windSpeed)*x + s.windSpeed;
		double ddir = e.windDir - s.windDir;
		if(Math.abs(ddir) > 180) {	// wind is tricky because of degree wrapping
			if(ddir > 0) {
				ddir = ddir - 360;
			} else {
				ddir = ddir + 360;
			}
		}
		double dir = ddir*x + s.windDir;
		return new AtmosphereState(p, alt, t, dp, dir, speed);
	}

}
