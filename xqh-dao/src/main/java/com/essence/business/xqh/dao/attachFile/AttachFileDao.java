package com.essence.business.xqh.dao.attachFile;

import com.essence.business.xqh.dao.entity.attachFile.AttachFile;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 数据访问接口层
 *
 * @author Gavin
 * @version 1.0 2018/01/08
 * @company Essence
 */
@Repository
public interface AttachFileDao extends EssenceJpaRepository<AttachFile, String> {
    public void deleteByGroupId(String groupId);

    public List<AttachFile> findByGroupIdOrderByCreateTimeAsc(String groupId);

    /**
     * 根据id查询数据
     *
     * @param id
     */
    @Query(value = "select t from AttachFile t where t.id=?1")
    public AttachFile findDataById(String id);

    @Query(value = "select t from AttachFile t where t.id in(?1)")
    public List<AttachFile> findDataByIdIn(List<String> idList);

    List<AttachFile> findByNameLike(String name);
}