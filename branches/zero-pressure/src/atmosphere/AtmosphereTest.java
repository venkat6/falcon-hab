package atmosphere;

public class AtmosphereTest {

	public static void main(String[] args) {
		AtmosphereModel atmo = AtmosphereFactory.getGfsModel(1288624355,41.9,-93.6);
		AtmosphereState y = atmo.getAtAltitude(300, 1288629000);
		System.out.println("P= " + y.pressure);
		System.out.println("T= " + y.temp);
		System.out.println("S= " + y.windSpeed);
		System.out.println("D= " + y.windDir);
	}
	
}
