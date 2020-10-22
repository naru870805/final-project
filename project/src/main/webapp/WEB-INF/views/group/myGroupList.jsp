<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
<script type="text/javascript">
$(document).ready(function () {
    $('.btnlink').click(function () {
	location.href=getContextPath()+'/cafe/'+$(this).val();
	})

	function getContextPath() {
		var hostIndex = location.href.indexOf( location.host ) + location.host.length;
		return location.href.substring( hostIndex, location.href.indexOf('/', hostIndex + 1) );
	};
})
</script>
<style type="text/css">

#groupTable {
	width: inherit;
	margin: 20px auto 0 auto;
	border-collapse: collapse;
	position: absolute;
}

#groupTable tr {
	height: 40px;
	font-family: Inter, "Noto Sans KR", "Noto Sans JP", "Malgun Gothic", "맑은 고딕", sans-serif;
}

#groupTable tr:nth-child(2n) {
	background-color: #f7f8f9
}
.btnlink{
	background-color: transparent;
	border: none;
	font-size: medium;
	font-family: Inter, "Noto Sans KR", "Noto Sans JP", "Malgun Gothic", "맑은 고딕", sans-serif;
	cursor: pointer;
}
.btnlink:hover {
	text-decoration: underline;
}
</style>
</head>
<body>
	<table id="groupTable">
			<tr style="background-color: #3E60DB; color: #FFF; font-weight: bold;">
				<th width="600px">그룹명</th>
				<th>그룹장</th>
				<th>카테고리</th>
				<th>멤버수</th>
			</tr>
			<c:forEach var="list" items="${list}">
				<tr>
					<td style="padding-left: 20px; text-align: left;"><button class="btnlink" value="${list.GROUP_NUM}">${list.GROUP_NAME}</button></td>
					<td>${list.GROUP_LEADER }</td>
					<td>${list.GROUP_CATEGORY}</td>
					<td>${list.MEMBER_COUNT}</td>
				</tr>
			</c:forEach>
		</table>
</body>
</html>