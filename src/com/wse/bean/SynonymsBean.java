package com.wse.bean;

import java.util.ArrayList;

public class SynonymsBean {
	
	private String word;
	private ArrayList<String> synonyms;
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public ArrayList<String> getSynonyms() {
		return synonyms;
	}
	public void setSynonyms(ArrayList<String> synonyms) {
		this.synonyms = synonyms;
	}
}