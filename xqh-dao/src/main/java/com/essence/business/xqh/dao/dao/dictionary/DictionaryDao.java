package com.essence.business.xqh.dao.dao.dictionary;

import com.essence.business.xqh.dao.entity.dictionary.Dictionary;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 数据字典数据访问接口层
 */
@Repository
public interface DictionaryDao extends EssenceJpaRepository<Dictionary, String> {
    public int countById(String id);
}