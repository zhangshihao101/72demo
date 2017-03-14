package com.spt.bean;

public class AddressInfo {

	// 地址Id
	private String addr_id;
	// 用户id
	private String user_id;
	// 收货人姓名
	private String consignee;
	// 地区ID
	private String region_id;
	// 地区名
	private String region_name;
	// 详细地址
	private String address;
	// 邮编
	private String zipcode;
	// 电话
	private String phone_tel;
	// 手机
	private String phone_mob;
	// 地区ID拆分（国）
	private String ext_region_id_0;
	// 地区ID拆分（省）
	private String ext_region_id_1;
	// 地区ID拆分（市）
	private String ext_region_id_2;
	// 地区ID拆分（区）
	private String ext_region_id_3;
	// 选中标记
	private boolean isChecked;

	public String getAddr_id() {
		return addr_id;
	}

	public void setAddr_id(String addr_id) {
		this.addr_id = addr_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getConsignee() {
		return consignee;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

	public String getRegion_id() {
		return region_id;
	}

	public void setRegion_id(String region_id) {
		this.region_id = region_id;
	}

	public String getRegion_name() {
		return region_name;
	}

	public void setRegion_name(String region_name) {
		this.region_name = region_name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getPhone_tel() {
		return phone_tel;
	}

	public void setPhone_tel(String phone_tel) {
		this.phone_tel = phone_tel;
	}

	public String getPhone_mob() {
		return phone_mob;
	}

	public void setPhone_mob(String phone_mob) {
		this.phone_mob = phone_mob;
	}

	public String getExt_region_id_0() {
		return ext_region_id_0;
	}

	public void setExt_region_id_0(String ext_region_id_0) {
		this.ext_region_id_0 = ext_region_id_0;
	}

	public String getExt_region_id_1() {
		return ext_region_id_1;
	}

	public void setExt_region_id_1(String ext_region_id_1) {
		this.ext_region_id_1 = ext_region_id_1;
	}

	public String getExt_region_id_2() {
		return ext_region_id_2;
	}

	public void setExt_region_id_2(String ext_region_id_2) {
		this.ext_region_id_2 = ext_region_id_2;
	}

	public String getExt_region_id_3() {
		return ext_region_id_3;
	}

	public void setExt_region_id_3(String ext_region_id_3) {
		this.ext_region_id_3 = ext_region_id_3;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	@Override
	public String toString() {
		return "AddressInfo [addr_id=" + addr_id + ", user_id=" + user_id + ", consignee=" + consignee + ", region_id="
				+ region_id + ", region_name=" + region_name + ", address=" + address + ", zipcode=" + zipcode
				+ ", phone_tel=" + phone_tel + ", phone_mob=" + phone_mob + ", ext_region_id_0=" + ext_region_id_0
				+ ", ext_region_id_1=" + ext_region_id_1 + ", ext_region_id_2=" + ext_region_id_2 + ", ext_region_id_3="
				+ ext_region_id_3 + "]";
	}

}
