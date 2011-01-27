package backend;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import connectors.database.MySQL;

/**
 * Manages global settings of FALCON Suite
 * 
 * @author Ethan Harstad
 */
public class Settings {
	
	public static boolean DEBUG = false;	// Debug state flag, controls special output and functions
	public static boolean LOCAL = false;	// Database state flag
	
	public static boolean MySQL = false;
	public static boolean AGWPE = false;
	public static boolean APRSIS = false;
	
	private static Properties settings = new Properties(); // Holds all the settings of the program
	
	public static MySQL db; // Global MySQL database connection
	
	/**
	 * Load settings from the state file
	 */
	public static void loadSettings() {
		try {
			FileInputStream file = new FileInputStream("state.ini");
			settings.loadFromXML(file);
		} catch (FileNotFoundException e) {
			System.err.println("Problem opening state file for read!");
			e.printStackTrace();
			return;
		} catch (InvalidPropertiesFormatException e) {
			System.err.println("Invalid state file!");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			System.err.println("Problem reading from state file!");
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Write all settings to the state file
	 */
	public static void saveSettings() {
		try {
			FileOutputStream file = new FileOutputStream("state.ini");
			settings.storeToXML(file, null);
		} catch (FileNotFoundException e) {
			System.err.println("Problem opening state file for write!");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			System.err.println("Problem writing to state file!");
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Store a new setting
	 * @param key Name of the setting
	 * @param value Value of the setting
	 */
	public static void setProperty(String key, String value) {
		settings.setProperty(key, value);
	}
	
	/**
	 * Get a setting value
	 * @param key The name of the setting
	 * @return Value of the setting
	 */
	public static String getProperty(String key) {
		return settings.getProperty(key);
	}

}
