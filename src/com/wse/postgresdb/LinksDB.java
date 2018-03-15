package com.wse.postgresdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.wse.bean.OutdegreeBean;

public class LinksDB {
	static Connection con;
	static PreparedStatement st;
	static ResultSet rs;
	
	public static boolean isFromAndToLinkPresent (int fromDocId, int toDocId) {
		boolean isPresent = false;
		try {
			con = DbConnection.getDBConnection();
			String query = "select * from links where from_docid=? and to_docid=?";
			st = con.prepareStatement(query);
			st.setInt(1, fromDocId);
			st.setInt(2, toDocId);
			st.execute();
			rs = st.getResultSet();
			isPresent = rs.next();
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error while fetching links");
			e.printStackTrace();
		}
		return isPresent;
	}
	
	public static boolean linksInsert (int fromDocId, int toDocId) {
		boolean result = false;
		//if (!isFromAndToLinkPresent(fromDocId, toDocId)) {
			try {
				con = DbConnection.getDBConnection();
				con.setAutoCommit(false);
				String query = "insert into links(from_docid,to_docid) values(?,?)";
				st = con.prepareStatement(query);
				st.setInt(1, fromDocId);
				st.setInt(2, toDocId);
				st.execute();
				con.commit();
				st.close();
				con.close();
				result = true;
			} catch (SQLException e) {
				System.out.println("Error while inserting into links");
				e.printStackTrace();
			}
		//}
		return result;
	}
	
	public static int fetchDistinctFromIdCount() {
		int count = 0;
		try {
			con = DbConnection.getDBConnection();
			String query = "select max(from_docId) as size from links";
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			while (rs.next()) {
				count = rs.getInt("size");
			}
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error in getting counts of distinct fromId");
			e.printStackTrace();
		}
		return count;
	}
	
	public static ArrayList<OutdegreeBean> getLinks() {
		ArrayList<OutdegreeBean> outdegreeBeans =  new ArrayList<OutdegreeBean>();
		OutdegreeBean outdegreeBean = null;
		try {
			con = DbConnection.getDBConnection();
			String query = "select from_docid ,to_docid from links";
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			while (rs.next()) {
				outdegreeBean = new OutdegreeBean();
				outdegreeBean.setFrom_docId(rs.getInt("from_docid"));
				outdegreeBean.setTo_docId(rs.getInt("to_docid"));
				outdegreeBeans.add(outdegreeBean);
			}
		} catch (SQLException e) {
			System.out.println("Error in getting Links table");
			e.printStackTrace();
		}
		return outdegreeBeans;
	}
	

}
