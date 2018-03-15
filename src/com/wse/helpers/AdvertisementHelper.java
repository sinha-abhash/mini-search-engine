package com.wse.helpers;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wse.bean.AdvertisementBean;
import com.wse.bean.NgramsBean;
import com.wse.postgresdb.AddFeaturesDB;
import com.wse.postgresdb.CustomerDB;

public class AdvertisementHelper {
	public static ArrayList<NgramsBean> gramsList;
	public static ArrayList<Integer> termOcc; 
	public static int index = 0;
	
	public static ArrayList<AdvertisementBean> getAdvertisementList (String query) {
		gramsList = new ArrayList<NgramsBean>();
		termOcc = new ArrayList<Integer>();
		ArrayList<NgramsBean> nGramsList = AddFeaturesDB.getNgrams();
		for (NgramsBean gramsBean : nGramsList) {
			isGramEligible(query, gramsBean);
		}
		ArrayList<AdvertisementBean> adBeanList = getTopFourAds();
		return adBeanList;
	}

	private static ArrayList<AdvertisementBean> getTopFourAds() {
		ArrayList<AdvertisementBean> ads = new ArrayList<AdvertisementBean>();
		sortList();
		if (gramsList.size() > 4) {
			for (int i = gramsList.size()-1; i > gramsList.size()-5; i--){
				NgramsBean ngramobj = gramsList.get(i);
				int custid = ngramobj.getCustid();
				ads.addAll(CustomerDB.getAddvertisement(custid));
			}
		} else {
			for (NgramsBean ngramobj: gramsList){
				int custid = ngramobj.getCustid();
				ads.addAll(CustomerDB.getAddvertisement(custid));
			}
		}
		return ads;
	}

	private static void sortList() {
		ArrayList<Integer> tempQueryOcc = new ArrayList<Integer>();
		ArrayList<NgramsBean> tempGramsList = new ArrayList<NgramsBean>();
		ArrayList<Integer> processingList = new ArrayList<Integer>(termOcc);
		ArrayList<NgramsBean> nGramProcessingList = new ArrayList<NgramsBean>(gramsList);
		for (int i = 0; i < termOcc.size(); i++) {
			if (processingList.size() < 0) {
				break;
			}
			int min = getMin(processingList);
			processingList.remove(new Integer(min));
			tempQueryOcc.add(min);
			tempGramsList.add(nGramProcessingList.get(index));
			nGramProcessingList.remove(index);
		}
		gramsList = tempGramsList;
		termOcc = tempQueryOcc;
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
	
	private static void isGramEligible(String query, NgramsBean gramsBean) {
		String gram = gramsBean.getNgrams();
		int count = 0;
		String[] gramSplit = gram.split(" ");
		String pattern = "";
		for (String word : gramSplit) {
			pattern += "("+word+")??\\s*";
		}
		Pattern p = Pattern.compile(pattern);
		String[] querySplit = query.split(" ");
		String temp = "";
		for (String word : querySplit) {
			Matcher m = p.matcher(word);
			if (m.matches()) {
				temp += word + " ";
				count++;
			}
		}
		temp = temp.trim();
		Matcher m1 = p.matcher(temp);
		if (count > 0 && m1.matches()) {
			termOcc.add(count);
			gramsList.add(gramsBean);
		}
	}

}
