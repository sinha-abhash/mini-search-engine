package com.wse.externalinterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.wse.bean.ConjDisBean;
import com.wse.postgresdb.DbConnection;
import com.wse.ui.QueryDisjConj;
import com.wse.ui.quotation;

public class json extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Connection con;
	static ResultSet rs;
	PreparedStatement ps;
	CallableStatement cst;
	ArrayList<ConjDisBean> resultList = new ArrayList<ConjDisBean>();
	
	protected void doGet(javax.servlet.http.HttpServletRequest request,HttpServletResponse response) 
			throws ServletException, IOException {
		
		con = DbConnection.getDBConnection();
		//Initializing JSONArray and JSONObject
		JSONObject root = new JSONObject();
		JSONArray resultListArray = new JSONArray();	//Holds the results: rank,url,score 
		JSONArray statisticDFArray = new JSONArray();	//Term wise df value 
		
		PrintWriter out = response.getWriter();
		String userKeywords = null;
		int resultsize = 0;
		ArrayList<ConjDisBean> finalresults;
		response.setContentType("application/json");
		String language = "EN";
		HashMap result = null;
		
	try{
		userKeywords = request.getParameter("query");
		resultsize = Integer.parseInt(request.getParameter("k"));
		String scoreType = request.getParameter("score");
		
		result = quotation.cleankeywords(userKeywords);
		//ArrayList<String> keywords=quotation.cleankeywords(userKeywords);	//For stemming and stop word elimination
		ArrayList<String> keywords = (ArrayList<String>)result.get("keyword");
		String key = getCleanedUpKeyString(keywords);
		//System.out.println(key);
		
		//TODO Call method to process quotation and site keywords
		
		JSONObject queryObject = new JSONObject();
		queryObject.put("k",resultsize);
		queryObject.put("query",key);
		
		//finalresults=QueryDisjConj.isDisjunctive(key, resultsize, language);		//Call made to isDisjunctive method
		finalresults=QueryDisjConj.isDisjunctiveWithoutSite(key, resultsize, language, scoreType);
		
		JSONObject objt = null;
		   for (ConjDisBean obj : finalresults) {
			   objt = new JSONObject();
		    	objt.put("rank",obj.getRank());
		    	objt.put("url", obj.getUrl());
			   	objt.put("score",obj.getscore());
		    	out.println();
				resultListArray.add(objt);	//Adding the result list to JSONArray object
			}    
		   //Number of documents in collection containing the specific terms.
		  // System.out.println("No Of Documents for Specific Terms");
		   String dfFunction="create or replace function Get_word(received_words varchar(100)) returns table (term_word varchar(80) ) as "
		   		+ " $$	 SELECT term_word FROM regexp_split_to_table(received_words, E'\\s+') term_word $$ "
		   		+ " language 'sql' VOLATILE;";
		   
		  String dfCount=" select term,count(*) TermCount from  features group by term having term in (select term_word from Get_word(?))";
		   
		  ps= con.prepareStatement(dfCount);
		  
		  cst = con.prepareCall("{call Get_word(?)}");
		  cst.setString(1,key);
		  cst.execute();
		  
		   ps.setString(1,key);
		   rs=ps.executeQuery();
		   while (rs.next()) {
			   JSONObject jsondf = new JSONObject();
			   jsondf.put("term", rs.getString("term"));
			   jsondf.put("df", rs.getFloat("Termcount"));
			   statisticDFArray.add(jsondf);
		   }
		   
		   //To find total number of documents	
		  // System.out.println("Total Count of Documents to be calculated next..");
		  // String totalCount="select sum(term_frequency) as TotalSum from features";
		   String totalCount="select count(distinct(term)) as TotalSum from features;";
		   ps=con.prepareStatement(totalCount);
		   
		   rs=ps.executeQuery();
		   int totalSum = 0;
		   
		   while (rs.next()){
			   totalSum= rs.getInt("TotalSum");
			   //System.out.println("Total COunt"+ totalSum);
		   }
		   // finally output the json string (it will return your json object)
		
		root.put("resultList", resultListArray);
		root.put("query", queryObject); 
		root.put("stat", statisticDFArray);
		root.put("cw", totalSum);
		
		System.out.println("Output in JSON Format :\n");
		System.out.println(root.toJSONString());	//Json values Printing On console
		
		StringWriter outstring = new StringWriter();
		root.writeJSONString(outstring);
		out.println(root.toString());	//out.print(jsonObject);  
		out.flush();

		}	catch (Exception e) {
				System.out.println("Error in Json servlet processing.!!!");
		        e.printStackTrace();
		    }finally{
				if(rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					} }
				if(ps != null) {
					try {
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					} }
				if(cst != null) {
					try {
						cst.close();
					} catch (SQLException e) {
						e.printStackTrace();
					} }
				if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} }
			}
	}
	
	//Convert Arraylist to String, for processing in Conj method.
	private String getCleanedUpKeyString (ArrayList<String> keywords) {
		String myResult = "";
		for (String key : keywords) {
			myResult += key + " ";
		}
		return myResult.trim();
	}
}
