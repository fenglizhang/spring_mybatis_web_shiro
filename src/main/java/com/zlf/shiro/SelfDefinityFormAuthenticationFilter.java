package com.zlf.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import com.google.code.kaptcha.Constants;

/**
 * 在shiro这个自定义的过滤器上，1.先进行用户名密码的解码，这样到Controller就是解码后的。
 * 2.对验证码进行验证，不正确则直接返回到登陆页面。正确跳转到对应controller进行后续验证
 * 
 * @author Administrator
 * 
 */
public class SelfDefinityFormAuthenticationFilter extends
		FormAuthenticationFilter {

	/**
	 * 重新复写createToken 方法
	 */
	@Override
	protected AuthenticationToken createToken(ServletRequest request,
			ServletResponse response) {
		// 下面这两个方法要求前台的input标签中的name必须是username和password.
		String username = getUsername(request);
		String password = getPassword(request);
		String validatecode = WebUtils.getCleanParam(request, "validatecode");

		// 对用户名密码和验证码转码解密
		if (username != null) {
			username = new String(Base64.decodeBase64(username));
		}
		if (password != null) {
			password = new String(Base64.decodeBase64(password));
			String salt = "java1234";// 盐值
			password = new Md5Hash(password, salt).toString();
		}
		if (validatecode != null) {
			validatecode = new String(Base64.decodeBase64(validatecode));
		}
		boolean rememberMe = isRememberMe(request);
		String host = getHost(request);
		return new UsernamePasswordToken(username, password, rememberMe, host);
	}

	/**
	 * 在这里进行验证码的校验
	 * 表示当访问拒绝时是否已经处理了；如果返回true表示需要继续处理；如果返回false表示该拦截器实例已经处理了，将直接返回即可
	 */
	@Override
	protected boolean onAccessDenied(ServletRequest request,
			ServletResponse response) throws Exception {
		System.out.println("----------进到过滤器里了---------------");
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpSession session = httpServletRequest.getSession();
		// 从session中取出传到前台页面的验证码
		String validateCode = (String) session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
		// 取出前台输入的验证码
		String randomcode = request.getParameter("validatecode");
		if (randomcode != null) {
			randomcode = new String(Base64.decodeBase64(randomcode));
		}
		if (randomcode != null && validateCode != null && !randomcode.equals(validateCode)) {
			// 如果校验失败，将验证码错误失败信息，通过shiroLoginFailure设置到request中
			httpServletRequest.setAttribute("shiroLoginFailure","randomCodeError");
			// 拒绝访问，不再校验账号和密码
			return true;
		}
		return super.onAccessDenied(request, response);

	}
	
	
	
	
}