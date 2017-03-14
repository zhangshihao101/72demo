package com.spt.utils;

import java.util.ArrayList;

/**
 * <b>自定义树形控件的叶子类 <br>
 * 
 * <pre>
 * 修改履历
 * ===========================================================
 * 2015/05/14   李瑜峰（七迦二）    商户通    新版作成
 * </pre>
 */
public class MyTreeElement {

	private String id;	// cate_id 分类id
	private String upId;	// parent_id 父级分类id
	private String outlineTitle;	// cate_name 分类名称
	private String sort;	// sort_order 排序
	public boolean checked;	// if_show 是否显示 1显示，0不显示
	private boolean mhasParent;
	private boolean mhasChild;
	private MyTreeElement parent;
	private int level;
	private ArrayList<MyTreeElement> childList = new ArrayList<MyTreeElement>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOutlineTitle() {
		return outlineTitle;
	}

	public void setOutlineTitle(String outlineTitle) {
		this.outlineTitle = outlineTitle;
	}

	public boolean isMhasParent() {
		return mhasParent;
	}

	public void setMhasParent(boolean mhasParent) {
		this.mhasParent = mhasParent;
	}

	public boolean isMhasChild() {
		return mhasChild;
	}

	public void setMhasChild(boolean mhasChild) {
		this.mhasChild = mhasChild;
	}

	public MyTreeElement getParent() {
		return parent;
	}

	public void setParent(MyTreeElement parent) {
		this.parent = parent;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public ArrayList<MyTreeElement> getChildList() {
		return childList;
	}

	public void setChildList(ArrayList<MyTreeElement> childList) {
		this.childList = childList;
	}

	private boolean expanded;

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public String getUpId() {
		return upId;
	}

	public void setUpId(String upId) {
		this.upId = upId;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public void addChild(MyTreeElement c) {

		this.childList.add(c);
		this.mhasParent = false;
		this.mhasChild = true;
		c.parent = this;
		c.level = this.level + 1;

	}

	public MyTreeElement(String id, String title, String upId, String sort, boolean checked) {

		super();
		this.id = id;
		this.upId = upId;
		this.outlineTitle = title;
		this.level = 0;
		this.mhasParent = true;
		this.mhasChild = false;
		this.parent = null;
		this.sort = sort;
		this.checked = checked;
		setParentChecked(checked);

	}

	public MyTreeElement(String id, String outlineTitle, String sort, boolean checked, boolean mhasParent,
			boolean mhasChild, MyTreeElement parent, int level, boolean expanded) {

		super();
		this.id = id;
		this.outlineTitle = outlineTitle;
		this.sort = sort;
		this.checked = checked;
		this.mhasParent = mhasParent;
		this.mhasChild = mhasChild;
		this.parent = parent;
		this.level = level;
		this.expanded = expanded;
		setParentChecked(checked);
		if (parent != null) {
			this.parent.getChildList().add(this);
		}
	}

	public void setParentChecked(boolean b) {

		checked = b;
		
		if (childList != null && childList.size() > 0) {// 如果children不为空，循环设置children的checked
			for (MyTreeElement each : childList) {
				each.checked = checked;
				if (each.childList != null && each.childList.size() > 0) {
					for (MyTreeElement each1 : each.childList) {
						each1.checked = checked;
					}
				}
				
			}
		}
	}
}
