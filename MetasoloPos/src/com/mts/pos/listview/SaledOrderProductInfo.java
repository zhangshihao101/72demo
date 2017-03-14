package com.mts.pos.listview;

public class SaledOrderProductInfo {
	
	String id;
	String url;
	String name;
	String style;
	String color;
	String size;
	Double price;
	Double unprice;
	Integer count;
	Integer isReturn;
	
	public Double getUnprice() {
		return unprice;
	}

	public void setUnprice(Double unprice) {
		this.unprice = unprice;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
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

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getIsReturn() {
		return isReturn;
	}

	public void setIsReturn(Integer isReturn) {
		this.isReturn = isReturn;
	}

}
