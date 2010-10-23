package atmosphere;

public class AtmosphereFactory {
	
	public static AtmosphereModel getGfsModel(int time, double lat, double lon) {
		int startTime = (time/AtmosphereProfile.mTimeStep)*AtmosphereProfile.mTimeStep;
		int endTime = ((time/AtmosphereProfile.mTimeStep)+1)*AtmosphereProfile.mTimeStep;
		AtmosphereProfile startState = parseGfsProfile(getGfsProfile(startTime, lat, lon));
		AtmosphereProfile endState = parseGfsProfile(getGfsProfile(endTime, lat, lon));
		
		AtmosphereModel model = new AtmosphereModel();
		//TODO
		return model;
	}
	
	public static String getGfsProfile(int time, double lat, double lon) {
		//TODO
		time = (time/AtmosphereProfile.mTimeStep)*AtmosphereProfile.mTimeStep;
		// connect to url
		// download data
		// put data in file
		String filename = Integer.toString(time/1000)+"_"+Integer.toString((int)(lat*100))+"_"+Integer.toString((int)(lon*100))+".dat";
		
		return filename;
	}
	
	public static AtmosphereProfile parseGfsProfile(String filename) {
		AtmosphereProfile atmo = new AtmosphereProfile();
		
		//TODO
		
		return atmo;
	}

}
