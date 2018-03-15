package com.wse.postgresdb;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShinglesDB {
	static Connection con;
	static PreparedStatement st;
	static CallableStatement cst;
	static ResultSet rs;
	
	public static void shinglesInsert (int docId, String shingle) {
			try {
				con = DbConnection.getDBConnection();
				con.setAutoCommit(false);
				
				//byte hash = calculateMD5(shingle);
				String query = "insert into shingles(docId, shingle) values(?,?)";
				//String query = "insert into shingles(docId, shingle,md5) values(?,?,?)";
				st = con.prepareStatement(query);
				st.setInt(1, docId);
				st.setString(2, shingle);
				//st.setByte(3,hash);
				st.execute();
				con.commit();
				st.close();
				con.close();
				calculateMD5(docId,shingle);
				
			} catch (SQLException e) {
				System.out.println("Error while inserting into Shingles!");
				e.printStackTrace();
			}
	}


	public static void calculateMD5(int docid,String shingle) {
		try {
			con = DbConnection.getDBConnection();
			String updateQuery = "update shingles set md5 = get_byte(decode(md5(?),'hex'),15) where docid= ? and shingle=? ; ";
			st = con.prepareStatement(updateQuery);
			st.setString(1, shingle );
			st.setInt(2, docid);
			st.setString(3, shingle );
			st.executeUpdate();
			st.close();
			con.close();
			
		} catch (SQLException e) {
			System.out.println("Error while calculating and inserting md5 into Shingles!");
			e.printStackTrace();
		}
	} 
	
	
	/*private static byte calculateMD5(String shingle) {
		byte hash = 0;
		try {
			con = DbConnection.getDBConnection();
			//String selectQuery = "md5(?);";
			cst = con.prepareCall("{call md5(?)}");
			cst.setString(1,shingle);
			cst.execute();
			rs = st.executeQuery();
			hash = rs.getByte(0);
			
			cst.close();
			rs.close();
			con.close();
			
		} catch (SQLException e) {
			System.out.println("Error while calculating and inserting md5 into Shingles!");
			e.printStackTrace();
		}
		return hash;
	} */
}
