<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>삽입</title>
</head>
<body>
<h3>삽입</h3>

<!-- 파일 업로드 화면 -->
<!-- 일반적으로 Multipart form으로 전송된 데이터는
      일반 request 메서드로 받아올 수 없습니다.  enctype="multipart/form-data" -->
<form method="post" action="insertResult.do" >
<table style="width:700px;border:1px solid #e2e2e2;">
	<tr>
		<td style="width:20%">
			파일명
		</td>
		<td>
			<input type="text" name="usrID" size="10">
			<input type="file" name="uploadFile" multiple>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="submit" value="전송">
		</td>
	</tr>
</table>
</form>


<!-- 멀티파트/데이터 전송 -->
<form method="post" action="insertMultiResult.do" enctype="multipart/form-data" >
<table style="width:700px;border:1px solid #e2e2e2;">
	<tr>
		<td style="width:20%">
			파일명
		</td>
		<td>
			<input type="text" name="usrID" size="10">
			<input type="password" name="usrPasswd" size="10">
			<input type="file" name="uploadFile" multiple>
			<input type="file" name="uploadFile" multiple>
			<input type="file" name="uploadFile" multiple>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<input type="submit" value="전송">
		</td>
	</tr>
</table>
</form>

</body>
</html>