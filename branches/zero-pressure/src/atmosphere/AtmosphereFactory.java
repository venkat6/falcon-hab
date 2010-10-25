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
		// create a file to store the data
		String filename = Integer.toString(time/1000)+"_"+Integer.toString((int)(lat*100))+"_"+Integer.toString((int)(lon*100))+".dat";
		FileOutputStream out;
		PrintStream p = null;
		try {
			out = new FileOutputStream(filename);
			p = new PrintStream(out);
		} catch(Exception e) {
			e.printStackTrace();
		}
		// read data, format and store to file
		//TODO
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
