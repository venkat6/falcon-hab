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
	private double mPayloadMass;	// mass of the payload excluding ballast
	private double mBallastMass;	// initial mass of the ballast
	private double mStartingLift;	// initial net lift of the system
	private Position mStartingPosition;
	
	// State variables
	private double currentTime;			// Current time of the simulation (unix time, seconds)
	private double currentMass;			// Current mass of the system (kg)
	private double ballastRemaining;	// Current mass of the remaining ballast (kg)
	private double gasVolume;			// Volume of the gas bubble (m^3)
	private double gasMass;				// Mass of the lifting gas (kg)
	private double netLift;				// Net lift of the system (N)
	private double vZ;					// Vertical velocity of the system (m/s)
	private Position position;			// Current position of the payload
	private AtmosphereModel atmo;		// Atmospheric model for the simulation
	
	/**
	 * Construct a new Spacecraft object
	 * @param gas Molecular density of the lifting gas (kg/mol) 
	 * @param volume Volume of balloon (m^3)
	 * @param balloonMass Mass of the balloon (kg)
	 * @param payloadMass Total mass of the payload excluding ballast (kg)
	 * @param ballastMass Initial mass of the expendable ballast (kg)
	 * @param lift Initial net lift of the system (N)
	 */
	public Spacecraft(double gas, double volume, double balloonMass, double payloadMass, double ballastMass, double lift) {
		// Set Initial Values
		mLiftingGas = gas;
		mBalloonVolume = volume;
		mBalloonMass = balloonMass;
		mPayloadMass = payloadMass;
		mBallastMass = ballastMass;
		mStartingLift = lift;
	}
	
	/**
	 * Setup the initial conditions for the simulation
	 * @param time Starting time
	 * @param lat Starting latitude
	 * @param lon Starting longitude
	 * @param alt Starting altitude
	 */
	public void launch(int time, double lat, double lon, double alt) {
		// Set initial conditions
		mStartingPosition = new Position(lat, lon, alt);
		position = mStartingPosition;
		netLift = mStartingLift;
		ballastRemaining = mBallastMass;
		currentMass = mPayloadMass + mBalloonMass + ballastRemaining;
		vZ = 0;
		currentTime = time;
		// Compute gas properties
		atmo = AtmosphereFactory.getGfsModel(time, lat, lon);
		AtmosphereState ambient = atmo.getAtAltitude(alt, time);
		double rho_gas = (mLiftingGas * ambient.pressure) / (R * ambient.temp);
		double rho = (AIR * ambient.pressure) / (R * ambient.temp);
		double totalLift = netLift + (currentMass * g);
		gasVolume = totalLift / ((rho_gas - rho) * g);
		gasMass = gasVolume * rho_gas;
		currentMass = mPayloadMass + mBalloonMass + ballastRemaining + gasMass;
	}
	
	public void simulate(double timeStep) {
		// Get new ambient conditions
		double altitude = position.getAltitude();
		AtmosphereState ambient = atmo.getAtAltitude(altitude, (int)currentTime);
		currentTime += timeStep;									// Update time
		// Z axis
		// Calculate change in velocity based on net lift of the balloon
		double aX = netLift / currentMass;							// Vertical acceleration
		double dh = (0.5*aX*timeStep*timeStep) + (vZ * timeStep);	// Vertical displacement
		altitude += dh;
		position.setAltitude(altitude);								// Update altitude
		vZ = aX * timeStep;											// Update velocity
		// Horizontal Plane
		// Horizontal velocities are assumed to be equal to velocity of the wind
		double angle = Math.toRadians(ambient.windDir + 180);		// Trig functions expect radians
		double vX = ambient.windSpeed * Math.sin(angle);			// Calculate East/West velocity
		double vY = ambient.windSpeed * Math.cos(angle);			// Calculate North/South velocity
		double dX = vX * timeStep;									// Calculate East/West displacement
		double dY = vY * timeStep;									// Calculate North/South displacement
		//TODO calculate lat/lon change
		// Update the gas properties
		ambient = atmo.getAtAltitude(altitude, (int)currentTime);	// Update ambient conditions
		gasVolume = ((gasMass / mLiftingGas) * R * ambient.temp) / ambient.pressure; // Determine gas volume
		gasVolume = Math.min(gasVolume, mBalloonVolume);			// Vent excess gas volume
		gasMass = (ambient.pressure * gasVolume * mLiftingGas) / (R * ambient.temp); // Determine gas mass
		double rho = (AIR * ambient.pressure) / (R * ambient.temp);	// Determine density of ambient air
		double B = rho * gasVolume * g;								// Determine buoyant force
		currentMass = mPayloadMass + mBalloonMass + ballastRemaining + gasMass; // Update mass of system
		netLift = B - currentMass;									// Update net lift
	}
	
	public Position getPosition() {
		return position;
	}
	
	public double getTime() {
		return currentTime;
	}
	
}
