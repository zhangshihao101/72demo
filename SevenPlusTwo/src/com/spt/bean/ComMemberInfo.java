package com.spt.bean;

public class ComMemberInfo {

	// 名称
	private String connectionName;
	// 手机号
	private String contactsTelephoneNumber;
	// 职位
	private String position;
	// 是否加好友
	private String isFirend;

	private String userLoginId;

	public String getUserLoginId() {
		return userLoginId;
	}

	public void setUserLoginId(String userLoginId) {
		this.userLoginId = userLoginId;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public String getContactsTelephoneNumber() {
		return contactsTelephoneNumber;
	}

	public void setContactsTelephoneNumber(String contactsTelephoneNumber) {
		this.contactsTelephoneNumber = contactsTelephoneNumber;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getIsFirend() {
		return isFirend;
	}

	public void setIsFirend(String isFirend) {
		this.isFirend = isFirend;
	}

}
