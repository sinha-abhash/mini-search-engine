package com.wse.postgresdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.wse.postgresdb.DbConnection;

public class ScoreCalculation {
static Connection con;
static PreparedStatement st;
static ResultSet rs;

	public static void calculateTFidfBM25(){
		
		Statement stmt=null;
		try
		{	con = DbConnection.getDBConnection();
		
			if (con!=null)
			{
				stmt = con.createStatement();
				con.setAutoCommit(false);
				
				//System.out.println("values to be updated");
				String updateTermdocfreq ="update features as ft "
						+ " set term_document_frequency=(select count(*) from features f where ft.term=f.term)";
				/*String updateTF = "update features as f"
						+ " set tf = (select (1+log(f.term_frequency)) from features fa where fa.docid=f.docid and fa.term=f.term)";
				*/
				String updateTF = " update features as f set tf = 1+log(fa.term_frequency) from features fa "
						+ " where fa.docid=f.docid and fa.term=f.term;";
				String updateIDF="update features f set idf= log(ab.a/gh.term_document_frequency) "
						+ " from features gh, (select count(distinct docid) as a from features) ab "
						+ " where f.docid= gh.docid and f.term=gh.term;";
				String updateTFIDF="update features set tfidf=(term_frequency*idf)"; 
				
				/*String updatenewIDF="update features f set newIDF = (select log(((select count(url) from documents)- term_document_frequency+.5) / (f.term_document_frequency+.5)) as newidf"
						+ " from features ff  where f.term=ff.term and ff.docid=f.docid )";
				*/
				String updatenewIDF = "update features f set newIDF = log(((select count(url) from documents)- ff.term_document_frequency+.5) / (ff.term_document_frequency+.5))"
						+ " from features ff where f.term=ff.term and ff.docid=f.docid";
				String tabcreateTermCount="create table if not exists TermCount as (select count(f.term) counte ,f.docid from features f inner join documents d on f.docid=d.docid"
						+ " group by f.docid order by f.docid )";
				String updateD="update features f set TermsCount = t.counte from TermCount t where t.docid=f.docid";
				String updateAvgdl="Update features set AVGDL= (select avg(counte) from termcount)";
				String updateBm25="with CTE as (select newidf*((term_frequency*2.2)/(term_frequency + 1.2*(1-1.2+1.2*(termscount/avgdl)))) as bm25,f.docid,f.term from features f inner join documents d on f.docid=d.docid)"
						+ " update features f set BM25score= t.bm25 from cte t where t.docid=f.docid and t.term=f.term";
				
				
				String dropTcount="drop table termcount;";
				String truncateStatus="truncate table Status";
				String truncateCrawlerRecovery="truncate table crawler_recovery";
				
					stmt.executeUpdate(updateTermdocfreq);
					stmt.executeUpdate(updateTF);
					stmt.executeUpdate(updateIDF);
					stmt.executeUpdate(updateTFIDF);
					
					stmt.executeUpdate(updatenewIDF);
					stmt.executeUpdate(tabcreateTermCount);
					stmt.executeUpdate(updateD);
					stmt.executeUpdate(updateAvgdl);
					stmt.executeUpdate(updateBm25);
					
					stmt.executeUpdate(dropTcount);
					stmt.executeUpdate(truncateStatus);
					stmt.executeUpdate(truncateCrawlerRecovery);
					
					con.commit();
					System.out.println("TFIDF and BM25 scores updated.");
			}
		}
	      
	   catch(SQLException se){
	      se.printStackTrace();
	      System.out.println("Error in TFIDF or BM25 score Updates in ScoreCalculation class!");
	   }catch(Exception e){
	      e.printStackTrace();
	      System.out.println("Error in TFIDF or BM25 score Updates in ScoreCalculation class!");
	      
	   }finally{
	      try{
	         if(stmt!=null)
	            stmt.close();
	      }catch(SQLException se){
	      }
	      try{
	         if(con!=null)
	            con.close();
	      }catch(SQLException se){
	         se.printStackTrace();
	      }
	   }
	}
}
	