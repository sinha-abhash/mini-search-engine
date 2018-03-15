package com.wse.bean;

import java.util.ArrayList;

public class MetaSEBean {
	private String seIPaddress;
	private ArrayList<ResultListBean> resultURLList;
	private long cw;
	private ArrayList<SEStatBean> stat;
	
	public long getCw() {
		return cw;
	}
	public void setCw(long cw) {
		this.cw = cw;
	}
	public ArrayList<SEStatBean> getStat() {
		return stat;
	}
	public void setStat(ArrayList<SEStatBean> stat) {
		this.stat = stat;
	}
	public String getSeIPaddress() {
		return seIPaddress;
	}
	public void setSeIPaddress(String url) {
		this.seIPaddress = url;
	}
	public ArrayList<ResultListBean> getResultURLList() {
		if (resultURLList == null) {
			resultURLList = new ArrayList<ResultListBean>();
		}
		return resultURLList;
	}
	public void setResultURLList(ArrayList<ResultListBean> resultList) {
		this.resultURLList = resultList;
	}
	
	
	
}
