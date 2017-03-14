package com.spt.bean;

public class StoreInfo {

	// 店铺Id
	private String sroreId;
	// 店铺名称
	private String sotreName;

	public String getSroreId() {
		return sroreId;
	}

	public void setSroreId(String sroreId) {
		this.sroreId = sroreId;
	}

	public String getSotreName() {
		return sotreName;
	}

	public void setSotreName(String sotreName) {
		this.sotreName = sotreName;
	}

	@Override
	public String toString() {
		return "StoreInfo [sroreId=" + sroreId + ", sotreName=" + sotreName + "]";
	}

}
