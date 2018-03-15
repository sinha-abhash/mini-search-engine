<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>TrickStar Web Search Engine</title>
<style>
button {
    background-color:Black;
    color: white;
    
    padding-top: 20px;
    padding-right: 40px;
    padding-bottom: 20px;
    padding-left: 40px;
    
    text-align: center;
    display: inline-block;
    font-size: 20px;
    margin: 20px 350px;
    cursor: pointer;
}
img {
    display: block;
    margin: auto;
}

div.alignKeyword{ 
	text-align: center;
 	padding-top: 10px;
    padding-right: 10px;
    padding-bottom: 1px;
    padding-left: 10px;
    margin: 20px 300px;
}

</style>
</head>
<body bgcolor="white">
<form action="json" method="GET">

<h1 style="text-align:center;color:Maroon;" >TrickStar Web Search</h1>
<br />
<img src="${pageContext.request.contextPath}/Image/kSunrise.jpg" alt="Peace Logo" align="middle" />

<p class="keyword" style="color:black; text-align:center;font-size: 20px;">Enter the keywords for Web Search</p> 

<div class='alignKeyword'>
	<input type="text"  name="query" />
	<br />
	<p class="keyword" style="color:black; text-align:center;font-size: 20px;">Choose the scoring model below:</p> 
	<input type="radio" name="score" value="1" checked> TFIDF
	<input type="radio" name="score" value="2" > BM25 
	<input type="radio" name="score" value="3" > Combined <br>
</div>

<p class="keyword" style="color:black; text-align:center;font-size: 20px;" >Enter the maximum number of results you wish to receive:</p>
	<div class='alignKeyword'>
	<input type="text" name="k" />
</div>

<div  class='button' style="text-align:center">
	<input type="submit" value="Search" />
</div>
</form>
</body>
</html>