package connectors.database;
//TODO Define

import java.sql.ResultSet;

public interface Database {
	
	public boolean connect();
	public boolean insert();
	public ResultSet select();

}
