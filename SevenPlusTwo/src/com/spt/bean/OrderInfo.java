package com.spt.bean;

public class OrderInfo {

	private String orderId;
	private String orderDate;
	private String shopName;
	private String channel;
	private Double totle;
	/**
	 * 支付状态
	 */
	private String isPayment;
	/**
	 * 订单状态
	 */
	private String isFinish;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Double getTotle() {
		return totle;
	}

	public void setTotle(Double totle) {
		this.totle = totle;
	}

	public String getIsPayment() {
		return isPayment;
	}

	public void setIsPayment(String isPayment) {
		this.isPayment = isPayment;
	}

	public String getIsFinish() {
		return isFinish;
	}

	public void setIsFinish(String isFinish) {
		this.isFinish = isFinish;
	}

}
