<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="org.apache.tomcat.util.net.URL" %>
<%@ page import="com.wse.bean.ConjDisBean" %>
<%@ page import="com.wse.bean.AdvertisementBean" %>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>TrickStar Meta Search Engine</title>
</head>
<body>
<h2 style="color:Red;">TrickStar Meta Search Results</h2>
    <%
    out.println("Welcome");
    out.println("<br>");
    ArrayList<ConjDisBean> topRawResults = (ArrayList<ConjDisBean>)request.getAttribute("FinalResultList");
    if (topRawResults != null && ! topRawResults.isEmpty()){
    	out.println("<table>");
    	out.println("<tr>");
    	out.println("<th>Search Engine IP address</th>");
    	out.println("<th>Rank</th>");		
    	out.println("<th>URL</th>");
    	out.println("<th>Score</th>");
    	out.println("</tr>");
    	for (ConjDisBean obj : topRawResults) {
      		out.println("<tr>");
      		out.println("<td>"+obj.getSnippet()+"</td>");
    	    out.println("<td>"+obj.getRank()+"</td>");
    	    out.println("<td>"+obj.getUrl()+"</td>");
            out.println("<td>"+obj.getscore()+"</td>");
    	    out.println("<br>");
    	    out.println("</tr>");
      	}
      	out.println("</table>");
	} 
    else {
    	out.println("Unfortunately no matching results found for your query.! Please try again!");
    }
    %>
</body>
</html>