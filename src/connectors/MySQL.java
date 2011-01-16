package connectors;

import java.sql.*;
import backend.Settings;

public class MySQL {
	
	private Connection con;
	private String add;
	private int port;
	private String user;
	private String pass;
	private String name;
	
	public MySQL() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("Could not load MySQL driver!");
			if(Settings.DEBUG) e.printStackTrace();
		}
	}
	
	public MySQL(String address, int port, String username, String password, String database) {
		this();
		setParameters(address, port, username, password, database);
	}
	
	public void setParameters(String address, int port, String username, String password, String database) {
		add = address;
		this.port = port;
		user = username;
		pass = password;
		name = database;
	}
	
	public boolean connect() {
		String url = "jdbc:mysql://" + add + ":" + port + "/" + name;
		if(Settings.DEBUG) System.out.println("Database URL: " + url);
		try {
			con = DriverManager.getConnection(url, user, pass);
		} catch (SQLException e) {
			System.err.println("SQL Error on connect!");
			if(Settings.DEBUG) e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean connect(String address, int port, String username, String password, String database) {
		setParameters(address, port, username, password, database);
		return connect();
	}
	
	public void disconnect() {
		try {
			con.close();
		} catch (SQLException e) {
			System.err.println("SQL Error on disconnect!");
			if(Settings.DEBUG) e.printStackTrace();
		}
	}
	
	public boolean isValid() {
		if(con == null) return false;
		boolean retVal;
		try {
			retVal = con.isValid(2);
		} catch (SQLException e) {
			retVal = false;
		}
		return retVal;
	}
	
	public boolean execute(String cmd) {
		if(con == null) return false;
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(cmd);
		} catch (SQLException e) {
			System.err.println("SQL Error in execution of: " + cmd);
			if(Settings.DEBUG) e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public ResultSet query(String cmd) {
		if(con == null) return null;
		ResultSet rs = null;
		try {
			Statement stmt = con.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(cmd);
		} catch (SQLException e) {
			System.err.println("SQL Error in query of: " + cmd);
			if(Settings.DEBUG) e.printStackTrace();
		}
		return rs;
	}

}
