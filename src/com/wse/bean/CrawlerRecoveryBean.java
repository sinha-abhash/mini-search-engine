package com.wse.bean;

import java.util.ArrayList;
import java.util.HashMap;

public class CrawlerRecoveryBean {
	private int seedDocId;
	private int docId;
	private int depth;
	private int noOfDocs;
	private boolean crawlComplete;
	private String crawlingUrl;
	
	public int getSeedDocId() {
		return seedDocId;
	}
	public void setSeedDocId(int seedDocId) {
		this.seedDocId = seedDocId;
	}
	public int getDocId() {
		return docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public int getNoOfDocs() {
		return noOfDocs;
	}
	public void setNoOfDocs(int noOfDocs) {
		this.noOfDocs = noOfDocs;
	}
	public boolean isCrawlComplete() {
		return crawlComplete;
	}
	public void setCrawlComplete(boolean crawlComplete) {
		this.crawlComplete = crawlComplete;
	}
	public String getCrawlingUrl() {
		return crawlingUrl;
	}
	public void setCrawlingUrl(String url) {
		this.crawlingUrl = url;
	}
}
