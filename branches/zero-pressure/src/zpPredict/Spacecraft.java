package zpPredict;

import datatypes.Position;
import atmosphere.*;

public class Spacecraft {
	
	// Constants
	public static final double HELIUM = 0.004002602;	// kg/mol
	public static final double HYDROGEN = 0.00100794;	// kg/mol
	public static final double AIR = 0.02896;			// kg/mol
	public static final double g = 9.807;				// accel due to gravity m/s^2
	public static final double R = 8.314472;			// gas constant J/(K*mol)
	
	// Initial values
	private double mLiftingGas;		// atomic density of lifting gas
	private double mBalloonVolume;	// volume of the balloon
	private double mBalloonMass;	// mass of the balloon
	private double mGrossPayload;	// mass of the payload including ballast
	private double mBallastMass;	// initial mass of the ballast
	private double mStartingLift;	// initial net lift of the system
	private Position mStartingPosition;
	
	// State variables
	private double currentMass;
	private double ballastRemaining;
	private double gasVolume;
	private double gasMass;
	private double netLift;
	private Position position;
	private AtmosphereModel atmo;
	
	/**
	 * Construct a new Spacecraft object
	 * @param gas Molecular density of the lifting gas (g/mol) 
	 * @param volume Volume of balloon (m^3)
	 * @param balloonMass Mass of the balloon (kg)
	 * @param payloadMass Total mass of the payload including ballast (kg)
	 * @param ballastMass Initial mass of the expendable ballast (kg)
	 * @param lift Initial net lift of the system
	 */
	public Spacecraft(double gas, double volume, double balloonMass, double payloadMass, double ballastMass, double lift) {
		// Set Initial Values
		mLiftingGas = gas;
		mBalloonVolume = volume;
		mBalloonMass = balloonMass;
		mGrossPayload = payloadMass;
		mBallastMass = ballastMass;
		mStartingLift = lift;
	}
	
	public void launch(int time, double lat, double lon, double alt) {
		mStartingPosition = new Position(lat, lon, alt); 
		position = mStartingPosition;
		// Compute gas properties
		atmo = AtmosphereFactory.getGfsModel(time, lat, lon);
		AtmosphereState ambient = atmo.getAtAltitude(alt, time);
		double rho_gas = (mLiftingGas * ambient.pressure)/(R*ambient.temp);
		double rho = (AIR * ambient.pressure)/(R*ambient.temp);
	}
	
}
