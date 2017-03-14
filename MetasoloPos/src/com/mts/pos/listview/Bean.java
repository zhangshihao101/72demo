package com.mts.pos.listview;


/**
 * 适配器数据
 */
public class Bean {
	private String name;//
	private String states;//状态 3种  1 选中  2 未选中 3不可选
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStates() {
		return states;
	}

	public void setStates(String states) {
		this.states = states;
	}

	@Override
	public String toString() {
		return "Bean [name=" + name + ", states=" + states + "]";
	}

}
