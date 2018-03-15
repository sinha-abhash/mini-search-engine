package com.wse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Date;
import java.util.Calendar;

import com.wse.bean.CrawlerRecoveryBean;
import com.wse.helpers.CrawlerHelper;
import com.wse.postgresdb.DbTableCreation;
import com.wse.postgresdb.DocumentsDB;

public class Crawler {
	private static URL seedURL = null; 
	private static int seedDocId = 0;
	
	public static void crawl (int depth, int maxNoOfDocs, URL url, boolean domainLeave, boolean isRecovery, String seedHost) throws IOException {
		CrawlerRecoveryBean crawlerRecover = null;
		
		if (isRecovery) {
			crawlerRecover = CrawlerHelper.getRecoveryData(url); //seed url
			url = new URL(crawlerRecover.getCrawlingUrl());
			depth = crawlerRecover.getDepth();
			maxNoOfDocs = crawlerRecover.getNoOfDocs();
			
		}
		System.out.println("Starting to crawl: " + url.toString());
		if (depth >= 0) {
			BufferedReader test = new BufferedReader(new InputStreamReader(url.openStream()));
			Calendar currenttime = Calendar.getInstance();
			Date crawled_on_date = new Date((currenttime.getTime()).getTime());
			int docId = DocumentsDB.docInsert(url, crawled_on_date);
			if (seedURL.toString().equalsIgnoreCase(url.toString())) {
				seedDocId = docId;
			}
			if (docId != 0) {	//if doc has not already been parsed
				Indexer.createIndex(depth, maxNoOfDocs, domainLeave, test, docId, isRecovery, crawlerRecover, seedDocId, seedHost);
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		URL url = new URL("https://de.wikipedia.org/wiki/Koog");
		seedURL = url;
		int depth = 2;
		int maxNoOfDocs = 4;
		boolean domainLeave = false;
		boolean isRecovery = false;
		DbTableCreation.tableDocuments();//Creates necessary tables if they do not exists.
		String seedHost = url.getHost();
		System.out.println("host = " + seedHost);
		
		crawl(depth, maxNoOfDocs, url, domainLeave, isRecovery, seedHost);
	}
}
