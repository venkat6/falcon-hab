package atmosphere;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class AtmosphereFactory {
	
	public static AtmosphereModel getGfsModel(int time, double lat, double lon) {
		int startTime = (time/AtmosphereProfile.mTimeStep)*AtmosphereProfile.mTimeStep;
		int endTime = ((time/AtmosphereProfile.mTimeStep)+1)*AtmosphereProfile.mTimeStep;
		AtmosphereProfile startState = getGfsProfile(startTime, lat, lon);
		AtmosphereProfile endState = getGfsProfile(endTime, lat, lon);
		
		AtmosphereModel model = new AtmosphereModel();
		//TODO
		return model;
	}
	
	public static AtmosphereProfile getGfsProfile(int time, double lat, double lon) {
		//TODO
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
		// read data, format and store to file
		
		
		// close the file
		
		// parse the file and return the profile
		return parseGfsProfile(filename);
	}
	
	public static AtmosphereProfile parseGfsProfile(String filename) {
		AtmosphereProfile atmo = new AtmosphereProfile();
		
		//TODO
		
		return atmo;
	}

}
