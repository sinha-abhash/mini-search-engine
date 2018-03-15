package com.wse.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wse.bean.ConjDisBean;
import com.wse.bean.ImageTextBean;
import com.wse.postgresdb.DocumentsDB;
import com.wse.postgresdb.FeaturesDB;

public class Snippets {

	public static final int maxLimit = 32;
	public static int termOccurrence = 0;
	public static int index = 0;
	
	public static void getSnippets(ArrayList<String> keyword, ArrayList<ConjDisBean> finalresults){
		termOccurrence = 0;
		String[] queryTermList = keyword.toArray(new String[keyword.size()]);
		String docText = "";
		String url = "";
		String snippet = "";
		if (queryTermList.length != 0) {
			for (int i = 0; i < finalresults.size(); i++){
				ConjDisBean obj = finalresults.get(i);
				url = obj.getUrl();
				docText = DocumentsDB.getDocText(url);
				if (docText != null && !docText.isEmpty()) {
					snippet = processSnippets(queryTermList, docText);
					snippet = snippet.toLowerCase();

					/*for (String term: queryTermList){	
						if (!snippet.contains(term)) {
							//snippet += "\n" + term + " not found.";
						}
					}*/
					if(snippet != null && !snippet.isEmpty()){
						ArrayList<String> termList = getQueryList(queryTermList);
						for (String term: termList){	
							term = term.toLowerCase();
							if (!snippet.contains(term)) {
								snippet += "\n" + term + " not found.";
							}
						}
					}

					obj.setSnippet(snippet);
				}
			}
		}
	}

	public static String processSnippets(String[] queryTermList, String docText ) {
		//String[] queryTermList = {"use", "wrote", "baba", "four"};
		//String docText = "I am supposed to methods in baba to count number of words in the sentence"
				//+ ". I wrote this use code and I am fix not quite sure why it doesn't work. No matter what I write, I only receive a count of 1 word. "
				//+ "If you could tell me how to fix what I four rather than give me a completely different  idea that would be great.";
		ArrayList<String> sentenceList = getSentenceList(docText);
		ArrayList<String> processingList = new ArrayList<String>();
		ArrayList<String> termSentenceList = null;
		ArrayList<String> snippetList = new ArrayList<String>();
		int queryLength = queryTermList.length;
		int wordsPerTerm = 0;
		if (queryLength >= 4) {
			processingList = getProcessingList(queryTermList);
			wordsPerTerm = maxLimit/4;
		} else {
			wordsPerTerm = (int)maxLimit/queryLength;
			processingList.addAll(getProcessingList(queryTermList,queryLength));
		}
		
		for (int i = 0; i < processingList.size(); i++) {
			termSentenceList = new ArrayList<String>();
			String term = processingList.get(i);
			termOccurrence = getTermOccurrence(docText, term);		//get the term occurrence in the whole text.
			for (int j = 0; j < termOccurrence; j++) {
				String termSentence = getTermSentence(sentenceList, term, j+1);
				termSentenceList.add(termSentence); 
			}
			snippetList.add(getSentenceWithMostQueryMatch(termSentenceList,processingList));
		}
		
		String snippetText = getSnippetText(snippetList, processingList, wordsPerTerm);
		System.out.println(snippetText);
		
		return snippetText;
	}

	private static String getSnippetText(
			ArrayList<String> snippetList, ArrayList<String> processingList, int wordsPerTerm) {
		ArrayList<String> sentenceList = new ArrayList<String>();
		ArrayList<Integer> queryOcc = new ArrayList<Integer>();
		String result = "";
		
		//count the number of terms in each sentence
		for (String sentence : snippetList) {
			int termCount = 0;
			for (String term : processingList) {
				if (sentence.contains(term)) {
					termCount++;
				}
			}
			sentenceList.add(sentence);
			queryOcc.add(termCount);
		}
		
		sortList(sentenceList,queryOcc);
		getUniqueList(sentenceList, queryOcc);
		
		for (int i = 0; i < sentenceList.size(); i++) {
			String sentence = sentenceList.get(i);
			if (sentence.isEmpty()) {
				continue;
			}
			int numberOfTerms = queryOcc.get(i);
			int wordsInSentence = wordsPerTerm * numberOfTerms;
			String[] words = sentence.split(" ");
			ArrayList<String> wordsList = getWordsList(words);
			if (wordsList.size() > wordsInSentence) {
				if (numberOfTerms == 1) {
					String termPresent = getTermPresent(wordsList, processingList);
					if (termPresent.isEmpty()) {
						continue;
					}
					int indexOfTerm = wordsList.indexOf(termPresent);
					int diff = indexOfTerm - wordsInSentence + 1;
					if (diff > 0) {
						result += getTextFrom(wordsList, diff, indexOfTerm) + "...";
					} else {
						result += getText(words, wordsInSentence) + "...";
					}
				} else {
					result += getText(words, wordsInSentence) + "...";
				}
				
			} else {
				result += sentence + "...";
			}
		}
		
		return result;
	}

	private static String getTextFrom(ArrayList<String> wordsList, int diff,
			int indexOfTerm) {
		String result = "";
		for (int i = diff; i <= indexOfTerm; i++) {
			result += wordsList.get(i) + " ";
		}
		return result.trim();
	}

	private static String getTermPresent(ArrayList<String> wordsList,
			ArrayList<String> processingList) {
		String result = "";
		for (String term : processingList) {
			if (wordsList.contains(term)) {
				result = term;
				break;
			}
		}
		return result;
	}

	private static ArrayList<String> getWordsList(String[] words) {
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < words.length; i++) {
			result.add(words[i]);
		}
		return result;
	}

	private static String getText(String[] words, int wordsInSentence) {
		String result = "";
		for (int i = 0; i < wordsInSentence; i++) {
			result += words[i] + " ";
		}
		return result.trim();
	}

	private static void getUniqueList(ArrayList<String> sentenceList,
			ArrayList<Integer> queryOcc) {
		int match;
		for (int i = 0; i < sentenceList.size(); i++) {
			match = 0;
			for (int j = 0; j < sentenceList.size(); j++) {
				if (sentenceList.get(i).equalsIgnoreCase(sentenceList.get(j))) {
					match++;
				}
				if (match > 1) {
					match = 1;		//to allow only one occurrence of the element in the list.
					sentenceList.remove(j);
					queryOcc.remove(j);
				}
			}
		}
	}

	private static void sortList(ArrayList<String> sentenceList,
			ArrayList<Integer> queryOcc) {
		ArrayList<Integer> tempQueryOcc = new ArrayList<Integer>();
		ArrayList<String> tempSentenceList = new ArrayList<String>();
		ArrayList<Integer> processingList = new ArrayList<Integer>(queryOcc);
		ArrayList<String> sentenceProcessing = new ArrayList<String>(sentenceList);
		for (int i = 0; i < queryOcc.size(); i++) {
			int min = getMin(processingList);
			processingList.remove(new Integer(min));
			tempQueryOcc.add(min);
			tempSentenceList.add(sentenceProcessing.get(index));
			sentenceProcessing.remove(index);
		}
		sentenceList = tempSentenceList;
		queryOcc = tempQueryOcc;
	}

	private static int getMin(ArrayList<Integer> processingList) {
		index = 0;
		int result = processingList.get(0);
		for (int i = 0; i < processingList.size(); i++) {
			Integer x = processingList.get(i);
			if (x < result) {
				result = x;
				index = i;
			}
		}
		return result;
	}

	private static String getSentenceWithMostQueryMatch(
			ArrayList<String> termSentenceList, ArrayList<String> processingList) {
		String result = "";
		int maxCount = 0;
		for (String termSentence : termSentenceList) {
			int termCount = 0;
			for (String term : processingList) {
				if (termSentence.contains(term)) {
					termCount++;
				}
			}
			if (maxCount < termCount) {
				maxCount = termCount;
				result = termSentence;
			} 
		}
		return result;
	}

	/**
	 * This method returns the sentence corresponding to the occurrence of the term.
	 * Means, for 1st occurrence, 1st sentence having the term, for 2nd occurrence, 2nd sentence having the term.
	 * @param sentenceList
	 * @param term
	 * @param j
	 * @return
	 */
	private static String getTermSentence(ArrayList<String> sentenceList, String term, int j) {
		String result = "";
		int occurrenceTracker = 0;
		for (String sentence : sentenceList) {
			if (sentence.contains(term)) {
				occurrenceTracker++;
			}
			if (occurrenceTracker == j) {
				result = sentence;
				int noOfTermsInSentence = getTermOccurrence(sentence, term);		//get the number of terms in the current sentence.
				termOccurrence = termOccurrence - noOfTermsInSentence+1;		//do this to reduce the termOccurrence loop in previous method as we found more than one
															//occurrence of the term in the current sentence itself
				break;
			}
			
		}
		return result;
	}

	private static ArrayList<String> getSentenceList(String docText) {
		ArrayList<String> result = new ArrayList<String>();
		Pattern pattern = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE | Pattern.COMMENTS);
		Matcher matcher = pattern.matcher(docText);
		while (matcher.find()) {
			result.add(matcher.group());
		}
		return result;
	}

	private static int getTermOccurrence(String docText, String term) {
		int result = 0;
		String[] termsInText = docText.split(" ");
		for (String eachTerm : termsInText) {
			if (eachTerm.contains(term)) {
				result++;
			}
		}
		return result;
	}

	private static Collection<? extends String> getProcessingList(
			String[] queryTermList, int queryLength) {
		ArrayList<String> results = new ArrayList<String>();
		for (String queryTerm : queryTermList) {
			results.add(queryTerm);
		}
		return results;
	}

	private static ArrayList<String> getProcessingList(String[] queryTermList) {
		ArrayList<String> results = new ArrayList<String>();
		for (int i = 0; i < 4; i++) {
			results.add(queryTermList[i]);
		}
		return results;
	}
	
	private static ArrayList<String> getQueryList(String[] queryTermList) {
		ArrayList<String> resultList = new ArrayList<String>();
		for (int i = 0; i < queryTermList.length; i++) {
			resultList.add(queryTermList[i]);
		}
		return resultList;
	}

}
