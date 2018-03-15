package com.wse.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.wse.bean.CrawlerRecoveryBean;
import com.wse.bean.ImageTextBean;
import com.wse.postgresdb.CrawlerRecoveryDB;
import com.wse.postgresdb.DocumentsDB;
import com.wse.postgresdb.FeaturesDB;
import com.wse.postgresdb.ImageDB;
import com.wse.postgresdb.ImageFeaturesDB;
import com.wse.postgresdb.JaccardDB;
import com.wse.postgresdb.LinksDB;
import com.wse.postgresdb.ShinglesDB;
import com.wse.postgresdb.StatusDB;

public class IndexerHelper {
	static boolean isCommentOpened = false;
	static boolean isBodyOpened = false;
	static boolean isScriptOpened = false;
	static ArrayList<String> outgoingLinks = new ArrayList<String>();
	static ArrayList<String> imgSrcList = new ArrayList<String>();
	static boolean imageFound = false;
	static ImageTextBean imageBean = null;
	static ArrayList<ImageTextBean> imageBeanList = new ArrayList<ImageTextBean>();
	
	public static HashMap parseHtml (BufferedReader inputStream) throws IOException {
		
		HashMap map = new HashMap<>();
		HashMap results = null;
		
		String title = null;
		String inputLine;
		String[] tempWordList = null;
		
		
		ArrayList<String> wordList = new ArrayList<String>();
		ArrayList<String> textStream = new ArrayList<String>();
		
		while ((inputLine = inputStream.readLine()) != null) {
			//System.out.println(inputLine);
			results = getTextFromLine(inputLine);
			if (results != null && !results.isEmpty()) {
				textStream.addAll((ArrayList<String>)results.get("text"));	//textStream holds all the text of the page.
				for (String text : (ArrayList<String>)results.get("text")) { //here textStream is not used because this loop works only on the current line and not on all the text present on the webpage.
					if (text != null && !text.isEmpty()) {
						tempWordList = text.split(" ");
						for (String tempWord : tempWordList) {
							wordList.add(tempWord);
						}
					}
				}
				
				if (title == null || title.isEmpty()) {
					title = (String)results.get("title");
				}
			}
		}
		map.put("words",wordList);
		map.put("title",title);
		map.put("outgoingLinks",outgoingLinks);
		map.put("textStream", textStream);
		map.put("imageBeanList", imageBeanList);
		return map;
	}
	
	public static HashMap getTextFromLine(String inputLine) throws MalformedURLException {		
		String tempString = inputLine.trim();
		String tempString1 = "";
		String tempString2 = "";
		String link;
		String linkWithDoubleQuotes;
		String title = null;
		char firstChar;
		char secondChar;
		 
		
		HashMap results = new HashMap();
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<String> wildExtensions= new ArrayList<String>();
		
		wildExtensions.addAll(Arrays.asList(".pdf",".rss",".jpg"));
		

		if (!tempString.isEmpty()) {
			checkBodyTag(tempString);
			checkScriptTag(tempString);
			if (inputLine.contains("<title>")) {	//capture title of the webpage
				while (tempString.contains("<") || tempString.contains(">")) {
					if (tempString.contains("<title>")) {
						tempString = tempString.substring(tempString.indexOf(">")+1);
					} else if (tempString.contains("</title>")) {
						tempString = tempString.substring(0,tempString.indexOf("</title>"));
					}
					
				}
				title = tempString;
			}
			if (isBodyOpened && !isScriptOpened) {
				while (tempString.contains("<") || tempString.contains(">")) {
					if (tempString.length() == 1) {
						break;
					}
					firstChar = tempString.charAt(0);
					secondChar = tempString.charAt(1);
					if (tempString.contains("href")) {
						linkWithDoubleQuotes = tempString.substring(tempString.indexOf("href=\"") + 6);
						link = linkWithDoubleQuotes.substring(0,linkWithDoubleQuotes.indexOf("\""));
						if (link.contains(".")) {
							
							if (!link.contains("https") && !link.contains("http")) {	
								link = "http:" + link;		//added http for the links which didn't have protocol
							}
							URL url=new URL(link);
							String urlHost = url.getHost();
							if (!outgoingLinks.contains(link) && !(outgoingLinks.contains(wildExtensions)) && urlHost != null && !urlHost.isEmpty()) {
								outgoingLinks.add(link);
							}
						}
					}
					if (tempString.contains("<img")) {
						//the line has image tag
						String tempImgSrc1 = tempString.substring(tempString.indexOf("img")-1);
						String tempImgSrc2 = tempImgSrc1.substring(0,tempImgSrc1.indexOf(">")+1);
						String tempImgSrc3 = tempImgSrc2.substring(tempImgSrc2.indexOf("src")+5);
						String imgSrc = tempImgSrc3.substring(0,tempImgSrc3.indexOf("\""));
						if (!imgSrcList.contains(imgSrc)) {
							imageFound = true;		//switch on the boolean when the image is found.
							imageBean = new ImageTextBean();
							imageBean.setImageUrl(imgSrc);
							imageBeanList.add(imageBean);
							imgSrcList.add(imgSrc);
						}
						
					}
					//If the line contains only '<' and not '>', take the string before '<' and ignore the rest.
					//If the line contains only '>', take the string after '>' and ignore everything before it.
					if (tempString.contains("<") && !tempString.contains(">")) {
						if (Character.toString(firstChar).equalsIgnoreCase("<")) {
							tempString = ""; //as we don't have any text to capture in this line.
							break;
						} else {
							tempString = tempString.substring(0, tempString.indexOf("<"));
						}
					} else if (tempString.contains(">") && !tempString.contains("<")) {
						tempString = tempString.substring(tempString.indexOf(">") + 1);
					}
					
					//<a href="http://www.uni-kl.de"><img src="/archiv/wwwdvs.informatik.uni-kl.de/images/uni2.gif"
					
					else if (!((tempString.length()-tempString.replace("!", "").length()) == 2)&&(Character.toString(secondChar).equalsIgnoreCase("/") || isComment(tempString))
							/*|| result.indexOf(">") == result.lastIndexOf(">")*/) {
	/*					tempString = null;
						break;		//this input line contains only the end tag or only comment. Return null as there is no valid text present in this line.*/	
						tempString = tempString.substring(tempString.indexOf(">")+1);
					} 
					else if (Character.toString(firstChar).equalsIgnoreCase("<") || Character.toString(firstChar).equalsIgnoreCase(">")) {
						tempString = tempString.substring(tempString.indexOf(">")+1);
					} else if (!tempString.substring(0,tempString.indexOf("<")).trim().equalsIgnoreCase(">") && 
							!tempString.substring(0,tempString.indexOf("<")).trim().equalsIgnoreCase("<")){
						tempString1 = tempString.substring(0,tempString.indexOf("<"));
						if (tempString.substring(tempString.indexOf(">")+1).length() > 0 ||
								!tempString1.contains(">") || !tempString1.contains("<")) {
							tempString2 += tempString1;
						} 
						
						tempString = tempString.substring(tempString.indexOf(">")+1);
					}
				}
				if (tempString2 != null && !tempString2.isEmpty()) {
					createImage(tempString2);
					result.add(tempString2);
				} else if (tempString1 != null && !tempString.isEmpty()){
					createImage(tempString2);
					result.add(tempString1.trim());
				}
				if (tempString != null && !tempString.isEmpty() && !isCommentOpened) {
					createImage(tempString2);
					result.add(tempString.trim());
				}
			}
		}
		results.put("text", result);
		results.put("title", title);
		return results;
}

	private static void createImage(String tempString2) {
		if (imageFound == true) {
			imageBean.setTextAfterImage(tempString2);
			imageFound = false; 		//switch off the boolean after capturing the text after image.
		}
	}
	/**
	 * This method checks if the input line belongs to a comment. It covers multiline comment as well.
	 * @param tempString
	 * @return
	 */
	private static boolean isComment(String tempString) {
		boolean result = false;
		if (tempString.startsWith("<!")) {
			isCommentOpened = true;
			result = true;
		} 
		if (tempString.endsWith("-->")) {
			isCommentOpened = false;
			result = true;
		}
		return result;
	}
	
	private static void checkBodyTag(String tempString) {
		if (tempString.startsWith("<body")) {
			isBodyOpened = true;
		} else if (tempString.endsWith("/body>")) {
			isBodyOpened = false;
		}
	}
	
	private static void checkScriptTag(String tempString) {
		if (tempString.startsWith("<script")) {
			isScriptOpened = true;
		} 
		if (tempString.endsWith("/script>")) {
			isScriptOpened = false;
		}
	}
	
	public static ArrayList<String> removeStopWords (ArrayList<String> inputWord) {
		ArrayList<String> result = new ArrayList<String>();
		boolean isAStopWord = false;
		String stopWords = "a able about above abst accordance according accordingly across act actually added adj affected affecting affects after afterwards again against ah all almost alone along already also although always am among amongst an and announce another any anybody anyhow anymore anyone anything anyway anyways anywhere apparently approximately are aren arent arise around as aside ask asking at auth available away awfully b back be became because become becomes becoming been before beforehand begin beginning beginnings begins behind being believe below beside besides between beyond biol both brief briefly but by c ca came can cannot can't cause causes certain certainly co com come comes contain containing contains could couldnt d date did didn't different do does doesn't doing done don't down downwards due during e each ed edu effect eg eight eighty either else elsewhere end ending enough especially et et-al etc even ever every everybody everyone everything everywhere ex except f far few ff fifth first five fix followed following follows for former formerly forth found four from further furthermore g gave get gets getting give given gives giving go goes gone got gotten h had happens hardly has hasn't have haven't having he hed hence her here hereafter hereby herein heres hereupon hers herself hes hi hid him himself his hither home how howbeit however hundred i id ie if i'll im immediate immediately importance important in inc indeed index information instead into invention inward is isn't it itd it'll its itself i've j just k keep keeps kept kg km know known knows l largely last lately later latter latterly least less lest let lets like liked likely line little 'll look looking looks ltd m made mainly make makes many may maybe me mean means meantime meanwhile merely mg might million miss ml more moreover most mostly mr mrs much mug must my myself n na name namely nay nd near nearly necessarily necessary need needs neither never nevertheless new next nine ninety no nobody non none nonetheless noone nor normally nos not noted nothing now nowhere o obtain obtained  obviously of off often oh ok  okay old omitted on  once one  ones only  onto or ord other others otherwise ought our ours ourselves out outside over overall owing own p page pages part particular particularly past  per perhaps placed please plus poorly possible possibly potentially pp predominantly present previously primarily probably promptly proud provides put q que  quickly quite qv r ran rather rd re readily really recent recently ref refs regarding regardless regards related relatively research respectively resulted resulting results right run  s saidsame  saw say saying says sec section see seeing seem seemed seeming eems seen self selves  sent seven everal shall she  shed she'llshes  should shouldn't showshowed shown showns shows significant significantly  similar similarly since six slightly so  some somebody somehow someone somethan something sometime sometimes somewhat somewhere soon sorry specifically specified specify specifying still stop strongly sub substantially successfully such sufficiently suggest sup sure t take taken taking tell tends th than thank thanks thanx that that'll thats that've the their theirs them themselves then thence there thereafter thereby thered therefore therein there'll thereof therere theres thereto thereupon there've these they theyd they'll theyre they've think this those thou though thoughh thousand throug through throughout thru thus til tip to together too took toward towards tried tries truly try trying ts twice two u un under unfortunately unless unlike unlikely until unto up upon ups us use used useful usefully usefulness uses using usually v value various 've very via viz vol vols vs w want wants was wasnt way we wed welcome we'll went were werent we've what whatever what'll whats when  whence whenever where whereafter whereas whereby wherein wheres whereupon wherever  whether which while whim whither whowhod whoever whole  who'll whom whomever whos whose  why widely willing wish with within without wont words world would wouldnt www x y yes  yet you youd you\'ll your  youre yours yourself yourselves you\'ve  z zero";
		String[] stopWordSet = stopWords.split(" ");
		for (String input : inputWord) {
			for (String stopWord : stopWordSet) {
				if (input.equalsIgnoreCase(stopWord)) {
					isAStopWord = true;
					break;
				} else {
					isAStopWord = false;
				}
			}
			if (!isAStopWord) {
				result.add(input);
			}
		}
		return result;
	}
	
	public static void persistFilteredWordsAndTitle (int docId, int depth, int maxNoOfDocs, 
			ArrayList<String> stemmedWordList, String title, boolean isRecover, 
			CrawlerRecoveryBean crawlerRecover,	ArrayList<String> notVisitedLinks, int seedDocId, int noOfDocsToBeVisited) {
		if (isRecover) {
			int persistedTermsCount = FeaturesDB.getTermsCount(docId);
			if (persistedTermsCount != stemmedWordList.size()) {
				//above condition: it means that either the webpage has been updated after the resume of the crawler or all the words were not persisted before the crawler terminated abruptly.
				FeaturesDB.deleteFeatures(docId);	//first empty feautres table for the docid and then insert all the words for the docid
				boolean crawlComplete = false;
				ArrayList<String> distinctUrlList = getDistinctUrlList(notVisitedLinks);
				persistCrawlerRecovery(seedDocId, docId, depth, maxNoOfDocs, crawlComplete, distinctUrlList, noOfDocsToBeVisited);
				persistFeatures(docId, stemmedWordList, title);
			}
		} else {
			boolean crawlComplete = false;
			ArrayList<String> distinctUrlList = getDistinctUrlList(notVisitedLinks);
			persistCrawlerRecovery(seedDocId, docId, depth, maxNoOfDocs, crawlComplete, distinctUrlList, noOfDocsToBeVisited);
			persistFeatures(docId, stemmedWordList, title);
		}	
	}

	/*** @param seedDocId 
	 * @param crawlComplete 
	 * @param notVisitedLinks 
	 * @param noOfDocsToBeVisited 
	 * @param docId, @param depth,@param maxNoOfDocs*/
	private static void persistCrawlerRecovery(int seedDocId, int docId, int depth, 
			int maxNoOfDocs, boolean crawlComplete, ArrayList<String> notVisitedLinks, int noOfDocsToBeVisited) {
		CrawlerRecoveryDB.crawlerRecoveryInsert(seedDocId, docId, depth, maxNoOfDocs, crawlComplete);
		for (int i = 0; i < noOfDocsToBeVisited; i++) {
			String notVisitedLink = notVisitedLinks.get(i);
			int linkId = DocumentsDB.getDocId(notVisitedLink);
			int nextDepth = depth - 1;
			CrawlerRecoveryDB.crawlerRecoveryInsert(seedDocId, linkId, nextDepth, maxNoOfDocs, crawlComplete);
		}
	}

	/**@param docId,@param stemmedWordList,@param title*/
	private static void persistFeatures(int docId,
			ArrayList<String> stemmedWordList, String title) {
		int count;
		for (String word : stemmedWordList) {
			count = 0;
			for (String tempWord : stemmedWordList) {
				if (word.equalsIgnoreCase(tempWord)) {
					count++;
				}
			}
			if(word != null && !word.isEmpty()){
			FeaturesDB.featuresInsert(docId, word, count);
			}
		}
		System.out.println("Words/Terms inserted in Features table.");
		//Insert title
		FeaturesDB.featuresInsert(docId, title, 1);
	}
	
	public static ArrayList<String> removejunkWords (ArrayList<String> stemmedWord){
		ArrayList<String> result = new ArrayList<String>();
		boolean isAJunkWord = false;
		String junkWords = "! @ # $ % ^ & * ( ) _ + - = / \" : ; ' | . ?";
		//String pattern ="\\d{4}-[01]\\d-[0-3]\\d";
		String[] junkWordSet = junkWords.split(" ");
		for (String input : stemmedWord) {
			for (String stopWord : junkWordSet) {
				if (input.contains(stopWord)) {
					isAJunkWord = true;
					break;
				} else {
					isAJunkWord = false;
				}
			}
			if (!isAJunkWord) {
				result.add(input);
			}
		}
		//final result that without junkwords.
		return result;
	}

	public static void persistLinks(int fromDocId, ArrayList<String> outgoingLinks) {
		for (String link : outgoingLinks) {
			int toDocId = DocumentsDB.getDocId(link);
			if (toDocId != 0) {
				LinksDB.linksInsert(fromDocId, toDocId);		
			}
		}
		
	}

	/**
	 * This method persists the documents table with crawled date as null as these URLs will be crawled later.
	 * @param outgoingLinks
	 * @param noOfDocsToBeVisited 
	 * @throws MalformedURLException
	 */
	public static void persistOutgoingLinks(ArrayList<String> outgoingLinks, int noOfDocsToBeVisited) throws MalformedURLException {
		ArrayList<String> distinctUrlList = getDistinctUrlList(outgoingLinks);
		//int sizeOfDistinctUrlList=distinctUrlList.size();
		for (int i = 0; i < noOfDocsToBeVisited; i++) {
		//for (int i = 0; i < sizeOfDistinctUrlList; i++) {
			String outgoingLink = distinctUrlList.get(i);
			DocumentsDB.docInsert(new URL(outgoingLink), null);
		}
	}

	/** This method inserts into status table for breadth first implementation. 
	 * @param seedDocId, @param docId, @param outgoingLinks,@param depth,@param noOfDocsToBeVisited,*/
	
	public static void persistStatus(int seedDocId, int docId,ArrayList<String> outgoingLinks, int depth, int noOfDocsToBeVisited) {
		boolean crawlCompleteStatus = false;
		int toDocId = 0;
		ArrayList<String> distinctUrlList = getDistinctUrlList(outgoingLinks);
		//int sizeOfDistinctUrlList=distinctUrlList.size();
		for (int i = 0; i < noOfDocsToBeVisited; i++) {
		//for (int i = 0; i < sizeOfDistinctUrlList; i++) {
			String outgoingLink = distinctUrlList.get(i);
			toDocId = DocumentsDB.getDocId(outgoingLink);
			if (DocumentsDB.getCrawledDate(toDocId) != null) {
				crawlCompleteStatus = true;
			}
			StatusDB.statusInsert(seedDocId, docId, toDocId, depth, crawlCompleteStatus);
		}
	}

	/** @param outgoingLinks,* @return */
	public static ArrayList<String> getDistinctUrlList(ArrayList<String> outgoingLinks) 
	{
		Set<String> s = new HashSet<String>(outgoingLinks);
		Iterator setItr = s.iterator();
		ArrayList<String> distinctUrlList = new ArrayList<String>();
		while (setItr.hasNext()) {
			String url = (String)setItr.next();
			distinctUrlList.add(url);
		}
		return distinctUrlList;
	}

	public static void updateStatus(int nextDocId) {
		StatusDB.updateStatusTableCompleteColumn(nextDocId);
		
	}
	
	public static void updateRecoveryVisitedStatus () {
		
	}

	public static void udpateCrawlerRecovery(int docId, boolean crawlComplete) {
		CrawlerRecoveryDB.crawlerRecoveryUpdate(docId, crawlComplete);
	}

	public static void persistDocLanguage(int docId, String docLanguage) {
		DocumentsDB.docLanguagePersist(docId, docLanguage);
	}

	public static void persistImageDB(ArrayList<ImageTextBean> imageBeanList, int docId) {
		for (ImageTextBean imageBean : imageBeanList) {
			int imageId = ImageDB.ImageInsert(imageBean, docId);
			persistImageFeature(imageBean, imageId);
		}
	}

	private static void persistImageFeature(ImageTextBean imageBean, int imageId) {
		double lambda = 0.3;
		String textBeforeFromBean = imageBean.getTextBeforeImage();
		String textAfterFromBean = imageBean.getTextAfterImage();

		if (textBeforeFromBean != null && !textBeforeFromBean.isEmpty()){
			String[] textBeforeList = textBeforeFromBean.split(" ");
			for (int i = textBeforeList.length; i >= 1; i--) {
				String word = (String)textBeforeList[i-1];
				double expScore = lambda*Math.exp(-(lambda * i));
				if(word != null && !word.isEmpty()){
					ImageFeaturesDB.persistImageFeature(word,imageId, expScore);
				}
			}
		}
		if (textAfterFromBean != null && !textAfterFromBean.isEmpty()){
			String[] textAfterList = textAfterFromBean.split(" ");
			for (int j = 1; j <= textAfterList.length; j++) {
				String word = (String)textAfterList[j-1];
				double expScore = lambda*Math.exp(-(lambda * j));
				if (word != null && !word.isEmpty()){
					ImageFeaturesDB.persistImageFeature(word,imageId, expScore);
				}
			}
		}
	}

	public static void getTextBeforeImage(ArrayList<ImageTextBean> imageBeanList, String webText) {
		
		for (ImageTextBean imageTextBean : imageBeanList) {
			String textBefore = "";
			int noOfSpace = 0;
			boolean periodPresent = false;
			boolean tabPresent = false;
			boolean extraSpacePresent = false;
			boolean startPointReached = false;

			String textAfter = imageTextBean.getTextAfterImage();

			if (textAfter != null && !textAfter.isEmpty()){
				
				int indexOfTextAfter = webText.indexOf(textAfter)-3;

				if (indexOfTextAfter != -1) {
					while (indexOfTextAfter >= 0) {
						char currentCharacter = webText.charAt(indexOfTextAfter);
						if (currentCharacter == '.') {
							periodPresent = true;
							break;
						} else if (currentCharacter =='\t') {
							tabPresent = true;
							break;
						} else if (noOfSpace == 2) {
							extraSpacePresent = true;
							break;
						} else if (indexOfTextAfter == 0) {
							startPointReached = true;
						}

						if (currentCharacter == ' ') {
							noOfSpace++;
						} else {
							noOfSpace = 0;
						}

						indexOfTextAfter--;
					}
				}

				if (periodPresent) {
					textBefore = webText.substring(indexOfTextAfter+2, webText.indexOf(textAfter)-2);
				} else if (tabPresent) {
					textBefore = webText.substring(indexOfTextAfter+1, webText.indexOf(textAfter)-2);
				} else if (extraSpacePresent) {
					textBefore = webText.substring(indexOfTextAfter+3, webText.indexOf(textAfter)-2);
				} else if (startPointReached) {
					textBefore = webText.substring(0,webText.indexOf(textAfter)-2);
				}
				imageTextBean.setTextBeforeImage(textBefore);
			}
		}
	}
	
	public static void persistWebText(int docID, String webText) {
		DocumentsDB.docTextInsert(docID, webText);
	}
	
	public static void persistShingles(int docID, ArrayList<String> shinglesList) {
		
		for ( int i = 0; i < shinglesList.size(); i++)
		{
			String shingles = shinglesList.get(i);
			ShinglesDB.shinglesInsert(docID, shingles);
		}
	}
	
	public static void persistJaccard() {
		ArrayList<Integer> docids = DocumentsDB.getAllDocId();
		int count = 0;
		for (int d1 : docids) {
			for (int d2 : docids) {
				if (d1 != d2) {
					if (++count > 10000) {
						break;
					}
					if (!JaccardDB.isPairPresent(d1, d2)){
						JaccardDB.persistJaccard(d1, d2);
					}
				}
			}
			if (count > 10000) {
				break;
			}
		}
		System.out.println("JaccardCoefficent Calculation done and value inserted!!");
	}
	
}
