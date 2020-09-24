<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>업로드 - 결과</title>
</head>
<body>
<h3>업로드 결과</h3>
<%

	//Object name = request.getAttribute("usrID");
	//Object login = request.getAttribute("login");
	
	Object obj = request.getAttribute("reqMap");
	Map<String, Object> map = null;
	
	if(obj != null){
		map = (HashMap<String, Object>)obj;
	}

%>

<table style="width:700px;border:1px solid #e2e2e2;">
	<tr>
		<td style="width:20%;">
		 <%
        	out.println("name : " + map.get("usrID") + "<br />");
		 %>
		</td>
		<td style="width:20%;border-left:1px solid #e2e2e2;">
		<%
			out.println("login : " + map.get("usrPasswd") + "<br />");
		%>
		</td>
	</tr>
	<tr>
		<td>
			
		</td>
		<td>
			
		</td>
	</tr>
</table>

</body>
</html>