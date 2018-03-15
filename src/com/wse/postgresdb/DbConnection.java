package com.wse.postgresdb;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbConnection {
	static Connection con = null;
	static String connURL = "jdbc:postgresql://localhost:5432/wse";
	static String userName = "postgres";
	//static String password = "postgres";
	static String password = "welcome123";
	
	public static Connection getDBConnection() {
		try {
			Class.forName("org.postgresql.Driver");
			con = DriverManager
		            .getConnection(connURL,userName,password);
		} catch (Exception e) {
			System.out.println("Error in getting connection!!");
			e.printStackTrace();
		}
		return con;
	}
}
