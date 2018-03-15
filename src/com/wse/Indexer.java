package com.wse;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.wse.bean.CrawlerRecoveryBean;
import com.wse.bean.ImageTextBean;
import com.wse.helpers.IndexerHelper;
import com.wse.helpers.LanguageClassifier;
import com.wse.helpers.Stemmer;
import com.wse.postgresdb.CombinedScore;
import com.wse.postgresdb.DocumentsDB;
import com.wse.postgresdb.ScoreCalculation;
import com.wse.postgresdb.ShinglesDB;
import com.wse.postgresdb.StatusDB;
import com.wse.scoringmodel.PageRank;

public class Indexer {
	
	public static void createIndex (int depth, int maxNoOfDocs, boolean domainLeave, BufferedReader inputStream, 
			int docId, boolean isRecover, CrawlerRecoveryBean crawlerRecover, int seedDocId, String seedHost) throws IOException {
		String title;
		HashMap map = null;
		ArrayList<String> outgoingLinks;
		
		map = IndexerHelper.parseHtml(inputStream); //Call ParseHTML method to get the text that will be stemmed to store in DB
		
		ArrayList<String> words = (ArrayList<String>)map.get("words");
		
		String webText = getFullText((ArrayList<String>)map.get("textStream"));
		IndexerHelper.persistWebText(docId, webText);
		
		System.out.println("Web text persisted in Documents table for DocID: "+ docId);
		System.out.println("Total length of Web text persisted is  "+ webText.length());
		
		ArrayList<String> shinglesList = Shingling.createShingles(webText); //Call persist Shingles creation
		IndexerHelper.persistShingles(docId, shinglesList);	//Persist Shingles into the DB
		System.out.println("Shingles persisted in DB for DocID "+ docId );
		
		ArrayList<ImageTextBean> imageBeanList = (ArrayList<ImageTextBean>)map.get("imageBeanList");
		IndexerHelper.getTextBeforeImage(imageBeanList, webText);
		IndexerHelper.persistImageDB(imageBeanList, docId);
		
		ArrayList<String> filteredWordsRemoved = new ArrayList<String>();
		
		String docLanguage = LanguageClassifier.classifier(words);
		IndexerHelper.persistDocLanguage(docId , docLanguage);
		
		if ( docLanguage == "EN" ){
			ArrayList<String> stopWordsRemoved = IndexerHelper.removeStopWords(words);
			System.out.println("Removed stop words.");

			char[] wordToChar;
			Stemmer stemmer = new Stemmer();
			String stemmedWord;
			ArrayList<String> stemmedWordList = new ArrayList<String>();
			System.out.println("-----Stemming words--------");
			for (String word : stopWordsRemoved) {
				wordToChar = word.toCharArray();
				stemmer.add(wordToChar, word.length());
				stemmer.stem();
				stemmedWord = stemmer.toString();
				stemmedWordList.add(stemmedWord);
			}
			System.out.println("Stemmed words.");

			filteredWordsRemoved = IndexerHelper.removejunkWords(stemmedWordList);
			System.out.println("Removed junk words.");
		} else {
			filteredWordsRemoved = words;
		}
		title = (String)map.get("title");
		
		System.out.println("-------------");
		
		//Keep a track for visited and not visited links.
		outgoingLinks = (ArrayList<String>)map.get("outgoingLinks");
		
		ArrayList<String> visitedLinks = new ArrayList<String>();
		ArrayList<String> notVisitedLinks = new ArrayList<String>();
		
		Iterator<String> outgoingLinksItr = outgoingLinks.iterator(); //used for removing links from the outgoing links that are out of seedHost
		
		while (outgoingLinksItr.hasNext()){
			String link=(String) outgoingLinksItr.next();
			//for (String link : outgoingLinks) {
			if (DocumentsDB.isURLPresent(link)) {
				if (isRecover && DocumentsDB.getCrawledDate(docId) == null) {
					notVisitedLinks.add(link);
				}
				//IndexerHelper.persistLinks(docId,link);
				visitedLinks.add(link);
			} else {
				//Add domain leave concept here: Check if domain leave is yes or no.
				//If domain leave is no, then check the host of the each URL with the host of the seed URL.
				//If the host are similar then add it to the notVisitedLinks.
				URL urlLink = new URL(link);
				String currentHost=urlLink.getHost();
				if ((domainLeave == false && currentHost.equalsIgnoreCase(seedHost)) || domainLeave == true) { 
					notVisitedLinks.add(link); 
				} else {
					//outgoingLinks.remove(link);
					outgoingLinksItr.remove();
				}
			}
		}
		
		//ArrayList<String> distinctUrlList = IndexerHelper.getDistinctUrlList(notVisitedLinks);
		//int noOfDocsToBeVisited = (distinctUrlList.size() <= maxNoOfDocs)?(distinctUrlList.size()):(maxNoOfDocs);
		int noOfDocsToBeVisited = (notVisitedLinks.size() <= maxNoOfDocs)?(notVisitedLinks.size()):(maxNoOfDocs);
		IndexerHelper.persistOutgoingLinks(notVisitedLinks,noOfDocsToBeVisited);
		//Persisting in feature here as we will have visited/not visited links available now.
		//the below method also internally calls the persistCrawlerRecovery method to build recovery table.
		IndexerHelper.persistFilteredWordsAndTitle(docId, depth, maxNoOfDocs, filteredWordsRemoved, title, 
				isRecover, crawlerRecover, notVisitedLinks, seedDocId, noOfDocsToBeVisited);
		
		System.out.println("Words indexed for docId: " + docId);
		IndexerHelper.persistStatus(seedDocId, docId,notVisitedLinks,depth,noOfDocsToBeVisited);
		IndexerHelper.persistLinks(docId,outgoingLinks);
		System.out.println("Links table persisted for docId: " + docId);
		HashMap<String,Integer> nextDocIdMap = StatusDB.getStatusTable(seedDocId);
		int nextDocId = nextDocIdMap.get("toDocId");
		depth = nextDocIdMap.get("depth");
		
		// Url for depth =1 not inserted in status table.
		if( depth != 0  ) {
			int nextLevelDepth = depth - 1;
			String nextURL = DocumentsDB.getURL(nextDocId);
			System.out.println("Preparing to crawl:  "+nextURL + " with docId: " + nextDocId);

			IndexerHelper.updateStatus(nextDocId);
			boolean crawlComplete = true;
			IndexerHelper.udpateCrawlerRecovery(docId, crawlComplete);
			isRecover = false;
			Crawler.crawl(nextLevelDepth, maxNoOfDocs, new URL(nextURL), domainLeave, isRecover, seedHost);
		}
		if ( depth == 0 ){
			
			ScoreCalculation.calculateTFidfBM25();
			PageRank.formulatePageRankScore();
			CombinedScore.combinedScoreDB();
			
			//TODO calculate the jaccard coefficient
			//Persist it in Jaccard table in DB
			//Calculate Jaccard value
			IndexerHelper.persistJaccard();
			
			System.out.println("Status and Recovery table deleted");
			System.out.println("Crawl completed successfully");
		}
	}

	private static String getFullText(ArrayList<String> lines) {
		String result = "";
		for (String line : lines) {
			//System.out.println(line);
			if (line.length() > 0 && line.charAt(line.length()-1) != ' ') {
				line += " ";	//adding space at the end of the line.
			}
			result = result + line;
		}
		return result;
	}	
}
		/*URL url = null;
		if (!notVisitedLinks.isEmpty()) {
			int nextLevelDepth = depth - 1;
			
			for (int i = 0; i < noOfDocsToBeVisited; i++) {
				String unvisitedLink = notVisitedLinks.get(i);
				adjustLinkStatus(unvisitedLink, notVisitedLinks, visitedLinks);
				url = new URL(unvisitedLink);
				isRecover = false;		//at this point the crawler has resumed completely 
				Crawler.crawl(nextLevelDepth, maxNoOfDocs, url, domainLeave, isRecover);
			}
		}
	/*
	 * This method deletes the unvisitedLink from unvisitedLinks list and add it to visitedLinks list as this unvisitedLink is going to be
	 * crawled next.*/
	 /*
	private static void adjustLinkStatus(String unvisitedLink,
			ArrayList<String> notVisitedLinks, ArrayList<String> visitedLinks) {
		notVisitedLinks.remove(unvisitedLink);
		visitedLinks.add(unvisitedLink);
	}
	*/
	

