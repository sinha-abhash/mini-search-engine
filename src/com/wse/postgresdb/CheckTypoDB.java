package com.wse.postgresdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.wse.bean.TermBean;

public class CheckTypoDB {
	static Connection con;
	static PreparedStatement st;
	static ResultSet rs;

	public static TermBean termPresent(String Keyword ){
	
	TermBean queryTerm = new TermBean();
	try {
		con = DbConnection.getDBConnection();
		String query = "select term_document_frequency from features where term=? "
				+ " group by term_document_frequency,term order by term_document_frequency limit 1;";
		
			st = con.prepareStatement(query);
			st.setString(1, Keyword);
			st.execute();
			rs = st.getResultSet();
			while(rs.next()){
				queryTerm.setTerm_doc_frequency(rs.getInt("term_document_frequency"));
			}
			rs.close();
			st.close();
			con.close();
		} 	catch (SQLException e) {
			System.out.println("Error in finding the term in features table!");
			e.printStackTrace();
		}
	return queryTerm;
	}
	
	public static TermBean getSimilarTerm(String Keyword ){
		
	TermBean queryTerm = new TermBean();
	try {
		con = DbConnection.getDBConnection();
		String query = " select term from features where levenshtein(term,?) < 2 "
				+ " group by term_document_frequency,term order by term_document_frequency desc limit 1 ;";
		
			st = con.prepareStatement(query);
			st.setString(1, Keyword);
			st.execute();
			rs = st.getResultSet();
			
			while(rs.next()){
				queryTerm.setTerm(rs.getString("term"));
				//queryTerm.setTerm_doc_frequency(rs.getInt("term_document_frequency"));
			}
			rs.close();
			st.close();
			con.close();
		} 	catch (SQLException e) {
			System.out.println("Error in finding the most similar term for Typo!");
			e.printStackTrace();
		}
		return queryTerm;
	}
	public static TermBean alternateQueryTerm(String Keyword){
		TermBean queryTerm=new TermBean();
		try {
			con = DbConnection.getDBConnection();
			String query="select term_document_frequency, term from features where levenshtein(term,?) < 2 "
					+ "group by term_document_frequency, term order by term_document_frequency desc limit 1;";
			st = con.prepareStatement(query);
			st.setString(1, Keyword);
			st.execute();
			rs = st.getResultSet();
		
		while (rs.next()){
			queryTerm.setTerm(rs.getString("term"));
			queryTerm.setTerm_doc_frequency(rs.getInt("term_document_frequency"));
		}
		rs.close();
		st.close();
		con.close();
		} catch(SQLException e){
			System.out.println("Error in finding alternate spelling");
			e.printStackTrace();
		}
		return queryTerm;
	}
	

}
