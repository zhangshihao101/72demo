package com.spt.bean;

public class ConPeopleInfo {

	private String mHeader;
	private String mName;
	private String mJob;
	private String isFriend;
	private String userLoginId;
	private String connectionRole;
	private boolean flag;

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getConnectionRole() {
		return connectionRole;
	}

	public void setConnectionRole(String connectionRole) {
		this.connectionRole = connectionRole;
	}

	public String getIsFriend() {
		return isFriend;
	}

	public void setIsFriend(String isFriend) {
		this.isFriend = isFriend;
	}

	public String getUserLoginId() {
		return userLoginId;
	}

	public void setUserLoginId(String userLoginId) {
		this.userLoginId = userLoginId;
	}

	public String getmHeader() {
		return mHeader;
	}

	public void setmHeader(String mHeader) {
		this.mHeader = mHeader;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public String getmJob() {
		return mJob;
	}

	public void setmJob(String mJob) {
		this.mJob = mJob;
	}

}
