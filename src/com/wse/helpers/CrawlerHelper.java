package com.wse.helpers;

import java.net.URL;

import com.wse.bean.CrawlerRecoveryBean;
import com.wse.postgresdb.CrawlerRecoveryDB;
import com.wse.postgresdb.DocumentsDB;

public class CrawlerHelper {
	
	//Crawler recovery started from below method
	public static CrawlerRecoveryBean getRecoveryData (URL url) {
		CrawlerRecoveryBean recovery = CrawlerRecoveryDB.getRecovery(url.toString());
		return recovery;
	}
	
}
