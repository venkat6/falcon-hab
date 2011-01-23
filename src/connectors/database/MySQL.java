package connectors.database;
//TODO Refactor to match Database interface

import java.sql.*;			// Sun SQL classes

import backend.Settings;	// Only used for DEBUG flag, can be removed

/**
 * Implements a connection of a MySQL database utilizing the MySQL database connector (JDBC)
 * 
 * @author Ethan Harstad
 *
 */
public class MySQL implements Database {
	
	private Connection con;	// Holds the connection object
	private String add;		// Holds the address of the server
	private int port;		// Holds the port the server listens on
	private String user;	// Holds the username to connect with
	private String pass;	// Holds the password to authenticate with
	private String name;	// Holds the name of the database to operate on
	
	/**
	 * Default constructor, loads the connector driver
	 */
	public MySQL() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("Could not load MySQL driver!");
			if(Settings.DEBUG) e.printStackTrace();
		}
	}
	
	/**
	 * Explicit constructor, specifies all relevant connection information
	 * @param address Address of the MySQL server
	 * @param port Port to connect to the server on
	 * @param username Username to connect with
	 * @param password Password to authenticate with
	 * @param database Name of database to operate on
	 */
	public MySQL(String address, int port, String username, String password, String database) {
		this();
		setParameters(address, port, username, password, database);
	}
	
	/**
	 * Set the connection parameters
	 * @param address Address of the MySQL server
	 * @param port Port to connect to the server on
	 * @param username Username to connect with
	 * @param password Password to authenticate with
	 * @param database Name of database to operate on
	 */
	public void setParameters(String address, int port, String username, String password, String database) {
		add = address;
		this.port = port;
		user = username;
		pass = password;
		name = database;
	}
	
	/**
	 * Establish a connection to the database
	 * @return True if connection was successful
	 */
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
	
	/**
	 * Connect to the specified database
	 * @param address Address of the MySQL server
	 * @param port Port to connect to the server on
	 * @param username Username to connect with
	 * @param password Password to authenticate with
	 * @param database Name of the database to operate on
	 * @return True if connection was successful
	 */
	public boolean connect(String address, int port, String username, String password, String database) {
		setParameters(address, port, username, password, database);
		return connect();
	}
	
	/**
	 * Disconnect from the database
	 */
	public void disconnect() {
		try {
			con.close();
		} catch (SQLException e) {
			System.err.println("SQL Error on disconnect!");
			if(Settings.DEBUG) e.printStackTrace();
		}
	}
	
	/**
	 * Test if the connection is valid
	 * @return
	 */
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
	
	/**
	 * Execute a command that expects no output
	 * @param cmd The command to run
	 * @return True if successful
	 */
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
	
	/**
	 * Execute a query that requires output
	 * @param cmd The query to run
	 * @return The ResultSet returned by the query
	 */
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

	@Override
	public boolean insert() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ResultSet select() {
		// TODO Auto-generated method stub
		return null;
	}

}
