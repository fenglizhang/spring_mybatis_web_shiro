package com.zlf.utils;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.w3c.dom.Document;

import com.alibaba.druid.util.StringUtils;

public class CommonResponse {
	public static final Logger logger=LoggerFactory.getLogger(CommonResponse.class);
	
	/**
	 * 
	 * @param status	成功：Success ，失败：Failure
	 * @param errorCode 成功："" 		，失败：五位流水号，从10001开始累加
	 * @param errorMessage 成功：""，失败：简单消息
	 * @return
	 */
	public static Document response(String status,String errorCode,String errorMessage){
		String code="";
		if(!StringUtils.isEmpty(errorCode)){
			code="TS-TSSPT"+errorCode;
		}
		HttpServletRequest request =((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		Document document=(Document) request.getAttribute("bizDOC");
		
		
		
		
		
		
		
		
		
		
		
		return null;
	}
	
	
	
	
	
	
	
	
	

			
}
