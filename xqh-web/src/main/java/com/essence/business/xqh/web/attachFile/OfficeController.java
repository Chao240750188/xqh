package com.essence.business.xqh.web.attachFile;


import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.framework.util.FileUtil;
import com.essence.framework.util.StrUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Map;


/**
 * 将html转化为offic文档，有excel（暂时只支持html table，其它元素忽略）和word(暂时无法支持包含表格的)
 *
 * @version 1.1 2018.4.2将html转原生excel的接口放出，并保留原方法
 */
@Controller
@RequestMapping(value = "/OfficeAction")
public class OfficeController {
    /**
     * 将html转换为excel，
     *
     * @param body 参数content：table本身的html
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/exportToExcel", method = RequestMethod.POST)
    public @ResponseBody
    SystemSecurityMessage exportToExcel(@RequestBody Map<String, String> body) throws Exception {
        String msg = body.get("content");
        String key = StrUtil.getUUID();
        String fileName = key + ".xls";
        String content = "<html xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:x=\"urn:schemas-microsoft-com:office:excel\" xmlns=\"http://www.w3.org/TR/REC-html40\"><head> <!--[if gte mso 9]> <xml> <x:ExcelWorkbook> <x:ExcelWorksheets> <x:ExcelWorksheet> <x:Name>Worksheet</x:Name> <x:WorksheetOptions> <x:DisplayGridlines/> </x:WorksheetOptions> </x:ExcelWorksheet> </x:ExcelWorksheets> </x:ExcelWorkbook> </xml> <![endif]--> </head><body>\r\n"
                + msg
                + "</body></html>";
        File file = FileUtil.getFile(content, fileName, "utf-8");
		/*HtmlExcel he=new HtmlExcel(msg, "导出数据");
		he.getExcelExportor().writeToFile(file);*/
        FileUtil.addFileInCache(key, file);
        SystemSecurityMessage id = new SystemSecurityMessage();
        id.setCode("ok");
        id.setInfo("导出成功");
        id.setResult(key);
        return id;
    }

    /**
     * html转换为word
     *
     * @param body 参数content：页面html
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/exportToWord", method = RequestMethod.POST)
    public @ResponseBody
    SystemSecurityMessage exportToWord(@RequestBody Map<String, String> body) throws Exception {
        String data = body.get("content");
        data = "<html xmlns:v=\"urn:schemas-microsoft-com:vml\"\r\n"
                + "xmlns:o=\"urn:schemas-microsoft-com:office:office\"\r\n"
                + "xmlns:w=\"urn:schemas-microsoft-com:office:word\"\r\n"
                + "xmlns:m=\"http://schemas.microsoft.com/office/2004/12/omml\"\r\n"
                + "xmlns=\"http://www.w3.org/TR/REC-html40\">\r\n" + data + "</html>";
        String key = StrUtil.getUUID();
        File file = FileUtil.getFile(data, key + ".doc", "utf-8");
        FileUtil.addFileInCache(key, file);
        SystemSecurityMessage id = new SystemSecurityMessage();
        id.setResult(key);
        return id;
    }

    /**
     * 将上边ajax方式生成的文件下载，下载完成后自动删除文件
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/download")
    public void download(HttpServletRequest request,
                         HttpServletResponse response) throws Exception {
        String key = request.getParameter("key");
        File file = FileUtil.getFileInCache(key);
        FileUtil.downloadFile(file.getName(), file, response);
        FileUtil.deleteFileInCache(key);
        FileUtil.deleteFile(file);
    }
}
