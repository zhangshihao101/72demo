package com.spt.bean;

/**
 * 商户首页Bean
 * */
public class HomePageDataInfo {
	private String order_count;// 待处理订单数
	private String newpm;// 待处理消息数
	private String qa_count;// 产品咨询
	private String notice_count;// 缺货登记
	private String goods_count;// 商品数
	private String grade;// 商户等级
	private String service_score;// 服务分
	private String avg_line;// 相比平均分的百分比，为0时表示持平
	private String credits;// 商城积分
	private String exp;// 经验值

	public String getOrder_count() {
		return order_count;
	}

	public void setOrder_count(String order_count) {
		this.order_count = order_count;
	}

	public String getNewpm() {
		return newpm;
	}

	public void setNewpm(String newpm) {
		this.newpm = newpm;
	}

	public String getQa_count() {
		return qa_count;
	}

	public void setQa_count(String qa_count) {
		this.qa_count = qa_count;
	}

	public String getNotice_count() {
		return notice_count;
	}

	public void setNotice_count(String notice_count) {
		this.notice_count = notice_count;
	}

	public String getGoods_count() {
		return goods_count;
	}

	public void setGoods_count(String goods_count) {
		this.goods_count = goods_count;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getService_score() {
		return service_score;
	}

	public void setService_score(String service_score) {
		this.service_score = service_score;
	}

	public String getAvg_line() {
		return avg_line;
	}

	public void setAvg_line(String avg_line) {
		this.avg_line = avg_line;
	}

	public String getCredits() {
		return credits;
	}

	public void setCredits(String credits) {
		this.credits = credits;
	}

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

}
