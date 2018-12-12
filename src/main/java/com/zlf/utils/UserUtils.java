package com.zlf.utils;

import java.util.HashMap;
import java.util.Map;

import com.zlf.bo.UserBo;

public class UserUtils {
	public static final Map<String,UserBo> map=new HashMap<String,UserBo>();//保存当前登陆人的信息。
	public static final String CACHE_USER="userBo";
}
