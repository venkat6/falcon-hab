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

public class AtmosphereFactory {
	
	public static AtmosphereModel getGfsModel(int time, double lat, double lon) {
		int startTime = (time/AtmosphereProfile.mTimeStep)*AtmosphereProfile.mTimeStep;
		int endTime = ((time/AtmosphereProfile.mTimeStep)+1)*AtmosphereProfile.mTimeStep;
		AtmosphereProfile startState = getGfsProfile(startTime, lat, lon);
		AtmosphereProfile endState = getGfsProfile(endTime, lat, lon);
		
		return new AtmosphereModel(startState, endState);
	}
	
	public static AtmosphereProfile getGfsProfile(int time, double lat, double lon) {
		time = (time/AtmosphereProfile.mTimeStep)*AtmosphereProfile.mTimeStep;
		// connect to url
		String address = 	"http://rucsoundings.noaa.gov/get_soundings.cgi?data_source=GFS;airport="+lat+","+lon+
							";hydrometeors=false&startSecs="+time+"&endSecs="+(time+1);
		//System.out.println(address);
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
		int hour, day, month, year;
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
						double p = parser.nextInt()/10;			// value is in tenths
						int h = parser.nextInt();
						double t = parser.nextInt()/10;			// value is in tenths
						double dp = parser.nextInt()/10;		// value is in tenths
						int dir = parser.nextInt();
						double spd = parser.nextInt()*0.51444;	// convert knots to m/s
						datas.add(p+"\t"+h+"\t"+t+"\t"+dp+"\t"+dir+"\t"+spd);
					} else if(type == 9) {	// surface level (might be unreliable)
						double p = parser.nextInt()/10;			// value is in tenths
						int h = parser.nextInt();
						double t = parser.nextInt()/10;			// value is in tenths
						double dp = parser.nextInt()/10;		// value is in tenths
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
						String temp = parser.next();
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
		String filename = "winds\\" + Integer.toString(time)+"_"+Integer.toString((int)(lat*100))+"_"+Integer.toString((int)(lon*100))+".dat";
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
		return parseGfsProfile(filename);
	}
	
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
