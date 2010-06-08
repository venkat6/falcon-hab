package falcon.components.datatypes;

/**
 * A class to hold a latitude/longitude/altitude triplet that makes up a position.
 * Locations are specified as decimal degrees with negative being South and West.
 * Altitude is a unit-less decimal and is up to the user to keep units constant.
 * Extends the Location class so that a 2D location object may be extracted from the
 * 3D Position object for tasks such as mapping.
 * 
 * @author Ethan Harstad
 */
public class Position extends Location {
	
	private Location loc;
	private double alt;
	
	/**
	 * Default constructor, all fields are zero.
	 */
	public Position() {
		loc = new Location();
		alt = 0.0;
	}
	
	/**
	 * Creates a position with the given lat/lon and an altitude of zero.
	 * @param latitude
	 * @param longitude
	 */
	public Position(double latitude, double longitude) {
		loc = new Location(latitude, longitude);
		alt = 0.0;
	}
	
	/**
	 * Creates a position with the given location and an altitude of zero.
	 * @param location
	 */
	public Position(Location location) {
		loc = location;
		alt = 0.0;
	}
	
	/**
	 * Creates a position with the given values.
	 * @param latitude
	 * @param longitude
	 * @param altitude
	 */
	public Position(double latitude, double longitude, double altitude) {
		loc = new Location(latitude, longitude);
		alt = altitude;
	}
	
	/**
	 * Creates a position with the given location and altitude.
	 * @param location
	 * @param altitude
	 */
	public Position(Location location, double altitude) {
		loc = location;
		alt = altitude;
	}
	
	/**
	 * Returns a location representing the position at ground level.
	 * @return
	 */
	public Location getLocation() {
		return loc;
	}
	
	/**
	 * Returns the altitude of the position.
	 * @return
	 */
	public double getAltitude() {
		return alt;
	}
	
	/**
	 * Returns the latitude of the position.
	 * @return
	 */
	@Override
	public double getLatitude() {
		return loc.getLatitude();
	}
	
	/**
	 * Returns the longitude of the position.
	 * @return
	 */
	@Override
	public double getLongitude() {
		return loc.getLongitude();
	}
	
	/**
	 * Returns a simple comma separated string of the position.
	 * Suitable for basic output only.
	 */
	@Override
	public String toString() {
		return loc.toString() + ", " + alt;
	}
	
	/**
	 * Returns a human readable string of the position.
	 * @return
	 */
	@Override
	public String toPrettyString() {
		return loc.toPrettyString() + " at " + alt;
	}

}
