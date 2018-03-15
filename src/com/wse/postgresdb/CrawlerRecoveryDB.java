package com.wse.postgresdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.wse.bean.CrawlerRecoveryBean;

public class CrawlerRecoveryDB {
	static Connection con;
	static PreparedStatement st;
	static ResultSet rs;
	
	public static CrawlerRecoveryBean getRecovery (String urlString) {
		CrawlerRecoveryBean recovery = new CrawlerRecoveryBean();
		try {
			con = DbConnection.getDBConnection();
			String query = "with ctet as (select c.docid,c.seeddocid,c.depth,c.numberofdocs, c.crawlcomplete "
					+ "from crawler_recovery c inner join documents d on d.docid=c.seeddocid and c.docid in "
					+ "(select docid from documents) where c.seeddocid in (select docid from documents where URL=? ) and c.crawlcomplete=false "
					+ "order by depth desc limit 1) "	
					+ "select u.url as crawlingUrl,x.depth as depth,x.numberofdocs as noOfDocs from documents u inner join ctet x on u.docid=x.docid";
			st = con.prepareStatement(query);
			st.setString(1, urlString);
			st.execute();
			rs = st.getResultSet();
			while (rs.next()) {
				recovery.setCrawlingUrl(rs.getString("crawlingUrl"));
				recovery.setDepth(rs.getInt("depth"));
				recovery.setNoOfDocs(rs.getInt("noOfDocs"));
			}
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error in getting the docId");
			e.printStackTrace();
		}
		return recovery;
	}
	
	public static void crawlerRecoveryInsert (int seedDocId, int docId, int depth, int numberOfDocs, 
			boolean crawlComplete) {
		if (!isDocIdPresent(docId)) {
			try {
				con = DbConnection.getDBConnection();
				con.setAutoCommit(false);
				String query = "insert into crawler_recovery(seedDocId, docid,depth,numberOfDocs,crawlComplete) values(?,?,?,?,?)";
				st = con.prepareStatement(query);
				st.setInt(1, seedDocId);
				st.setInt(2, docId);
				st.setInt(3, depth);
				st.setInt(4, numberOfDocs);
				st.setBoolean(5, crawlComplete);
				st.execute();
				con.commit();
				st.close();
				con.close();
			} catch (SQLException e) {
				System.out.println("Error in inserting into crawler_recovery");
				e.printStackTrace();
			}
		}
	}

	public static void crawlerRecoveryUpdate(int docId, boolean crawlComplete) {
		if (isDocIdPresent(docId)) {
			try {
				con = DbConnection.getDBConnection();
				con.setAutoCommit(false);
				String query = "update crawler_recovery set crawlcomplete = ? where docid = ?";
				st = con.prepareStatement(query);
				st.setBoolean(1, crawlComplete);
				st.setInt(2, docId);
				st.executeUpdate();
				con.commit();
				st.close();
				con.close();
			} catch (SQLException e) {
				System.out.println("Error in updating crawler_recovery for docId : " + docId);
				e.printStackTrace();
			}
		}
	}
	
	public static boolean isDocIdPresent (int docId) {
		boolean myResult = false;
		try {
			con = DbConnection.getDBConnection();
			String query = "select docid from crawler_recovery where docid = ?";
			st = con.prepareStatement(query);
			st.setInt(1, docId);
			rs = st.executeQuery();
			while (rs.next()) {
				myResult = true;
				break;
			}
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error in getting the docId : " + docId);
			e.printStackTrace();
		}
		return myResult;
	}
}
