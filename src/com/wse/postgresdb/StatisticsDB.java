package com.wse.postgresdb;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.wse.bean.SEStatBean;

public class StatisticsDB {
	static Connection con;
	static ResultSet rs;
	static PreparedStatement st;
	static PreparedStatement st1;
	static PreparedStatement st2;

	public static String insertStatistics(int seid, SEStatBean statObj){
		
		String term = statObj.getTerm();
		long df = statObj.getDf();
		
		try{	
			con = DbConnection.getDBConnection();
			con.setAutoCommit(false);
			String insertSQL = " WITH upsert AS (UPDATE statistics SET seid=?, term=?, df=? "
					+ " WHERE seid=? AND term=? RETURNING *) INSERT INTO statistics (seid, term,df) SELECT ?, ?,? "
					+ " WHERE NOT EXISTS (SELECT * FROM upsert);";

			st = con.prepareStatement(insertSQL);
			st.setInt(1, seid);
			st.setString(2, term);
			st.setLong(3, df);
			st.setInt(4, seid);
			st.setString(5, term);
			st.setInt(6, seid);
			st.setString(7, term);
			st.setLong(8, df);
			st.execute();
			
			con.commit();
			
			st.close();
			con.close();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Error in inserting into Statistics table!");
		}
		return term;
	}

	public static void updateTIStatistics(String term,int seid){
		try
		{
			con = DbConnection.getDBConnection();
			if (con != null){	
				String tQuery =" update statistics set t=(df/(df+50+150*(select st.cw from searchengines st where st.seid = ?)"
						+ " /(select avg(cw) from searchengines)))	where term = ? and seid = ?  ;";

				String iQuery="update statistics set i = (log(((select count(seid) from searchengines)+0.5)/ "
						+ "(select distinct count(seid) from statistics where term =? ))/log((select count(seid) from searchengines)+1.0))"
						+ " where term = ? and seid = ? ;";

				String scoreQuery=" update statistics set score = 0.4+(1-0.4)*t*i where term = ? and seid = ? ;";
				
				st = con.prepareStatement(tQuery);
				st.setInt(1, seid);
				st.setString(2, term);
				st.setInt(3, seid);
				st.executeUpdate();
				
				st1 = con.prepareStatement(iQuery);
				st1.setString(1, term);
				st1.setString(2, term);
				st1.setInt(3, seid);
				st1.executeUpdate();
				
				st2 = con.prepareStatement(scoreQuery);
				st2.setString(1, term);
				st2.setInt(2, seid);
				st2.executeUpdate();
				
				con.close();
				st.close();
			}
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Error in updating Statistics for T,I and score calculation!");

		}
	}
	
	
	public static ArrayList<String> getSeIPaddress(String term) {
		ArrayList<String> seIPList = new ArrayList<String>();
		try
		{
		con = DbConnection.getDBConnection();
		if (con != null){
		/*String selectQuery = " select sg.searchengineurl as ipaddress from statistics s inner join searchengines sg  on s.seid=sg.seid"
				+ " where s.term=? order by s.score desc;	"; */
			
		String selectQuery = " select distinct sg.searchengineurl as ipaddress,s.score from statistics s inner join searchengines sg  on s.seid=sg.seid"
				+ " where s.term = ? and sg.status is true order by s.score desc ; ";
		st = con.prepareStatement(selectQuery);
		st.setString(1, term);
	//	st.setInt(2, SEpercent);
		st.execute();
		rs = st.getResultSet();
		while (rs.next()) {
			seIPList.add(rs.getString("ipaddress"));
		}
		
		st.close();
		con.close();
		rs.close();
		}
	}catch(Exception e){
		e.printStackTrace();
		System.out.println("Error in fetching SE ipaddress for the term in Statistics!!");
	}
		return seIPList;
	}
}
