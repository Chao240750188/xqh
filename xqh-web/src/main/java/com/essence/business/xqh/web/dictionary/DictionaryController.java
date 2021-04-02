package com.essence.business.xqh.web.dictionary;

import com.essence.business.xqh.api.dictionary.DictionaryService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.dao.entity.dictionary.Dictionary;
import com.essence.framework.jpa.PaginatorParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 数据字典控制层
 */
@RestController
@RequestMapping("/dictionary")
public class DictionaryController {
    @Autowired
    private DictionaryService dictionaryService;

    /**
     * 添加一条数据字典记录
     *
     * @param aDictionary 添加的对象
     * @return SystemSecurityMessage
     */
    @RequestMapping(value = "/addDictionary", method = RequestMethod.POST)
    public SystemSecurityMessage addDictionary(@RequestBody Dictionary aDictionary) {
        Dictionary u = dictionaryService.addDictionary(aDictionary);
        if (u == null) {
            return new SystemSecurityMessage("error", "添加失败！", "字典编号已经存在。");
        } else {
            return new SystemSecurityMessage("ok", "添加成功！", u);
        }
    }

    /**
     * 删除一条数据字典记录
     *
     * @param id 主键id
     * @return SystemSecurityMessage
     */
    @RequestMapping(value = "/deleteDictionary/{id}", method = RequestMethod.GET)
    public SystemSecurityMessage deleteDictionary(@PathVariable(value = "id") String id) {
        dictionaryService.deleteDictionary(id);
        return new SystemSecurityMessage("ok", "删除成功！", id);
    }

    /**
     * 更新一条数据字典记录
     *
     * @param aDictionary
     * @return SystemSecurityMessage
     * @see Dictionary 其中result属性值为Dictionary
     */
    @RequestMapping(value = "/updateDictionary", method = RequestMethod.POST)
    public SystemSecurityMessage updateDictionary(@RequestBody Dictionary aDictionary) {
        Dictionary u = dictionaryService.updateDictionary(aDictionary);
        if (u == null) {
            return new SystemSecurityMessage("error", "修改失败！", "字典编号已经存在。");
        } else {
            return new SystemSecurityMessage("ok", "修改成功！", u);
        }
    }

    /**
     * 查询一条数据字典记录
     *
     * @param id String 主键id
     * @return SystemSecurityMessage
     * @see Dictionary 其中result属性值为Dictionary
     */
    @RequestMapping(value = "/queryDictionary/{id}", method = RequestMethod.GET)
    public SystemSecurityMessage queryDictionary(@PathVariable(value = "id") String id) {
        Dictionary d = dictionaryService.queryDictionary(id);
        return new SystemSecurityMessage("ok", "查询成功！", d);
    }


    /**
     * 获取所有数据字典所有记录
     *
     * @return SystemSecurityMessage
     * @throws Exception
     * @see Dictionary 其中result属性值为List<Dictionary>
     */
    @RequestMapping(value = "/getDictionaryList", method = RequestMethod.GET)
    public SystemSecurityMessage getDictionaryList() throws Exception {
        return new SystemSecurityMessage("ok", "查询成功！", dictionaryService.queryDictionaryList());
    }

    /**
     * 分页获取数据字典所有记录
     *
     * @param param 条件过滤
     * @return SystemSecurityMessage
     * @throws Exception
     * @see Dictionary 其中result属性值为分页格式的的数据列表
     * @see com.essence.framework.jpa.Paginator 分页格式对象
     */
    @RequestMapping(value = "/getDictionaryListPage", method = RequestMethod.POST)
    public SystemSecurityMessage getDictionaryListPage(@RequestBody PaginatorParam param) throws Exception {
        return new SystemSecurityMessage("ok", "查询成功！", dictionaryService.queryDictionaryListPage(param));
    }

    /**
     * 获取所有资源-树型结构
     *
     * @return
     * @throws
     */
    @RequestMapping(value = "/getDictionaryTree", method = RequestMethod.GET)
    public SystemSecurityMessage getResourceTree() throws Exception {
        return new SystemSecurityMessage("ok", "查询成功！", dictionaryService.getRootDictionary());
    }
}