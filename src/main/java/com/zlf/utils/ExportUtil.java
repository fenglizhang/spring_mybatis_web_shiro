package com.zlf.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * HSSF是POI工程对Excel 97(-2007)文件操作的纯Java实现 
 * XSSF是POI工程对Excel 2007 OOXML (.xlsx)文件操作的纯Java实现
 * 
 * 从POI 3.8版本开始，提供了一种基于XSSF的低内存占用的API----SXSSF
 * 
 * SXSSF通过一个滑动窗口来限制访问Row的数量从而达到低内存占用的目录，XSSF可以访问所有行。旧的行数据不再出现在滑动窗口中并变得无法访问，
 * 与此同时写到磁盘上。 在自动刷新的模式下，可以指定窗口中访问Row的数量，从而在内存中保持一定数量的Row。当达到这一数量时，在窗口中产生新的Row数据，
 * 并将低索引的数据从窗口中移动到磁盘中。 或者，滑动窗口的行数可以设定成自动增长的。它可以根据需要周期的根据一次明确的flushRow(int
 * keepRows)调用来进行修改。
 * 
 * @author Administrator
 * 
 */
public class ExportUtil {
	private static final Logger logger = LoggerFactory
			.getLogger(ExportUtil.class);

	/**
	 * 此为老版本poi
	 * 
	 * @param filePath excel模版所在全路徑，maven工程有两种区分，放在src/main/java下和放在web-app下，本次是web-app/web-content下
	 * @param list    要导入的内容List<里面这个list是放的每一行要写入的内容>
	 * @return
	 */
	public static Workbook buildWorkBook(String filePath,
			List<List<String>> list) {
		FileInputStream fs = null;
		File file = new File(filePath.trim());
		String fileName = file.getName();
		if (file.exists()) {
			try {
				fs = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				logger.error("获取模版文件流时发生异常：", e);
			}
		}
		String suffex = fileName.substring(fileName.lastIndexOf("."));// 获取文件名的后缀。
		Workbook wb = null;
		try {
			wb = suffex.equals(".xlsx") ? new XSSFWorkbook(fs)
					: new HSSFWorkbook(fs);
			wb.setSheetName(0, fileName.toString());
			Sheet sheet = wb.getSheetAt(0);
			int dataRows = list.size();
			for (int i = 0; i < dataRows; i++) {
				Row row = sheet.createRow(i + 2);
				List<String> currentLineValue = list.get(i);
				int cells = currentLineValue.size();
				for (int j = 0; j < cells; j++) {
					Cell cell = row.createCell(j, Cell.CELL_TYPE_STRING);
					if (currentLineValue == null
							|| currentLineValue.equals("null")) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(String.valueOf(currentLineValue
								.get(j)));
					}
				}
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return wb;
	}
	
	/**
	 * POI 3.8最新版本
	 * 模版是在【maven项目】src/main/java目录下的做法或者在【web项目】src下
	 * @param filePath  excel文件名，不含后缀。
	 * @param colspan	报表最后合计列需要合并的单元格数
	 * @param list		要写入excel的内容集合
	 * @param totalResult
	 * @return
	 */
	public static Workbook buildWorkBookNew(String filePath,int colspan,List<List<String>> list,List<String> totalResult){
		//这里既定了excel模版在web项目src/template文件夹内。
		Resource resource=new ClassPathResource("template/"+filePath.toString()+".xlsx");
		SXSSFWorkbook sweb=null;
		try {
			XSSFWorkbook web=new XSSFWorkbook(resource.getInputStream());
			sweb=new SXSSFWorkbook(web,100);
			sweb.setSheetName(0, filePath.toString());
			Sheet sheet =sweb.getSheetAt(0);
			String nameString=sheet.getSheetName();
			int dataRows=list.size();
			for(int i=0;i< dataRows;i++){
				Row row=sheet.createRow(i+2);
				List<String> current=list.get(i);
				int cells=current.size();
				for(int j=0;j<cells;j++){
					Cell cell=row.createCell(j);
					if(null==current.get(j)||current.get(j).equals("")){
						cell.setCellValue("");
					}else{
						cell.setCellValue(String.valueOf(current.get(j)));
					}
				}
			}
			
			
			if(totalResult!=null){
				Row row=sheet.createRow(list.size()+2);
				//和并列---所在的行数
				int contRowNumber=list.size()+2;
				sheet.addMergedRegion(new CellRangeAddress(contRowNumber, contRowNumber, 0, colspan));
				//填充最后的统计行
				for (int i = 0; i < totalResult.size(); i++) {
					Cell cell = null;
					if(i==0){
						cell=row.createCell(i);
						CellStyle cellStyle=sweb.createCellStyle();
						cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
						cell.setCellStyle(cellStyle);
					}else{
						row.createCell(i+colspan,Cell.CELL_TYPE_STRING);
					}
					
					cell.setCellValue(totalResult.get(i));
				}
			}
			
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return sweb;
	}
	
	
	
	

	/**
	 * 将生成内容excel返回前台页面
	 * 
	 * @param wb
	 * @param response
	 */
	public static void renderExcel(Workbook wb, HttpServletResponse response) {
		response.setContentType("application/octet-stream");

		try {
			response.addHeader("Content-Disposition", "attachment;filename="
					+ URLEncoder.encode(wb.getSheetName(0) + ".xlsx", "utf-8"));
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
