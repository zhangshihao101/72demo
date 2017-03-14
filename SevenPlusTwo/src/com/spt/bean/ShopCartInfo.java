package com.spt.bean;

public class ShopCartInfo {

	// 用户名（用户id）
	private String user_id;
	// 商品名称
	private String goodsName;
	// 商品规格
	private String goodsSpec;
	// 商品图片
	private String goodsImg;
	// 商品分销价格
	private String goodsPrice;
	// 购买的商品数量
	private int goodsCount;
	// 购物车唯一ID
	private String goodsRec_id;
	// 商品Id
	private String goodsId;
	// 规格Id
	private String goodsSpecId;
	// 选中状态
	private boolean isChecked;

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsSpec() {
		return goodsSpec;
	}

	public void setGoodsSpec(String goodsSpec) {
		this.goodsSpec = goodsSpec;
	}

	public String getGoodsImg() {
		return goodsImg;
	}

	public void setGoodsImg(String goodsImg) {
		this.goodsImg = goodsImg;
	}

	public String getGoodsPrice() {
		return goodsPrice;
	}

	public void setGoodsPrice(String goodsPrice) {
		this.goodsPrice = goodsPrice;
	}

	public int getGoodsCount() {
		return goodsCount;
	}

	public void setGoodsCount(int goodsCount) {
		this.goodsCount = goodsCount;
	}

	public String getGoodsRec_id() {
		return goodsRec_id;
	}

	public void setGoodsRec_id(String goodsRec_id) {
		this.goodsRec_id = goodsRec_id;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsSpecId() {
		return goodsSpecId;
	}

	public void setGoodsSpecId(String goodsSpecId) {
		this.goodsSpecId = goodsSpecId;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

}
