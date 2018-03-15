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
<title>Testing Search Engine</title>
</head>
<body>
<h2 style="color:Red;">Testing Meta Search Results</h2>
    <%
    	out.println("Welcome");
        out.println("<br>");
        ArrayList<String> reultList = (ArrayList<String>)request.getAttribute("reultList");
        
        for (String str: reultList ){
    		out.println("Json string returned is  " + str);
    		if (!str.isEmpty()) {
    			out.println("<br>");
    			out.println("done");
    		} else {
    			out.println("Unfortunate! Please try again!");
    		}
    	}
    %>
</body>
</html>