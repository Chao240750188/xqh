package com.essence.business.xqh.web.attachFile;

import com.essence.business.xqh.api.attachFile.AttachFileService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.dao.entity.attachFile.AttachFile;
import com.essence.framework.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author NoBugNoCode
 *
 * 2020年4月8日 下午4:32:45
 */
@Controller
@RequestMapping("attachFile")
public class AttachFileAction {
    @Autowired
    private AttachFileService fileService;
	
    /**
     * 上传文件（支持多文件上传）
     * @param groupId
     * @param files
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/upload/{groupId}", method = RequestMethod.POST)
    public @ResponseBody
    SystemSecurityMessage upload(@PathVariable(value="groupId") String groupId, @RequestParam MultipartFile[] files, HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<AttachFile> list = null;
		if(null !=files && files.length>0){
			list=fileService.uploadFile(groupId,files);
		}
		SystemSecurityMessage sm = new SystemSecurityMessage();
		if(list.size() > 0){
			 sm.setCode("ok");
		     sm.setInfo("上传完成！");
		     sm.setResult(list);
		}else{
			sm.setCode("error");
		    sm.setInfo("上传失败！");
		}
		files = null;
		response.setContentType("text/html;charset=UTF-8");
		return sm;
    }
    /**
     * 根据group更新上传文件（支持多文件上传）
     * @param groupId
     * @param files
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/update/{groupId}", method = RequestMethod.POST)
    public @ResponseBody
    SystemSecurityMessage updateByGroup(@PathVariable(value="groupId") String groupId, @RequestParam MultipartFile[] files, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	List<AttachFile> list = null;
    	if(null !=files && files.length>0){
    		list=fileService.updateByGroup(groupId,files);
    	}
    	SystemSecurityMessage sm = new SystemSecurityMessage();
    	if(list.size() > 0){
    		sm.setCode("ok");
    		sm.setInfo("更新上传附件完成！");
    		sm.setResult(list);
    	}else{
    		sm.setCode("error");
    		sm.setInfo("上传失败！");
    	}
    	files = null;
    	response.setContentType("text/html;charset=UTF-8");
    	return sm;
    }
    
    /**
     * 更新文件（限制单文件上传）
     * @param files
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/update/{groupId}/{id}", method = RequestMethod.POST)
    public @ResponseBody
    SystemSecurityMessage update(@PathVariable(value="groupId") String groupId, @PathVariable(value="id") String id, @RequestParam MultipartFile files, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	SystemSecurityMessage sm = new SystemSecurityMessage();
		if(fileService.updateFile(groupId,id,files)!=null){
			sm.setCode("ok");
    		sm.setInfo("上传完成！");
		}else{
			sm.setCode("error");
			sm.setInfo("上传失败！");
		}
    	files = null;
    	response.setContentType("text/html;charset=UTF-8");
    	return sm;
    }

    /**
     * 下载文件，并自动拼装上格式化的文件名前缀
     * @param id
     * @param preName
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/downLoad/{id}/{preName}", method = RequestMethod.GET)
    public void downLoad(@PathVariable(value="id") String id, @PathVariable(value="preName") String preName, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	preName = StrUtil.unescape(preName.replaceAll("-_-_-", "%"));
        fileService.downloadFile(request,response,id,preName);
    }
    
    /**
     * 下载文件
     * @param id
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/downLoad/{id}", method = RequestMethod.GET)
    public void downLoad(@PathVariable(value="id") String id, HttpServletRequest request, HttpServletResponse response) throws Exception {
        fileService.downloadFile(request,response,id,null);
    }

    /**
     * 删除一条记录
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteFile/{id}", method = RequestMethod.GET)
    public @ResponseBody
    SystemSecurityMessage deleteFile(@PathVariable(value="id") String id) {
        fileService.deleteFile(id);
        return new SystemSecurityMessage("ok","删除成功！",null);
    }

    /**
     * 查询一条记录
     * @param id
     * @return
     */
    @RequestMapping(value = "/queryFile/{id}", method = RequestMethod.GET)
    public @ResponseBody
    SystemSecurityMessage queryFile(@PathVariable(value="id") String id) {
    	AttachFile element = fileService.queryFile(id);
        SystemSecurityMessage sm = new SystemSecurityMessage();
        if (element != null) {
            List<AttachFile> list = new ArrayList<AttachFile>();
            list.add(element);
            sm.setResult(list);
            sm.setCode("ok");
            sm.setInfo("取数成功！");
        } else {
            sm.setCode("error");
            sm.setInfo("无数据");
        }
        return sm;
    }

    /**
     * 获取一个文件组的所有记录
     * @param groupId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getFileList/{groupId}",method = RequestMethod.GET)
    public @ResponseBody
    SystemSecurityMessage getFileList(@PathVariable(value="groupId") String groupId) throws Exception {
        SystemSecurityMessage sm = new SystemSecurityMessage();
        List<AttachFile> list = fileService.findByGroupId(groupId);
        if (list.size() > 0) {
            sm.setResult(list);
            sm.setCode("ok");
            sm.setInfo("查询成功！");
        } else {
            sm.setCode("nodata");
            sm.setInfo("无数据");
        }
        return sm;
    }

    /**
     * 打开管理附件页面
     * @param groupId
     * @param request
     * @return
     */
    @RequestMapping("/list")
    public String listFile(String groupId, HttpServletRequest request) {
        List<AttachFile> list = fileService.findByGroupId(groupId);
        request.setAttribute("list", list);
        return "other/attachment";
    }
    
    /**
     * 打开管理附件页面，下载文件时强制重命名为预定义文件名
     * @param groupId
     * @param pre
     * @param request
     * @return
     */
    @RequestMapping("/manage")
    public String manageFile(String groupId,String pre, HttpServletRequest request) {
		List<AttachFile> list = fileService.findByGroupId(groupId);
		request.setAttribute("list", list);
		request.setAttribute("pre", pre);
    	return "other/attachment";
    }
    /**
     * 预览文件，原文件
     * @param id
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/preview/{id}", method = RequestMethod.GET)
    public @ResponseBody
    void preview(@PathVariable(value="id") String id, HttpServletRequest request, HttpServletResponse response) throws Exception {
         fileService.previewFile(request,response,id);
    }
    
    /**
     * 预览文件，指定大小进行预览，支持图片
     * @param id
     * @param size 60x60
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/preview/{id}/{size}", method = RequestMethod.GET)
    public @ResponseBody
    void preview(@PathVariable(value="id") String id, @PathVariable(value="size") String size, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	fileService.previewFile(request,response,id,size,true);
    }
    
    /**
     * 预览文件，指定大小进行预览，支持图片
     * @param id
     * @param size 60x60
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/preview/{id}/{size}/{cut}", method = RequestMethod.GET)
    public @ResponseBody
    void preview(@PathVariable(value="id") String id, @PathVariable(value="size") String size, @PathVariable(value="cut") boolean cut, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	fileService.previewFile(request,response,id,size,cut);
    }
}