package com.essence.business.xqh.web.attachFile;

import com.essence.business.xqh.api.attachFile.SysDocumentService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.dao.entity.attachFile.SysDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.essence.framework.jpa.PaginatorParam;


/**
 * 资料库控制层
 * @author NoBugNoCode
 *
 * 2019年11月14日 下午3:58:50
 */
@Controller
@RequestMapping("/sysDocument")
public class SysDocumentController {
	 
	@Autowired
	private SysDocumentService sysDocumentService;

	/**
	 * 新增资料库文档记录对象
	 * @param sysDocument
	 * @return
	 */
    @RequestMapping(value = "addSysDocument", method = RequestMethod.POST)
    public @ResponseBody SystemSecurityMessage addSysDocument(@RequestBody SysDocument sysDocument){
		SysDocument addSysDocument = sysDocumentService.addSysDocument(sysDocument);
		return new SystemSecurityMessage("ok", "新增资料库文档记录对象成功！", addSysDocument);
    }
    
    /**
     * 删除资料库文档记录对象
     * @param id
     * @return
     */
    @RequestMapping(value = "deleteSysDocument/{id}", method = RequestMethod.GET)
    public @ResponseBody
	SystemSecurityMessage deleteSysAttach(@PathVariable String id){
    	sysDocumentService.deleteSysDocument(id);
    	return new SystemSecurityMessage("ok", "删除料库文档记录对象成功！", id);
    }
    
   
    @RequestMapping(value = "findDocumentByAttachPage", method = RequestMethod.POST)
    public @ResponseBody SystemSecurityMessage findDocumentByAttachPage(@RequestBody PaginatorParam param){
    	return new SystemSecurityMessage("ok", "根据资料库类型分页查询资料对象成功！", sysDocumentService.findDocumentByAttachPage(param));
    }
}