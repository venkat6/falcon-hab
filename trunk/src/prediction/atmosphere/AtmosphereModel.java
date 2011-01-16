package prediction.atmosphere;


/**
 * A class that extends the functionality of AtmosphereProfile by using two profiles to interpolate based on time.
 * 
 * @author Ethan Harstad
 * Space Systems and Controls Lab
 * http://www.sscl.iastate.edu
 */
public class AtmosphereModel {
        
        // Model specifications
        protected int mStartTime;
        protected int mEndTime;
        protected double mLat;
        protected double mLon;
        protected double mResolution = 0.5;
        
        // Start and end states of the model
        protected AtmosphereProfile start;
        protected AtmosphereProfile end;
        
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
         * Get the state of the model at the given altitude and time
         * @param alt Altitude in the units used to create the model
         * @param time Unix time
         * @return
         */
        public AtmosphereState getAtAltitude(double alt, int time) {
                AtmosphereState s = start.getAtAltitude(alt);
                AtmosphereState e = end.getAtAltitude(alt);
                double tStep = mEndTime - mStartTime;
                double deltaT = time - mStartTime;
                double x = deltaT / tStep;
                double p = (e.pressure - s.pressure)*x + s.pressure;
                double t = (e.temp - s.temp)*x + s.temp;
                double dp = (e.dewPoint - s.dewPoint)*x + s.dewPoint;
                double speed = (e.windSpeed - s.windSpeed)*x + s.windSpeed;
                double ddir = e.windDir - s.windDir;
                if(Math.abs(ddir) > 180) {      // wind is tricky because of degree wrapping
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
