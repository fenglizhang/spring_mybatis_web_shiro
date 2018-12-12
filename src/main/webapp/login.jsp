<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd" >
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>用户登录 -管理系统</title>
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
 <script type="text/javascript">  
        function changeVerifyCode() {  
            var time=new Date().getTime();  
            document.getElementById("kaptchaImage").src="<%=basePath%>queryKaptcha/kaptcha?d="+time;//为了不让验证码缓存，为了安全起见，需要次次都刷新  
        }  
 </script>  
</head>
<body>
	<div style="margin-left: 400px; margin-top: 250px;">
		<div>
			<form id="LoginForm" method="post"	action="<%=basePath%>login/checkLogin">
				<input type="hidden" name="username" id="userNamela"/> 
				<input type="hidden" name="password" id="pwdla"/>
				<input type="hidden" name="rememberMe" id="rememberMe"/>
				<input type="hidden" name="validatecode" id="validatecode"/>
			</form>
			<form>
				<table class="tableStyle">
					<tr>
						<td class="title">用户名：</td>
						<td class="content">
							<input type="text" class="easyui-textbox" id="usernamepro" maxlength="18"/>
						</td>
					</tr>
					<tr>
						<td class="title">密&nbsp;&nbsp;&nbsp;码：</td>
						<td class="content">
							<input type="password"	class="easyui-textbox" id="passwordpro" />
						</td>
					</tr>
					<tr>
						<td> 验  证  码：</td>
						<td>
							<input type="text" class="easyui-textbox" name="validatecodepro" id="validatecodepro" placeholder="请输入验证码"/>  
	            			<img src="/static/img/kaptcha.jpg" id="kaptchaImage" title="看不清，点击换一张" onclick="changeVerifyCode()"><br><br>  
						</td>
					</tr>
					<tr>
						<td>
							<input type="checkbox" id="rememberpro" /><span>系统记住我</span>
 						</td>
					</tr>
					<tr>
						<td><font color="red"> ${error}</font></td>
					</tr>
				</table>
				<br />
				<div class="serbut" style="width: 170px; text-align: right">
					<a href="javascript:void(0)" class="easyui-linkbutton"
						iconCls="icon-ok" onclick="login()">登陆</a> <a
						href="javascript:void(0)" class="easyui-linkbutton"
						iconCls="icon-clear" onclick="clearForm()">清空</a>
				</div>
			</form>
		</div>
		<div style="margin-top: 200px;">
			<font color="red">管理系统</font> Version 1.0 由wool科技提供技术支持！ <br />
		</div>
	</div>
	<script type="text/javascript">
		function login() {
			var username = $("#usernamepro");
			var password = $("#passwordpro");
			var validatecodepro=$("#validatecodepro");
			var base = new Base64();//加密算法对象。
			if (username.val() == "") {
				$.messager.alert("提示", "请输入用户名！");
				username.focus();
				return false;
			} else if (password.val() == "") {
				$.messager.alert("提示", "请输入密码！");
				password.focus();
				return false;
			}else if(validatecodepro.val()=="" ||validatecodepro.val()==null){
				$.messager.alert("提示", "请输入验证码！");
				validatecodepro.focus();
				return false;
			}else {
				//将加密的数据赋值给隐藏的表
				var name = base.encode($("#usernamepro").val());//对用户名加密
				var pwd = base.encode($("#passwordpro").val());//密码加密
				if($("#rememberpro").is(":checked")){
					$("#rememberMe").val("true");
				}else{
					$("#rememberMe").val("false");
				}
				var validateCode=base.encode($("#validatecodepro").val());
				$("#validatecode").val(validateCode);
				//将加密后的数据付给input,并提交
				$("#userNamela").val(name);
				$("#pwdla").val(pwd);
				$("#LoginForm").submit();
			}
		}

		$(window).keydown(function(event) {
			var code = event.keyCode || event.which || event.charCode;
			if (code == 13) {
				login();
			}
		});
		
		function clearForm(){
// 			$("#usernamepro").setValue("");
// 			$("#passwordpro").setValue("");
// 			$("#validatecodepro").setValue("");
			$("#usernamepro").textbox('clear');
			$("#passwordpro").textbox('clear');
			$("#validatecodepro").textbox('clear');
		}
	</script>

</body>
</html>