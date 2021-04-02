package com.essence.business.xqh.api.dictionary;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.essence.business.xqh.dao.entity.dictionary.Dictionary;
import com.essence.framework.jpa.Paginator;
import org.springframework.web.multipart.MultipartFile;

import com.essence.framework.jpa.PaginatorParam;

/**
 * 数据字典服务接口层
 * @company Essence
 * @author Gavin
 * @version 1.0 2018/01/11
 */
public interface DictionaryService{
	/**
	* 添加一条数据字典新记录
	* @param aDictionary 添加的数据字典对象
	*/
	public Dictionary addDictionary(Dictionary aDictionary);
	
	/**
	* 根据主键删除一条数据字典记录
	* @param PK 主键
	*/
	public void deleteDictionary(String PK);
	
	/**
	* 根据主键更新一条数据字典
	* @param aDictionary 要更新的对象
	*/
	public Dictionary updateDictionary(Dictionary aDictionary);
	
	/**
	* 根据主键查询一条数据字典
	* @param PK 主键
	* @return 查询到的数据字典对象
	*/
	public Dictionary queryDictionary(String PK);
	
	/**
	*根据主键批量更新数据字典
	* @param parameters 对象集
	*/
	public void updateBatchDictionary(List<Dictionary> parameters);
	
	/**
	* 根据主键批量删除数据字典
	* @param PKs 主键集
	*/
	public void deleteBatchDictionary(List<String> PKs);

	/**
	* 查询所有数据字典
	* @return 数据字典对象集
	*/
	public List<Dictionary> queryDictionaryList();
	
	/**
	* 批量添加数据字典记录
	* @param parameters List<Dictionary>数据字典对象集
	*/
	public void addBatchDictionary(List<Dictionary> parameters);
	
	/**
	* 分页获取数据字典记录
	* @param param 条件
	* @return 分页结果
	*/
	public Paginator<Dictionary> queryDictionaryListPage(PaginatorParam param);
	public Dictionary getRootDictionary();
}