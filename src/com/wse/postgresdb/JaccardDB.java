package com.wse.postgresdb;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.wse.bean.JaccardBean;

public class JaccardDB {
	static Connection con;
	static CallableStatement cst;
	static PreparedStatement st;
	static PreparedStatement st1;
	static ResultSet rs;

	/*public static void main (String[] args) {
		persistJaccard(758,760);
	} */

	public static boolean isPairPresent (int docid, int withdocid) {
		boolean isPresent = false;
		try {
			con = DbConnection.getDBConnection();
			String query = "select * from jaccard where docid = ? and withdocid = ?";
			st = con.prepareStatement(query);
			st.setInt(1, docid);
			st.setInt(2, withdocid);
			rs = st.executeQuery();
			while (rs.next()) {
				isPresent = true;
			}
			if (!isPresent) {
				st.setInt(1, docid);
				st.setInt(2, withdocid);
				rs = st.executeQuery();
				while (rs.next()) {
					isPresent = true;
				}
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

	/*public static void persistJaccard(int docid, int withdocid) {
		//float jacardcoeffient = 0;
		double jacardcoeffient = 0;
		try {
			con = DbConnection.getDBConnection();

			String unionQuery = "select  cast (( select count(*) from (select  shingle from shingles "
					+ " where docid=? union select  shingle from shingles where docid=?) a) as numeric(8,0)) as unioncnt;";
			
			String intersectQuery = " select  cast (( select count(*) from (select  shingle from shingles "
					+ " where docid=? intersect select  shingle from shingles where docid=?) a) as numeric(8,0)) as intersctcount;";
			
			String calculateJaccardCoefficient =" select cast (cast (( select count(*) from (select  shingle from shingles "
					+ " where docid = ? intersect select  shingle from shingles where docid = ?) intersctcount) as numeric(9,6))"
					+ " / cast ((select count(*) from (select shingle from shingles where docid = ? union select shingle from shingles "
					+ " where docid = ?) b)"
					+ " as numeric(9,6)) as numeric(9,6)) as jaccardcoefficient; ";

			//String insertQuery=" Insert into jaccard(docid,withdocid,jaccardcoefficient) values(?,?,?);";

			st = con.prepareStatement(unionQuery);
			st.setInt(1,docid);
			st.setInt(2,withdocid);
			st.execute();
			rs = st.getResultSet();

			while (rs.next()) {
				int unioncnt = rs.getInt("unioncnt");

				String insertJaccard = " Insert into jaccard(docid,withdocid,jaccardcoefficient) values(?,?,?);";

				if (unioncnt == 0){
					st = con.prepareStatement(insertJaccard);
					st.setInt(1,docid);
					st.setInt(2,withdocid);
					//st.setFloat(3,jacardcoeffient);
					st.setDouble(3, jacardcoeffient);
					st.execute();

				}else {
					st1 = con.prepareStatement(intersectQuery);
					st1.setInt(1, docid);
					st1.setInt(2, withdocid);
					st1.setInt(3, docid);
					st1.setInt(4, withdocid);
					st1.execute();
					rs = st1.getResultSet();
					while(rs.next()){
						int intersectCnt = rs.getInt("intersctcount");
						jacardcoeffient = intersectCnt/unioncnt;
						st = con.prepareStatement(insertJaccard);
						st.setInt(1,docid);
						st.setInt(2,withdocid);
						//st.setFloat(3,jacardcoeffient);
						st.setDouble(3, jacardcoeffient);
						st.execute();
					}

					System.out.println("JaccardCoefficent Calculation done and value inserted for " +docid+" "+withdocid);
				}
			}

			rs.close();
			st.close();
			st1.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error in JaccardCoefficent Calculation or insertion in to DB table!!");
			e.printStackTrace();
		} 
	}
*/	
	
	public static void persistJaccard(int docid, int withdocid) {
		//float jacardcoeffient = 0;
		BigDecimal jacardcoeffient = null;
		try {
			con = DbConnection.getDBConnection();

			String unionQuery = "select  cast (( select count(*) from (select  shingle from shingles "
					+ " where docid=? union select  shingle from shingles where docid=?) a) as numeric(8,0)) as unioncnt";

			String calculateJaccardCoefficient =" select cast (cast (( select count(*) from (select  shingle from shingles "
					+ " where docid = ? intersect select  shingle from shingles where docid = ?) intersctcount) as double precision)"
					+ " / cast ((select count(*) from (select shingle from shingles where docid = ? union select shingle from shingles "
					+ " where docid = ?) b)"
					+ " as double precision) as double precision) as jaccardcoefficient; ";

			//String insertQuery=" Insert into jaccard(docid,withdocid,jaccardcoefficient) values(?,?,?);";

			st = con.prepareStatement(unionQuery);
			st.setInt(1,docid);
			st.setInt(2,withdocid);
			st.execute();
			rs = st.getResultSet();

			while (rs.next()) {
				int unioncnt = rs.getInt("unioncnt");

				String insertJaccard = " Insert into jaccard(docid,withdocid,jaccardcoefficient) values(?,?,?);";

				if (unioncnt == 0){
					st = con.prepareStatement(insertJaccard);
					st.setInt(1,docid);
					st.setInt(2,withdocid);
					//st.setFloat(3,jacardcoeffient);
					st.setBigDecimal(3, jacardcoeffient);
					st.execute();

				}else {
					st1 = con.prepareStatement(calculateJaccardCoefficient);
					st1.setInt(1, docid);
					st1.setInt(2, withdocid);
					st1.setInt(3, docid);
					st1.setInt(4, withdocid);
					st1.execute();
					rs = st1.getResultSet();
					while(rs.next()){
						jacardcoeffient = rs.getBigDecimal("jaccardcoefficient");

						st = con.prepareStatement(insertJaccard);
						st.setInt(1,docid);
						st.setInt(2,withdocid);
						//st.setFloat(3,jacardcoeffient);
						st.setBigDecimal(3, jacardcoeffient);
						st.execute();
					}

				}
			}

			rs.close();
			st.close();
			st1.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error in JaccardCoefficent Calculation or insertion in to DB table!!");
			e.printStackTrace();
		} 
	}

	public static void udfJaccard(int docid, int threshold ){

		ArrayList<JaccardBean> jaccardList = new ArrayList<JaccardBean>();
		try {
			con = DbConnection.getDBConnection();
			String udfQuery ="create or replace function thresholdmatch (docid INT, threshold int) "
					+ " returns table ( withdocid int, jaccardcoefficient float )  as"
					+ " $$ select withdocid, jaccardcoefficient from jaccard where docid=? and jaccardcoefficient >=? $$";

			cst = con.prepareCall("{call thresholdmatch(?,?)}");
			cst.setInt(1,docid);
			cst.setInt(2,threshold);
			st = con.prepareStatement(udfQuery);
			cst.execute();
			st.executeQuery();
			rs = st.getResultSet();
			while (rs.next())
			{
				JaccardBean obj = new JaccardBean();
				obj.setWithDocid(rs.getInt("withdocid"));
				obj.setJaccardCoefficient(rs.getFloat("jaccardcoefficient"));
				jaccardList.add(obj);
			}

			for(int i =0; i< jaccardList.size(); i++ ){
				JaccardBean obj1 = jaccardList.get(i);
				int withDocid = obj1.getWithDocid();
				float JaccardCoefficient = obj1.getJaccardCoefficient();
				System.out.println("withDocID: "+withDocid);
				System.out.println("JaccardCoefficient: "+JaccardCoefficient);
				System.out.println();
			}
			rs.close();
			st.close();
			cst.close();
			con.close();
		}
		catch (SQLException e) {
			System.out.println("Error injaccard threshold udf !!");
			e.printStackTrace();
		} 
	}
}


