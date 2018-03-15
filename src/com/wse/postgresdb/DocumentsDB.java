package com.wse.postgresdb;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.la4j.Matrix;

public class DocumentsDB {
	static Connection con;
	static PreparedStatement st;
	static ResultSet rs;
	
	public static boolean isURLPresent(String URL) {
		boolean isPresent = false;
		try {
			con = DbConnection.getDBConnection();
			String query = "select url from documents where url=?";
			st = con.prepareStatement(query);
			st.setString(1, URL);
			st.execute();
			rs = st.getResultSet();
			while (rs.next()) {
				isPresent = true;
				break;
			}
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error in selecting URL!!");
			e.printStackTrace();
		}
		return isPresent;
	}
	
	public static int docInsert (URL url, Date crawled_on_date) {
		String urlString = url.toString();
		int docId = 0;
		
		if (!DocumentsDB.isURLPresent(urlString)) {
			try {
				con = DbConnection.getDBConnection();
				con.setAutoCommit(false);
				String insertSQL = "insert into documents(url,crawled_on_date)"
						+ "values(?,?);";
				st = con.prepareStatement(insertSQL);
				st.setString(1, urlString);
				st.setDate(2, crawled_on_date);
				st.execute();
				st.close();
				con.commit();
				
				//fetch the inserted DocId
				String selectQuery = "select docId from documents where url=?";
				st = con.prepareStatement(selectQuery);
				st.setString(1, urlString);
				st.execute();
				rs = st.getResultSet();
				while (rs.next()) {
					docId = rs.getInt("docid");
				}
				con.close();
			} catch (SQLException e) {
				System.out.println("Error inserting the document");
				e.printStackTrace();
			}
		} else if (getCrawledDate(url.toString()) == null) {
			try {
				con = DbConnection.getDBConnection();
				con.setAutoCommit(false);
				String query = "update documents set crawled_on_date = ? where url = ?";
				st = con.prepareStatement(query);
				st.setDate(1, crawled_on_date);
				st.setString(2, urlString);
				st.executeUpdate();
				st.close();
				con.commit();
				docId = getDocId(urlString);
				con.close();
			} catch (SQLException e) {
				System.out.println("Error updating the crawled_on_date in documents");
				e.printStackTrace();
			}
		} else {
				docId = getDocId (urlString);
		}
		
		return docId;
	}
	
	public static int getDocId (String URL) {
		int docId = 0;
		try {
			con = DbConnection.getDBConnection();
			String query = "select docid from documents where url=?";
			st = con.prepareStatement(query);
			st.setString(1, URL);
			st.execute();
			rs = st.getResultSet();
			while (rs.next()) {
			docId = rs.getInt("docid");
			}
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error in getting the docId");
			e.printStackTrace();
		}
		return docId;
	}
	
	public static Date getCrawledDate (String url) {
		Date crawledDate = null; 
		int docId = getDocId (url);
		crawledDate = getCrawledDate(docId);
		return crawledDate;
	}
	
	public static Date getCrawledDate (int docId) {
		Date crawledDate = null;
		try {
			con = DbConnection.getDBConnection();
			String query = "select crawled_on_date from documents where docid = ?";
			st = con.prepareStatement(query);
			st.setInt(1, docId);
			st.execute();
			rs = st.getResultSet();
			while (rs.next()) {
				crawledDate = rs.getDate("crawled_on_date");
			}
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error in getting the docId");
			e.printStackTrace();
		}
		return crawledDate;
	}
	
	public static String getURL (int docId) {
		String url = null;
		try {
			con = DbConnection.getDBConnection();
			String query = "select url from documents where docId = ?";
			st = con.prepareStatement(query);
			st.setInt(1, docId);
			rs = st.executeQuery();
			while (rs.next()) {
				url = rs.getString("url");
			}
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error in getting the docId");
			e.printStackTrace();
		}
		return url;
	}

	public static void updateDocumentsWithPagerank(int matrixSize, Matrix pageRank) {
		try {
			con = DbConnection.getDBConnection();
			con.setAutoCommit(false);
			int i = 0;
			for (i = 0; i < matrixSize; i++){
				int docid = i+1;	//For docid to start from 1.
				String updatequery = "Update documents set pagerank = ? where docid = ?;";
				st = con.prepareStatement(updatequery);
				st.setDouble(1, pageRank.get(0, i));
				st.setInt(2,docid);
				st.executeUpdate();
			}
			con.commit();
			st.close();
			con.close();
			
		} catch (SQLException e){
			System.out.println("Error in updating Pagerank in Documents table!");
			e.printStackTrace();
		}
	}

	public static void docLanguagePersist(int docId, String docLanguage) {
		try {
			con = DbConnection.getDBConnection();
			con.setAutoCommit(false);
			String updateLanguageQuery = "Update documents set language = ? where docid = ?;";
			st = con.prepareStatement(updateLanguageQuery);
			st.setString(1, docLanguage);
			st.setInt(2, docId);
			st.executeUpdate();
			con.commit();
			st.close();
			con.close();
			
		} catch (SQLException e){
			System.out.println("!");
			e.printStackTrace();
		}
	}
	
	public static void docTextInsert (int docId, String docText ) {
		try {
			con = DbConnection.getDBConnection();
			con.setAutoCommit(false);
			
			String updateDocText = " Update documents set doctext = ? where docid = ? ; ";
			st = con.prepareStatement(updateDocText);
			st.setString(1, docText);
			st.setInt(2, docId);
			st.executeUpdate();
			
			st.close();
			con.commit();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error updating docText in Documents table!");
			e.printStackTrace();
		}
	}
	
	public static String getDocText (String url) {
		String docText = null;
		try {
			con = DbConnection.getDBConnection();
			String query = "select docText from documents where url = ?";
			st = con.prepareStatement(query);
			st.setString(1, url);
			rs = st.executeQuery();
			while (rs.next()) {
				docText = rs.getString("docText");
			}
			rs.close();
			st.close();
			con.close();
			
			return docText;
			
		} catch (SQLException e) {
			System.out.println("Error in getting the docText for respective docID from Documents! ");
			e.printStackTrace();
		}
		return docText;
	}
	
	public static ArrayList<Integer> getAllDocId() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		try {
			con = DbConnection.getDBConnection();
			String query = "select docid from documents";
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			while (rs.next()) {
				result.add(rs.getInt("docid"));
			}
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error in getting all docid from the documents table");
			e.printStackTrace();
		}
		return result;
	}
	
}
