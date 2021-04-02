package com.essence.business.xqh.service.dictionary;

import com.essence.business.xqh.api.dictionary.DictionaryService;
import com.essence.business.xqh.dao.dao.dictionary.DictionaryDao;
import com.essence.business.xqh.dao.entity.dictionary.Dictionary;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import com.essence.framework.util.DateUtil;
import com.essence.framework.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 数据字典服务实现层
 */
@Transactional
@Service
public class DictionaryServiceImpl implements DictionaryService {
    public static final String FINAL_DICTIONARY = "1";
    @Autowired
    DictionaryDao dictionarydao;

    /**
     * 添加一条数据字典数据
     *
     * @param aDictionary 添加的数据字典对象
     */
    @Override
    public Dictionary addDictionary(Dictionary aDictionary) {
        aDictionary.setOrder(BigDecimal.valueOf(DateUtil.getCurrentTime().getTime()));
        if(dictionarydao.countById(aDictionary.getId())>0){
            return null;
        }else{
            return dictionarydao.save(aDictionary);
        }
    }

    /**
     * 根据主键删除一条数据字典数据
     *
     * @param PK 主键
     */
    @Override
    public void deleteDictionary(String PK) {
        if (!FINAL_DICTIONARY.equals(PK))
            dictionarydao.deleteById(PK);
    }

    /**
     * 更新一条数据字典
     *
     * @param aDictionary 要更新的对象
     */
    @Override
    public Dictionary updateDictionary(Dictionary aDictionary) {
        if (aDictionary.getId().equals(FINAL_DICTIONARY)) {
            aDictionary.setParentId(null);
        }
        return dictionarydao.save(aDictionary);
    }

    /**
     * 根据主键查询一条数据字典
     *
     * @param PK 主键
     * @return 查询到的数据字典对象
     */
    @Override
    public Dictionary queryDictionary(String PK) {
        return dictionarydao.findOne(PK);
    }

    /**
     * 根据主键批量更新数据字典
     *
     * @param parameters 对象集
     */
    @Override
    public void updateBatchDictionary(List<Dictionary> parameters) {
        if (null != parameters && parameters.size() > 0) {
            for (Dictionary d : parameters) {
                updateDictionary(d);
            }
        }
    }

    /**
     * 根据主键批量删除数据字典
     *
     * @param PKs 主键集
     */
    @Override
    public void deleteBatchDictionary(List<String> PKs) {
        if (null != PKs && PKs.size() > 0) {
            for (String d : PKs) {
                deleteDictionary(d);
            }
        }
    }

    /**
     * 查询所有数据字典数据
     *
     * @return 数据字典对象集
     */
    @Override
    public List<Dictionary> queryDictionaryList() {
        return dictionarydao.findAll();
    }

    /**
     * 批量添加数据字典数据
     *
     * @param parameters 数据字典对象集
     * @return int[] 每个对象添加的成功数量
     */
    @Override
    public void addBatchDictionary(List<Dictionary> parameters) {
        if (null != parameters && parameters.size() > 0) {
            for (Dictionary d : parameters) {
                addDictionary(d);
            }
        }
    }

    /**
     * 分页查询数据字典数据
     *
     * @param param 条件
     * @return 分页结果
     */
    @Override
    public Paginator<Dictionary> queryDictionaryListPage(PaginatorParam param) {
        return dictionarydao.findAll(param);
    }

    @Override
    public Dictionary getRootDictionary() {
        Dictionary root = dictionarydao.findOne(FINAL_DICTIONARY);
        return root;
    }
}