package com.wse.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wse.postgresdb.CustomerDB;

/**
 * Servlet implementation class UrlClickServlet
 */
@WebServlet("/UrlClickServlet")
public class UrlClickServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UrlClickServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("in servlet");
		String urlClicked = request.getParameter("urlClicked");
		System.out.println("The Addvertisement url clicked by the user is: "+urlClicked);

		int clicksleft = CustomerDB.getUrlclicksleft(urlClicked);
		if (clicksleft > 0){
			clicksleft = clicksleft - 1;
			CustomerDB.updateUrlClickLeft(urlClicked, clicksleft);
		}

		response.sendRedirect(urlClicked);
		System.out.println("End of Addvertisement processing servlet.");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

}
