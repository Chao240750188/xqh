package com.essence.business.xqh.api.attachFile;

import com.essence.business.xqh.dao.entity.attachFile.SysDocument;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;

/**
 * 资料库业务管理
 * @author NoBugNoCode
 *
 * 2019年11月14日 下午4:00:05
 */
public interface SysDocumentService{
	/**
	 * 添加资料库记录
	 * @param sysDocument
	 * @return
	 */
	SysDocument addSysDocument(SysDocument sysDocument);
	/**
	 * 添加资料库记录
	 * @param id
	 * @return
	 */
	void deleteSysDocument(String id);
	/**
	 * 根据资料库类型分页查询资料记录
	 * @param param
	 * @return
	 */
	Paginator<SysDocument> findDocumentByAttachPage(PaginatorParam param);
}