package com.spt.bean;

/**
 * 个人信息类
 * */
public class UserDetailInfo {
	private String user_id;// 用户id
	private String user_name;// 用户名
	private String avatar;// 用户头像
	private String phone;// 用户手机
	private String phone_activated;// 为1时手机已验证，为0时手机未验证
	private String real_name;// 真实姓名
	private String gender;// 性别，0保密，1男，2女
	private String birthday;// 生日，xxxx-xx-xx

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone_activated() {
		return phone_activated;
	}

	public void setPhone_activated(String phone_activated) {
		this.phone_activated = phone_activated;
	}

	public String getReal_name() {
		return real_name;
	}

	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

}
