package atmosphere;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * A factory class to generate atmosphere objects
 * 
 * @author Ethan Harstad
 * Space Systems and Controls Lab
 * http://www.sscl.iastate.edu
 */
public class AtmosphereFactory {
	
	/**
	 * Generate an AtmosphereModel object and return it. Use this method instead of the AtmosphereModel constructor.
	 * 
	 * @param time A time that is guaranteed to be included in the model
	 * @param lat Center latitude of the model
	 * @param lon Center longitude of the model
	 * @return
	 */
	public static AtmosphereModel getGfsModel(int time, double lat, double lon) {
		int startTime = (time/AtmosphereProfile.mTimeStep)*AtmosphereProfile.mTimeStep;
		int endTime = ((time/AtmosphereProfile.mTimeStep)+1)*AtmosphereProfile.mTimeStep;
		AtmosphereProfile startState = getGfsProfile(startTime, lat, lon);
		AtmosphereProfile endState = getGfsProfile(endTime, lat, lon);
		AtmosphereModel atmo = new AtmosphereModel();
		atmo.start = startState;
		atmo.end = endState;
		atmo.mStartTime = startTime;
		atmo.mEndTime = endTime;
		atmo.mLat = lat;
		atmo.mLon = lon;
		return atmo;
	}
	
	/**
	 * Generate an AtmosphereProfile object and return it. Use this method instead of the AtmosphereProfile constructor.
	 * 
	 * @param time A time the is guaranteed to be included in the profile
	 * @param lat Center latitude of the model
	 * @param lon Center longitude of the model
	 * @return
	 */
	public static AtmosphereProfile getGfsProfile(int time, double lat, double lon) {
		time = (time/AtmosphereProfile.mTimeStep)*AtmosphereProfile.mTimeStep;
		// connect to url
		String address = 	"http://rucsoundings.noaa.gov/get_soundings.cgi?data_source=GFS;airport="+lat+","+lon+
							";hydrometeors=false&startSecs="+time+"&endSecs="+(time+1);
		URL url;
		InputStream is = null;
		InputStreamReader isr = null;
		try {
			url = new URL(address);
			is = url.openStream();
		} catch (MalformedURLException e) {
			System.err.println("Bad URL: " + address);
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		ArrayList<String> datas = new ArrayList<String>();
		// read data and format
		String line = null;
		int hour = 0, day = 0, year = 0;
		String month = null;
		int i = 0;
		try {
			while((line = br.readLine()) != null) {
				i++;
				if(i == 1) {
					continue;	// Discard the first line
				}
				Scanner parser = new Scanner(line);
				if(parser.hasNextInt()) { // Data line
					int type = parser.nextInt();
					if(type == 1) {	// Station ID Line
						parser.next();
						parser.next();
						lat = parser.nextDouble();
						lon = -parser.nextDouble();
						continue;
					} else if(type == 4) {
						double p = parser.nextInt()/10.0;			// value is in tenths
						int h = parser.nextInt();
						double t = parser.nextInt()/10.0;			// value is in tenths
						double dp = parser.nextInt()/10.0;		// value is in tenths
						int dir = parser.nextInt();
						double spd = parser.nextInt()*0.51444;	// convert knots to m/s
						datas.add(p+"\t"+h+"\t"+t+"\t"+dp+"\t"+dir+"\t"+spd);
					} else if(type == 9) {	// surface level (might be unreliable)
						double p = parser.nextInt()/10.0;			// value is in tenths
						int h = parser.nextInt();
						double t = parser.nextInt()/10.0;			// value is in tenths
						double dp = parser.nextInt()/10.0;		// value is in tenths
						int dir = parser.nextInt();
						double spd = parser.nextInt()*0.51444;	// convert knots to m/s
						datas.add(p+" "+h+" "+t+" "+dp+" "+dir+" "+spd);
					} else {
						continue;
					}
				} else {
					if(!parser.hasNext()) continue;
					String blah = parser.next();		// See if should be discarded
					if(blah.equals("CAPE")) continue;
					if(parser.hasNextInt()) {
						hour = parser.nextInt();
						day = parser.nextInt();
						month = parser.next();
						year = parser.nextInt();
						continue;
					} else {
						continue;
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Problem reading URL stream!");
			e.printStackTrace();
		}
		// create a file to store the data
		String filename = "winds\\" + Integer.toString(year)+ month + Integer.toString(day)+ "-" + Integer.toString(hour) +"_"+Integer.toString((int)(lat*100))+"_"+Integer.toString((int)(lon*100))+".dat";
		FileOutputStream out;
		PrintStream p = null;
		try {
			out = new FileOutputStream(filename);
			p = new PrintStream(out);
		} catch(FileNotFoundException e) {
			System.err.println("Problem opening file: " + filename);
			e.printStackTrace();
		}
		// write to the file
		Iterator<String> itr = datas.iterator();
		while(itr.hasNext()) {
			p.println(itr.next());
		}
		// close the file
		p.close();
		// parse the file and return the profile
		AtmosphereProfile atmo = parseGfsProfile(filename);
		atmo.mStartTime = time;
		atmo.mEndTime = time + AtmosphereProfile.mTimeStep;
		atmo.mLat = lat;
		atmo.mLon = lon;
		return atmo;
	}
	
	/**
	 * Parse a GFS wind profile and return an AtmosphereProfile created from it.
	 * It is recommended to use downloadGfsProfile instead.
	 * 
	 * @param filename The filename of the wind profile file
	 * @return
	 */
	public static AtmosphereProfile parseGfsProfile(String filename) {
		AtmosphereProfile atmo = new AtmosphereProfile();
		FileInputStream is = null;
		try {
			is = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file: " + filename);
			e.printStackTrace();
			return atmo;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		try {
			while((line = br.readLine()) != null) {
				Scanner parser = new Scanner(line);
				double p = parser.nextDouble();
				double a = parser.nextDouble();
				double t = parser.nextDouble();
				double dp = parser.nextDouble();
				double dir = parser.nextDouble();
				double spd = parser.nextDouble();
				atmo.addData(p, a, t, dp, dir, spd);
			}
		} catch (IOException e) {
			System.err.println("Problem reading file: " + filename);
			e.printStackTrace();
			return atmo;
		}		
		return atmo;
	}

}
