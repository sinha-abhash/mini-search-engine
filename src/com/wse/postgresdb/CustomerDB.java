package com.wse.postgresdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.wse.bean.AdvertisementBean;

public class CustomerDB {

	static Connection con;
	static PreparedStatement st;
	static ResultSet rs;

	public static int customerInsert(String addURL, String addText,
			int budget, int moneyPerClick, int clicksleft) {

		int custId = 0;

		if (!CustomerDB.isCustomerPresent(addURL)) {
			try {
				con = DbConnection.getDBConnection();
				con.setAutoCommit(false);
				String insertSQL = " insert into customer(addURL, addDesc, budget, moneyPerClick, clicksleft) "
						+ " values(?,?,?,?,?);";
				st = con.prepareStatement(insertSQL);
				st.setString(1, addURL);
				st.setString(2, addText);
				st.setInt(3, budget);
				st.setInt(4, moneyPerClick);
				st.setInt(5, clicksleft);

				st.execute();
				st.close();
				con.commit();

				//fetch the inserted custID
				String selectQuery = "select custid from customer where addURL = ? ;";
				st = con.prepareStatement(selectQuery);
				st.setString(1, addURL);
				st.executeQuery();
				rs = st.getResultSet();
				while (rs.next()) {
					custId = rs.getInt("custid");
				}
				con.close();
			} catch (SQLException e) {
				System.out.println("Error inserting the Customer table!");
				e.printStackTrace();
			}
		}
		return custId;
	}
	
	

	public static boolean isCustomerPresent(String addURL) {

		boolean isPresent = false;
		try {
			con = DbConnection.getDBConnection();
			String query = "select custID from customer where addURL=?";
			st = con.prepareStatement(query);
			st.setString(1, addURL);
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
			System.out.println("Error in getting the custID from CustomerDB table!!");
			e.printStackTrace();
		}
		return isPresent;
	}
	
	public static ArrayList<AdvertisementBean> getAddvertisement(int custid) {
		
		ArrayList<AdvertisementBean> addList = new ArrayList<>();
		try {
			con = DbConnection.getDBConnection();
			/*String getADDquery = "select addurl,adddesc from customer "
					+ " where custid in (select custid from addsFeatures where ngrams=?);"*/;
			String getADDquery = "select addurl, adddesc from customer where custid =?  ;";
			st = con.prepareStatement(getADDquery);
			st.setInt(1, custid);
			st.execute();
			rs = st.getResultSet();
			while (rs.next()) {
				AdvertisementBean addobj = new AdvertisementBean();
				addobj.setAddUrl(rs.getString("addurl"));
				addobj.setAddDesc(rs.getString("adddesc"));
				addList.add(addobj);
				System.out.println(addobj.getAddUrl() + " " + custid);
			}
			rs.close();
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error in getting the addvertisements from CustomerDB table!!");
			e.printStackTrace();
		}
		return addList;
	}
	
	public static int getUrlclicksleft (String URL) {
		int clicksleft = 0;
		try {
			con = DbConnection.getDBConnection();
			String query = "select clicksleft from customer where addurl=?";
			st = con.prepareStatement(query);
			st.setString(1, URL);
			st.execute();
			rs = st.getResultSet();
			while (rs.next()) {
				clicksleft = rs.getInt("clicksleft");
			}
			st.close();
			con.close();
		} catch (SQLException e) {
			System.out.println("Error in getting the clicksleft from customer table");
			e.printStackTrace();
		}
		return clicksleft;
	}
	public static void updateUrlClickLeft (String url, int clicksleft) {
		try {
			con = DbConnection.getDBConnection();
			con.setAutoCommit(false);
			String query = "update customer set  clicksleft= ? where addurl = ?";
			st = con.prepareStatement(query);
			st.setInt(1, clicksleft);
			st.setString(2, url);
			st.executeUpdate();
			con.commit();
			st.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error in updating clicksleft in the customer table");
		}
	}
}
