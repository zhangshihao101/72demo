package com.spt.bean;

import java.util.ArrayList;

public class ParentEntity {

//	private int groupColor;

	private String groupName;

	private String groupId;

	private String description;
	
	private String parentCategoryId;

	private ArrayList<ChildEntity> childs;
	
	public String getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(String parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

//	public int getGroupColor() {
//		return groupColor;
//	}

	public String getGroupName() {
		return groupName;
	}

	public ArrayList<ChildEntity> getChilds() {
		return childs;
	}

//	public void setGroupColor(int groupColor) {
//		this.groupColor = groupColor;
//	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setChilds(ArrayList<ChildEntity> childs) {
		this.childs = childs;
	}

}
