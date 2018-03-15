package com.wse.postgresdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.wse.postgresdb.DbConnection;

public class FetchUrlDB {
	static Connection con;
	static PreparedStatement st;
	static ResultSet rs;
	
	public static int getDocid(String keyword) {
		int url = 0;
		try {
			con = DbConnection.getDBConnection();
			if(con != null){
			String query = "select docid from features where term=?";	
			st = con.prepareStatement(query);
			st.setString(1, keyword);
			st.execute();
			rs = st.getResultSet();
			}
			while (rs.next()) {
				url = rs.getInt("docid");
				break;
			}
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error in selecting URL!!");
			e.printStackTrace();
		}
		return url;
	}
}
