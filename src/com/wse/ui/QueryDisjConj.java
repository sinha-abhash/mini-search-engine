package com.wse.ui;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

import com.wse.bean.ConjDisBean;
import com.wse.bean.CrawlerRecoveryBean;
import com.wse.postgresdb.DbConnection;

public class QueryDisjConj {
	static Connection con;
	static CallableStatement cst;
	static PreparedStatement st;
	static ResultSet rs;
	
	public static ArrayList<ConjDisBean> isConjunctive(String keyword,int resultSize) throws SQLException {
		String url = null;
		ConjDisBean results = null;
		con = DbConnection.getDBConnection();
		ArrayList<ConjDisBean> resultList = new ArrayList<ConjDisBean>();
		int resultsize = 20;
		try {
			if(con != null)
			{				
				String functionQuery="create or replace function Get_word(received_words varchar(100) returns table (term_word varchar(80) ) as"
						+ " $$"
						+ " SELECT term_word FROM regexp_split_to_table(received_words, E'\\s+') term_word "
						+ " $$"
						+ " language 'sql' VOLATILE;";
				
				String selectQuery="select a.url, a.score,a.ranknumber from ("
						+ " select d.docid,d.url, sum(f.tfidf) score, row_number() over (order by sum(f.tfidf) desc) ranknumber"
						+ " from features f inner join documents d on f.docid=d.docid "
						+ " inner join Get_word(?) g on g.term_word =f.term group by d.docid,d.url "
						+ " having count(*)=(select count(*) from Get_word(?)))a"
						+ " limit ?; ";
				
				cst = con.prepareCall("{call Get_word(?)}");
				cst.setString(1,keyword);
				
				st= con.prepareStatement(selectQuery);
				st.setString(1,keyword);
				st.setString(2,keyword);
				st.setInt(3,resultsize);
			
				cst.execute();
				rs = st.executeQuery();	//Select query is executed
				
			while (rs.next()) {
				results = new ConjDisBean();
				//System.out.print("adding result to arraylist");
				results.setUrl(rs.getString("url"));
				results.setscore(rs.getFloat("score"));
				results.setRank(rs.getInt("ranknumber"));
				resultList.add(results);
				}
			}
		} catch (SQLException e) {
			System.out.println("Error in Conjunctive Query!!");
			e.printStackTrace();
		} finally{
			if(cst != null){
				cst.close();}
			if(rs != null) {
				rs.close(); }
			if(st != null) {
				st.close(); }
			if(con != null) {
			con.close(); }
		}
		
		return resultList;
	}
	
	//For UiCLI class call
	public static ArrayList<ConjDisBean> isDisjunctive (String keyword, int resultSize, String language) throws SQLException {
		ArrayList<ConjDisBean> resultList = new ArrayList<ConjDisBean>();
		ConjDisBean results = null;
		con = DbConnection.getDBConnection();
		try {
			if(con != null)
			{
				String functionQuery = "create or replace function Get_word(received_words varchar(100))"
						+ " returns table (term_word varchar(80) ) as "
						+ " $$"
						+ " SELECT term_word FROM regexp_split_to_table(received_words, E'\\s+') term_word "
						+ " $$"
						+ " language 'sql' VOLATILE;";
					
				String selectQuery="select a.url,a.score,a.ranknumber from "
						+ " (select f.term,d.docid,d.url, sum(f.tfidf) score, row_number() over (order by sum(f.tfidf) desc) ranknumber "
						+ " from features f inner join documents d on f.docid=d.docid "
						+ " inner join Get_word(?) g on g.term_word =f.term "
						+ " where d.language=? "
						+ " group by f.term,d.docid,d.url) a "
						+ " limit ?; ";

				cst = con.prepareCall("{call Get_word(?)}");
				cst.setString(1,keyword);
				
				st= con.prepareStatement(selectQuery);
				st.setString(1,keyword);
				st.setString(2,language);
				st.setInt(3,resultSize);
								
				cst.execute();  //Function is executed
				rs = st.executeQuery();	//Select query is executed

				while (rs.next()) {
					results = new ConjDisBean();
					results.setUrl(rs.getString("url"));
					results.setscore(rs.getFloat("score"));
					results.setRank(rs.getInt("ranknumber"));
					resultList.add(results);
				}
			}
			}catch (SQLException e) {
				System.out.println("Error getting urls, rank and TFIDF via Disjunctive query mode");
				e.printStackTrace();
			}finally{
				if(cst != null){
					cst.close();}
				if(rs != null) {
					rs.close(); }
				if(st != null) {
					st.close(); }
				if(con != null) {
				con.close(); }
			}
		return resultList;
	}
	
	public static ArrayList<ConjDisBean> isDisjunctiveWithSite (String keyword, int resultSize, String site, String language, String scoreType) throws SQLException {
		ArrayList<ConjDisBean> resultList = new ArrayList<ConjDisBean>();
		ConjDisBean results = null;
		String tfidf = "1";
		String bm25 = "2";
		String combined = "3";
		
		if ( scoreType.equalsIgnoreCase(tfidf) ){
		con = DbConnection.getDBConnection();
		try {
			if(con != null)
			{
				String functionQuery = "create or replace function Get_word(received_words varchar(100))"
						+ " returns table (term_word varchar(80) ) as "
						+ " $$"
						+ " SELECT term_word FROM regexp_split_to_table(received_words, E'\\s+') term_word "
						+ " $$"
						+ " language 'sql' VOLATILE;";
					
				String selectQuery="select a.url,a.score,a.ranknumber from "
						+ " (select f.term,d.docid,d.url, sum(f.tfidf) score, row_number() over (order by sum(f.tfidf) desc) ranknumber "
						+ " from features f inner join documents d on f.docid=d.docid "
						+ " inner join Get_word(?) g on g.term_word =f.term "
						+ " where d.language=? and d.url like ? "
						+ " group by f.term,d.docid,d.url) a "
						+ " limit ?; ";

				cst = con.prepareCall("{call Get_word(?)}");
				cst.setString(1,keyword);
				
				st= con.prepareStatement(selectQuery);
				st.setString(1,keyword);
				st.setString(2,language);
				st.setString(3,"%" +site+ "%");
				st.setInt(4,resultSize);
				
				cst.execute();  //Function is executed
				rs = st.executeQuery();	//Select query is executed

				while (rs.next()) {
					results = new ConjDisBean();
					results.setUrl(rs.getString("url"));
					results.setscore(rs.getFloat("score"));
					results.setRank(rs.getInt("ranknumber"));
					resultList.add(results);
				}
			}
			}catch (SQLException e) {
				System.out.println("Error getting urls, rank and BM25 via Disjunctive query mode");
				e.printStackTrace();
			}finally{
				if(cst != null){
					cst.close();}
				if(rs != null) {
					rs.close(); }
				if(st != null) {
					st.close(); }
				if(con != null) {
				con.close(); }
			}
		return resultList;
	}
		else if (scoreType.equalsIgnoreCase(bm25)){

			con = DbConnection.getDBConnection();
			try {
				if(con != null)
				{
					String functionQuery = "create or replace function Get_word(received_words varchar(100))"
							+ " returns table (term_word varchar(80) ) as "
							+ " $$"
							+ " SELECT term_word FROM regexp_split_to_table(received_words, E'\\s+') term_word "
							+ " $$"
							+ " language 'sql' VOLATILE;";
						
					String selectQuery="select a.url,a.score,a.ranknumber from "
							+ " (select f.term,d.docid,d.url, sum(f.bm25score) score, row_number() over (order by sum(f.bm25score) desc) ranknumber "
							+ " from features f inner join documents d on f.docid=d.docid "
							+ " inner join Get_word(?) g on g.term_word =f.term "
							+ " where d.language=? and d.url like ?"
							+ " group by f.term,d.docid,d.url) a "
							+ " limit ?; ";

					cst = con.prepareCall("{call Get_word(?)}");
					cst.setString(1,keyword);
					
					st= con.prepareStatement(selectQuery);
					st.setString(1,keyword);
					st.setString(2,language);
					st.setString(3,"%" +site+ "%");
					st.setInt(4,resultSize);
					
					cst.execute();  //Function is executed
					rs = st.executeQuery();	//Select query is executed

					while (rs.next()) {
						results = new ConjDisBean();
						results.setUrl(rs.getString("url"));
						results.setscore(rs.getFloat("score"));
						results.setRank(rs.getInt("ranknumber"));
						resultList.add(results);
					}
				}
				}catch (SQLException e) {
					System.out.println("Error getting BM25 via Disjunctive query mode");
					e.printStackTrace();
				}finally{
					if(cst != null){
						cst.close();}
					if(rs != null) {
						rs.close(); }
					if(st != null) {
						st.close(); }
					if(con != null) {
					con.close(); }
				}
			return resultList;
		}
		else {
			con = DbConnection.getDBConnection();
			try {
				if(con != null)
				{
					String functionQuery = "create or replace function Get_word(received_words varchar(100))"
							+ " returns table (term_word varchar(80) ) as "
							+ " $$"
							+ " SELECT term_word FROM regexp_split_to_table(received_words, E'\\s+') term_word "
							+ " $$"
							+ " language 'sql' VOLATILE;";
						
					String selectQuery="select a.url,a.score,a.ranknumber from "
							+ " (select f.term,d.docid,d.url, sum(f.combined_score) score, row_number() over (order by sum(f.combined_score) desc) ranknumber "
							+ " from features f inner join documents d on f.docid=d.docid "
							+ " inner join Get_word(?) g on g.term_word =f.term "
							+ " where d.language=? and d.url like ? "
							+ " group by f.term,d.docid,d.url) a "
							+ " limit ?; ";

					cst = con.prepareCall("{call Get_word(?)}");
					cst.setString(1,keyword);
					
					st= con.prepareStatement(selectQuery);
					st.setString(1,keyword);
					st.setString(2,language);
					st.setString(3,"%" +site+ "%");
					st.setInt(4,resultSize);
					
					cst.execute();  //Function is executed
					rs = st.executeQuery();	//Select query is executed

					while (rs.next()) {
						results = new ConjDisBean();
						results.setUrl(rs.getString("url"));
						results.setscore(rs.getFloat("score"));
						results.setRank(rs.getInt("ranknumber"));
						resultList.add(results);
					}
				}
				}catch (SQLException e) {
					System.out.println("Error getting combined score via Disjunctive query mode");
					e.printStackTrace();
				}finally{
					if(cst != null){
						cst.close();}
					if(rs != null) {
						rs.close(); }
					if(st != null) {
						st.close(); }
					if(con != null) {
					con.close(); }
				}
			return resultList;
		
		}
}
	
	public static ArrayList<ConjDisBean> isDisjunctiveWithoutSite (String keyword, int resultSize, String language, String scoreType) throws SQLException {
		ArrayList<ConjDisBean> resultList = new ArrayList<ConjDisBean>();
		ConjDisBean results = null;
		String tfidf = "1";
		String bm25 = "2";
		String combined = "3";
		
		if ( scoreType.equalsIgnoreCase(tfidf) ){
		
		con = DbConnection.getDBConnection();
		try {
			if(con != null)
			{
				String functionQuery = "create or replace function Get_word(received_words varchar(100))"
						+ " returns table (term_word varchar(80) ) as "
						+ " $$"
						+ " SELECT term_word FROM regexp_split_to_table(received_words, E'\\s+') term_word "
						+ " $$"
						+ " language 'sql' VOLATILE;";
					
				String selectQuery="select a.url,a.score,a.ranknumber from "
						+ " (select f.term,d.docid,d.url, sum(f.tfidf) score, row_number() over (order by sum(f.tfidf) desc) ranknumber "
						+ " from features f inner join documents d on f.docid=d.docid "
						+ " inner join Get_word(?) g on g.term_word =f.term "
						+ " where d.language=? "
						+ " group by f.term,d.docid,d.url) a "
						+ " limit ?; ";

				cst = con.prepareCall("{call Get_word(?)}");
				cst.setString(1,keyword);
				
				st= con.prepareStatement(selectQuery);
				st.setString(1,keyword);
				st.setString(2,language);
				st.setInt(3,resultSize);
				
				cst.execute();  //Function is executed
				rs = st.executeQuery();	//Select query is executed

				while (rs.next()) {
					results = new ConjDisBean();
					//System.out.print("adding result to arraylist");
					results.setUrl(rs.getString("url"));
					results.setscore(rs.getFloat("score"));
					results.setRank(rs.getInt("ranknumber"));
					resultList.add(results);
				}
			}
			}catch (SQLException e) {
				System.out.println("Error getting TFIDF via Disjunctive query mode without site!");
				e.printStackTrace();
			}finally{
				if(cst != null){
					cst.close();}
				if(rs != null) {
					rs.close(); }
				if(st != null) {
					st.close(); }
				if(con != null) {
				con.close(); }
			}
		return resultList;
	}
		else if (scoreType.equalsIgnoreCase(bm25)){
			con = DbConnection.getDBConnection();
			try {
				if(con != null)
				{
					String functionQuery = "create or replace function Get_word(received_words varchar(100))"
							+ " returns table (term_word varchar(80) ) as "
							+ " $$"
							+ " SELECT term_word FROM regexp_split_to_table(received_words, E'\\s+') term_word "
							+ " $$"
							+ " language 'sql' VOLATILE;";
						
					String selectQuery="select a.url,a.score,a.ranknumber from "
							+ " (select f.term,d.docid,d.url, sum(f.bm25score) score, row_number() over (order by sum(f.bm25score) desc) ranknumber "
							+ " from features f inner join documents d on f.docid=d.docid "
							+ " inner join Get_word(?) g on g.term_word =f.term "
							+ " where d.language=? "
							+ " group by f.term,d.docid,d.url) a "
							+ " limit ?; ";

					cst = con.prepareCall("{call Get_word(?)}");
					cst.setString(1,keyword);
					
					st= con.prepareStatement(selectQuery);
					st.setString(1,keyword);
					st.setString(2,language);
					st.setInt(3,resultSize);
					
					cst.execute();  //Function is executed
					rs = st.executeQuery();	//Select query is executed

					while (rs.next()) {
						results = new ConjDisBean();
						//System.out.print("adding result to arraylist");
						results.setUrl(rs.getString("url"));
						results.setscore(rs.getFloat("score"));
						results.setRank(rs.getInt("ranknumber"));
						resultList.add(results);
					}
				}
				}catch (SQLException e) {
					System.out.println("Error getting BM25 via Disjunctive query mode without site!");
					e.printStackTrace();
				}finally{
					if(cst != null){
						cst.close();}
					if(rs != null) {
						rs.close(); }
					if(st != null) {
						st.close(); }
					if(con != null) {
					con.close(); }
				}
			return resultList;
		}
		else {

			con = DbConnection.getDBConnection();
			try {
				if(con != null)
				{
					String functionQuery = "create or replace function Get_word(received_words varchar(100))"
							+ " returns table (term_word varchar(80) ) as "
							+ " $$"
							+ " SELECT term_word FROM regexp_split_to_table(received_words, E'\\s+') term_word "
							+ " $$"
							+ " language 'sql' VOLATILE;";
						
					String selectQuery="select a.url,a.score,a.ranknumber from "
							+ " (select f.term,d.docid,d.url, sum(f.combined_score) score, row_number() over (order by sum(f.combined_score) desc) ranknumber "
							+ " from features f inner join documents d on f.docid=d.docid "
							+ " inner join Get_word(?) g on g.term_word =f.term "
							+ " where d.language=? "
							+ " group by f.term,d.docid,d.url) a "
							+ " limit ?; ";

					cst = con.prepareCall("{call Get_word(?)}");
					cst.setString(1,keyword);
					
					st= con.prepareStatement(selectQuery);
					st.setString(1,keyword);
					st.setString(2,language);
					st.setInt(3,resultSize);
					
					cst.execute();  //Function is executed
					rs = st.executeQuery();	//Select query is executed

					while (rs.next()) {
						results = new ConjDisBean();
						//System.out.print("adding result to arraylist");
						results.setUrl(rs.getString("url"));
						results.setscore(rs.getFloat("score"));
						results.setRank(rs.getInt("ranknumber"));
						resultList.add(results);
					}
				}
				}catch (SQLException e) {
					System.out.println("Error getting combined score via Disjunctive query mode without site!");
					e.printStackTrace();
				}finally{
					if(cst != null){
						cst.close();}
					if(rs != null) {
						rs.close(); }
					if(st != null) {
						st.close(); }
					if(con != null) {
					con.close(); }
				}
			return resultList;
		}
	}
}
	
/*
	public static void main(String[] args) throws SQLException {
		ArrayList<ConjDisBean> topresults;
		int resultSize=10;
		
		String keyword="Zur main navig Zum dieser";
		
		topresults=isDisjunctive(keyword,resultSize);
		for (ConjDisBean cdb : topresults) {
			System.out.println(cdb.getRank());
			System.out.println(cdb.getUrl());
			System.out.println(cdb.getTfidfScore());
			System.out.println("------------");
		}
		System.out.print(topresults);
		
	}
	*/

