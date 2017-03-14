package com.mts.pos.listview;

/**
 * 颜色、尺码属性
 */
public class SkuItme {
	private String colorId;// 颜色id
	private String sizeId;// 尺码id
	private String skuSize;// 尺码
	private String skuColor;// 颜色
	private int skuStock;// 库存
	private String productName;// 商品名
	private String nowPrice;// 现价
	private String dropPrice;// 吊牌价
	private String productId;// 单品Id
	private String imgUrl;// 单品图片

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getNowPrice() {
		return nowPrice;
	}

	public void setNowPrice(String nowPrice) {
		this.nowPrice = nowPrice;
	}

	public String getColorId() {
		return colorId;
	}

	public void setColorId(String colorId) {
		this.colorId = colorId;
	}

	public String getSizeId() {
		return sizeId;
	}

	public void setSizeId(String sizeId) {
		this.sizeId = sizeId;
	}

	public String getSkuSize() {
		return skuSize;
	}

	public void setSkuSize(String skuSize) {
		this.skuSize = skuSize;
	}

	public String getSkuColor() {
		return skuColor;
	}

	public void setSkuColor(String skuColor) {
		this.skuColor = skuColor;
	}

	public int getSkuStock() {
		return skuStock;
	}

	public void setSkuStock(int skuStock) {
		this.skuStock = skuStock;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getDropPrice() {
		return dropPrice;
	}

	public void setDropPrice(String dropPrice) {
		this.dropPrice = dropPrice;
	}

	@Override
	public String toString() {
		return "SkuItme [colorId=" + colorId + ", sizeId=" + sizeId + ", skuSize=" + skuSize + ", skuColor=" + skuColor
				+ ", skuStock=" + skuStock + ", productName=" + productName + ", dropPrice=" + dropPrice + "]";
	}

}
