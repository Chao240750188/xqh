/**
 * 缓存对象
 */
package com.essence.business.xqh.common.URMS.pojo;

import java.io.Serializable;

public class UserInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 当前登录用户
	 */
	private String userId;
	/**
	 * 登录账号
	 */
	private String loginName;
	/**
	 * 登陆人名字
	 */
	private String userName;
	/**
	 * 登陆人单位名称
	 */
	private String corpName;

	/**
	 * 登陆人单位id
	 */
   private String corpId;
   
	public UserInfo() {
		super();
	}

	public UserInfo(String userId, String loginName, String userName, String corpName, String corpId) {
		super();
		this.userId = userId;
		this.loginName = loginName;
		this.userName = userName;
		this.corpName = corpName;
		this.corpId = corpId;
	}



	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCorpName() {
		return corpName;
	}

	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}


	public String getCorpId() {
		return corpId;
	}


	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}


	@Override
	public String toString() {
		return "UserInfo [userId=" + userId + ", loginName=" + loginName + ", userName=" + userName + ", corpName="
				+ corpName + ", corpId=" + corpId + "]";
	}
	
}
