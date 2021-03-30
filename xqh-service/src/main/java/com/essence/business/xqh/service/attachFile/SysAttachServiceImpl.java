package com.essence.business.xqh.service.attachFile;

import javax.transaction.Transactional;

import com.essence.business.xqh.api.attachFile.SysAttachService;
import com.essence.business.xqh.common.URMS.pojo.UserInfo;
import com.essence.business.xqh.common.URMS.util.URMSUtil;
import com.essence.business.xqh.dao.attachFile.SysAttachDao;
import com.essence.business.xqh.dao.entity.attachFile.SysAttach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.essence.framework.util.DateUtil;
import com.essence.framework.util.StrUtil;

import java.util.List;

@Service
public class SysAttachServiceImpl implements SysAttachService {

	@Autowired
	private SysAttachDao sysAttachDao;
	
	/**
	 * 添加资料库文档类型对象
	 * @return
	 */
	@Override
	@Transactional
	public SysAttach addSysAttach(SysAttach sysAttach) {
		UserInfo userInfo = URMSUtil.getCurrentUserInfo();
		if(StrUtil.isEmpty(sysAttach.getId()))
			sysAttach.setId(StrUtil.getUUID());
		sysAttach.setCreateTime(DateUtil.getCurrentTime());
		if(userInfo!=null){
			sysAttach.setCreateUser(userInfo.getUserId());
			sysAttach.setCreateUserName(userInfo.getUserName());
		}
		return sysAttachDao.save(sysAttach);
	}

	@Override
	@Transactional
	public void deleteSysAttach(String id) {
		sysAttachDao.delete(id);
	}

	@Override
	@Transactional
	public SysAttach updateSysAttach(SysAttach sysAttach) {
		return sysAttachDao.save(sysAttach);
	}

	@Override
	public List<SysAttach> findAllSysAttach() {
		return sysAttachDao.findAll();
	}
}
