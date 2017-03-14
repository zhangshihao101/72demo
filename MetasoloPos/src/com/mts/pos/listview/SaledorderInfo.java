package com.mts.pos.listview;

import java.util.List;

public class SaledorderInfo {

	String orderId;
	String clientName;
	String orderTime;
	String orderSum;

	List<SaledorderPicInfo> list;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getOrderSum() {
		return orderSum;
	}

	public void setOrderSum(String orderSum) {
		this.orderSum = orderSum;
	}

	public List<SaledorderPicInfo> getList() {
		return list;
	}

	public void setList(List<SaledorderPicInfo> list) {
		this.list = list;
	}

}
