package com.wse.helpers;

import java.util.ArrayList;

public class LanguageClassifier {
	public static String classifier ( ArrayList<String> wordList ){
		String languageFlag = "DE"; //Defaults is 1 for English. Set it to 0 for German
		double stopwordMatchPercentage = countStopWords(wordList);
		if ( stopwordMatchPercentage >= 10){
			languageFlag = "EN";
			return languageFlag;
		}
		else{
			return languageFlag;
		}
	}
	
	public static double countStopWords (ArrayList<String> wordList) {
		double stopwordMatchCount = 0;
		double stopWordsPercent = 11;
		String stopWords = "a an the and but if or because as until while i'm you're he's she's it's we're they're i've you've we've they've i'd you'd he'd she'd we'd they'd i'll you'll he'll she'll we'll they'll isn't aren't wasn't weren't hasn't haven't hadn't doesn't don't didn't won't wouldn't shan't shouldn't can't cannot couldn't mustn't let's that's who's what's here's there's when's where's why's how's of at by for with about against between into through during before after above below to from up down in out on  off over under again further then once here there when where why how all any both each few more most other some such no nor not only own same so than too very one  every least less many now ever never say says said also get go goes just made make put see seen whether like well back even still way take since another however two three four five first second new old high long i me  my myself we us our ours ourselves you your yours yourself yourselves he him his himself she her hers herself it its itself they them their theirs themselves what which who whom this that these those am is are was were be been being have has had having do does did doing could should would ought";
		String[] stopWordSet = stopWords.split(" ");
		for (String stopWord :  stopWordSet) {
			for (String input : wordList) {
				if (input.equalsIgnoreCase(stopWord)) {
					stopwordMatchCount ++;
					//System.out.println("Stopword detected :" + stopWord);
				} 
			}
		}
		if ( wordList.size() != 0 ){
			stopWordsPercent = (stopwordMatchCount/wordList.size())*100;
			//System.out.println("Stopword count is: "+ stopWordsPercent);
			return stopWordsPercent;
		}
		return stopWordsPercent;
	}
}
