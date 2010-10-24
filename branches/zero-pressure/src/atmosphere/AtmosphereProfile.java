package atmosphere;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class AtmosphereProfile {
	
	protected int mStartTime;
	protected int mEndTime;
	protected static final int mTimeStep = 10800;
	protected double mLat;
	protected double mLon;
	protected double mResolution = 0.5;
	
	private ArrayList<AtmosphereState> data = new ArrayList<AtmosphereState>(5);
	private ArrayList<AtmosphereState> roc = new ArrayList<AtmosphereState>(5);
	
	public boolean isValid(int time, double lat, double lon) {
		if((time >= mStartTime) && ((time <= mEndTime) || (mEndTime < 0))) {
			if((Math.abs(lat-mLat) <= mResolution) && (Math.abs(lon-mLon) <= mResolution)) {
				return true;
			}
		}
		return false;
	}
	
	public void addData(double pressure, double altitude, double temp, double dewPoint, double windDir, double windSpeed) {
		data.add(new AtmosphereState(pressure, altitude, temp, dewPoint, windDir, windSpeed));	// store the new data point
		Collections.sort(data);	// sort the data with increasing altitude
		Iterator<AtmosphereState> itr = data.iterator();
		AtmosphereState cur = itr.next();
		roc.clear();
		//TODO test interpolation
		while(itr.hasNext()) {	// iterate over steps
			AtmosphereState next = itr.next();
			// compute rates of change with altitude
			double dh = next.altitude - cur.altitude;
			double dp = (next.pressure - cur.pressure)/dh;
			double dt = (next.temp - cur.temp)/dh;
			double dd = (next.dewPoint - cur.dewPoint)/dh;
			double ds = (next.windSpeed - cur.windSpeed)/dh;
			double ddir = next.windDir - cur.windDir;
			//TODO test wind direction interpolation
			if(Math.abs(ddir) > 180) {	// wind is tricky because of degree wrapping
				if(ddir > 0) {
					ddir = ddir - 360;
				} else {
					ddir = ddir + 360;
				}
			}
			ddir = ddir / dh;
			roc.add(new AtmosphereState(dp, dh, dt, dd, ddir, ds));	// store into rate of change table
			cur = next;
		}
	}
	
	public AtmosphereState getAtAltitude(double altitude) {
		Iterator<AtmosphereState> itr = data.iterator();
		AtmosphereState base = itr.next();
		AtmosphereState next = itr.next();
		int i = 0;
		while(altitude > next.altitude) {
			base = next;	// advance base value
			i++;
			if(itr.hasNext()) {	// more defined data exists
				next = itr.next();	// use it
			} else {	// no defined data exists
				// standard atmosphere approximation
				//TODO standard atmosphere approximation
				return new AtmosphereState(0,0,0,0,0,0);
			}
		}
		// compute interpolated values
		AtmosphereState slope = data.get(i);
		double dh = altitude-base.altitude;
		double p = slope.pressure*dh + base.pressure;
		double t = slope.temp*dh + base.temp;
		double d = slope.dewPoint*dh + base.dewPoint;
		double dir = slope.windDir*dh + base.windDir;
		double speed = slope.windSpeed*dh + base.windSpeed;
		return new AtmosphereState(p, altitude, t, d, dir, speed);
	}

}
