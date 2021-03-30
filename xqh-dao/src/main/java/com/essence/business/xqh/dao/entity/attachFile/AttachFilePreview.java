package com.essence.business.xqh.dao.entity.attachFile;
/**
 * 文件预览
 * @title AttachFilePreview.java
 * @author Gavin
 * @since 2016年8月27日 上午9:38:47
 * @version 1.0 Gavin 2016年8月27日 上午9:38:47
 */
public class AttachFilePreview {
	private String id;
	private String suffix;
	private String toSuffix;
	private Object info;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public Object getInfo() {
		return info;
	}
	public void setInfo(Object info) {
		this.info = info;
	}
	public String getToSuffix() {
		return toSuffix;
	}
	public void setToSuffix(String toSuffix) {
		this.toSuffix = toSuffix;
	}

}
