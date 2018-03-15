package com.wse.helpers;

import java.util.ArrayList;

import com.wse.postgresdb.AddFeaturesDB;
import com.wse.postgresdb.CustomerDB;

public class CustomerRegistrationHelper {

	public static boolean persistCustDetails(String nGrams, String addURL,
			String addText, int budget, int moneyPerClick) {

		boolean status = false;
		ArrayList<String> ngramList = new ArrayList<>();
		if(budget >= moneyPerClick){
			int clicksleft = budget / moneyPerClick ;
			ngramList = getNgramList(nGrams);
			if (!ngramList.isEmpty()){
				int custId = CustomerDB.customerInsert(addURL, addText, budget, moneyPerClick, clicksleft);
				if ( custId != 0 ){
					for(String ngram: ngramList){
						AddFeaturesDB.ngramsInsert(custId, ngram);
					}
					//Only once n-grams are inserted into the table then set status =true.
					status = true;
				}
			}
		}
		return status;
	}

	private static ArrayList<String> getNgramList(String nGrams) {
		
		//Sample:  {[Europe Germann],[informatik tu kl],[database research]}
		ArrayList<String> nGramsList = new ArrayList<>();
		String keyCap = "";
		String bracket= "[";
		
		if(nGrams.startsWith(bracket)){
			while ( nGrams.contains("[") ){
				if (nGrams.charAt(0) == ',') {
					nGrams = nGrams.substring(nGrams.indexOf(',')+1);
				}
				keyCap = nGrams.substring(1,nGrams.indexOf("]"));
				System.out.println(keyCap);
				nGramsList.add(keyCap);
				nGrams = nGrams.substring(nGrams.indexOf("]")+1);
			}
		}
		return nGramsList;
	}
	
	
}
