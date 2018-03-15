package com.wse.postgresdb;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.wse.bean.ImageDetailsBean;

public class GetImageDetails {
	static Connection con;
	static CallableStatement cst;
	static PreparedStatement st;
	static ResultSet rs;
	
	public static ArrayList<ImageDetailsBean> getImagesWithSite(String keyword, String site){
		
		int resultsize = 20;
		ImageDetailsBean results = null;
		con = DbConnection.getDBConnection();
		ArrayList<ImageDetailsBean> resultList = new ArrayList<ImageDetailsBean>();
		
		try {
			if(con != null)
			{				
				String functionQuery = "create or replace function Get_word(received_words varchar(100) returns table (term_word varchar(80) ) as"
						+ " $$"
						+ " SELECT term_word FROM regexp_split_to_table(received_words, E'\\s+') term_word "
						+ " $$"
						+ " language 'sql' VOLATILE;";
				
				String selectQuery = "select a.imageurl, a.score,a.ranknumber from "
						+ "(select i.imageid,i.imageurl, sum(f.expscore) score, "
						+ " row_number() over (order by sum(f.expscore) desc) ranknumber "
						+ "	from imagefeatures f inner join image i on f.imageid = i.imageid "
						+ "	inner join Get_word(?) g on g.term_word =f.imageterms "
						+ " where i.url like ?  "
						+ " group by i.imageid,i.imageurl )a"
						+ " limit ?;";
				
				cst = con.prepareCall("{call Get_word(?)}");
				cst.setString(1,keyword);
				
				st= con.prepareStatement(selectQuery);
				st.setString(1,keyword);
				st.setString(2,keyword);
				st.setInt(3,resultsize);
			
				cst.execute();
				rs = st.executeQuery();	//Select query is executed
				
			while (rs.next()) {
				results = new ImageDetailsBean();
				results.setImageUrl(rs.getString("imageurl"));
				resultList.add(results);
				}
			}
			rs.close();
			con.close();
			cst.close();
			st.close();
		} catch (SQLException e) {
			System.out.println("Error in image Query with site operator!!");
			e.printStackTrace();
		} 		
		return resultList;
		
	}
	
	public static ArrayList<ImageDetailsBean> getImagesWithoutSite(String keyword){
		
		int resultsize = 20;
		ImageDetailsBean results = null;
		con = DbConnection.getDBConnection();
		ArrayList<ImageDetailsBean> resultList = new ArrayList<ImageDetailsBean>();
		
		try {
			if(con != null)
			{				
				String functionQuery="create or replace function Get_word(received_words varchar(100) returns table (term_word varchar(80) ) as"
						+ " $$"
						+ " SELECT term_word FROM regexp_split_to_table(received_words, E'\\s+') term_word "
						+ " $$"
						+ " language 'sql' VOLATILE;";
				
				String selectQuery="select a.imageurl, a.score,a.ranknumber from "
						+ "	(select i.imageid,i.imageurl, sum(f.expscore) score, "
						+ " row_number() over (order by sum(f.expscore) desc) ranknumber "
						+ "	from imagefeatures f inner join image i on f.imageid = i.imageid "
						+ "	inner join Get_word(?) g on g.term_word = f.imageterms "
						+ " group by i.imageid,i.imageurl )a"
						+ "	limit ?;";
				
				cst = con.prepareCall("{call Get_word(?)}");
				cst.setString(1,keyword);
				
				st= con.prepareStatement(selectQuery);
				st.setString(1,keyword);
				//st.setString(2,keyword);
				st.setInt(2,resultsize);
			
				cst.execute();
				rs = st.executeQuery();	//Select query is executed
				
			while (rs.next()) {
				results = new ImageDetailsBean();
				results.setImageUrl(rs.getString("imageurl"));
				resultList.add(results);
				}
			}
			rs.close();
			con.close();
			cst.close();
			st.close();
		} catch (SQLException e) {
			System.out.println("Error in image Query without site operator!!");
			e.printStackTrace();
		} 		
		return resultList;
	}
	
}
