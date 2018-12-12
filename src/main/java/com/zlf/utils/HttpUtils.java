package com.zlf.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.cxf.common.util.Base64Utility;
import org.apache.cxf.jaxrs.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpUtils {
	public static final Logger logger=LoggerFactory.getLogger(HttpUtils.class);
	/**
	 * HttpURLConnection形式
	 * 
	 * @param url 要调用的接口地址
	 * @param xml 入参报文
	 * @return
	 */
	public static String doPost(String url,String xml){
		logger.info("调用接口入参： "+xml);
		StringBuilder responseBuilder=null;
		BufferedReader reader=null;
		OutputStreamWriter wr=null;
		URL ur;
		try{
			ur=new URL(url);
			HttpURLConnection conn=(HttpURLConnection) ur.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(1000*10);
			conn.connect();
			
			wr=new OutputStreamWriter(conn.getOutputStream());
			wr.write(new String(xml.getBytes("UTF-8")));
			wr.flush();
			
			reader=new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			responseBuilder =new StringBuilder();
			
			String line=null;
			while ((line=reader.readLine())!=null) {
				responseBuilder.append(line);
			}
			logger.info("调用接口出参： "+responseBuilder.toString());
		}catch(IOException e){
			logger.error(""+e);
		}finally{
			try {
				if(wr!=null){
						wr.close();
				}
				
				if(reader !=null){
					reader.close();
				}
				if(responseBuilder !=null){
					return responseBuilder.toString();
				}
			} catch (IOException e) {
				logger.error("流关闭异常！ ",e);
			}
		}
		
		
		return "";
	}
	
	/**
     * <pre>
     * 方法体说明：向远程接口发起请求，返回字符串类型结果
     * @param url 接口地址
     * @param requestMethod 请求方式
     * @param params 传递参数     重点：参数值需要用Base64进行转码
     * @return String 返回结果
     * </pre>
     */
    public static String httpRequestToString(String url, String requestMethod, Map<String, String> params){
        String result = null;
        try {
            InputStream is = httpRequestToStream(url, requestMethod, params);
            byte[] b = new byte[is.available()];
            is.read(b);
            result = new String(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
	
	
	
	/**方法体说明：向远程接口发起请求，返回字节流类型结果
	 * 
	 * @param url  请求接口地址
	 * @param requestMethod  请求方式
	 * @param params  传递参数     重点：参数值需要用Base64进行转码
	 * @return
	 */
	 protected static InputStream httpRequestToStream(String url, String requestMethod, Map<String, String> params){

	        InputStream is = null;
	        try {
	            String parameters = "";
	            boolean hasParams = false;
	            //将参数集合拼接成特定格式，如name=zhangsan&age=24
	            for(String key : params.keySet()){
	                String value = URLEncoder.encode(params.get(key), "UTF-8");
	                parameters += key +"="+ value +"&";
	                hasParams = true;
	            }
	            if(hasParams){
	                parameters = parameters.substring(0, parameters.length()-1);
	            }

	            //请求方式是否为get
	            boolean isGet = "get".equalsIgnoreCase(requestMethod);
	            //请求方式是否为post
	            boolean isPost = "post".equalsIgnoreCase(requestMethod);
	            if(isGet){
	                url += "?"+ parameters;
	            }

	            URL u = new URL(url);
	            HttpURLConnection conn = (HttpURLConnection) u.openConnection();

	            //请求的参数类型(使用restlet框架时，为了兼容框架，必须设置Content-Type为“”空)
	            conn.setRequestProperty("Content-Type", "application/octet-stream");
	            //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	            //设置连接超时时间
	            conn.setConnectTimeout(50000);  
	            //设置读取返回内容超时时间
	            conn.setReadTimeout(50000);
	            //设置向HttpURLConnection对象中输出，因为post方式将请求参数放在http正文内，因此需要设置为ture，默认false
	            if(isPost){
	                conn.setDoOutput(true);
	            }
	            //设置从HttpURLConnection对象读入，默认为true
	            conn.setDoInput(true);
	            //设置是否使用缓存，post方式不能使用缓存
	            if(isPost){
	                conn.setUseCaches(false);
	            }
	            //设置请求方式，默认为GET
	            conn.setRequestMethod(requestMethod);

	            //post方式需要将传递的参数输出到conn对象中
	            if(isPost){
	                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
	                dos.writeBytes(parameters);
	                dos.flush();
	                dos.close();
	            }

	            //从HttpURLConnection对象中读取响应的消息
	            //执行该语句时才正式发起请求
	            is = conn.getInputStream();
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return is;
	    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	protected  static WebClient createClient(String uri){
		WebClient client =WebClient.create(uri);
		String auth="Basic"+Base64Utility.encode("kermit:kermit".getBytes());
		client.header("Authorization", auth);
		return client;
	}
	
	protected static String streamToString(InputStream inputStream){
		int bufferSize=1024;
		byte[] buffer=new byte[bufferSize];
		ByteArrayOutputStream outputStream =new ByteArrayOutputStream();
		int readLen=0;
		byte[] be = null;
		try {
			while((readLen=inputStream.read(buffer))!=-1){
				outputStream.write(buffer,0,readLen);
			}
			be=outputStream.toByteArray();
			
		} catch (IOException e) {
			logger.error("输出流读取异常！",e);
		}
		return new String(be);
	}
	
	/**
	 * 入参：以xml报文形式，不是json
	 * @param uri
	 * @param xml
	 * @return
	 */
	public static final String post(String uri,String xml){
		WebClient client =createClient(uri);
		client.type("application/xml;charset=UTF-8");
		Response response=client.post(xml);
		InputStream stream =(InputStream) response.getEntity();
		String str= streamToString(stream);
		return str;
	} 
	
	
	public static String send(String uri,String operationName,String resp,String xml){
		Service service=new Service();
		String result=null;
		
		try {
			Call call=(Call) service.createCall();
			call.setTargetEndpointAddress(uri);
			//WSDL里面描述的接口名称
			call.setOperationName(operationName);
			//接口参数
			call.addParameter(resp, org.apache.axis.encoding.XMLType.XSD_DATE, javax.xml.rpc.ParameterMode.IN);
			//设置返回值类型
			call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
			result=(String) call.invoke(new Object[]{xml});
			
		} catch (ServiceException e) {
			throw new RuntimeException(e);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		
		return result;
		
	}
	
	
}
