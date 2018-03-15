package com.wse.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wse.bean.ImageDetailsBean;
import com.wse.helpers.AdminHelper;
import com.wse.postgresdb.GetImageDetails;

/**
 * Servlet implementation class AdminServlet
 */
@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminServlet() {
        super();
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String seURL = request.getParameter("SEurl");
		String action = request.getParameter("ACTION");
		
		AdminHelper.admin(action, seURL);
		
		//request.setAttribute("rawResults", finalresults); // Will be available in JSP
		//request.setAttribute("rawResults", finalresults); // Will be available in JSP
		//request.getRequestDispatcher("/UiImageResult.jsp").forward(request, response);
		System.out.println("End of servlet processing from the AdminSE page.");
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
}
