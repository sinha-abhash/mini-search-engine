package com.wse.ui;

import java.util.ArrayList;

import com.wse.bean.TermBean;
import com.wse.postgresdb.CheckTypoDB;

public class SpellChecker {

	public static ArrayList<String> typoChecker(ArrayList<String> keywords) {
		
		//TermBean queryTerm = new TermBean();
		ArrayList<TermBean> queryTerm = new ArrayList();
		
		for ( String term : keywords ){

			TermBean resultTerm = CheckTypoDB.termPresent(term);
			resultTerm.setTerm(term);

			//System.out.println(resultTerm.getTerm());
			//System.out.println(resultTerm.getTerm_doc_frequency());

			int term_doc_frequency = resultTerm.getTerm_doc_frequency();

			if ( term_doc_frequency != 0 ){
				//add the function  to get the nearest
				TermBean alternateResult = CheckTypoDB.alternateQueryTerm(term);

				int alternateTermDocFrequency = alternateResult.getTerm_doc_frequency();
				String alternateTerm = alternateResult.getTerm();
				System.out.println("alternate term found id: "+ alternateTerm);
				//TODO get the term from termbean object compare with the term . if the term is not similar to the alternate term then add to queryterm
				if (!term.equalsIgnoreCase(alternateTerm)){
						queryTerm.add(alternateResult);
				}
			}else {
				//Term is not present then, Call the function to get similar term.
				TermBean similarTerm = CheckTypoDB.getSimilarTerm(term);
				int Term_doc_frequency_Of_Similar_Term = resultTerm.getTerm_doc_frequency();
				String similartermfound = similarTerm.getTerm();
				if(!term.equalsIgnoreCase(similartermfound)){
					queryTerm.add(similarTerm);
				}
			}
		}
		
		ArrayList<String> alternateTerm = new ArrayList();
		for ( TermBean obj : queryTerm  ){
			alternateTerm.add(obj.getTerm());
		}
		return alternateTerm;
	}
}
