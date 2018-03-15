package com.wse.servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wse.bean.ConjDisBean;
import com.wse.helpers.MetaSearchEngineHelper;
import com.wse.postgresdb.MergeResultsDB;

public class MetaSearchEngineServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		String query = request.getParameter("query");
		//String searchEnginePercentStr = request.getParameter("percentage");
		int score = Integer.parseInt(request.getParameter("score"));
		int k = Integer.parseInt(request.getParameter("k"));
		//public static void main(String[] args) {
		//String query = "main";
		//int score = 1;
		//int k = 2;
		String searchEnginePercentStr = request.getParameter("searchEnginePercentStr");
		int noOfSearchEngine = 0;
		if (searchEnginePercentStr != null && isNumeric(searchEnginePercentStr)) {
			int searchEnginePercent = Integer.parseInt(searchEnginePercentStr);
			noOfSearchEngine = MetaSearchEngineHelper.getNoOfSearchEngine(searchEnginePercent, query);
		}

		if (noOfSearchEngine == 0) {
			System.out.println("None of them are there.");
		} else {
			MetaSearchEngineHelper.startSEThreading(noOfSearchEngine, query, score, k);
			/*try {
				MetaSearchEngineHelper.joinThreads();
			} catch (InterruptedException e) {
				System.out.println("Connection timed out: connect");
				e.printStackTrace();
			}*/
		}
		MetaSearchEngineHelper.persistStatistics();
		ArrayList<ConjDisBean> resultsList = MetaSearchEngineHelper.getResultsToDisplay(k);
		
		request.setAttribute("FinalResultList", resultsList);
		request.getRequestDispatcher("/MetaSeResult.jsp").forward(request, response);
		
		MergeResultsDB.truncateMergeResultsTable();	// Truncate the Mergeresults table
		
		System.out.println("Congratulations!! Meta Search engine Servlet Complete..");
	}

	public static boolean isNumeric(String str)  
	{  
		try  
		{  
			double d = Double.parseDouble(str);  
		}  
		catch(NumberFormatException nfe)  
		{  
			return false;  
		}  
		return true;  
	}


}
