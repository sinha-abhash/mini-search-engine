package com.wse.synonyms;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import com.wse.bean.ConjDisBean;
import com.wse.bean.SynonymsBean;
import com.wse.ui.QueryDisjConj;

public class GermanSynonym {

	public static ArrayList<ConjDisBean> germanSynonyms( String keywords, int resultsize, String site, String language, String scoreType ) throws SQLException {
		ArrayList<ConjDisBean> finalresults = new ArrayList<ConjDisBean>();
		ArrayList<ArrayList<ConjDisBean>> parentList = new ArrayList<ArrayList<ConjDisBean>>();
		
		File file = new File("C:/Users/ROSHNI/Desktop/MS/WS 16/IS Project/Project roshni rishabh/extra packages installed in eclipse/openthesaurus.txt");
		//File file = new File("/home/project/openthesaurus.txt");
		String[] synonyms = null;
		try {
			Scanner scann = new Scanner (file);
			String termsWithoutTilde; 
			ArrayList<String> termsWithTilde = new ArrayList(); 
			termsWithTilde = EnglishSynonym.getTermWithTilde(keywords);
			termsWithoutTilde = EnglishSynonym.getTermWithoutTilde(keywords);
			
			if (termsWithoutTilde != null && !termsWithoutTilde.isEmpty()){
				ArrayList<ConjDisBean> urlResultList = new ArrayList<ConjDisBean>();
				urlResultList = QueryDisjConj.isConjunctive(termsWithoutTilde,20);
				parentList.add(urlResultList);
			}
			
			System.out.println("Searching synonyms for words with Tilde operator");

			for (int i = 0;i < termsWithTilde.size();i++)	
			{	
				ArrayList<String> synonymList = new ArrayList();
				String word = termsWithTilde.get(i);
				SynonymsBean obj = new SynonymsBean();
				obj.setWord(word);
				synonymList.add(word);

				while (scann.hasNext()) {
					String line = scann.nextLine();

					if (line.contains(word)) { 
						synonyms = line.split(";");
						System.out.println("Word Found "+ synonyms[0]);
						break;
					}
				}
				if (synonyms != null ){
					for (int j = 0; j < synonyms.length ; j++){
						String currentWord = synonyms[j];
							if (!currentWord.startsWith("(") && !currentWord.endsWith(")")){
								synonymList.add(synonyms[j]);
						}
					}
				}
				
				if(!synonymList.isEmpty()){
					String cleanSynonyms = EnglishSynonym.getCleanedUpKeyString(synonymList);
					if ( site == null || site.isEmpty() ){
						finalresults = QueryDisjConj.isDisjunctiveWithoutSite(cleanSynonyms, resultsize, language, scoreType);
						parentList.add(finalresults);
					}else {
						finalresults = QueryDisjConj.isDisjunctiveWithSite(cleanSynonyms, resultsize, site, language, scoreType);
						parentList.add(finalresults);
					}
					System.out.println("Synonyms for "+ word +" are: "+ synonymList);
				}
			}
			scann.close();
		}
		catch(FileNotFoundException e) { 
			System.out.println("Error in German Word search!");
		}
		
		ArrayList<ArrayList<String>> listURLList = EnglishSynonym.getURLList(parentList);
		ArrayList<String> commonURLList = EnglishSynonym.getCommonURL(listURLList);
		finalresults = EnglishSynonym.getBeanList(parentList, commonURLList); //TODO Max. 20 results must be sent to UIresults.jsp.
		
		ArrayList<ConjDisBean> sortedresultList = EnglishSynonym.sorter(finalresults);
		//return finalresults;
		return sortedresultList;
	}
}

