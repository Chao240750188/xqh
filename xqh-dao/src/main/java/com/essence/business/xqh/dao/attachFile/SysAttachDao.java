package com.essence.business.xqh.dao.attachFile;

import com.essence.business.xqh.dao.entity.attachFile.SysAttach;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 资料库类型管理数据交互层
 *
 * @author NoBugNoCode
 * <p>
 * 2019年11月14日 下午4:02:16
 */
@Repository
public interface SysAttachDao extends EssenceJpaRepository<SysAttach, String> {

}