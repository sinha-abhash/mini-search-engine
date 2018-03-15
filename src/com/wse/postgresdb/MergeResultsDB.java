package com.wse.postgresdb;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.wse.bean.ConjDisBean;


public class MergeResultsDB {
	static Connection con;
	static ResultSet rs;
	static PreparedStatement st;
	static PreparedStatement st1;

	public static ArrayList<ConjDisBean> insertMergeResults(int seid, String term, String ipaddress,String url, double score, int size ){
		ArrayList<ConjDisBean> resultList = new ArrayList<ConjDisBean>();
		try{	
			con = DbConnection.getDBConnection();
			con.setAutoCommit(false);
			String insertSQL = " insert into mergeresults( seid, term, ipaddress, url, score) values(?,?,?,?,?); ";
			st = con.prepareStatement(insertSQL);
			st.setInt(1, seid);
			st.setString(2, term);
			st.setString(3, ipaddress);
			st.setString(4, url);
			st.setDouble(5, score);
			st.execute();

			con.commit();
			
			updateMergeResultsTable( term,  seid);
			
			resultList = getFinalDisplayList(size);
			
			st.close();
			con.close();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Error in inserting into mergeresults table!");
		}
		return resultList;
	}

	public static void updateMergeResultsTable(String term, int seid){
		try
		{
			con = DbConnection.getDBConnection();
			if (con != null){	
				String collScoreQuery = " update mergeresults set coll_score = ((select sum(score) from statistics where term=? and seid=?) -(.4))/"
						+ " ((0.4+(1-0.4)*1*(select sum(i) from statistics where term=? and seid =? )) - .4)"
						+ " where term = ? and seid = ? ;";

				String normDocScore = "update mergeresults set norm_doc_score=(score+.4*score*coll_score)/1.4 where term=? and seid=? ;";

				st = con.prepareStatement(collScoreQuery);
				st.setString(1, term);
				st.setInt(2, seid);
				st.setString(3, term);
				st.setInt(4, seid);
				st.setString(5, term);
				st.setInt(6, seid);
				st.executeUpdate();

				st1 = con.prepareStatement(normDocScore);
				st1.setString(1, term);
				st1.setInt(2, seid);
				st1.executeUpdate();
				
				con.close();
				st.close();
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Error in updating MergeResults for collScoreQuery and normDocScore calculation!");
		}
	}

	public static ArrayList<ConjDisBean> getFinalDisplayList(int resultsize)
	{
		ConjDisBean results = null;
		ArrayList<ConjDisBean> resultList = new ArrayList<ConjDisBean>();
		try
		{
			con = DbConnection.getDBConnection();
			if (con != null){
				String selectMergeResult = "select ipaddress, row_number() over (order by norm_doc_score desc) rank, url, norm_doc_score   "
						+ " from mergeresults limit ?;";

				st = con.prepareStatement(selectMergeResult);
				st.setInt(1,resultsize);
				st.execute();
				rs = st.executeQuery();	//Select query is executed

				while (rs.next()) {
					results = new ConjDisBean();
					results.setSnippet(rs.getString("ipaddress"));
					results.setRank(rs.getInt("rank"));
					results.setUrl(rs.getString("url"));
					results.setscore(rs.getFloat("norm_doc_score"));
					resultList.add(results);
				}
			}
			rs.close();
			con.close();
			st.close();
		}catch (SQLException e) {
			System.out.println("Error in fetching final results from mergeResults table! ");
			e.printStackTrace();
		}
		return resultList;
	}
	
	public static void truncateMergeResultsTable(){
		try
		{
			con = DbConnection.getDBConnection();
			if (con != null){
				String truncateMergeResult = "truncate table mergeresults ;";

				st = con.prepareStatement(truncateMergeResult);
				st.execute();
				
				con.close();
				st.close();
			}
		}catch (SQLException e) {
			System.out.println("Error in truncating  mergeResults table! ");
			e.printStackTrace();
		}
	}
}
