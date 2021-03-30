package com.essence.business.xqh.service.attachFile;


import java.util.ArrayList;

import java.util.List;

import javax.transaction.Transactional;

import com.essence.business.xqh.api.attachFile.AttachFileService;
import com.essence.business.xqh.api.attachFile.SysDocumentService;
import com.essence.business.xqh.common.URMS.pojo.UserInfo;
import com.essence.business.xqh.common.URMS.util.URMSUtil;
import com.essence.business.xqh.dao.attachFile.SysDocumentDao;
import com.essence.business.xqh.dao.entity.attachFile.AttachFile;
import com.essence.business.xqh.dao.entity.attachFile.SysDocument;
import com.essence.framework.jpa.Paginator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.essence.framework.jpa.Criterion;
import com.essence.framework.jpa.PaginatorParam;
import com.essence.framework.util.DateUtil;
import com.essence.framework.util.StrUtil;

@Service
public class SysDocumentServiceImpl implements SysDocumentService {

	@Autowired
	private SysDocumentDao sysDocumentDao;
	
	@Autowired
	private AttachFileService attachFileService;
	
	@Override
	@Transactional
	public SysDocument addSysDocument(SysDocument sysDocument) {
		//获取当前登录用户
		UserInfo currentUserInfo = URMSUtil.getCurrentUserInfo();
		if(StrUtil.isEmpty(sysDocument.getId()))
			sysDocument.setId(StrUtil.getUUID());
		sysDocument.setCreateTime(DateUtil.getCurrentTime());
		if(currentUserInfo!=null){
			sysDocument.setUserId(currentUserInfo.getUserId());
			sysDocument.setUserName(currentUserInfo.getUserName());
		}
		return sysDocumentDao.save(sysDocument);
	}

	@Override
	@Transactional
	public void deleteSysDocument(String id) {
		//删除相关附件
		sysDocumentDao.delete(id);		
		//删除相关文档
		attachFileService.deleteFileByGroupId(id);
	}

	@Override
	public Paginator<SysDocument> findDocumentByAttachPage(PaginatorParam param) {
		//时间排序
		List<Criterion> orders = param.getOrders();
		if(orders==null) {
			orders = new ArrayList<Criterion>();
			param.setOrders(orders);
		}
		Criterion criterion = new Criterion();
		criterion.setFieldName("createTime");
		criterion.setOperator(Criterion.DESC);
		orders.add(criterion);
		Paginator<SysDocument> item = sysDocumentDao.findAll(param);
		List<SysDocument> items = item.getItems();
		for (SysDocument sysDocument:items) {
			List<AttachFile> attachFiles = attachFileService.findByGroupId(sysDocument.getId());
			sysDocument.setAttachFile(attachFiles);
		}
		return item;
	}

}
