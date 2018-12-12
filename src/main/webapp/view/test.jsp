<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>主页面</title>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<script type="text/javascript">
	var basepath='<%=basePath%>';
</script>
</head>
<body>
<h1>SUCCESS!!!</h1>
<h2>登录成功，来到了主页面！！！</h2>
<h3><a href="javascript:void(0)" onclick="logout()">退出登陆</a></h3>

<script type="text/javascript">
	function logout(){
		window.location.href=basepath+"/logout/now";
	}

</script>
</body>
</html>