<%@ page language="java" contentType="text/html; charset=ISO-8859-1"

pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>HTML Validator Framework</title>
</head>
<body>

	<center>
		<h2> Validator FrameWork</h2>
	</center>
	 <form action="#" th:action="@{/url}" th:object="${urlForm}" method="post">
    	<p>URL: <input type="text" th:field="*{url}" /></p>
        <p><input type="submit" value="Submit" /> <input type="reset" value="Reset" /></p>
    </form>
</body>
</html>