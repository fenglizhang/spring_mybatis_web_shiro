package com.zlf.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

public class FileUtils {
	private static final Logger logger = Logger.getLogger(FileUtils.class);

	/**
	 * 从指定路径下载模版：本方法按maven工程--webapp/excelModel/快递公司配置导入模版.xlsx
	 * 
	 * @param response
	 * @param request
	 * @param fileName
	 *            比如：快递公司配置导入模版.xlsx
	 */
	public static void downloadFiles(HttpServletRequest request,
			HttpServletResponse response, String fileName) {
		response.setContentType("application/octet-stream");
		response.setCharacterEncoding("utf-8");
		String path = request.getSession().getServletContext()
				.getRealPath("excelMode");
		path = path.replace("\\", "/");
		if (!path.endsWith("/")) {
			path += "/";
		}
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		String fulFileName = path + fileName;

		FileInputStream fs = null;
		BufferedInputStream buff = null;
		OutputStream myout = null;
		File fi = new File(fulFileName.trim());
		try {
			if (fi.exists()) {
				String fiName = fi.getName();
				fs = new FileInputStream(fi);
				response.addHeader(
						"Content-Disposition",
						"attachment;filename="
								+ URLEncoder.encode(fiName, "utf-8"));
				buff = new BufferedInputStream(fs);
				byte[] b = new byte[1024];
				long k = 0;
				myout = response.getOutputStream();
				while (k < file.length()) {
					int j = buff.read(b, 0, 1024);
					k += j;
					myout.write(b, 0, j);
				}
				buff.close();
			} else {
				PrintWriter p = null;
				p = response.getWriter();
				p.write("文件不存在");
				p.close();
			}
		} catch (IOException e) {
			logger.error("文件不存在导致下载模版异常！" + e);
		} finally {
			if (myout != null) {
				try {
					myout.flush();
					myout.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
	}

	/**
	 * 文件上传
	 * 
	 * @param upFilePath
	 *            上传文件的路径
	 * @param upFileName
	 *            上传文件名
	 * @param realName
	 *            上传到服务器要保存的文件名
	 * @param realPath
	 *            文件上传后的保存路径
	 */
	public static void uploadFiles(String upFilePath, String upFileName,
			String realName, String realPath) {
		FileOutputStream fos = null;
		FileInputStream fis = null;
		Date time = new Date();
		String dates = new SimpleDateFormat().format(time);
		try {
			realPath.replace("\\", "/");
			if (!realPath.endsWith("/")) {
				realPath += "/";
			}
			File file = new File(realPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			String importFileName = realName;
			fos = new FileOutputStream(realPath + "/" + importFileName);
			fis = new FileInputStream(upFilePath);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);

			}
			fos.close();
			fis.close();
			// 完成上传
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}

			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}

	}

	/**
	 * 解析前台导入的excel数据表单
	 * 
	 * @param request
	 * @return
	 */
	public static Sheet excelImportToDBAnalyse(HttpServletRequest request) {
		InputStream is = null;
		SXSSFWorkbook sweb = null;
		Sheet sheet = null;
		// 解析器
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		// 判断request是否是文件上传请求，即多部分请求
		if (multipartResolver.isMultipart(request)) {
			// 转换成多部分请求
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			// 获取request中的所有文件名
			Iterator<String> iter = multiRequest.getFileNames();
			while (iter.hasNext()) {
				// 取得每个上传的文件
				MultipartFile file = multiRequest.getFile(iter.next());
				if (file != null) {
					try {
						is = new ByteArrayInputStream(file.getBytes());
					} catch (IOException e) {
						logger.error(e.getMessage());
					}
				}
			}
		}

		try {
			XSSFWorkbook web = new XSSFWorkbook(is);
			sweb = new SXSSFWorkbook(web, 100);
			// 获取表单
			sheet = sweb.getSheetAt(0);
			/*
			 * //获取总行数 int allRows=sheet.getPhysicalNumberOfRows(); //获取每一行 for
			 * (int i = 2; i < allRows; i++) { Row
			 * row=sheet.getRow(i);//从第三行开始获取 //获取每个单元格,单元格个数是知道的，10个吧 for(int
			 * j=0;j<10;j++){ Cell cell = row.getCell(j);//得到每个单元格对象 } }
			 */
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		return sheet;
	}

	/**
	 * poi getCellType门类说明 poi 
	 * getCellType类型说明	 
	 * CELL_TYPE_NUMERIC 数值型 0
	 * CELL_TYPE_STRING 字符串型 1
	 * CELL_TYPE_FORMULA 公式型 2 
	 * CELL_TYPE_BLANK 空值 3
	 * CELL_TYPE_BOOLEAN 布尔型 4 
	 * CELL_TYPE_ERROR 错误 5
	 * 
	 * @param cell
	 * @return
	 */
	public static String getCellValue(Cell cell) {
		String result="";
		if(cell !=null){
			switch (cell.getCellType()) {
			// 数字类型  +日期类型
	        case HSSFCell.CELL_TYPE_NUMERIC:
	            if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式  
	                SimpleDateFormat sdf = null;  
	                if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {  
	                    sdf = new SimpleDateFormat("HH:mm");  
	                } else {// 日期  
	                    sdf = new SimpleDateFormat("yyyy-MM-dd");  
	                }  
	                Date date = cell.getDateCellValue();  
	                result = sdf.format(date);  
	            } else if (cell.getCellStyle().getDataFormat() == 58) {  
	                // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)  
	                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	                double value = cell.getNumericCellValue();  
	                Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);  
	                result = sdf.format(date);  
	            } else {  
	                DecimalFormat df=new DecimalFormat();
	                df.setGroupingUsed(false);
	                result=String.valueOf(df.format(cell.getNumericCellValue()));
	            }  
	            break;  
	         // String类型  
	        case HSSFCell.CELL_TYPE_STRING:
	            result = String.valueOf(cell.getStringCellValue());
	            break;  
	        case HSSFCell.CELL_TYPE_BLANK:  
	            result = "";  
	        default:  
	            result = "";  
	            break;  
	        }  
		}
		
        return result;  
	}

}
