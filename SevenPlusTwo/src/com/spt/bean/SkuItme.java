package com.spt.bean;

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
	private String disPrice;// 分销价
	private String productId;// 单品Id
	private int skuStockSale;// 销售库存
	
	public int getSkuStockSale() {
		return skuStockSale;
	}

	public void setSkuStockSale(int skuStockSale) {
		this.skuStockSale = skuStockSale;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getDisPrice() {
		return disPrice;
	}

	public void setDisPrice(String disPrice) {
		this.disPrice = disPrice;
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

}
