package com.wse.helpers;

import java.util.ArrayList;

import com.wse.bean.ConjDisBean;
import com.wse.bean.MetaSEBean;
import com.wse.bean.ResultListBean;
import com.wse.bean.SEStatBean;
import com.wse.postgresdb.MergeResultsDB;
import com.wse.postgresdb.SearchEnginesDB;
import com.wse.postgresdb.StatisticsDB;

public class MetaSearchEngineHelper {
	public static ArrayList<String> seIPAddress;
	public static ArrayList<MetaSEThreading> threads = new ArrayList<MetaSEThreading>();

	public static int getNoOfSearchEngine(int searchEnginePercent, String query) {
		seIPAddress = new ArrayList<String>();
		int noOfSearchEngine = 0;
		String[] queryWordList = query.split(" ");
		for (String word : queryWordList) {
			ArrayList<String> ipAddressList = StatisticsDB.getSeIPaddress(word);
			if (!ipAddressList.isEmpty()) {
				seIPAddress.addAll(ipAddressList);
			}
		}
		if (seIPAddress.isEmpty()) {
			seIPAddress = SearchEnginesDB.getActiveSE();
		}
		int sizeSE = seIPAddress.size();
		noOfSearchEngine = (int)((searchEnginePercent*sizeSE)/100);
		return noOfSearchEngine;
	}

	public static void startSEThreading(int noOfSearchEngine, String query, int score, int k) {
		for (int i = 0; i < noOfSearchEngine; i++) {
			MetaSEThreading thread = new MetaSEThreading(seIPAddress.get(i) + "/is-project/json?query=" + query + "&k=" + k + "&score=" + score, seIPAddress.get(i));
			/*threads.add(thread);
			thread.start();*/
			thread.getResultFromIP();
		}
	}

	public static ArrayList<String> getSearchEngine() {
		return seIPAddress;
	}

	public static void joinThreads() throws InterruptedException {
		for(MetaSEThreading thread : threads) {
			//thread.join();
		}
	}

	public static void persistStatistics() {
		ArrayList<MetaSEBean> seResultBeanList = MetaSEThreading.getResult();

		if (!seResultBeanList.isEmpty()){
			for (MetaSEBean metaSEobj: seResultBeanList){

				int seid = SearchEnginesDB.getSEID(metaSEobj.getSeIPaddress());
				double cw = metaSEobj.getCw();
				SearchEnginesDB.updateCW(seid,cw);

				ArrayList<SEStatBean> statObjList = metaSEobj.getStat();

				if (!statObjList.isEmpty()){
					for (SEStatBean statObj:statObjList ){
						String term = StatisticsDB.insertStatistics(seid, statObj);
						StatisticsDB.updateTIStatistics(term, seid);
					}
				}
			}
		}
	}
	
	public static ArrayList<ConjDisBean> getResultsToDisplay(int k) {
		ArrayList<ConjDisBean> results = new ArrayList<ConjDisBean>();
		ArrayList<MetaSEBean> seResultBeanList = MetaSEThreading.getResult();
		for (MetaSEBean seBean : seResultBeanList) {
			int seid = SearchEnginesDB.getSEID(seBean.getSeIPaddress());
			String ipAddress = seBean.getSeIPaddress();
			for (SEStatBean statBean : seBean.getStat()) {
				String term = statBean.getTerm();
				for (ResultListBean resultBean : seBean.getResultURLList()) {
					String urlResult = resultBean.getUrl();
					double score = resultBean.getScore();
					results = MergeResultsDB.insertMergeResults(seid, term, ipAddress, urlResult, score, k);
				}
			}
		}
		return results;
	}
}
