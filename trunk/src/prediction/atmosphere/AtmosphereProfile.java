package prediction.atmosphere;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


/**
 * A class to extract the state of the atmosphere at any point given a discrete sounding.
 * 
 * @author Ethan Harstad
 * Space Systems and Controls Lab
 * http://www.sscl.iastate.edu
 */
public class AtmosphereProfile {
        
        // Profile specifications
        protected int mStartTime;
        protected int mEndTime;
        protected static final int mTimeStep = 10800;
        protected double mLat;
        protected double mLon;
        protected double mResolution = 0.5;
        
        // Discrete sample data
        private ArrayList<AtmosphereState> data = new ArrayList<AtmosphereState>(5);
        // Lapse rates of the above data
        private ArrayList<AtmosphereState> roc = new ArrayList<AtmosphereState>(5);
        
        /**
         * See if the model is valid at the given time and location.
         * @param time Unix time to check
         * @param lat Latitude of the point to check
         * @param lon Longitude of the point to check
         * @return
         */
        public boolean isValid(int time, double lat, double lon) {
                if((time >= mStartTime) && ((time <= mEndTime) || (mEndTime < 0))) {
                        if((Math.abs(lat-mLat) <= mResolution) && (Math.abs(lon-mLon) <= mResolution)) {
                                return true;
                        }
                }
                return false;
        }
        
        /**
         * Add a discrete sample to the profile
         * @param pressure
         * @param altitude
         * @param temp
         * @param dewPoint
         * @param windDir
         * @param windSpeed
         */
        protected void addData(double pressure, double altitude, double temp, double dewPoint, double windDir, double windSpeed) {
                data.add(new AtmosphereState(pressure, altitude, temp, dewPoint, windDir, windSpeed));  // store the new data point
                Collections.sort(data); // sort the data with increasing altitude
                Iterator<AtmosphereState> itr = data.iterator();
                AtmosphereState cur = itr.next();
                roc.clear();
                while(itr.hasNext()) {  // iterate over steps
                        AtmosphereState next = itr.next();
                        // compute rates of change with altitude
                        double dh = next.altitude - cur.altitude;
                        double dp = (next.pressure - cur.pressure)/dh;
                        double dt = (next.temp - cur.temp)/dh;
                        double dd = (next.dewPoint - cur.dewPoint)/dh;
                        double ds = (next.windSpeed - cur.windSpeed)/dh;
                        double ddir = next.windDir - cur.windDir;
                        if(Math.abs(ddir) > 180) {      // wind is tricky because of degree wrapping
                                if(ddir > 0) {
                                        ddir = ddir - 360;
                                } else {
                                        ddir = ddir + 360;
                                }
                        }
                        ddir = ddir / dh;
                        roc.add(new AtmosphereState(dp, dh, dt, dd, ddir, ds)); // store into rate of change table
                        cur = next;
                }
        }
        
        /**
         * Get the state of the model at the given altitude and time
         * @param alt Altitude in the units used to create the model
         * @return
         */
        public AtmosphereState getAtAltitude(double altitude) {
                Iterator<AtmosphereState> itr = data.iterator();
                AtmosphereState base = itr.next();
                AtmosphereState next = itr.next();
                int i = 0;
                while(altitude > next.altitude) {
                        base = next;    // advance base value
                        i++;
                        if(itr.hasNext()) {     // more defined data exists
                                next = itr.next();      // use it
                        } else {        // no defined data exists
                                // standard atmosphere approximation
                                //TODO standard atmosphere approximation
                                return new AtmosphereState(0,0,0,0,0,0);
                        }
                }
                // compute interpolated values
                AtmosphereState slope = roc.get(i);
                double dh = altitude-base.altitude;
                double p = slope.pressure*dh + base.pressure;
                double t = slope.temp*dh + base.temp;
                double d = slope.dewPoint*dh + base.dewPoint;
                double dir = slope.windDir*dh + base.windDir;
                double speed = slope.windSpeed*dh + base.windSpeed;
                return new AtmosphereState(p, altitude, t, d, dir, speed);
        }

}