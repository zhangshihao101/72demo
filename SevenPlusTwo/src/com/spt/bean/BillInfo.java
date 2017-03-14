package com.spt.bean;

import java.io.Serializable;

/**
 * 【对账单】类
 * */
@SuppressWarnings("serial")
public class BillInfo implements Serializable {
	private String sta_id; // 对账单id
	private String sta_sn; // 对账单号
	private String sta_status; // 对账单状态0：未确认；1：已确认；2：已结
	private String sta_plat; // 所属平台。1：商城；2：团购
	private String total_order_pay; // 总花费总计
	private String add_time; // 添加时间
	private String confirm_time; // 确认时间

	public String getSta_id() {
		return sta_id;
	}

	public void setSta_id(String sta_id) {
		this.sta_id = sta_id;
	}

	public String getSta_sn() {
		return sta_sn;
	}

	public void setSta_sn(String sta_sn) {
		this.sta_sn = sta_sn;
	}

	public String getSta_status() {
		return sta_status;
	}

	public void setSta_status(String sta_status) {
		this.sta_status = sta_status;
	}

	public String getSta_plat() {
		return sta_plat;
	}

	public void setSta_plat(String sta_plat) {
		this.sta_plat = sta_plat;
	}

	public String getTotal_order_pay() {
		return total_order_pay;
	}

	public void setTotal_order_pay(String total_order_pay) {
		this.total_order_pay = total_order_pay;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public String getConfirm_time() {
		return confirm_time;
	}

	public void setConfirm_time(String confirm_time) {
		this.confirm_time = confirm_time;
	}
}
