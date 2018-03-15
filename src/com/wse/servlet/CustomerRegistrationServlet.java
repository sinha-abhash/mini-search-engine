package com.wse.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wse.helpers.CustomerRegistrationHelper;

@WebServlet("/CustomerRegistrationServlet")
public class CustomerRegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public CustomerRegistrationServlet() {
		super();
		// TODO Auto-generated constructor stub
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String responseMessage = "";
		String nGrams = request.getParameter("nGrams");
		System.out.println(nGrams);

		String addURL = request.getParameter("addURL");

		String addText = request.getParameter("addText");

		int budget =  Integer.parseInt(request.getParameter("budget"));

		int moneyPerClick = Integer.parseInt(request.getParameter("moneyPerClick"));

		boolean status = CustomerRegistrationHelper.persistCustDetails(nGrams, addURL, addText, budget, moneyPerClick);
		if (status){
			responseMessage = "Congratulations. Customer's ADD registered successfully.";
		}else{
			responseMessage = "Sorry Registration failed. Please try again.";
		}
		request.setAttribute("result", responseMessage);
		request.getRequestDispatcher("CustomerRegistration.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
