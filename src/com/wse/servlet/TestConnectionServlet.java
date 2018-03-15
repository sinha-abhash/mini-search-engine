package com.wse.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import com.wse.bean.MetaSEBean;
import com.wse.bean.ResultListBean;
import com.wse.bean.SEStatBean;
import com.wse.helpers.MetaSearchEngineHelper;

/**
 * Servlet implementation class TestConnectionServlet
 */
@WebServlet("/TestConnectionServlet")
public class TestConnectionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public TestConnectionServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String query = request.getParameter("query");
		int score = Integer.parseInt(request.getParameter("score"));
		int k = Integer.parseInt(request.getParameter("k"));
		String searchEnginePercentStr = request.getParameter("searchEnginePercentStr");
		
		ArrayList<String> ipaddressList = new ArrayList<>();
		
		String url1 = "http://192.168.18.25:8080/is-project/json?query=" + query + "&k=" + k + "&score=" + score;
		ipaddressList.add(url1);
		String url2 = "http://192.168.18.12:8080/is-project/json?query=" + query + "&k=" + k + "&score=" + score;
		ipaddressList.add(url2);
		String url3 = "http://192.168.18.13:8080/is-project/json?query=" + query + "&k=" + k + "&score=" + score;
		ipaddressList.add(url3);
		
		ArrayList<String> reultList = new ArrayList<>();
		
		for (String url: ipaddressList ){
			String jsonString = getTextFromURL(url);
			reultList.add(jsonString);
		}
		
		request.setAttribute("reultList", reultList);
		request.getRequestDispatcher("/TestResult.jsp").forward(request, response);
	}
	
	private String getTextFromURL(String url) throws IOException {
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


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
