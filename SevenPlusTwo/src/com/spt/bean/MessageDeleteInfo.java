package com.spt.bean;

import java.io.Serializable;

/**
 * 【待处理消息（删除）】类
 * */
@SuppressWarnings("serial")
public class MessageDeleteInfo implements Serializable {
	private String msg_id; // 消息id
	private String content; // 消息内容
	private String add_time; // 发送时间
	private String portrait; // 头像
	private String user_name; // 用户名
	private boolean isChecked; // 是否被选中

	public String getMsg_id() {
		return msg_id;
	}

	public void setMsg_id(String msg_id) {
		this.msg_id = msg_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

}
