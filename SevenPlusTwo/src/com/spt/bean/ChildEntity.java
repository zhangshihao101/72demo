package com.spt.bean;

import java.util.ArrayList;

public class ChildEntity {

	// private int groupColor;

	private String groupName;

	private String groupId;

	private ArrayList<String> childNames;

	// public int getGroupColor() {
	// return groupColor;
	// }

	public String getGroupName() {
		return groupName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public ArrayList<String> getChildNames() {
		return childNames;
	}

	// public void setGroupColor(int groupColor) {
	// this.groupColor = groupColor;
	// }

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setChildNames(ArrayList<String> childNames) {
		this.childNames = childNames;
	}

}
