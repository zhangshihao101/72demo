package com.spt.bean;

public class DisOrderTopInfo {

	// 订单号
	private String order_sn;
	// 订单状态
	private String status;
	// 支付时间
	private String pay_time;
	// 总金额
	private String final_amount;
	// 订单id
	private String order_id;
	// 预计收益
	private String profit;
	// 是否显示预计收益
	private String agent_id;

	public String getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}

	public String getProfit() {
		return profit;
	}

	public void setProfit(String profit) {
		this.profit = profit;
	}

	public String getOrder_sn() {
		return order_sn;
	}

	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPay_time() {
		return pay_time;
	}

	public void setPay_time(String pay_time) {
		this.pay_time = pay_time;
	}

	public String getFinal_amount() {
		return final_amount;
	}

	public void setFinal_amount(String final_amount) {
		this.final_amount = final_amount;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	@Override
	public String toString() {
		return "DisOrderTopInfo [order_sn=" + order_sn + ", status=" + status + ", pay_time=" + pay_time
				+ ", final_amount=" + final_amount + ", order_id=" + order_id + "]";
	}

}
