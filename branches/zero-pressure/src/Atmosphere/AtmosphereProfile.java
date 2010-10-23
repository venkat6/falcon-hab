package atmosphere;

public class AtmosphereProfile {
	
	protected int mStartTime;
	protected int mEndTime;
	protected static final int mTimeStep = 10800;
	protected double mLat;
	protected double mLon;
	protected double mResolution = 0.5;
	
	//private ArrayList<>
	
	public boolean isValid(int time, double lat, double lon) {
		if((time >= mStartTime) && ((time <= mEndTime) || (mEndTime < 0))) {
			if((Math.abs(lat-mLat) <= mResolution) && (Math.abs(lon-mLon) <= mResolution)) {
				return true;
			}
		}
		return false;
	}

}
