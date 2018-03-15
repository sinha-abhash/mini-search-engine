package com.wse.postgresdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FeaturesDB {
	static Connection con;
	static PreparedStatement st;
	static ResultSet rs;
	
	public static boolean isTermAndDocIdPresent (int docId, String term) {
		boolean isPresent = false;
		try {
			con = DbConnection.getDBConnection();
			String query = "select * from features where docId=? and term=?";
			st = con.prepareStatement(query);
			st.setInt(1, docId);
			st.setString(2, term);
			st.execute();
			rs = st.getResultSet();
			isPresent = rs.next();
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error in selecting URL!!");
			e.printStackTrace();
		}
		return isPresent;
	}
	
	public static boolean featuresInsert (int docId, String term, int termFrequency) {
		boolean result = false;
		
		if (!FeaturesDB.isTermAndDocIdPresent(docId, term)) {
			try {
				con = DbConnection.getDBConnection();
				con.setAutoCommit(false);
				String insertSQL = "insert into features(docId,term,term_frequency)"
						+ "values(?,?,?);";
				st = con.prepareStatement(insertSQL);
				st.setInt(1, docId);
				st.setString(2, term);
				st.setInt(3, termFrequency);
				st.execute();
				st.close();
				con.commit();
				con.close();
				result = true;
			} catch (SQLException e) {
				System.out.println("Error inserting the feature");
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public static int getTermsCount (int docId) {
		int count = 0;
		try {
			con = DbConnection.getDBConnection();
			//String query = "select term from features where docid = ?";
			String query = "select count(*) as count from features where docid = ?";
			st = con.prepareStatement(query);
			st.setInt(1, docId);
			st.execute();
			rs = st.getResultSet();
			while (rs.next()){
				count = rs.getInt("count");
			}
			/*while (rs.next()) {
				if (rs.last()) {
					count = rs.getRow();
				}
			}*/
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error getting terms count");
			e.printStackTrace();
		} 
		return count;
	}
	
	public static void deleteFeatures (int docId) {
		try {
			con = DbConnection.getDBConnection();
			String query = "delete from features where docid = ?";
			st = con.prepareStatement(query);
			st.setInt(1, docId);
			st.execute();
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error getting terms count");
			e.printStackTrace();
		}
	}
	
	public static int getTermFrequency (int docId, String term){
		int termFrequency = 0;
		try{
			con = DbConnection.getDBConnection();
			if (con != null){
				String selectQuery = "select term_frequency from features where docid = ? and term = ? ";
				st = con.prepareStatement(selectQuery);
				st.setInt(0, docId);
				st.setString(1, term );
				st.execute();
				rs = st.getResultSet();
				while (rs.next()){
					termFrequency = rs.getInt("term_frequency");
				}
				rs.close();
				st.close();
				con.close();
			}
		} catch (SQLException e) {
			System.out.println("Error getting terms freuqency from features table!");
			e.printStackTrace();
		}
		return termFrequency;
	}
}
