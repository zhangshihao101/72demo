package com.mts.pos.listview;

public class ProductInfo {
	/* 商品id */
	String product_id;
	/* 商品名称 */
	String product_name;
	/* 商品名称2 */
	String product_name2;
	/* ��Ʒid */
	String single_product_id;

	public String getSingle_product_id() {
		return single_product_id;
	}

	public void setSingle_product_id(String single_product_id) {
		this.single_product_id = single_product_id;
	}

	public String getProduct_name2() {
		return product_name2;
	}

	public void setProduct_name2(String product_name2) {
		this.product_name2 = product_name2;
	}

	/* 商品实际库存 */
	int product_count;
	/* 商品可销售库存 */
	int product_salecount;

	public int getProduct_salecount() {
		return product_salecount;
	}

	public void setProduct_salecount(int product_salecount) {
		this.product_salecount = product_salecount;
	}

	/* 商品原价 */
	double original_cost;
	/* 商品现价 */
	double present_cost;

	double total;
	/* 商品图片 */
	String product_img;
	/* 商品条码 */
	String barcode;

	/* 商品尺码 */
	String productsize;

	/* 商品颜色 */
	String productcolor;

	/* 优惠价格 */
	int promo_cost;

	/* 减价后的价格 */
	int basePrice;

	boolean ispromo;

	public int getPromo_cost() {
		return promo_cost;
	}

	public void setPromo_cost(int promo_cost) {
		this.promo_cost = promo_cost;
	}

	public int getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(int basePrice) {
		this.basePrice = basePrice;
	}

	public boolean isIspromo() {
		return ispromo;
	}

	public void setIspromo(boolean ispromo) {
		this.ispromo = ispromo;
	}

	public String getProductsize() {
		return productsize;
	}

	public void setProductsize(String productsize) {
		this.productsize = productsize;
	}

	public String getProductcolor() {
		return productcolor;
	}

	public void setProductcolor(String productcolor) {
		this.productcolor = productcolor;
	}

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

	String description;

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public int getProduct_count() {
		return product_count;
	}

	public void setProduct_count(int product_count) {
		this.product_count = product_count;
	}

	public double getOriginal_cost() {
		return original_cost;
	}

	public void setOriginal_cost(double original_cost) {
		this.original_cost = original_cost;
	}

	public double getPresent_cost() {
		return present_cost;
	}

	public void setPresent_cost(double present_cost) {
		this.present_cost = present_cost;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public String getProduct_img() {
		return product_img;
	}

	public void setProduct_img(String product_img) {
		this.product_img = product_img;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

}
