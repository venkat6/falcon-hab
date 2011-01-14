package backend;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import database.DatabaseConnection;


public class Settings {
	
	public static boolean DEBUG = true;
	private static Properties settings = new Properties();
	
	public static DatabaseConnection db;
	
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
	
	public static void setProperty(String key, String value) {
		settings.setProperty(key, value);
	}
	
	public static String getProperty(String key) {
		return settings.getProperty(key);
	}

}
