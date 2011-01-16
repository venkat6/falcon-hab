package prediction.atmosphere;

/**
 * A class to hold all values defining the state of the atmosphere.
 * 
 * @author Ethan Harstad
 * Space Systems and Controls Lab
 * http://www.sscl.iastate.edu
 */
public class AtmosphereState implements Comparable<AtmosphereState> {
        
        public double pressure;
        public double altitude;
        public double temp;
        public double dewPoint;
        public double windDir;
        public double windSpeed;
        
        /**
         * Create an atmospheric state
         * @param p
         * @param a
         * @param t
         * @param dp
         * @param dir
         * @param speed
         */
        public AtmosphereState(double p, double a, double t, double dp, double dir, double speed) {
                pressure = p;
                altitude = a;
                temp = t;
                dewPoint = dp;
                windDir = dir;
                windSpeed = speed;
        }
        
        /**
         * Compare this state to the given state by altitude
         * @param x The state to compare to
         */
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