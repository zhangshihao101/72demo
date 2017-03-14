package com.mts.pos.listview;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchMemberInfo implements Serializable, Parcelable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 卡号
	private String card;
	// 手机号
	private String phone;
	// 姓名
	private String name;
	// 性别
	private String sex;
	// 等级
	private String grade;
	private String partyId;
	// 关键词
	private String keyword;
	// 备注
	private String remark;

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public SearchMemberInfo() {
	}

	private SearchMemberInfo(Parcel parcel) {
		card = parcel.readString();
		phone = parcel.readString();
		name = parcel.readString();
		sex = parcel.readString();
		grade = parcel.readString();
		partyId = parcel.readString();
		keyword = parcel.readString();
		remark = parcel.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(card);
		dest.writeString(phone);
		dest.writeString(name);
		dest.writeString(sex);
		dest.writeString(grade);
		dest.writeString(partyId);
		dest.writeString(keyword);
		dest.writeString(remark);
	}

	public static final Parcelable.Creator<SearchMemberInfo> CREATOR = new Parcelable.Creator<SearchMemberInfo>() {

		@Override
		public SearchMemberInfo createFromParcel(Parcel source) {
			return new SearchMemberInfo(source);
		}

		@Override
		public SearchMemberInfo[] newArray(int size) {
			return new SearchMemberInfo[size];
		}
	};

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
