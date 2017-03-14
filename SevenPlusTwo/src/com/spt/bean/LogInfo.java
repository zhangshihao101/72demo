package com.spt.bean;

/**
 * 【操作日志】信息类
 * */
public class LogInfo {
	private String operator;// 操作人
	private String order_status;// 订单改变前状态
	private String changed_status;// 改变后的状态
	private String remark;// 修改原因
	private String log_time;// 日志时间

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOrder_status() {
		return order_status;
	}

	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}

	public String getChanged_status() {
		return changed_status;
	}

	public void setChanged_status(String changed_status) {
		this.changed_status = changed_status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getLog_time() {
		return log_time;
	}

	public void setLog_time(String log_time) {
		this.log_time = log_time;
	}

}
