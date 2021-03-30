package com.essence.business.xqh.web.attachFile;

import com.essence.business.xqh.api.attachFile.SysAttachService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.dao.entity.attachFile.SysAttach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 资料库控制层
 * @author NoBugNoCode
 *
 * 2019年11月14日 下午3:58:50
 */
@Controller
@RequestMapping("/sysAttach")
public class SysAttachController {
	 
	@Autowired
	private SysAttachService sysAttachService;
	
	/**
	 * 新增资料库文档类型对象
	 * @param sysAttach
	 * @return
	 */
    @RequestMapping(value = "addSysAttach", method = RequestMethod.POST)
    public @ResponseBody
    SystemSecurityMessage addSysAttach(@RequestBody SysAttach sysAttach){
		SysAttach addSysAttach = sysAttachService.addSysAttach(sysAttach);
		return new SystemSecurityMessage("ok", "添加资料库文档类型成功！", addSysAttach);
    }
    
    /**
     * 删除资料库文档类型对象
     * @param id
     * @return
     */
    @RequestMapping(value = "deleteSysAttach/{id}", method = RequestMethod.GET)
    public @ResponseBody SystemSecurityMessage deleteSysAttach(@PathVariable String id){
    	sysAttachService.deleteSysAttach(id);
    	return new SystemSecurityMessage("ok", "删除资料库文档类型成功！", id);
    }
    
    /**
     * 修改资料库文档类型
     * @param sysAttach
     * @return
     */
    @RequestMapping(value = "updateSysAttach", method = RequestMethod.POST)
    public @ResponseBody SystemSecurityMessage updateSysAttach(@RequestBody SysAttach sysAttach){
		return new SystemSecurityMessage("ok", "修改资料库文档类型成功！", sysAttachService.updateSysAttach(sysAttach));
    }
    
    /**
     * 查询资料库类型列表
     * @return
     */
    @RequestMapping(value = "findAllSysAttach", method = RequestMethod.GET)
    public @ResponseBody SystemSecurityMessage findAllSysAttach(){
    	return new SystemSecurityMessage("ok", "查询资料库文档类型成功！", sysAttachService.findAllSysAttach());
    }
}