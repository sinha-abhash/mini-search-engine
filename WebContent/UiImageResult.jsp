<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="org.apache.tomcat.util.net.URL" %>
<%@ page import="com.wse.bean.ImageDetailsBean" %>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>TrickStar Web Search Engine</title>
</head>
<body>
<h2 style="color:Red;">TrickStar Web Search Results</h2>
<h3 style="color:Green;">Images for the Entered Keywords: </h3>

<%
    ArrayList<ImageDetailsBean> topRawResults = (ArrayList<ImageDetailsBean>)request.getAttribute("rawResults");
    if ( ! topRawResults.isEmpty() && topRawResults != null  ){
    	out.println("<table>");
    	out.println("<tr>");
    	//out.println("<th>Images</th>");		
    	//out.println("<th>Images</th>");
    	out.println("</tr>");
    	
      	for (ImageDetailsBean obj : topRawResults) {
      		out.println("<tr>");
    	    out.println("<img src ="+obj.getImageUrl()+" />");
    	  // out.println("<a href="+obj.getDocURL()+"</a>");
    	    out.println("<br>");
    	    out.println("<br>");
    	    out.println("</tr>");
      	}
      	out.println("</table>");
	} 
    else {
    	out.println("Unfortunately no matching results found for your query.! Please try again!");
    	URL trickstarUrl = new URL("http://localhost:8022/WSE/ImageSearch.jsp");
  		out.println("<a href=ImageSearch.jsp>Click here to search again.</a>");
    }
    
    %>

</body>
</html>