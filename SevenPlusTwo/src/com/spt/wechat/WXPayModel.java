package com.spt.wechat;

public class WXPayModel {

	private String appId;
	
	private String partnerId;
	
	private String prepayId;
	
	private String packageStr;
	
	private String noncestr;
	
	private String timestamp;
	
	private String sign;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getPrepayId() {
		return prepayId;
	}

	public void setPrepayId(String prepayId) {
		this.prepayId = prepayId;
	}

	public String getPackageStr() {
		return packageStr;
	}

	public void setPackageStr(String packageStr) {
		this.packageStr = packageStr;
	}

	public String getNoncestr() {
		return noncestr;
	}

	public void setNoncestr(String noncestr) {
		this.noncestr = noncestr;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	@Override
	public String toString() {
		return "WXPayModel [appId=" + appId + ", partnerId=" + partnerId + ", prepayId=" + prepayId + ", packageStr="
				+ packageStr + ", noncestr=" + noncestr + ", timestamp=" + timestamp + ", sign=" + sign + "]";
	}
	
}
