<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd" >
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>无权限操作</title>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<script type="text/javascript">
	var basepath='<%=basePath%>';
</script>
<script type="text/javascript"
	src="<%=basePath%>static/js/easyui/jquery.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>static/js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>static/js/easyui/base64.js"></script>
<link type="text/css" rel="stylesheet"
	href="<%=basePath%>static/js/easyui/themes/default/easyui.css" />
<link type="text/css" rel="stylesheet"
	href="<%=basePath%>static/js/easyui/themes/icon.css" />

</head>
<body>
	<h1>对不起，你没有对应的操作权限！！</h1>
</body>
</html>