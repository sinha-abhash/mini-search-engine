package com.wse.ui;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.wse.helpers.IndexerHelper;
import com.wse.helpers.Stemmer;

public class quotation {

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
			//String keywordsWithoutQuotes = keywordSubString.replaceAll("\"", " ");
			ArrayList<String> words = new ArrayList<String>(Arrays.asList(keywordsWithoutQuotes.split("\\s+")));			
			//System.out.println(" Keywords after removing quotes are: "+words);
						
			ArrayList<String> stopWordsRemoved = IndexerHelper.removeStopWords(words);
			//System.out.println("Keywords left after removing stop word:" +stopWordsRemoved);
			
			char[] wordToChar;
			Stemmer stemmer = new Stemmer();
			String stemmedWord;
			ArrayList<String> stemmedWordList = new ArrayList<String>();
			//System.out.println("-------------stemmed words-------------------");
			for (String word : stopWordsRemoved) {
				wordToChar = word.toCharArray();
				stemmer.add(wordToChar, word.length());
				stemmer.stem();
				stemmedWord = stemmer.toString();
				stemmedWordList.add(stemmedWord);
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

