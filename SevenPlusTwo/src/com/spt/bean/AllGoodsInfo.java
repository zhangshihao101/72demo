package com.spt.bean;

public class AllGoodsInfo {
	// 商品ID
	private String goodsId;
	// 商品名称
	private String goodsName;
	// 商品图片
	private String goodsImg;
	// 商品库存
	private String goodsStock;
	// 商品最小分销价
	private String goodsMinDisPrice;
	// 商品最大分销价
	private String goodsMaxDisPrice;
	// 商品最小市场价
	private String goodsMinMarPrice;
	// 商品最大市场价
	private String goodsMaxMarPrice;
	// 商品最小商城价
	private String goodsMinShopPrice;
	// 商品最大商城价
	private String goodsMaxShopPrice;
	// 代销开始时间
	private String disStartTime;
	// 代销结束时间
	private String disEndTime;
	// 代销状态
	private String disTimeType;

	public String getDisTimeType() {
		return disTimeType;
	}

	public void setDisTimeType(String disTimeType) {
		this.disTimeType = disTimeType;
	}

	public String getDisStartTime() {
		return disStartTime;
	}

	public void setDisStartTime(String disStartTime) {
		this.disStartTime = disStartTime;
	}

	public String getDisEndTime() {
		return disEndTime;
	}

	public void setDisEndTime(String disEndTime) {
		this.disEndTime = disEndTime;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsImg() {
		return goodsImg;
	}

	public void setGoodsImg(String goodsImg) {
		this.goodsImg = goodsImg;
	}

	public String getGoodsStock() {
		return goodsStock;
	}

	public void setGoodsStock(String goodsStock) {
		this.goodsStock = goodsStock;
	}

	public String getGoodsMinDisPrice() {
		return goodsMinDisPrice;
	}

	public void setGoodsMinDisPrice(String goodsMinDisPrice) {
		this.goodsMinDisPrice = goodsMinDisPrice;
	}

	public String getGoodsMaxDisPrice() {
		return goodsMaxDisPrice;
	}

	public void setGoodsMaxDisPrice(String goodsMaxDisPrice) {
		this.goodsMaxDisPrice = goodsMaxDisPrice;
	}

	public String getGoodsMinMarPrice() {
		return goodsMinMarPrice;
	}

	public void setGoodsMinMarPrice(String goodsMinMarPrice) {
		this.goodsMinMarPrice = goodsMinMarPrice;
	}

	public String getGoodsMaxMarPrice() {
		return goodsMaxMarPrice;
	}

	public void setGoodsMaxMarPrice(String goodsMaxMarPrice) {
		this.goodsMaxMarPrice = goodsMaxMarPrice;
	}

	public String getGoodsMinShopPrice() {
		return goodsMinShopPrice;
	}

	public void setGoodsMinShopPrice(String goodsMinShopPrice) {
		this.goodsMinShopPrice = goodsMinShopPrice;
	}

	public String getGoodsMaxShopPrice() {
		return goodsMaxShopPrice;
	}

	public void setGoodsMaxShopPrice(String goodsMaxShopPrice) {
		this.goodsMaxShopPrice = goodsMaxShopPrice;
	}
}
