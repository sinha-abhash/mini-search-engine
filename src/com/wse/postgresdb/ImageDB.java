package com.wse.postgresdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.wse.bean.ImageTextBean;

public class ImageDB {
	static Connection con;
	static PreparedStatement st;
	static ResultSet rs;
	
	public static int ImageInsert (ImageTextBean imageBean, int docId) {
		int imageId = 0;
		String imageURL = imageBean.getImageUrl();
		
		if (!isImageURLPresent(imageURL)) {
			try {
				con = DbConnection.getDBConnection();
				con.setAutoCommit(false);
				String insertSQL = "insert into image(docId, imageURL) values(?,?);";
				st = con.prepareStatement(insertSQL);
				st.setInt(1, docId);
				st.setString(2, imageBean.getImageUrl());
				st.execute();
				st.close();
				con.commit();
			
				//fetch the inserted ImageID
				imageId = getImageId(imageBean);
			
			} catch (SQLException e) {
					System.out.println("Error inserting in Image table!");
					e.printStackTrace();
			}
		} else {
			try {
				imageId = getImageId(imageBean);
			} catch (SQLException e) {
				System.out.println("Error inserting in Image table!");
				e.printStackTrace();
			}
		}
		return imageId;
	
	}

	/**
	 * @param imageBean
	 * @param imageId
	 * @return
	 * @throws SQLException
	 */
	private static int getImageId(ImageTextBean imageBean)
			throws SQLException {
		int myResult = 0;
		con = DbConnection.getDBConnection();
		String selectQuery = "select imageId from image where imageURL=?";
		st = con.prepareStatement(selectQuery);
		st.setString(1, imageBean.getImageUrl());
		st.execute();
		rs = st.getResultSet();
		while (rs.next()) {
			myResult = rs.getInt("imageId");
		}
		
		st.close();
		con.close();
		rs.close();
		return myResult;
	}
	
	public static boolean isImageURLPresent(String imageURL) {
		boolean isPresent = false;
		try {
			con = DbConnection.getDBConnection();
			String query = "select imageId from image where imageurl=?";
			st = con.prepareStatement(query);
			st.setString(1, imageURL);
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
			System.out.println("Error in selecting ImageURL from image table!!");
			e.printStackTrace();
		}
		return isPresent;
	}
}
