package com.wse.postgresdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SearchEnginesDB {
	static Connection con;
	static PreparedStatement st;
	static ResultSet rs;

	public static ArrayList<String> getActiveSE() {
		ArrayList<String> result = new ArrayList<String>();
		boolean status = true;
		try {
			con = DbConnection.getDBConnection();
			//TODO write the query to get active urls from the db and add it to result list.
			String query = " select searchengineurl from searchengines where status=? ";
			st = con.prepareStatement(query);
			st.setBoolean(1, status);
			rs = st.executeQuery();
			while (rs.next()) {
				result.add(rs.getString("searchengineurl"));
			}
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error in fetching active SE from searchengineurl table!!");
			e.printStackTrace();
		}
		return result;
	}

	public static int isSEAdded(String seURL) {
		int count = 0;
		try {
			con = DbConnection.getDBConnection();
			String query = " select count(*) as count from searchengines where searchengineurl=? ";
			st = con.prepareStatement(query);
			st.setString(1, seURL);
			rs = st.executeQuery();
			while (rs.next()) {
				count = rs.getInt("count");
			}
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error in fetching if SE is added in searchengineurl table or not!!");
			e.printStackTrace();
		}
		return count;
	}

	public static int persistSEurl(String SEurl) {
		boolean status = true;
		int seid = 0;
		try {
			con = DbConnection.getDBConnection();
			con.setAutoCommit(false);
			String insertQquery = "insert into searchengines (searchengineurl, status) values (?,?); ";
			st = con.prepareStatement(insertQquery);
			st.setString(1, SEurl);
			st.setBoolean(2, status);
			st.execute();
			con.commit();

			//fetch the inserted DocId
			seid = getSEID(SEurl);
		} catch (SQLException e) {
			System.out.println("Error in fetching seid of SE inserted into the searchengineurl table!!");
			e.printStackTrace();
		}
		return seid;
	}
	public static int removeSE(String seURL) {
		int count = 0;
		try {
			con = DbConnection.getDBConnection();
			con.setAutoCommit(false);
			String deleteQuery = " delete from searchengines where searchengineurl = ? ";
			st = con.prepareStatement(deleteQuery);
			st.setString(1, seURL);
			st.execute();
			con.commit();
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error in deleting SE from the searchengineurl table!!");
			e.printStackTrace();
		}
		return count;
	}

	public static void disableSE(String seURL) {
		boolean status = false;
		try {
			con = DbConnection.getDBConnection();
			con.setAutoCommit(false);
			String disableQuery = " update searchengines set status = ? where searchengineurl = ? ";
			st = con.prepareStatement(disableQuery);
			st.setBoolean(1, status);
			st.setString(2, seURL);
			st.executeUpdate();
			con.commit();
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error in disabling SE from the searchengineurl table!!");
			e.printStackTrace();
		}
	}

	public static int getSEID(String seIPaddress) {
		int seid = 0;
		try{
			con = DbConnection.getDBConnection();
			String selectQuery = "select seid from searchengines where searchengineurl=?";
			st = con.prepareStatement(selectQuery);
			st.setString(1, seIPaddress);
			st.execute();
			rs = st.getResultSet();
			while (rs.next()) {
				seid = rs.getInt("seid");
			}
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error in getting SEID from the searchengineurl table!!");
			e.printStackTrace();
		}
		return seid;
	}

	public static void updateCW (int seid, double cw) {
		try {
			con = DbConnection.getDBConnection();
			con.setAutoCommit(false);
			String query = "update searchengines set cw = ? where seid = ?";
			st = con.prepareStatement(query);
			st.setDouble(1, cw);
			st.setInt(2, seid);
			st.executeUpdate();
			con.commit();
			st.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error in inserting cw into searchengines table");
		}
	}
}
