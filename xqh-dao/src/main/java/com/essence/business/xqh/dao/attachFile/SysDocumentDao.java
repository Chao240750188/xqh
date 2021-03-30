package com.essence.business.xqh.dao.attachFile;
import com.essence.business.xqh.dao.entity.attachFile.SysDocument;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 资料库类型管理数据交互层
 * 
 * @author NoBugNoCode
 *
 * 2019年11月14日 下午4:02:16
 */
@Repository
public interface SysDocumentDao extends EssenceJpaRepository<SysDocument, String> {

}