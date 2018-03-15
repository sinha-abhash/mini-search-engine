package com.wse.helpers;

import static java.lang.Math.toIntExact;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import com.wse.bean.MetaSEBean;
import com.wse.bean.ResultListBean;
import com.wse.bean.SEStatBean;

public class MetaSEThreading /*extends Thread*/ {
	private String url;
	private String seIPaddress;
	private static ArrayList<MetaSEBean> resulMSEtList;
	
	public MetaSEThreading (String urlSE, String seIPaddress) {
		this.url = urlSE;
		this.seIPaddress = seIPaddress;
	}
	
	public void getResultFromIP() {
		resulMSEtList = new ArrayList<MetaSEBean>();
		try {
			long cw = 0;
			String jsonString = getTextFromURL();
			if (jsonString != null && !jsonString.isEmpty()) {
				JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(jsonString);
				MetaSEBean mseBean = new MetaSEBean();
				ArrayList<SEStatBean> stat = new ArrayList<>();
				ArrayList<ResultListBean> resultURLList = new ArrayList<>();
				if (jsonObject != null) {
					//Below code retrieves the value from other search engine
					cw = (long)jsonObject.get("cw");
					stat = getSEStatBeanList(jsonObject);
					resultURLList = getResultList(jsonObject);
				}
				mseBean.setSeIPaddress(seIPaddress);
				mseBean.setCw(cw);
				mseBean.setStat(stat);
				mseBean.setResultURLList(resultURLList);
				/*System.out.println("Name of the search engine is: ");
				System.out.println("Results returned: ");
				System.out.println(cw + " " + stat.get(0).getDf() + " " + resultURLList.get(0).getUrl());*/
				//synchronized(resulMSEtList) {
				resulMSEtList.add(mseBean);
				//}
	            //System.out.println(cw + " " + stat.get(0).getDf() + " " + resultURLList.get(0).getUrl());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	

	private ArrayList<ResultListBean> getResultList(JSONObject jsonObject) {
		ArrayList<ResultListBean> result = new ArrayList<ResultListBean>();
		ResultListBean resultBean;
		if (jsonObject.get("resultList") instanceof JSONArray) {
			JSONArray resultJSONArray = (JSONArray)jsonObject.get("resultList");
			if (resultJSONArray != null) {
				for (int i = 0; i < resultJSONArray.size(); i++) {
					resultBean = new ResultListBean();
					if (resultJSONArray.get(i) instanceof JSONObject) {
						JSONObject resultJsonObj = (JSONObject)resultJSONArray.get(i);
						
						if(resultJsonObj.get("rank") instanceof Long){
							long rankLong = (long)resultJsonObj.get("rank");
							int rank = toIntExact(rankLong);
							resultBean.setRank(rank);
						}
						//resultBean.setRank((int)resultJsonObj.get("rank"));
						resultBean.setScore((double)resultJsonObj.get("score"));
						resultBean.setUrl((String)resultJsonObj.get("url"));
						result.add(resultBean);
					}
				}
			}
		}
		return result;
	}

	private ArrayList<SEStatBean> getSEStatBeanList(JSONObject jsonObject) {
		ArrayList<SEStatBean> result = new ArrayList<SEStatBean>();
		SEStatBean statBean;
		if (jsonObject.get("stat") instanceof JSONArray) {
			JSONArray statisticDFArray = (JSONArray)jsonObject.get("stat");
			if (statisticDFArray != null) {
				for (int i = 0; i < statisticDFArray.size(); i++) {
					statBean = new SEStatBean();
					if (statisticDFArray.get(i) instanceof JSONObject) {
						JSONObject statJsonObj = (JSONObject)statisticDFArray.get(i);
						
						if(statJsonObj.get("df") instanceof Double){
							double dfDouble = (double)statJsonObj.get("df");
							long df = Math.round(dfDouble);
							statBean.setDf(df);
						}else if (statJsonObj.get("df") instanceof Integer){
							int dfInt = (int)statJsonObj.get("df");
							long df = Long.valueOf(dfInt);
							statBean.setDf(df);
						}else {
						statBean.setDf((long)statJsonObj.get("df"));}
						
						statBean.setTerm((String)statJsonObj.get("term"));
						result.add(statBean);
					}
					
				}
			}
		}
		return result;
	}

	private String getTextFromURL() throws IOException {
		URL seURL = new URL(url);
		URLConnection connection = seURL.openConnection();
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
						connection.getInputStream()));

		StringBuilder response = new StringBuilder();
		String inputLine;

		while ((inputLine = in.readLine()) != null) 
			response.append(inputLine);

		in.close();

		return response.toString();
	}

	public static ArrayList<MetaSEBean> getResult() {
		return resulMSEtList;
	}
}
