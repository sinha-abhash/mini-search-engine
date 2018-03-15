package com.wse.postgresdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ImageFeaturesDB {
	static Connection con;
	static PreparedStatement st;
	static ResultSet rs;
	
	public static void persistImageFeature(String word, int imageId, double expScore){
		
		try {
			con = DbConnection.getDBConnection();
			con.setAutoCommit(false);
			String insertSQL = " insert into imagefeatures(imageId,imageterms,expScore)"
					+ " values(?,?,?);";
			st = con.prepareStatement(insertSQL);
			st.setInt(1, imageId);
			st.setString(2,word);
			st.setDouble(3,expScore);
			st.execute();
			
			st.close();
			con.commit();
			con.close();
			
		} catch (SQLException e) {
			System.out.println("Error inserting in ImageFeatures table!");
			e.printStackTrace();
		}
	}
}
