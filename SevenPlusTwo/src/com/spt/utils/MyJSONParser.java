package com.spt.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.spt.adapter.GoodsManagerAdapter;
import com.spt.bean.GoodsInfo;
import com.spt.bean.HomePageDataInfo;
import com.spt.bean.UserDetailInfo;

/**
 * JSON解析类
 * */
public class MyJSONParser {

	/**
	 * 商户首页
	 * */
	public static HomePageDataInfo parse_homePageData(String jsonStr) throws JSONException {
		HomePageDataInfo info = new HomePageDataInfo();
		JSONTokener jasonParser = new JSONTokener(jsonStr);
		JSONObject jsonReturn = (JSONObject) jasonParser.nextValue();
		String order_count = jsonReturn.getString("order_count");
		String newpm = jsonReturn.getString("newpm");
		String qa_count = jsonReturn.getString("qa_count");
		String notice_count = jsonReturn.getString("notice_count");
		String goods_count = jsonReturn.getString("goods_count");
		String grade = jsonReturn.getString("grade");
		String service_score = jsonReturn.getString("service_score");
		String avg_line = jsonReturn.getString("avg_line");
		String credits = jsonReturn.getString("credits");
		String exp = jsonReturn.getString("exp");

		info.setOrder_count(order_count);
		info.setNewpm(newpm);
		info.setQa_count(qa_count);
		info.setNotice_count(notice_count);
		info.setGoods_count(goods_count);
		info.setGrade(grade);
		info.setService_score(service_score);
		info.setAvg_line(avg_line);
		info.setCredits(credits);
		info.setExp(exp);

		return info;
	}

	/**
	 * 用户基本信息
	 * */
	public static UserDetailInfo parse_userDetail(String jsonStr) throws JSONException {
		UserDetailInfo info = new UserDetailInfo();
		JSONTokener jasonParser = new JSONTokener(jsonStr);
		JSONObject jsonReturn = (JSONObject) jasonParser.nextValue();
		String user_id = jsonReturn.getString("user_id");
		String user_name = jsonReturn.getString("user_name");
		String avatar = jsonReturn.getString("avatar");
		String real_name = jsonReturn.getString("real_name");
		String gender = jsonReturn.getString("gender");
		String birthday = jsonReturn.getString("birthday");
		String phone_activated = jsonReturn.getString("phone_activated");
		String phone = jsonReturn.getString("phone");

		if ("1".equals(gender)) {
			info.setGender("男");
		} else if ("0".equals(gender)) {
			info.setGender("女");
		} else {
			info.setGender("保密");
		}

		if ("1".equals(phone_activated)) {
			info.setPhone_activated("已验证");
		} else {
			info.setPhone_activated("未验证");
		}

		info.setUser_id(user_id);
		info.setUser_name(user_name);
		info.setAvatar(avatar);
		info.setReal_name(real_name);
		info.setBirthday(birthday);
		info.setPhone(phone);

		return info;
	}

	/**
	 * 商品列表
	 * */
	public static void parse_goodsList(JSONArray jsonGoodList, GoodsManagerAdapter gma, int width) throws JSONException {
		int length = jsonGoodList.length();
		for (int i = 0; i < length; i++) {
			JSONObject jsonReturn1 = jsonGoodList.getJSONObject(i);
			String s_gid = jsonReturn1.getString("s_gid");
			String default_image = jsonReturn1.getString("default_image");
			String goods_id = jsonReturn1.getString("goods_id");
			String goods_name = jsonReturn1.getString("goods_name");
			String brand = jsonReturn1.getString("brand");
			String cate_name = jsonReturn1.getString("cate_name");
			String stock = jsonReturn1.getString("stock");
			String ext_activity_type_name = jsonReturn1.getString("ext_activity_type_name");
			String price = jsonReturn1.getString("price");
			String recommended = jsonReturn1.getString("recommended");
			String ext_commend_sort = jsonReturn1.getString("ext_commend_sort");
			String ext_new_sort = jsonReturn1.getString("ext_new_sort");
			String closed = jsonReturn1.getString("closed");

			GoodsInfo info = new GoodsInfo();
			info.setS_gid(s_gid);
			info.setGoods_id(goods_id);
			info.setGoods_name(goods_name);
			info.setBrand(brand);
			info.setCate_name(cate_name);
			info.setExt_activity_type_name(ext_activity_type_name);
			info.setDefault_image(default_image);
			info.setStock(stock);
			info.setPrice(price);
			info.setRecommended(recommended);
			info.setExt_commend_sort(ext_commend_sort);
			info.setExt_new_sort(ext_new_sort);
			info.setClosed(closed);
			info.setWidth(width);
			gma.addGoodsInfo(info);
		}
	}
}
