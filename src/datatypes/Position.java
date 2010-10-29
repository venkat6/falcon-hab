package datatypes;

public class Position extends Location {
	
	private Location location;
	private double alt;
	
	public Position() {
		location = new Location();
		alt = 0;
	}
	
	public Position(double latitude, double longitude, double altitude) {
		location = new Location(latitude, longitude);
		alt = altitude;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setAltitude(double altitude) {
		alt = altitude;
	}
	
	public double getAltitude() {
		return alt;
	}
	
	public void setLatitude(double latitude) {
		location.setLatitude(latitude);
	}
	
	public void setLongitude(double longitude) {
		location.setLongitude(longitude);
	}
	
	public double getLatitude() {
		return location.getLatitude();
	}
	
	public double getLongitude() {
		return location.getLongitude();
	}

}
