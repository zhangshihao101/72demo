package com.spt.bean;

/**
 * 【产品咨询】model
 * */
public class ChatInfo {

	private String ques_id; // 问题id
	private String question_content; // 问题内容
	private String user_id; // 提问人id，为0时为游客
	private String user_name; // 提问用户名
	private String item_id; // 提问商品id
	private String item_name; // 商品名称
	private String reply_content; // 回复内容
	private String time_post; // 提问时间
	private String time_reply; // 回复时间

	public String getQues_id() {
		return ques_id;
	}

	public void setQues_id(String ques_id) {
		this.ques_id = ques_id;
	}

	public String getQuestion_content() {
		return question_content;
	}

	public void setQuestion_content(String question_content) {
		this.question_content = question_content;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public String getReply_content() {
		return reply_content;
	}

	public void setReply_content(String reply_content) {
		this.reply_content = reply_content;
	}

	public String getTime_post() {
		return time_post;
	}

	public void setTime_post(String time_post) {
		this.time_post = time_post;
	}

	public String getTime_reply() {
		return time_reply;
	}

	public void setTime_reply(String time_reply) {
		this.time_reply = time_reply;
	}

}
