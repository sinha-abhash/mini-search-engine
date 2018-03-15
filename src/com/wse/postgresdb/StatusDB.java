package com.wse.postgresdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class StatusDB {
	static Connection con;
	static PreparedStatement st;
	static ResultSet rs;
	
	public static void statusInsert (int seedDocId, int fromDocId, int toDocId, int depth,
			boolean crawlCompleteStatus) {
		
		try {
			con = DbConnection.getDBConnection();
			con.setAutoCommit(false);
			String query = "insert into status(seedDocid,fromdocid,todocid,depth,complete) values(?,?,?,?,?)";
			st = con.prepareStatement(query);
			st.setInt(1, seedDocId);
			st.setInt(2, fromDocId);
			st.setInt(3, toDocId);
			st.setInt(4, depth);
			st.setBoolean(5, crawlCompleteStatus);
			st.execute();
			con.commit();
			st.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error in inserting into status table");
		}
	}
	
	/**
	 * select the first row which has complete status as false when ordered by depth
	 * @param seedDocId
	 * @return
	 */
	public static HashMap<String,Integer> getStatusTable (int seedDocId) {
		HashMap<String,Integer> result = new HashMap<String,Integer>();
		int toDocId = 0;
		int depth = 0;
		try {
			con = DbConnection.getDBConnection();
			String query = "select toDocId,depth from status where complete=? order by depth desc limit 1";
			st = con.prepareStatement(query);
			st.setBoolean(1, false);
			rs = st.executeQuery();
			while (rs.next()) {
					toDocId = rs.getInt("toDocId"); 
					depth = rs.getInt("depth");
			}
			st.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error in inserting into status table");
		}
		result.put("toDocId", new Integer(toDocId));
		result.put("depth", new Integer(depth));
		return result;
	}
	
	public static void updateStatusTableCompleteColumn (int toDocId) {
		try {
			con = DbConnection.getDBConnection();
			String query = "update status set complete = ? where todocid = ?";
			st = con.prepareStatement(query);
			st.setBoolean(1, true);
			st.setInt(2, toDocId);
			st.executeUpdate();
			st.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error in inserting into status table");
		}
	}
}
