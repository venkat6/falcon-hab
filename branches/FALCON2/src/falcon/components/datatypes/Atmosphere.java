package falcon.components.datatypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * A class to compute atmospheric conditions based on NWS-ARL sounding data
 * or optionally on standard atmosphere data.
 * @author Ethan Harstad
 *
 */
public class Atmosphere {
	
	private boolean ok = false;
	
	/**
	 * Construct an atmosphere object from the standard atmosphere.  This is much
	 * less accurate and lacks wind data.  Should only be used for burst prediction.
	 */
	public Atmosphere() {
		//TODO Standard Atmosphere
	}
	
	/**
	 * Construct an atmosphere object from the given sounding file.  This is the
	 * preferred method of instantiation.
	 * @param filename
	 */
	public Atmosphere(String filename) {
		Scanner file;
		try {
			file = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			System.err.println("Winds file '" + filename + "' could not be loaded!");
			return;
		}
		
		//TODO Wind file parsing
	}

}
