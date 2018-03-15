package com.wse.postgresdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.wse.bean.NgramsBean;

public class AddFeaturesDB {
	static Connection con;
	static PreparedStatement st;
	static ResultSet rs;
	
public static ArrayList<NgramsBean> getNgrams() {
		
	ArrayList<NgramsBean> nGramsList = new ArrayList<>();
		try {
			con = DbConnection.getDBConnection();
			//String getNgrams = "select custid, ngrams from addsfeatures;";
			String getNgrams = "select af.custid, af.ngrams from addsfeatures af inner join customer c on af.custid = c.custid where c.clicksleft > 0";
			st = con.prepareStatement(getNgrams);
			st.execute();
			rs = st.getResultSet();
			while (rs.next()) {
				NgramsBean ngramObj = new NgramsBean();
				ngramObj.setCustid(rs.getInt("custid"));
				ngramObj.setNgrams(rs.getString("ngrams"));
				nGramsList.add(ngramObj);
			}
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error in getting the ngrams from AddFeatures table!!");
			e.printStackTrace();
		}
		return nGramsList;
	}

	public static void ngramsInsert(int custid, String ngram) {

		try {
			con = DbConnection.getDBConnection();
			con.setAutoCommit(false);
			String insertSQL = " insert into addsfeatures(custid, ngrams) "
					+ " values(?,?); ";
			st = con.prepareStatement(insertSQL);
			st.setInt(1, custid);
			st.setString(2, ngram);

			st.execute();
			st.close();
			con.commit();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error inserting the Addfeature table!");
			e.printStackTrace();
		}
	}


}
