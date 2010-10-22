package Atmosphere;

public class AtmosphereFactory {
	
	public static AtmosphereModel downloadGfsModel(int time, float lat, float lon) {
		int startTime = time; //FIXME
		int endTime = time; //FIXME
		AtmosphereState startState = parseGfsProfile(downloadGfsProfile(startTime, lat, lon));
		AtmosphereState endState = parseGfsProfile(downloadGfsProfile(endTime, lat, lon));
		
		AtmosphereModel model = new AtmosphereModel();
		//TODO
		return model;
	}
	
	public static String downloadGfsProfile(int time, float lat, float lon) {
		//TODO
		// get correct time value
		// connect to url
		// download data
		// put data in file
		String filename = Integer.toString(time/1000)+"_"+Integer.toString((int)(lat*100))+"_"+Integer.toString((int)(lon*100))+".dat";
		
		return filename;
	}
	
	public static AtmosphereState parseGfsProfile(String filename) {
		AtmosphereState atmo = new AtmosphereState();
		
		//TODO
		
		return atmo;
	}

}
