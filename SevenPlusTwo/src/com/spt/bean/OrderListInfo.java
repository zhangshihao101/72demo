package com.spt.bean;

/**
 * 【订单列表】信息
 * */
public class OrderListInfo {

	private String order_id;// 订单id
	private String order_sn;// 订单号
	private String type;// 类型（实物or虚拟）
	private String extension;// 平台，normal：商城商品，tuan：团购商品
	private String status;// 订单状态代码
	private String add_time;// 下单时间，格林威治时间戳
	private String final_amount;// 最终需要支付的金额
	private String is_change;// 是否修改过订单金额

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getOrder_sn() {
		return order_sn;
	}

	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public String getFinal_amount() {
		return final_amount;
	}

	public void setFinal_amount(String final_amount) {
		this.final_amount = final_amount;
	}

	public String getIs_change() {
		return is_change;
	}

	public void setIs_change(String is_change) {
		this.is_change = is_change;
	}

}
