<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
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
	<form:form action="validateForm" method="post" commandName="ValidationRequest">
	<table border="0">
	  <tr>
		<td colspan="2" align="center"><h2>Validation Framework Form</h2></td>
	 </tr>

	  <tr>  
		<td><form:label path="url">URL:</form:label></td>  
		<td><form:input path="url" value="WWW.google.com"></form:input></td>  
     </tr> 

	  <tr>
		<td colspan="2" align="center"><input type="submit" value="Submit" /></td>
	 </tr>
	</table>
   </form:form>
</body>
</html>