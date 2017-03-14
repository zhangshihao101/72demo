package com.mts.pos.listview;

public class SearchProductInfo {
	/* 商品id */
	String productid;
	/* 商品名称 */
	String productname;
	/* 商品图片 */
	String product_img;
	/* 商品条码 */
	String barcode;
	/* 商品说明 */
	String description;
	/* 商品颜色 */
	String productcolor;
	/* 商品名称 */
	String productname2;
	/* 款号 */
	String modeId;
	/* 品牌名 */
	String brandName;
	/* 吊牌价 */
	String productPrice;

	public String getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getModeId() {
		return modeId;
	}

	public void setModeId(String modeId) {
		this.modeId = modeId;
	}

	public int getProduct_count() {
		return product_count;
	}

	public void setProduct_count(int product_count) {
		this.product_count = product_count;
	}

	public int getProduct_salecount() {
		return product_salecount;
	}

	public void setProduct_salecount(int product_salecount) {
		this.product_salecount = product_salecount;
	}

	/* 商品实际库存 */
	int product_count;
	/* 商品可销售库存 */
	int product_salecount;

	public String getProductname2() {
		return productname2;
	}

	public void setProductname2(String productname2) {
		this.productname2 = productname2;
	}

	public String getProductcolor() {
		return productcolor;
	}

	public void setProductcolor(String productcolor) {
		this.productcolor = productcolor;
	}

	public String getProductsize() {
		return productsize;
	}

	public void setProductsize(String productsize) {
		this.productsize = productsize;
	}

	/* 商品尺码 */
	String productsize;

	String product_smallimg;
	String product_detailimg;
	String product_largeimg;
	String product_mediumimg;

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProductid() {
		return productid;
	}

	public void setProductid(String productid) {
		this.productid = productid;
	}

	public String getProductname() {
		return productname;
	}

	public void setProductname(String productname) {
		this.productname = productname;
	}

	public String getProduct_img() {
		return product_img;
	}

	public void setProduct_img(String product_img) {
		this.product_img = product_img;
	}

	public String getProduct_smallimg() {
		return product_smallimg;
	}

	public void setProduct_smallimg(String product_smallimg) {
		this.product_smallimg = product_smallimg;
	}

	public String getProduct_detailimg() {
		return product_detailimg;
	}

	public void setProduct_detailimg(String product_detailimg) {
		this.product_detailimg = product_detailimg;
	}

	public String getProduct_largeimg() {
		return product_largeimg;
	}

	public void setProduct_largeimg(String product_largeimg) {
		this.product_largeimg = product_largeimg;
	}

	public String getProduct_mediumimg() {
		return product_mediumimg;
	}

	public void setProduct_mediumimg(String product_mediumimg) {
		this.product_mediumimg = product_mediumimg;
	}

	Boolean flag;

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

}
