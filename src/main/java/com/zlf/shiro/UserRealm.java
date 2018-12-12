package com.zlf.shiro;

import javax.annotation.Resource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Service;

import com.zlf.bo.UserBo;
import com.zlf.dao.UserBoMapper;
import com.zlf.utils.UserUtils;

@Service
public class UserRealm extends AuthorizingRealm {
	
	@Resource
	private UserBoMapper userDAO;
	
	/**
	 * 给权限，在页面有操作动作时，用shiro标签进入这个进行判断。
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 登陆验证，在登录页面点击登录时，在controller层调用login方法，进入到这个方法验证，如果无此用户，则异常返回。
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		//获取用户名和密码的第一种方式
		UsernamePasswordToken userToken=(UsernamePasswordToken) token;
		String username = userToken.getUsername();
		char[] cs = userToken.getPassword();
		String password=String.valueOf(cs);
		//获取用户名和密码的第二种方式
		String username1 = (String) token.getPrincipal(); // 得到用户名
		String password1 = new String((char[]) token.getCredentials()); // 得到密码

		UserBo user = userDAO.selectByPrimaryKey(username);
		UserUtils.map.put("userBo", user);
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(
				user.getUsername(), user.getPwd(), getName());
		return info;
	}

}
