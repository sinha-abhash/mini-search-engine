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

div.alignKeyword {
	text-align: center;
	padding-top: 10px;
	padding-right: 50px;
	padding-bottom: 10px;
	padding-left: 50px;
	margin: 20px 300px;
}

.left {
	width: 30%;
	float: left;
	text-align: right;
}

.right {
	width: 65%;
	margin-left: 10px;
	float: left;
}
</style>
</head>

<body bgcolor="white">
	<form action="CustomerRegistrationServlet" method="GET">

		<h1 style="text-align: center; color: Maroon;">TrickStar
			Meta-Search Engine</h1>
		<h2 style="text-align: center; color: Maroon;">Customer
			Registration</h2>
		<p class="keyword"
			style="color: black; text-align: center; font-size: 20px;">Enter
			the Advertisement details:</p>

		<div class='alignKeyword'>
			<p>nGrams</p>
			<p>Format: [Europe Germany],[kaiserslautern],[abc def xyz] </p>
			<input type="text" name="nGrams" /> <br />
			<p>Advertisement URL</p>
			<p>Format:http://www.abc.com </p>
			<input type="text" name="addURL" /> <br />
			<p>Advertisement Text</p>
			<input type="text" name="addText" /> <br />
			<p>Amount Paid Per Click</p>
			<input type="text" name="moneyPerClick" /> <br />
			<p>Total Budget</p>
			<input type="text" name="budget" /> <br />
		</div>
		<div class='button' style="text-align: center">
			<input type="submit" value="Register" />
		</div>
		<div id="result" style="text-align: center">
			<pre>
        ${requestScope.result}
    </pre>
		</div>
	</form>
</body>
</html>