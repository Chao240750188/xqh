package com.essence.business.xqh.dao.entity.dictionary;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
/**
 * 数据字典实体类
 */
@Entity
@Table(name = "SYS_DICTIONARY", schema = "XQH", catalog = "")
public class Dictionary implements Serializable {
	private static final long serialVersionUID = 550;
	
	/**数据编号*/
	@Id
	@Column(name = "C_ID")
	private String id;
	
	/**字典值*/
	@Column(name = "C_NAME")
	private String name;
	
	/**用于其他附属信息，比如单位(m3)*/
	@Column(name = "C_INFO")
	private String info;
	
	/**父节点编号（null为根节点，一类数据只能有一个根节点）*/
	@Column(name = "C_PARENT_ID")
	private String parentId;
	
	/**排序（用时间排序更灵活）*/
	@Column(name = "N_ORDER")
	private BigDecimal order;

	/**子节点*/
	@OneToMany(mappedBy="parentId",fetch=FetchType.LAZY)
	@OrderBy("order ASC")
	private List<Dictionary> children=new ArrayList<Dictionary>();

	public void setChildren(List<Dictionary> children) {
		this.children = children;
	}

	public List<Dictionary> getChildren() {
		return children;
	}

	/**
	 * 设置数据编号
	 * @param id String
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * 获取数据编号
	 */
	public String getId() {
		return this.id;
	}
	/**
	 * 设置字典值
	 * @param name String
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 获取字典值
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * 设置用于其他附属信息，比如单位(m3)
	 * @param info String
	 */
	public void setInfo(String info) {
		this.info = info;
	}
	
	/**
	 * 获取用于其他附属信息，比如单位(m3)
	 */
	public String getInfo() {
		return this.info;
	}
	/**
	 * 设置父节点编号（null为根节点，一类数据只能有一个根节点）
	 * @param parentId String
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	/**
	 * 获取父节点编号（null为根节点，一类数据只能有一个根节点）
	 */
	public String getParentId() {
		return this.parentId;
	}
	/**
	 * 设置排序（用时间排序更灵活）
	 * @param order BigDecimal
	 */
	public void setOrder(BigDecimal order) {
		this.order = order;
	}
	
	/**
	 * 获取排序（用时间排序更灵活）
	 */
	public BigDecimal getOrder() {
		return this.order;
	}

	/**
	* 重写toString方法
	* @return String
	*/
	public String toString() {
		return
		"id:"+getId()+","+
		"name:"+getName()+","+
		"info:"+getInfo()+","+
		"parentId:"+getParentId()+","+
		"order:"+getOrder()+","+
		"children:"+getChildren();
	}
	public boolean isLeaf() {
		return children==null||children.size()==0;
	}
}