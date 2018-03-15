<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>TrickStar Web Search Engine</title>
<style>
button {
    background-color: Black;
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
    padding-right: 50px;
    padding-bottom: 10px;
    padding-left: 50px;
    margin: 20px 300px;
}
</style>
</head>

<body bgcolor="white">
<form action="AdminServlet" method="GET">

	<h1 style="text-align:center;color:Maroon;" >TrickStar Meta-Search Engine</h1>
	<h2 style="text-align:center;color:Maroon;" >Administrative Details</h2>
	<br />
	<p class="keyword" style="color:black; text-align:center;font-size: 20px;">Enter Search Engine URL</p>
 
	<div class='alignKeyword'>
		<input type="text" name="SEurl" />
		<br />
		<input type="radio" name="ACTION" value="ADD" checked> ADD <br />
		<input type="radio" name="ACTION" value="REMOVE"> REMOVE <br />
		<input type="radio" name="ACTION" value="DEACTIVATE"> DEACTIVATE <br>
		<br />
	</div>
	<div  class='button' style="text-align:center">
		<input type="submit" value="Update & Save" />
	</div>
	
</form>
</body>
</html>