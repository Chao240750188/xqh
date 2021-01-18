package com.essence.business.xqh.common.util;

import org.apache.log4j.Logger;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public  final class ExcelUtil {

    private static Logger logger  =Logger.getLogger(ExcelUtil.class);
    public final static String xls="xls";
    public final static String xlsx="xlsx";

    /**
     * 判断excel是属于xls类型还是 xlsx类型
     * @param file
     * @return
     */

    public static Workbook getWorkBook(MultipartFile file) {
        //获得文件名
        String fileName = file.getOriginalFilename();
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        try {
            //获取excel文件的io流
            InputStream is = file.getInputStream();
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if(fileName.endsWith("xls")){
                //2003
                workbook = new HSSFWorkbook(is);
            }else if(fileName.endsWith("xlsx")){
                //2007
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            e.getMessage();
        }
        return workbook;
    }


    /**
     * 检查文件类型是否符合要求
     * @param file
     * @return
     */
    public static String checkFile(MultipartFile file) {
        String flog="";
        if (null == file) {
            logger.error("文件不存在！");
            flog="文件不存在";
        } else {
            //获得文件名
            String fileName = file.getOriginalFilename();
            //判断文件是否是excel文件
            if (!fileName.endsWith("xls") && !fileName.endsWith("xlsx") && !fileName.endsWith("csv")) {
                logger.error(fileName + "不是excel或csv文件");
                flog="不是excel或csv文件";
            } else if(fileName.endsWith("xls") || fileName.endsWith("xlsx")){
                flog="excel";
            }else if(fileName.endsWith("csv")){
                flog="csv";
            }
        }
        return flog;
    }

    /**
     * 格式化数据类型
     * @param cell
     * @return
     */

    public static String  getCellValue(Cell cell){
        String cellValue="";
        if(cell == null){
            return "";
        }
        //把数字当成String来读，避免出现1读成1.0的情况
        if((cell.getCellType() == Cell.CELL_TYPE_NUMERIC) && (!DateUtil.isCellDateFormatted(cell))){
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }

        //判断数据的类型
        switch (cell.getCellType()){
            case Cell.CELL_TYPE_NUMERIC: //数字
                //处理日期格式
                if (DateUtil.isCellDateFormatted(cell)){
                    Date date = cell.getDateCellValue();
                    long dateTime = date.getTime();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    cellValue = dateFormat.format(new Date(dateTime));
                }else{
                    //处理数字格式
                    cellValue = String.valueOf(cell.getNumericCellValue());
                }

                //cell的数字类型默认为double类型
                break;
            case Cell.CELL_TYPE_STRING: //字符串
                cellValue=String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: //Boolean
                cellValue=String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: //公式
                cellValue=String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK: //空值
                cellValue= "";
                break;
            case Cell.CELL_TYPE_ERROR: //故障
                cellValue= "非法字符";
                break;
            default:
                cellValue=  "未知类型";
                break;
        }
        return cellValue;
    }




    /**
     * 判断日期格式是否合法
     * @param dateData   日期数据
     * @param parseFormat  日期格式
     * @return
     */
    public static boolean isValidDate(String dateData,String parseFormat){
        boolean convertSuccess=true;
        SimpleDateFormat format = new SimpleDateFormat(parseFormat);
        try {
                  // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
                format.setLenient(false);
                format.parse(dateData);
                  } catch (ParseException e) {
                      // e.printStackTrace();
             // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
                       convertSuccess=false;
                   }
               return convertSuccess;
         }
    
    
    public static List<String[]> readFiles(MultipartFile mutilpartFile,int rowNumber){
    	// 获得Workbook工作薄对象
		Workbook workbook = ExcelUtil.getWorkBook(mutilpartFile);
		// 创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
		String[] columnName = null; // 存放字段名
		List<String[]> columnData = null; // 存放行数据
		if (workbook != null) {
			columnData = new ArrayList<String[]>();// 存放行数据
			// 获得每一个工作簿
			for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
				// 获得当前sheet工作表
				Sheet sheet = workbook.getSheetAt(sheetNum);
				if (sheet == null) {
					continue;
				}
				// 获得当前sheet的开始行
				int firstRowNum = sheet.getFirstRowNum();
				// 获得当前sheet的结束行
				int lastRowNum = sheet.getLastRowNum();

				// 获取第一行字段名
				Row firstRow = sheet.getRow(firstRowNum);
				if (firstRow == null) {
					continue;
				}
				int firstCellNum1 = firstRow.getFirstCellNum();
				int lastCellNum1 = firstRow.getLastCellNum();
				columnName = new String[lastCellNum1]; // 存放字段名
				for (int i = firstCellNum1; i < lastCellNum1; i++) {
					Cell cell = firstRow.getCell(i);
					columnName[i] = ExcelUtil.getCellValue(cell);
				}
				// 遍历文件中的行数据
				if (lastRowNum >= 1) {
					// 获取当前工作薄的每一行，循环除了第一行的所有行
					for (int rowNum = firstRowNum + rowNumber; rowNum <= lastRowNum; rowNum++) {

						// 获得当前行
						Row row = sheet.getRow(rowNum);
						if (row == null) {
							continue;
						}

						// 获得当前行的开始列
						int firstCellNum = row.getFirstCellNum();

						String[] rowData = new String[lastCellNum1];// 行数据
						// 循环当前行,将列存入String[]中
						for (int cellNum = firstCellNum; cellNum < lastCellNum1; cellNum++) {
							Cell cell = row.getCell(cellNum);
							if(cell!=null) {
								rowData[cellNum] =ExcelUtil.getCellValue(cell);
							}else {
								rowData[cellNum]="";
							}
						}

						columnData.add(rowData);
					}
				}
			}
		}
		return columnData;
    }
}

