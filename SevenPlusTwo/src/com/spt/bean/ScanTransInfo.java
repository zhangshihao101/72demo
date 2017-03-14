package com.spt.bean;

import java.io.Serializable;

/**
 * 【查看物流】类
 * */
@SuppressWarnings("serial")
public class ScanTransInfo implements Serializable {
	private String time; // 时间
	private String context; // 内容

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

}
