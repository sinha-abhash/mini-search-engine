package com.wse.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wse.bean.ImageDetailsBean;
import com.wse.helpers.IndexerHelper;
import com.wse.postgresdb.GetImageDetails;

public class ImageSearchServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{

		ArrayList<ImageDetailsBean> finalresults;
		String site = null;

		String reqKey = request.getParameter("query");

		HashMap result = cleankeywords(reqKey);
		ArrayList<String> keyword = (ArrayList<String>)result.get("keyword");
		site = (String)result.get("site");

		String rawKeywords = getCleanedUpKeyString(keyword);	//Called to convert ArrayList to String
		if ( site == null || site.isEmpty() ){
			finalresults = GetImageDetails.getImagesWithoutSite(rawKeywords);
			request.setAttribute("rawResults", finalresults); // Will be available in JSP
		}
		else {
			finalresults = GetImageDetails.getImagesWithSite(rawKeywords, site);
			request.setAttribute("rawResults", finalresults); // Will be available in JSP
		}
		request.getRequestDispatcher("/UiImageResult.jsp").forward(request, response);
		System.out.println("End of servlet processing from the Image SearchServlet page.");

	}

	private String getCleanedUpKeyString (ArrayList<String> keywords) {
		String myResult = "";
		for (String key : keywords) {
			myResult += key + " ";
		}
		return myResult.trim();
	}

	public static HashMap cleankeywords(String keyword)  {

		HashMap result = new HashMap<>(); 
		//String keyword="\"roshni\"\"germany\"\"kaiserslautern\" site:uni.kl.de";
		boolean isSite=keyword.contains("site:");
		ArrayList<String> stemmedWordList = new ArrayList<String>();
		if(isSite)
		{
			System.out.print("Keywords contain Site operator. ");
			String[] split = keyword.split("site:");
			String keywordSubString = split[0];
			String domainSubString = split[1];

			stemmedWordList = stemStopWord(keywordSubString);
			result.put("site", domainSubString);
			result.put("keyword", stemmedWordList);
			return result;
			//return stemmedWordList;
		}
		else {
			//System.out.print(" Inside normal flow , keywords without site:  operator. ");
			stemmedWordList = stemStopWord(keyword);
			result.put("keyword", stemmedWordList);
			return result;
			//return stemmedWordList;
		}
	}

	public static ArrayList<String> stemStopWord (String keyword)
	{
		String keywordsWithoutQuotes = getKeyFromQuotes(keyword);
		ArrayList<String> words = new ArrayList<String>(Arrays.asList(keywordsWithoutQuotes.split("\\s+")));			
		ArrayList<String> stopWordsRemoved = IndexerHelper.removeStopWords(words);
		ArrayList<String> stemmedWordList = new ArrayList<String>();
		//System.out.println("-------------stemmed words-------------------");
		for (String word : stopWordsRemoved) {
			stemmedWordList.add(word);
		}
		return stemmedWordList;
	}

	public static String getKeyFromQuotes(String keywords){
		ArrayList<String> keyword = new ArrayList<>();
		boolean isQuotesOpened = false;
		String keyCap = "";
		String finalKey = "";
		if (!keywords.contains("\"")) {
			finalKey = keywords;
		} else {
			while ( keywords.contains("\"") ){
				if (isQuotesOpened) {
					keyCap = keywords.substring(0,keywords.indexOf("\""));
					keyword.add(keyCap);
					keywords = keywords.substring(keywords.indexOf("\"") + 1);
					isQuotesOpened = false;
				} else {
					keywords = keywords.substring(keywords.indexOf("\"")+1);
					isQuotesOpened = true;
				}
			}
			for (String tempKey : keyword) {
				finalKey = finalKey + " " + tempKey;
				finalKey = finalKey.trim();
			}
		}
		return finalKey;
	}

}
