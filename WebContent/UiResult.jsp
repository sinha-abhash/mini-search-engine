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
<title>TrickStar Web Search Engine</title>
</head>

<body>
<h2 style="color:Red;">TrickStar Web Search Results</h2>
<h3 style="color:Green;">Advertisements </h3>
<%
	ArrayList<AdvertisementBean> addvertisementResult = (ArrayList<AdvertisementBean>)request.getAttribute("addResults");
if (addvertisementResult != null && ! addvertisementResult.isEmpty()){
	out.println("<br>");
	out.println("<br>");
	for (AdvertisementBean obj : addvertisementResult) {
  		out.println("<tr>");
	    out.println("<a href=http://localhost:9004/is-project/UrlClickServlet?urlClicked="+obj.getAddUrl()+">"+obj.getAddUrl()+"</a>");
	    out.println("<br>");
	    out.println(obj.getAddDesc());
	    out.println("<br>");
	    out.println("</tr>");
  	}
  	out.println("</table>");
} 
else {
	out.println("No Ads Registered for the keyword!");
	}
%>
<h3 style="color:Green;">Web Search Results: </h3>
    <%
    ArrayList<ConjDisBean> topRawResults = (ArrayList<ConjDisBean>)request.getAttribute("rawResults");
    if (topRawResults != null && ! topRawResults.isEmpty()){
    	String rawKeywords = (String)request.getAttribute("rawKeywords");
    	out.println("Keyword entered by User: "+ rawKeywords);
    	out.println("<table>");
    	out.println("<tr>");
    	out.println("<th>Rank</th>");		
    	out.println("<th>URL</th>");
    	out.println("<th>Score</th>");
    	out.println("</tr>");
    	for (ConjDisBean obj : topRawResults) {
      		out.println("<tr>");
    	    out.println("<td>"+obj.getRank()+"</td>");
    	    out.println("<td>"+obj.getUrl()+"</td>");
            out.println("<td>"+obj.getscore()+"</td>");
    	    out.println("<br>");
    	    out.println("</tr>");
    	    out.println("<tr>");
    	    out.println("<td>"+obj.getSnippet()+"</td>");
    	    out.println("</tr>");
      	}
      	out.println("</table>");
	} 
    else {
    	out.println("Unfortunately no matching results found for your query.! Please try again!");
    	URL trickstarUrl = new URL("http://localhost:8022/WSE/index.html");
  		out.println("<a href=index.html>Click here to search again.</a>");
    }
    out.println("<p>|===========================================|</p>");
    out.println("<p>|===========================================|</p>");
    %>
    <%
    ArrayList<ConjDisBean> topAlternateResults = (ArrayList<ConjDisBean>)request.getAttribute("topResults");
    if ( topAlternateResults != null && ! topAlternateResults.isEmpty()){
    	String alternateTerm = (String)request.getAttribute("alternateTerm");
    	out.println("<h3> Alternate Keyword Available. </h3>");
    	out.println("Alternate Suggestion: "+ alternateTerm);
    	
    	out.println("<table>");
    	out.println("<tr>");
    	out.println("<th>Rank</th>");		
    	out.println("<th>URL</th>");
    	out.println("<th>Score</th>");
    	out.println("</tr>");
    	
      	for (ConjDisBean obj : topAlternateResults) {
      		out.println("<tr>");
    	    out.println("<td>"+obj.getRank()+"</td>");
    	    out.println("<td>"+obj.getUrl()+"</td>");
            out.println("<td>"+obj.getscore()+"</td>");
    	    out.println("<br>");
    	    out.println("</tr>");
    	    out.println("<tr>");
    	    out.println("<td>"+obj.getSnippet()+"</td>");
    	    out.println("</tr>");
      	}
      	out.println("</table>");
	} 
    else {
    	out.println("Unfortunately no alternate keywords found! Please try again!");
    }
    %>
    <%
    	String alternateUrl = (String)request.getAttribute("alternateQueryURL");
   		 //out.println(alternateUrl);
    	out.println("<br>");
    	URL trickstarUrl = new URL("http://localhost:8022/WSE/index.html");
		out.println("<a href=index.html>Click here to search again.</a>");
    %>
	 <!--  <a href=<%=alternateUrl%>>Link To Trickstar Web Search With Alternate Query</a> -->
</body>
</html>