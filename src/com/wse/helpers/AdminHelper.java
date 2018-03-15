package com.wse.helpers;

import com.wse.postgresdb.SearchEnginesDB;

public class AdminHelper {
	
	public static void admin(String action, String seURL){
		
		int seid;
		int isSEAvailabile = AdminHelper.isSEPresent(seURL);
		System.out.println("SE availability in DB: "+ isSEAvailabile);
		String add = "ADD";
		String remove = "REMOVE";
		
		if (action.equalsIgnoreCase(add)){
			if (isSEAvailabile == 0){
				seid = SearchEnginesDB.persistSEurl(seURL);
				System.out.println("SEID generated for the SE is: "+ seid);
			}else{
				System.out.println("SE already present in DB.");
			}
		}else if (action.equalsIgnoreCase(remove)){
			if( isSEAvailabile == 1){
				SearchEnginesDB.removeSE(seURL);
			}
		}else{
			if( isSEAvailabile == 1){
				SearchEnginesDB.disableSE(seURL);
			}
		}
	}

	private static int isSEPresent(String seURL) {
		int isAvailabile;
		isAvailabile = SearchEnginesDB.isSEAdded(seURL);
		return isAvailabile;
	}
	
}
