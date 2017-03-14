package com.mts.pos.listview;

public class GuideInfo {

	// 商品图片
	private String smallImageUrl;
	// 商品名称
	private String productName;
	// 商品库存
	private String totalAvailableQuantity;
	// 商品价格
	private String productListPrice;
	// 商品Id
	private String productId;
	// 总页数
	private int totalPage;

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getSmallImageUrl() {
		return smallImageUrl;
	}

	public void setSmallImageUrl(String smallImageUrl) {
		this.smallImageUrl = smallImageUrl;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getTotalAvailableQuantity() {
		return totalAvailableQuantity;
	}

	public void setTotalAvailableQuantity(String totalAvailableQuantity) {
		this.totalAvailableQuantity = totalAvailableQuantity;
	}

	public String getProductListPrice() {
		return productListPrice;
	}

	public void setProductListPrice(String productListPrice) {
		this.productListPrice = productListPrice;
	}

	// 分类名称
	private String categoryName;
	// 分类ID
	private String productCategoryId;

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getProductCategoryId() {
		return productCategoryId;
	}

	public void setProductCategoryId(String productCategoryId) {
		this.productCategoryId = productCategoryId;
	}

	@Override
	public String toString() {
		return "GuideInfo [categoryName=" + categoryName + ", productCategoryId=" + productCategoryId + "]";
	}

	// 品牌ID
	private String brandId;
	// 品牌名称
	private String brandName;

	public String getBrandId() {
		return brandId;
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	private Boolean flag;

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

}
