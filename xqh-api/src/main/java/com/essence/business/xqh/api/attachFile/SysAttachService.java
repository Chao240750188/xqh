package com.essence.business.xqh.api.attachFile;

import com.essence.business.xqh.dao.entity.attachFile.SysAttach;

import java.util.List;

/**
 * 资料库业务管理
 * @author NoBugNoCode
 *
 * 2019年11月14日 下午4:00:05
 */
public interface SysAttachService{

	/**
	 * 添加资料库文档类型
	 * @return
	 */
	SysAttach addSysAttach(SysAttach sysAttach);
	/**
	 * 删除资料库文档类型
	 * @return
	 */
	void deleteSysAttach(String id);
	/**
	 * 修改资料库文档类型
	 * @return
	 */
	SysAttach updateSysAttach(SysAttach sysAttach);
	/**
	 * 查询所有资料库文档类型
	 * @return
	 */
	List<SysAttach> findAllSysAttach();
	
}