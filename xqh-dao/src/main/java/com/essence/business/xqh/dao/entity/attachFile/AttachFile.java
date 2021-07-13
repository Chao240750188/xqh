package com.essence.business.xqh.dao.entity.attachFile;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体类
 * @company Essence
 * @author Gavin
 * @version 1.0 2018/01/08
 */
@Entity
@Table(name = "SYS_ATTACHFILE")
public class AttachFile implements Serializable{
	private static final long serialVersionUID = 79;
	
	/***/
	@Id
	@Column(name = "C_ID")
	private String id;
	
	/***/
	@Column(name = "C_GROUP_ID")
	private String groupId;
	
	/***/
	@Column(name = "C_PATH")
	private String path;
	
	/***/
	@Column(name = "C_NAME")
	private String name;
	
	/***/
	@Column(name = "N_SIZE")
	private BigDecimal size;
	
	/***/
	@Column(name = "D_CREATE_TIME")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	
	/***/
	@Column(name = "C_INFO")
	private String info;
	
	/***/
	@Column(name = "C_USERID")
	private String userid;
	//是否归档 : 0未归档 1已归档
	@Column(name = "N_FILING")
	private int filing;
	
	/***/
	@Column(name = "C_SUFFIX")
	private String suffix;


	/**
	 * 设置
	 * @param id String
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * 获取
	 */
	public String getId() {
		return this.id;
	}
	/**
	 * 设置
	 * @param groupId String
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	/**
	 * 获取
	 */
	public String getGroupId() {
		return this.groupId;
	}
	/**
	 * 设置
	 * @param path String
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * 获取
	 */
	public String getPath() {
		return this.path;
	}
	/**
	 * 设置
	 * @param name String
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 获取
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * 设置
	 * @param size int
	 */
	public void setSize(BigDecimal size) {
		this.size = size;
	}
	
	/**
	 * 获取
	 */
	public BigDecimal getSize() {
		return this.size;
	}
	/**
	 * 设置
	 * @param createTime Date
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	/**
	 * 获取
	 */
	public Date getCreateTime() {
		return this.createTime;
	}
	/**
	 * 设置
	 * @param info String
	 */
	public void setInfo(String info) {
		this.info = info;
	}
	
	/**
	 * 获取
	 */
	public String getInfo() {
		return this.info;
	}
	/**
	 * 设置
	 * @param userid String
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	/**
	 * 设置是否归档 : 0未归档 1已归档
	 * @param filing BigDecimal
	 */
	public void setFiling(int filing) {
		this.filing = filing;
	}
	
	/**
	 * 获取是否归档 : 0未归档 1已归档
	 */
	public int getFiling() {
		return this.filing;
	}
	
	/**
	 * 获取操作人
	 */
	public String getUserid() {
		return this.userid;
	}
	/**
	 * 设置
	 * @param suffix String
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	/**
	 * 获取
	 */
	public String getSuffix() {
		return this.suffix;
	}

	/**
	* 重写toString方法
	* @return String
	*/
	public String toString() {
		return
		"id:"+getId()+","+
		"groupId:"+getGroupId()+","+
		"path:"+getPath()+","+
		"name:"+getName()+","+
		"size:"+getSize()+","+
		"createTime:"+getCreateTime()+","+
		"info:"+getInfo()+","+
		"userid:"+getUserid()+","+
		"suffix:"+getSuffix();
	}
}