package com.wse.postgresdb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.wse.postgresdb.DbConnection;

public class CombinedScore {
		static Connection con;
		static ResultSet rs;
		
	public static void combinedScoreDB(){
			
			Statement stmt = null;
			try
			{
				con = DbConnection.getDBConnection();
				if (con != null)
				{	
					con.setAutoCommit(false);
					stmt = con.createStatement();
					
					String normBM25 = "update features f set normalizebm25 = (f.bm25score-(select min(bm25score) from features)/((select max(bm25score) from features)-(select min(bm25score) from features)))"
							+ " from features fs where fs.docid=f.docid";
					String normPGRank = "update features f set normalizepgrank = (d.pagerank-(select min(pagerank) from documents)/ ((select max(pagerank) from documents)-(select min(pagerank) from documents)))"
							+ " from documents d where d.docid=f.docid";
					String combinedScore = "update features set combined_score = (.3 * normalizepgrank)+(.7 * normalizebm25)";
					
					stmt.executeUpdate(normBM25);
					stmt.executeUpdate(normPGRank);
					stmt.executeUpdate(combinedScore);
					
					con.commit();
				}
			}
		      
		   catch(Exception e){
		      e.printStackTrace();
		      System.out.println("Error in Combined Score calculation!");
		      
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
