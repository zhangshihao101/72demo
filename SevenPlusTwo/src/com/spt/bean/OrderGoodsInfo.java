package com.spt.bean;

public class OrderGoodsInfo {

	// 购物车Id
	private String id;
	// 商品id
	private String goods_id;
	// 商品名称
	private String goods_name;
	// 规格id
	private String spec_id;
	// 规格
	private String specification;
	// 商品图片
	private String goods_image;
	// 商品数量
	private String quantity;
	// 商品单价
	private String price;
	// 运费计算明细id，为-1时包邮，大于或等于0时需要运费
	private int freight_id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getSpec_id() {
		return spec_id;
	}

	public void setSpec_id(String spec_id) {
		this.spec_id = spec_id;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getGoods_image() {
		return goods_image;
	}

	public void setGoods_image(String goods_image) {
		this.goods_image = goods_image;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public int getFreight_id() {
		return freight_id;
	}

	public void setFreight_id(int freight_id) {
		this.freight_id = freight_id;
	}

	@Override
	public String toString() {
		return "OrderGoodsInfo [id=" + id + ", goods_id=" + goods_id + ", goods_name=" + goods_name + ", spec_id="
				+ spec_id + ", specification=" + specification + ", goods_image=" + goods_image + ", quantity="
				+ quantity + ", price=" + price + ", freight_id=" + freight_id + "]";
	}

}
