package com.wse.ui;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.wse.bean.ConjDisBean;

public class Uicli {
	static Scanner input = new Scanner(System.in);
	
	public static void getCLIargumnts(String keyword, int resultsize, int mode )
	{
		ArrayList<ConjDisBean> finalresults;
		try {	
			System.out.println("Please enter the keywords for Web Search"); 
			keyword = input.nextLine();
			
			System.out.println("Enter the Result Size");
			resultsize=input.nextInt();
			
			System.out.println("Please enter the query processing mode:");
			System.out.println("Enter 1 for Disjunctive Mode. OR Enter 2 for Conjunctive Mode.");
			mode=input.nextInt();
			String language = "EN";
			HashMap results = null;
				if(mode == 1)
				{
					System.out.println("Disjunctive Query Mode Selected!");

					String reqKey=keyword;
					
					results = quotation.cleankeywords(reqKey);
					//ArrayList<String> keywords=quotation.cleankeywords(reqKey); //call quotation class/method to clear site and quotation operator
					ArrayList<String> keywords = (ArrayList<String>)results.get("keyword");
					String key = getCleanedUpKeyString(keywords);
					System.out.println(key);
					
					finalresults=QueryDisjConj.isDisjunctive(key, resultsize, language);
					
					for (ConjDisBean obj : finalresults) {
				    	System.out.println(obj.getRank());
				    	System.out.println(obj.getUrl());
				    	System.out.println(obj.getscore());
				    	System.out.println();
					}
				}
				else 
				{
					System.out.println("Conjunctive Query Mode Selected!");
					String reqKey = keyword;
					
					results = quotation.cleankeywords(reqKey);
					//ArrayList<String> keywords=quotation.cleankeywords(reqKey);	//call quotation class/method to clear site and quotation operator
					ArrayList<String> keywords = (ArrayList<String>)results.get("keyword");
					String key = getCleanedUpKeyString(keywords);
					System.out.println(key);
					
					finalresults=QueryDisjConj.isConjunctive(keyword, resultsize);
					for (ConjDisBean obj : finalresults) {
				    	System.out.println(obj.getRank());
				    	System.out.println(obj.getUrl());
				    	System.out.println(obj.getscore());
				    	System.out.println();
					}
				}		
			}
		   catch(Exception e){
			      e.printStackTrace();
			      System.out.println("Error in receiving CLI value.");
		}
	}
	private static String getCleanedUpKeyString (ArrayList<String> keywords) {
		String myResult = "";
		for (String key : keywords) {
			myResult += key + " ";
		}
		return myResult.trim();
	}
	public static void main (String[] args)
	{
		int resultsize = 0;
		String keyword = null;
		int mode = 0;
		getCLIargumnts(keyword,resultsize,mode);
	}
}

