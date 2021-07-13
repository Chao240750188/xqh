package com.essence.business.xqh.dao.entity.attachFile;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 知识库类型
 */
@Entity
@Table(name = "SYS_ATTACH")
public class SysAttach implements Serializable{
	private static final long serialVersionUID = -7326394760810350134L;

	/**编号*/
	@Id
	@Column(name = "C_ID")
	private String id;
	
	/**名称*/
	@Column(name = "C_NAME")
	private String name;

	/**创建时间*/
	@Column(name = "D_CREATE_TIME")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	/**创建用户id*/
	@Column(name = "C_CREATE_USER")
	private String createUser;
	
	/**创建人名字*/
	@Column(name = "C_CREATE_USER_NAME")
	private String createUserName;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	
	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	
	@Override
	public String toString() {
		return "SysAttach [id=" + id + ", name=" + name + ", createTime=" + createTime + ", createUser=" + createUser
				+ ", createUserName=" + createUserName + "]";
	}
}
