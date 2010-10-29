package datatypes;

public class Location {
	
	private double lat;
	private double lon;
	
	public Location() {
		lat = 0;
		lon = 0;
	}
	
	public Location(double latitude, double longitude) {
		lat = latitude;
		lon = longitude;
	}
	
	public void setLatitude(double latitude) {
		lat = latitude;
	}
	
	public void setLongitude(double longitude) {
		lon = longitude;
	}
	
	public double getLatitude() {
		return lat;
	}
	
	public double getLongitude() {
		return lon;
	}

}
