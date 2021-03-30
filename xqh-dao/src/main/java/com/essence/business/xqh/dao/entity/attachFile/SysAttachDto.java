package com.essence.business.xqh.dao.entity.attachFile;

import java.io.Serializable;

/**
 * 知识库模糊查询用
 */
public class SysAttachDto implements Serializable{
	private static final long serialVersionUID = -7326394760810350134L;

	private String id;
	private String name="";
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
	@Override
	public String toString() {
		return "SysAttachDto [id=" + id + ", name=" + name + "]";
	}
	

}
