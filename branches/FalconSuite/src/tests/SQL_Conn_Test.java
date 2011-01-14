package tests;

import java.sql.*;

public class SQL_Conn_Test {
	
	public static void main(String[] args) {
		Statement stmt;
		ResultSet rs;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			String url = "jdbc:mysql://129.186.192.118:3306/falcon";
			
			Connection con = DriverManager.getConnection(url, "falcon", "falcon224");
			
			System.out.println("URL: " + url);
			System.out.println("Connection: " + con);
			
			stmt = con.createStatement();
			
			String cmd = "show tables";
			
			rs = stmt.executeQuery(cmd);
			
			System.out.println("Display results:");
			while(rs.next()) {
				String r = rs.getString(1);
				System.out.println(r);
			}
			
			con.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
