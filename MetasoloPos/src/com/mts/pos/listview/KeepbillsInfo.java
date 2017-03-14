package com.mts.pos.listview;

import java.util.List;

public class KeepbillsInfo {

	String customerName;
	String billsTime;
	String billsID;
	String amount;
	Boolean being;

	List<KeepbillsPicInfo> list;

	public Boolean getBeing() {
		return being;
	}

	public void setBeing(Boolean being) {
		this.being = being;
	}

	public List<KeepbillsPicInfo> getList() {
		return list;
	}

	public void setList(List<KeepbillsPicInfo> list) {
		this.list = list;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getBillsTime() {
		return billsTime;
	}

	public void setBillsTime(String billsTime) {
		this.billsTime = billsTime;
	}

	public String getBillsID() {
		return billsID;
	}

	public void setBillsID(String billsID) {
		this.billsID = billsID;
	}

}
