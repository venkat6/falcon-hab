package falcon.components.datatypes;

/**
 * A class to hold latitude/longitude pairs that make up a location.
 * Locations are stored as decimal values with west longitude being negative.
 * 
 * @author Ethan Harstad
 */
public class Location {
	
	private double mLat;
	private double mLon;

	/**
	 * Default constructor, all values are set to zero.
	 */
	public Location() {
		mLat = 0;
		mLon = 0;
	}
	
	/**
	 * Create a location object with the given parameters.
	 * @param latitude
	 * @param longitude
	 */
	public Location(double latitude, double longitude) {
		mLat = latitude;
		mLon = longitude;
	}
	
	/**
	 * Get the latitude of the location object.
	 * @return
	 */
	public double getLatitude() {
		return mLat;
	}
	
	/**
	 * Get the longitude of the location object.
	 * @return
	 */
	public double getLongitude() {
		return mLon;
	}
	
	/**
	 * Get a string representation of the location.
	 * Simple string suitable for basic output. "LATITUDE, LONGITUDE"
	 * For a more readable format use toPrettyString.
	 */
	public String toString() {
		return mLat + ", " + mLon;
	}
	
	/**
	 * Returns a human readable string representing the location.
	 * @return
	 */
	public String toPrettyString() {
		String lat = mLat > 0 ? mLat + " N" : mLat + " S";
		String lon = mLon > 0 ? mLon  + " E" : mLon + " W";
		return lat + ", " + lon;
	}
}
