package com.wse.synonyms;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rita.*;

import com.wse.bean.ConjDisBean;
import com.wse.bean.SynonymsBean;
import com.wse.helpers.IndexerHelper;
import com.wse.helpers.Stemmer;
import com.wse.postgresdb.DbConnection;
import com.wse.ui.QueryDisjConj;

public class EnglishSynonym {
	static Connection con;
	static PreparedStatement st;
	static PreparedStatement st1;
	static ResultSet rs;
	
	public static final RiWordNet wordnet = new RiWordNet("C:\\Program Files (x86)\\WordNet\\2.1");
	//public static final RiWordNet wordnet = new RiWordNet("/home/project/WordNet-3.0");
	public static ArrayList<ConjDisBean> englishSynonyms ( String keywords, int resultsize, String site, String language, String scoreType ) throws SQLException// (String[] args) throws IOException {
	{	
		ArrayList<String> termsWithTilde = new ArrayList(); 
		ArrayList<ConjDisBean> finalresults;
		ArrayList<ArrayList<ConjDisBean>> parentList = new ArrayList<ArrayList<ConjDisBean>>();

		String termsWithoutTilde = getTermWithoutTilde(keywords);

		if (termsWithoutTilde != null && !termsWithoutTilde.isEmpty()){
			ArrayList<ConjDisBean> urlResultList = new ArrayList<ConjDisBean>();
			urlResultList = QueryDisjConj.isConjunctive(termsWithoutTilde,20);
			parentList.add(urlResultList);
		}

		termsWithTilde = getTermWithTilde(keywords);
		System.out.println("Searching synonyms for words with Tilde operator");
		for (int i = 0; i < termsWithTilde.size(); i++)	
		{	
			ArrayList<String> synonymList = new ArrayList();
			String nounSynonym = "";
			String verbSynonym = ""; 
			String adjSynonym = ""; 
			String word = termsWithTilde.get(i);
			SynonymsBean obj = new SynonymsBean();
			obj.setWord(word);
			synonymList.add(word);

			nounSynonym = getNounSynonym(word);
			if (nounSynonym != null && !nounSynonym.isEmpty()){
				synonymList.add(nounSynonym);
			}

			verbSynonym = getVerbSynonym(word);
			if (verbSynonym != null && !verbSynonym.isEmpty()){
				synonymList.add(verbSynonym);
			}

			adjSynonym = getAdjectiveSynonym(word);
			if (adjSynonym != null && !adjSynonym.isEmpty()){
				synonymList.add(adjSynonym);
			}

			if(!synonymList.isEmpty()){
				String synonyms = getCleanedUpKeyString(synonymList);
				if ( site == null || site.isEmpty() ){
					finalresults = QueryDisjConj.isDisjunctiveWithoutSite(synonyms, resultsize, language, scoreType);
					parentList.add(finalresults);
				}else {
					finalresults = QueryDisjConj.isDisjunctiveWithSite(synonyms, resultsize, site, language, scoreType);
					parentList.add(finalresults);
				}
				System.out.println("Synonyms for "+ word +" are: "+ synonymList);
			}
			obj.setSynonyms(synonymList);
		}

		ArrayList<ArrayList<String>> listURLList = getURLList(parentList);
		ArrayList<String> commonURLList = getCommonURL(listURLList);
		finalresults = getBeanList(parentList, commonURLList); //TODO Max. 20 results must be sent to UIresults.jsp.
		
		ArrayList<ConjDisBean> sortedresultList = sorter(finalresults);
		//return finalresults;
		return sortedresultList;
	}
	
	public static ArrayList<ConjDisBean> sorter(ArrayList<ConjDisBean> finalresults) {
		ArrayList<ConjDisBean> sortedresultList = new ArrayList<ConjDisBean>();

		for ( int i = 0 ; i< finalresults.size(); i++){
			try {
				ConjDisBean obj = finalresults.get(i);
				String url = obj.getUrl();
				float score = obj.getscore();
				con = DbConnection.getDBConnection();
				con.setAutoCommit(false);
				String insertSQL = "insert into temp (url, score) values (?,?)";

				st = con.prepareStatement(insertSQL);
				st.setString(1, url);
				st.setFloat(2, score);
				st.execute();

				st.close();
				con.commit();
				con.close();
			} catch (SQLException e) {
				System.out.println("Error inserting in temp table!");
				e.printStackTrace();
			}
		}

		for ( int i = 0 ; i < finalresults.size(); i++){
			try {
				con = DbConnection.getDBConnection();
				String selectQuery = "select row_number() over (order by score desc) as rank , url, score from temp";
				String clearTempTable = "truncate table temp";
				
				st = con.prepareStatement(selectQuery);
				st1 = con.prepareStatement(clearTempTable);
				st.execute();

				rs = st.getResultSet();
				while (rs.next()) {
					ConjDisBean obj = new ConjDisBean();
					obj.setRank(rs.getInt("rank"));
					obj.setUrl( rs.getString("url"));
					obj.setscore(rs.getFloat("score"));
					sortedresultList.add(obj);
				}
				
				st1.execute();
				st1.close();
				st.close();
				rs.close();
				con.close();
			}catch (SQLException e) {
				System.out.println("Error fetching data from DB!");
				e.printStackTrace();
			}
		}
		return sortedresultList;
	}
	/**
	 * This method builds the list of (list of URLs) for different query terms.
	 * @param parentList
	 * @return
	 */
	public static ArrayList<ConjDisBean> getBeanList(ArrayList<ArrayList<ConjDisBean>> parentList,	ArrayList<String> finalList) {
		ArrayList<ConjDisBean> resultList = new ArrayList();
		boolean toBreak;
		for(String url: finalList ){
			toBreak = false;
			for (int i = 0; i< parentList.size(); i++ ){
				ArrayList<ConjDisBean> objList = parentList.get(i);
				for (ConjDisBean obj : objList){
					String urlCompare = obj.getUrl();
					if ( url.equalsIgnoreCase(urlCompare)){

						ConjDisBean toAddObj = new ConjDisBean();
						toAddObj.setscore(obj.getscore());
						toAddObj.setUrl(url);
						toAddObj.setRank(i); //TODO Rank the url based on score and max 20 url must be displayed.
						resultList.add(toAddObj);
						toBreak = true;
						break;
					}
				}
				if (toBreak) {
					break;
				}
			}
		}
		return resultList;
	}

	public static ArrayList<ArrayList<String>> getURLList(	ArrayList<ArrayList<ConjDisBean>> parentList) {

		ArrayList<ArrayList<String>> listURLList = new ArrayList<>();
		ArrayList<String> urlList = null;

		for ( int i = 0; i< parentList.size(); i++ ){
			ArrayList<ConjDisBean> objList = parentList.get(i);
			urlList = new ArrayList<String>();
			for (ConjDisBean obj : objList){
				urlList.add(obj.getUrl());
			}
			listURLList.add(urlList);
		}

		return listURLList;
	}

	public static ArrayList<String> getCommonURL(ArrayList<ArrayList<String>> listURLList) {
		ArrayList<String> finalList = new ArrayList<String>();
		boolean common = false;
		ArrayList<String> firstURLList = listURLList.get(0);
		if (listURLList.size() == 1) {
			return firstURLList;
		}
		for (String url : firstURLList) {
			for (int i = 1; i< listURLList.size(); i++){
				if (listURLList.get(i).contains(url)) {
					common = true;
				} else {
					common = false;
				}
			}
			if (common) {
				finalList.add(url);
				common = false;		//resetting common boolean for the next iteration.
			}
		}


		return finalList;
	}

	public static ArrayList<String> getTermWithTilde(String keywords) {
		ArrayList<String> termsWithTilde =  new ArrayList<String>(); 

		Pattern pattern=Pattern.compile("~\\s*(\\w+)");
		Matcher match = pattern.matcher(keywords);

		while (match.find()) {
			String temp = match.group(1);
			termsWithTilde.add(temp);
			//System.out.println(match.group(1));
		}
		System.out.println("Terms with the tilde operator  "+termsWithTilde);

		return termsWithTilde;
	}

	public static String getTermWithoutTilde( String keyword) {	
		String	replacedWord = keyword.replaceAll("~\\s*(\\w+)", "");    
		String[] items = replacedWord.split(" ");
		List<String> itemList = new ArrayList<String>();
		for (String item: items){
			if (item != null && !item.isEmpty())
				itemList.add(item.trim());
		}
		System.out.println("Terms without the tilde operator  " +itemList);
		System.out.println();

		ArrayList<String> termsWithoutTilde = new ArrayList<String>(itemList);
		String termsWithoutTild = getCleanedUpKeyString(termsWithoutTilde);

		return termsWithoutTild;
	}

	public static String getCleanedUpKeyString (ArrayList<String> keywords) {
		String myResult = "";
		for (String key : keywords) {
			myResult += key + " ";
		}
		return myResult.trim();
	}

	public static String getNounSynonym (String word){

		String[] synonymsn = wordnet.getAllSynonyms(word, "n");
		Arrays.sort(synonymsn);
		ArrayList<String> nounSynonymList = new ArrayList<>();

		if (synonymsn != null && synonymsn.length != 0) {
			for (int j = 0; j < 20; j++) {
				nounSynonymList.add(synonymsn[j]);
			}
		} else {
			System.out.println("No synyonyms found for noun position"+ word);
		}

		String nounString = "";
		for (String s : nounSynonymList)
		{
			nounString += s + " ";
		}
		nounString = nounString.trim();
		return nounString;
	}

	public static String getVerbSynonym (String word){
		String[] synonymsv = wordnet.getAllSynonyms(word, "v");
		Arrays.sort(synonymsv);
		ArrayList<String> verbSynonymList = new ArrayList<>();
		if (synonymsv != null && synonymsv.length != 0) {

			for (int j = 0; j < 20; j++) {
				verbSynonymList.add(synonymsv[j]);
			}
		} else {
			System.out.println("No synyonyms found for verb position "+ word);
		}

		String verbString = "";
		for (String s : verbSynonymList)
		{
			verbString += s + " ";
		}
		verbString = verbString.trim();

		return verbString;
	}

	public static String getAdjectiveSynonym (String word){

		String[] synonymsa = wordnet.getAllSynonyms(word, "a");
		Arrays.sort(synonymsa);
		ArrayList<String> adjSynonymList = new ArrayList<>();
		if (synonymsa != null && synonymsa.length != 0) {
			for (int j = 0; j< 20; j++) {
				adjSynonymList.add(synonymsa[j]);
			}
		} else {
			System.out.println("No synyonyms found for adjective position for "+ word);
		}

		String adjString = "";
		for (String s : adjSynonymList)
		{
			adjString += s + " ";
		}
		adjString = adjString.trim();

		return adjString;

	}

	public static ArrayList<String> StemStopWord (String keyword)
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
		ArrayList<String> keyword = new ArrayList();
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

