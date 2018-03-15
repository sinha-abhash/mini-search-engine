package com.wse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Shingling {
	
	public static ArrayList<String> createShingles (String webText) {
		//String webText = "a rose is a rose is a rose";
		int k = 4;
		ArrayList<String> shinglingText = getShinglingSet(webText, k);
		return shinglingText;
	}

	public static ArrayList<String> getShinglingSet(String webText, int k) {
		ArrayList<String> result = new ArrayList<String>();
		String[] words = webText.split(" ");
		ArrayList<String> wordsWithoutJunk = getWordsWithoutJunk(words);
		for (int i = 0; i < wordsWithoutJunk.size() && wordsWithoutJunk.size() >= k; i++) {
			String shingling = "";
			for (int j = 0; j < k; j++) {
				shingling += wordsWithoutJunk.get(i + j) + " ";
			}
			shingling = shingling.trim();
			result.add(shingling);
			if ((i+k) == wordsWithoutJunk.size()) {
				break;
			}
		}
		result = getUniqueSet(result);
		return result;
	}

	private static ArrayList<String> getUniqueSet(ArrayList<String> list) {
		ArrayList<String> result = new ArrayList<String>();
		Set<String> set = new HashSet<String>(list);
		Iterator<String> itr = set.iterator();
		while (itr.hasNext()) {
			result.add(itr.next());
		}
		return result;
	}

	private static ArrayList<String> getWordsWithoutJunk(String[] words) {
		ArrayList<String> result = new ArrayList<String>();
		String junkCharacters = "! @ # $ % ^ & * ( ) _ + - = / \" : ; ' | . ? , \\t";
		String[] junkCharacterSet = junkCharacters.split(" ");
		for (String word : words) {
			for (String ch : junkCharacterSet) {
				if (word.contains(ch)) {
					word = word.replace(ch, "");
					
				}
			}
			result.add(word);
		}
		return result;
	}

}
