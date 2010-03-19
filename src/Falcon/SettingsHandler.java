/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Falcon;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author jcoleman
 */
public class SettingsHandler {
    private static SettingsHandler sh_;



	private static String setupFilename_;
	private static SettingsHandler handler_;
	private static Map<String, String> setupMap_;

	/**
	 * Private constructor so that only one copy of the LayoutSaver is ever created.
	 */
	private SettingsHandler(){
		//storage location for all of the window frame information
		setupMap_ = new HashMap<String,String>();
		setupFilename_ = "./falconsetup.txt";
	}

	/**
	 * return instance of MasterSetupDataHandler
	 * @return instance of MasterSetupDataHandler
	 */
	public static SettingsHandler getInstance(){
		if(handler_ == null) handler_ = new SettingsHandler();
		return handler_;
	}

	/**
	 * loads the settings of the GUI
	 *
	 */
	public void loadSettings(){
		//getInstance();	//to make sure that the class has been initialized before loading
		//load the settings into the hashmap
		Properties properties = new Properties();
		try{
			properties.load(new FileInputStream(getFile(setupFilename_)));
			Iterator<Object> it = properties.keySet().iterator();
			while(it.hasNext()){
				String name = (String)it.next();
				String data = (String)properties.get(name);
				setupMap_.put(name,data);
			}
		}catch(IOException e){
			System.out.println(e);
		}
	}
	/**
	 * saves the settings of the GUI
	 *
	 */
	public void saveSettings(){
		//getInstance();
		//save the hashmap to the settings file
		Properties properties = new Properties();
		Iterator<String> it = setupMap_.keySet().iterator();
		while(it.hasNext()){
			String name = it.next();
			String data = setupMap_.get(name);
			properties.setProperty(name,data);
		}
		try{
			properties.store(new FileOutputStream(getFile(setupFilename_)),null);
		}catch(IOException e){
			System.out.println(e);
		}
	}

	/**
	 * gets the file with the specified name
	 * @param source of file
	 * @return the file
	 */
	private File getFile(String s){
		File file = null;
		try{
		file = new File(s);
		//the following line only creates the file if it's not already there
		file.createNewFile();
		}catch(IOException e){
			System.out.println(e);
		}
		return file;
	}

	/**
	 * returns instance of setupmap put
	 * @param groupCode
	 * @param key
	 * @param val
	 * @return
	 */
	public String put(String groupCode, String key, String val){
//		getInstance();
		return setupMap_.put(groupCode + key, val);
	}

	/**
	 *
	 * @param groupCode
	 * @param key
	 * @return
	 */
	public String get(String groupCode, String key){
//		getInstance();
		return setupMap_.get(groupCode + key);
	}

	/**
	 *
	 * @param groupCode
	 * @param key
	 * @return
	 */
	public boolean containsKey(String groupCode, String key){
		return setupMap_.containsKey(groupCode + key);
	}

	/**
	 *
	 * @param groupCode
	 * @param key
	 * @return
	 */
	public String remove(String groupCode, String key){
		return setupMap_.remove(groupCode + key);
	}

	/**
	 *
	 * @param groupCode
	 * @param key
	 * @param delimiter
	 * @param numFields
	 * @return
	 */
	public String[] getData(String groupCode, String key, String delimiter, int numFields){
		loadSettings();
		String temp = get(groupCode, key);
		if(temp == null){
			String[] failRet = new String[numFields];
			for(int i = 0; i < numFields; i++){
				failRet[i] = "";
			}
			return failRet;
		}
		String[] s = temp.split(delimiter, numFields);
		return s;
	}

	/**
	 *
	 * @param groupCode
	 * @param key
	 * @param data
	 * @param delim
	 * @param numFields
	 */
	public void setData(String groupCode, String key, String[] data, String delim, int numFields){
		String s = data[0];
		for(int i = 1; i < numFields; i++){
			s = s + delim + data[i];
		}
		put(groupCode, key, s);
		saveSettings();
	}

	/**
	 *
	 * @param key
	 * @param val
	 * @return
	 */
	public String put(String key, String val){
//		getInstance();
		return setupMap_.put(key, val);
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	public String get(String key){
//		getInstance();
		return setupMap_.get(key);
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	public boolean containsKey(String key){
		return setupMap_.containsKey(key);
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	public String remove(String key){
		return setupMap_.remove(key);
	}

}

