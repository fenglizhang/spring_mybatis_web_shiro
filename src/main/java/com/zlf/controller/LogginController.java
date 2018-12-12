package com.zlf.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zlf.bo.UserBo;
import com.zlf.utils.UserUtils;

@Controller
@RequestMapping("/login")
public class LogginController {
	
	@RequestMapping("/checkLogin")
	public String checkLogin(HttpServletRequest request,Model model){
		Subject subject = SecurityUtils.getSubject();
		String host = request.getRemoteHost();
		boolean rememberMe = false;
		
		String username=request.getParameter("username");
		String password=request.getParameter("password");
		
		if (username != null) {
			username = new String(Base64.decodeBase64(username));
		}
		if (password != null) {
			password = new String(Base64.decodeBase64(password));
			String salt = "java1234";// 盐值
			password = new Md5Hash(password, salt).toString();
		}
		
		
		Map<String, UserBo> map = UserUtils.map;
		if(map!=null && map.get(UserUtils.CACHE_USER)!=null){
			UserBo userBo = map.get(UserUtils.CACHE_USER);
			String username2 = userBo.getUserid();
			String pwd = userBo.getPwd();
			if(username==username2&&pwd==password){
				return "test";
			}
		}
		
		UsernamePasswordToken token = new UsernamePasswordToken(username,password.toCharArray(), rememberMe, host);
		
		try {
			subject.login(token);
		} catch (AuthenticationException e) {
			model.addAttribute("error", "用户名或密码错误");
			return "../login";
		}
		
		return "test";// 都通过，进入主页面
		
	}
	
	
	
	
}
