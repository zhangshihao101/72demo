package com.spt.bean;

public class EditPriceInfo {

	// 规格ID
	private String spec_id;
	// 颜色
	private String color;
	// 尺码
	private String size;
	// 销售价
	private String salePrice;
	// 代销价
	private double disPrice;
	// 是否可编辑
	private String isEdit;

	public String getSpec_id() {
		return spec_id;
	}

	public void setSpec_id(String spec_id) {
		this.spec_id = spec_id;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(String salePrice) {
		this.salePrice = salePrice;
	}

	public double getDisPrice() {
		return disPrice;
	}

	public void setDisPrice(double disPrice) {
		this.disPrice = disPrice;
	}

	public String getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(String isEdit) {
		this.isEdit = isEdit;
	}

}
