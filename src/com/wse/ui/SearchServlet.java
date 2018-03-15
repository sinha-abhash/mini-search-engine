package com.wse.ui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.net.URL;

import com.wse.bean.AdvertisementBean;
import com.wse.bean.ConjDisBean;
import com.wse.bean.SynonymsBean;
import com.wse.helpers.AdvertisementHelper;
import com.wse.postgresdb.CustomerDB;
import com.wse.synonyms.EnglishSynonym;
import com.wse.synonyms.GermanSynonym;

public class SearchServlet extends HttpServlet{

	protected void doGet(javax.servlet.http.HttpServletRequest request,HttpServletResponse response) 
			throws ServletException, IOException {

		int resultsize = 20;	//Specified in Sheet to limit results to 20
		ArrayList<ConjDisBean> finalresults = new ArrayList<ConjDisBean>();

		String reqKey = request.getParameter("query");
		String language = request.getParameter("language");
		String scoreType = request.getParameter("score");

		request.getSession().setAttribute("language", language);
		request.getSession().setAttribute("score", scoreType);
		//Quotation and site operator processed in cleanKeywords method itself
		//ArrayList<String> keywords = quotation.cleankeywords(reqKey); //call quotation class/method to clear site and quotation operator
		String site = null;
		String alternateKeywords = null; //name was key before
		boolean reQuery = false;
		
		if (request.getSession().getAttribute("requery") != null) {
			reQuery = (boolean)request.getSession().getAttribute("requery");
		}
		System.out.println("status of requery:" + reQuery);
		
		HashMap result = quotation.cleankeywords(reqKey);
		ArrayList<String> keyword = (ArrayList<String>)result.get("keyword");
		site = (String)result.get("site");
		String rawKeywords = getCleanedUpKeyString(keyword);	//Called to convert ArrayList to String
		keyword = getAllTerms(keyword);	//reset keyword to contain all query terms containing no tilde operator
		
		System.out.println(rawKeywords);
		//ArrayList<AdvertisementBean> addsList = CustomerDB.getAddvertisement(rawKeywords);
		ArrayList<AdvertisementBean> addsList = AdvertisementHelper.getAdvertisementList(reqKey);
		if(!addsList.isEmpty()){
			request.setAttribute("addResults", addsList);
			
			System.out.println("Addvertisement is found.");
			for (AdvertisementBean obj: addsList){
				System.out.println(obj.getAddUrl());
				System.out.println(obj.getAddDesc());
			}
		}else{
			System.out.println("None Addvertisement not found.");
		}
		
		if ( !reQuery ){
			if (reqKey.contains("~")){
				if ( language.equalsIgnoreCase("EN") ){
					System.out.println("language is English and tildeeeee operator is found!");
					try {
						finalresults = EnglishSynonym.englishSynonyms(rawKeywords, resultsize, site, language, scoreType);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}else {
					System.out.println("language is german and tildeeeee operator is found!");
					try {
						finalresults  = GermanSynonym.germanSynonyms(rawKeywords, resultsize, site, language, scoreType);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}else {
				try{
					if ( site == null || site.isEmpty() ){
						finalresults = QueryDisjConj.isDisjunctiveWithoutSite(rawKeywords, resultsize, language, scoreType);
					}
					else {
						finalresults = QueryDisjConj.isDisjunctiveWithSite(rawKeywords, resultsize, site, language, scoreType);
					}
				}catch (SQLException e) {
					System.out.println("Issue with Disjunctive Query call in raw keyword.");
					e.printStackTrace();
				}
			}
			Snippets.getSnippets(keyword, finalresults);
			request.setAttribute("rawResults", finalresults); // Will be available in JSP
			
			System.out.println("Find alternate terms or spelling mistakes!");		

			ArrayList<String> keywords = SpellChecker.typoChecker(keyword);
			alternateKeywords = getCleanedUpKeyString(keywords);	//Called to convert ArrayList to String

			if (alternateKeywords != null && !alternateKeywords.isEmpty()) {
				try{
					if ( site == null || site.isEmpty() ){
						finalresults = QueryDisjConj.isDisjunctiveWithoutSite(alternateKeywords, resultsize, language, scoreType);
					}else {
						finalresults = QueryDisjConj.isDisjunctiveWithSite(alternateKeywords, resultsize, site, language, scoreType);
					}
				} catch (SQLException e) {
					System.out.println("Issue with Disjunctive Query call during alternative terms.");
					e.printStackTrace();
				}
				request.setAttribute("alternateTerm", alternateKeywords);
				//Snippets.getSnippets(keyword, finalresults);
				Snippets.getSnippets(keywords, finalresults); //keywords contain the alternate terms
				request.setAttribute("topResults", finalresults);
			}

			request.setAttribute("rawKeywords", rawKeywords);
			//setting the value of site in session to be accessed when servlet gets hit from the alternate url link.
			request.getSession().setAttribute("site", site);

			String extension = "query=" + alternateKeywords + "+&language=" + language + "&score=" + scoreType;
			String fixedPart = "http://localhost:8022/WSE/SearchServlet?" + extension ;
			//URL url = new URL(fixedPart);
			request.setAttribute("alternateQueryURL", fixedPart);

			request.getSession().setAttribute("requery", false);
			//request.getSession().setAttribute("requery", true);

			request.getRequestDispatcher("/UiResult.jsp").forward(request, response);
			System.out.println("End of servlet processing from the index.html page");
			
		}else {
			String siteOld = (String) request.getSession().getAttribute("site");
			String reqKeyOld = (String) request.getSession().getAttribute("alternateTerm");
			String languageOld = (String) request.getSession().getAttribute("language1");
			String scoreTypeOld = (String) request.getSession().getAttribute("scoreType");

			request.getSession().setAttribute("requery", false);		//resetting it to false for new searches.

			try{
				//if ( site == null || site.isEmpty() ){
				//finalresults = QueryDisjConj.isDisjunctiveWithoutSite(reqKey, resultsize, language, scoreType);

				if ( siteOld == null || siteOld.isEmpty() ){
					request.setAttribute("alternateTerm",reqKeyOld);
					if (reqKeyOld != null && !reqKeyOld.isEmpty() && languageOld != null && !languageOld.isEmpty() && scoreTypeOld != null && !scoreTypeOld.isEmpty() )
					{
						finalresults = QueryDisjConj.isDisjunctiveWithoutSite(reqKeyOld, resultsize, languageOld, scoreTypeOld);
						request.setAttribute("results", finalresults); // Will be available in JSP
					}
					else {
						String message = "Alternate term not found!";
						request.setAttribute("alternateTerm",message);
					}
					request.getRequestDispatcher("/UiResult.jsp").forward(request, response);
				}
				else {
					request.setAttribute("alternateTerm",reqKeyOld);

					if (reqKeyOld != null && !reqKeyOld.isEmpty() && languageOld != null && !languageOld.isEmpty() && scoreTypeOld != null && !scoreTypeOld.isEmpty() )
					{
						finalresults = QueryDisjConj.isDisjunctiveWithSite(reqKeyOld, resultsize, siteOld, languageOld, scoreTypeOld);
						request.setAttribute("results", finalresults); // Will be available in JSP
					}
					else {
						String message = "Alternate term not found!";
						request.setAttribute("alternateTerm",message);
					}
					request.getRequestDispatcher("/UiResult.jsp").forward(request, response);
				}
			} catch (SQLException e) {
				System.out.println("Issue with Disjunctive Query call in alternate spelling suggestion.");
				e.printStackTrace();
			}
		}
	}

	private ArrayList<String> getAllTerms(ArrayList<String> keyword) {
		ArrayList<String> result = new ArrayList<String>();
		for (String key : keyword) {
			if (key.contains("~")) {
				key = key.replace("~", "");
			}
			result.add(key);
		}
		return result;
	}

	private String getCleanedUpKeyString (ArrayList<String> keywords) {
		String myResult = "";
		for (String key : keywords) {
			myResult += key + " ";
		}
		return myResult.trim();
	}
}
