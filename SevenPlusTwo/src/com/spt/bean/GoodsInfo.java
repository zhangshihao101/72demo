package com.spt.bean;

import java.io.Serializable;

/**
 * 【商品信息】类
 * */
@SuppressWarnings("serial")
public class GoodsInfo implements Serializable {
	private String s_gid;// 商品店铺联合id
	private String goods_id; // 商品id
	private String default_image; // 商品图片
	private String goods_name; // 商品名称
	private String brand; // 品牌名称
	private String cate_name; // 分类名称
	private String ext_activity_type_name; // 所在平台
	private String stock; // 库存
	private String price; // 会员价
	private String recommended; // 是否推荐
	private String ext_commend_sort; // 推荐排序
	private String ext_new_sort; // 新品排序
	private String closed; // 是否禁售
	private int width;

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getS_gid() {
		return s_gid;
	}

	public void setS_gid(String s_gid) {
		this.s_gid = s_gid;
	}

	public String getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}

	public String getDefault_image() {
		return default_image;
	}

	public void setDefault_image(String default_image) {
		this.default_image = default_image;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getCate_name() {
		return cate_name;
	}

	public void setCate_name(String cate_name) {
		this.cate_name = cate_name;
	}

	public String getExt_activity_type_name() {
		return ext_activity_type_name;
	}

	public void setExt_activity_type_name(String ext_activity_type_name) {
		this.ext_activity_type_name = ext_activity_type_name;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getRecommended() {
		return recommended;
	}

	public void setRecommended(String recommended) {
		this.recommended = recommended;
	}

	public String getExt_commend_sort() {
		return ext_commend_sort;
	}

	public void setExt_commend_sort(String ext_commend_sort) {
		this.ext_commend_sort = ext_commend_sort;
	}

	public String getExt_new_sort() {
		return ext_new_sort;
	}

	public void setExt_new_sort(String ext_new_sort) {
		this.ext_new_sort = ext_new_sort;
	}

	public String getClosed() {
		return closed;
	}

	public void setClosed(String closed) {
		this.closed = closed;
	}

}
