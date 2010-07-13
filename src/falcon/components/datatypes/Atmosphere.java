package falcon.components.datatypes;

//TODO Atmosphere

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * A class to compute atmospheric conditions based on NWS-ARL sounding data
 * or optionally on standard atmosphere data.
 * @author Ethan Harstad
 *
 */
public class Atmosphere {
	
	LinkedList<AtmosphereBase> a;
	
	private boolean ok = false;
	
	/**
	 * Construct an atmosphere object from the standard atmosphere.  This is much
	 * less accurate and lacks wind data.  Should only be used for burst prediction.
	 * Based on 1976 US Standard Atmosphere
	 */
	public Atmosphere() {
		//TODO Standard Atmosphere
		a.add(new AtmosphereBase(0, 1013.25, 288.15, 0, 0, -0.0065, 0, 0));
		a.add(new AtmosphereBase(11000, 226.321, 216.65, 0, 0, 0, 0, 0));
		a.add(new AtmosphereBase(20000, 57.7489, 216.65, 0, 0, 0.00, 0, 0));
		a.add(new AtmosphereBase(32000, 8.68019, 228.65, 0, 0, 0.0028, 0, 0));
	}
	
	/**
	 * Construct an atmosphere object from the given sounding file.  This is the
	 * preferred method of instantiation.  Use Press/Hgt/Temp/DewPt/WndDir/WndSpd
	 * table from GFS Sounding from NOAA ARL.
	 * @param filename
	 */
	public Atmosphere(String filename) {
		Scanner file;
		try {
			file = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			System.err.println("Winds file '" + filename + "' could not be loaded!");
			return;
		}
		
		LinkedList<AtmosphereState> base = new LinkedList<AtmosphereState>();
		while(file.hasNextLine()) {
			String line = file.nextLine();
			if(line.charAt(0) == '!') continue;
			Scanner parser = new Scanner(line);
			double p = 0, alt = 0, t = 0, dp = 0, dir = 0, s = 0;
			try {
				p = parser.nextDouble();
				alt = parser.nextDouble();
				t = parser.nextDouble();
				dp = parser.nextDouble();
				dir = parser.nextDouble();
				s = parser.nextDouble();
			} catch(Exception e) {
				System.err.println("Winds file '" + filename +"' did not parse correctly!");
			}
			base.add(new AtmosphereState(p, alt, t, dp, dir, s));	
		}
		
		//Sort the list
		Collections.sort(base, new heightOrder());
		
		//TODO determine interpolation
		
		ok = true;
	}
	
	/**
	 * Convert given MSL altitude in meters to geopotential altitude in meters
	 * @param a
	 * @return
	 */
	private static double toGeopotential(double a) {
		return (6378100 * a) / (6378100 + a);
	}

}

class AtmosphereState {
	
	public double pressure;		//Pressure in hPa
	public double altitude;		//Altitude MSL in meters
	public double temp;			//Temperature in C
	public double dewPoint;		//Dew Point in C
	public double direction;	//Wind direction in degrees from north
	public double speed;		//Wind speed in meters per second
	
	public AtmosphereState(double p, double alt, double t, double dp, double dir, double s) {
		pressure = p;
		altitude = alt;
		temp = t;
		dewPoint = dp;
		direction = dir;
		speed = s;
	}
	
}

class AtmosphereBase {
	
	public double altitude;
	public double basePressure;
	public double baseTemp;
	public double baseSpeed;
	public double baseDir;
	public double lapseRate;
	//public double dP
	public double dSpd;
	public double dDir;
	
	public AtmosphereBase(double alt, double p, double t, double s, double dir, double lapse, double ds, double dd) {
		altitude = alt;
		basePressure = p;
		baseTemp = t;
		baseSpeed = s;
		baseDir = dir;
		lapseRate = lapse;
		//dP = dPres;
		dSpd = ds;
		dDir = dd;
	}
	
}

class heightOrder implements Comparator<AtmosphereState> {
	public int compare(AtmosphereState x, AtmosphereState y) {
		if(x.altitude == y.altitude) {
			return 0;
		} else if(x.altitude < y.altitude) {
			return -1;
		} else {
			return 1;
		}
	}
}
