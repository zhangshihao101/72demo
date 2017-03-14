package com.mts.pos.listview;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.mts.pos.R;
import com.mts.pos.Activity.PayActivity;
import com.mts.pos.Common.Constants;
import com.mts.pos.Common.Localxml;
import com.mts.pos.Common.MyPostTask;
import com.mts.pos.Common.NetworkUtil;
import com.mts.pos.Common.Urls;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class KeepbillsAdapter extends BaseAdapter {

	Context mContext;
	List<KeepbillsInfo> mlist;
	// List<KeepbillsPicInfo> mPiclist;
	String name, names;

	public KeepbillsAdapter(Context mContext, List<KeepbillsInfo> mlist) {
		super();
		this.mContext = mContext;
		this.mlist = mlist;
		// this.mPiclist = mPiclist;
	}

	@Override
	public int getCount() {
		return mlist.size();
	}

	@Override
	public Object getItem(int position) {
		return (KeepbillsInfo) mlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {

			LayoutInflater ln = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = ln.inflate(R.layout.item_keepbills, parent, false);
			holder = new ViewHolder();
			holder.tv_billsname = (TextView) convertView.findViewById(R.id.tv_billsname);
			holder.tv_keeptime = (TextView) convertView.findViewById(R.id.tv_keeptime);
			holder.tv_bills = (TextView) convertView.findViewById(R.id.tv_bills);
			holder.tv_billsamount = (TextView) convertView.findViewById(R.id.tv_billsamount);
			holder.btn_keepbills_cancel = (Button) convertView.findViewById(R.id.btn_keepbills_cancel);
			holder.btn_keepbills_enter = (Button) convertView.findViewById(R.id.btn_keepbills_enter);
			holder.ll_gallery = (LinearLayout) convertView.findViewById(R.id.ll_gallery);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (mlist.get(position).getCustomerName().equals("null") || mlist.get(position).getCustomerName() == null) {
			holder.tv_billsname.setText("零售客户");
		} else {
			holder.tv_billsname.setText(mlist.get(position).getCustomerName());
		}

		holder.tv_keeptime.setText("挂单时间:" + mlist.get(position).getBillsTime());
		DecimalFormat df = new DecimalFormat("0.00");
		holder.tv_billsamount.setText("金额: " + df.format(Integer.valueOf(mlist.get(position).getAmount())));
		for (int i = 0; i < mlist.get(position).getList().size(); i++) {
			name += mlist.get(position).getList().get(i).getProductName() + "/";
		}

		holder.tv_bills.setText(name);

		// LayoutInflater ln2 = (LayoutInflater)
		// mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// for (int i = 0; i < mPiclist.size(); i++) {
		// View view = ln2.inflate(R.layout.item_gallery, holder.ll_gallery,
		// false);
		// ImageView img = (ImageView) view.findViewById(R.id.iv_pics);
		// Picasso.with(mContext).load(mPiclist.get(i).getImageUrl()).into(img);
		// }
		holder.btn_keepbills_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final KeepBillsDialog keepbillsdialog = new KeepBillsDialog(mContext, "确定删除挂单信息么？", "", "确定", "取消");
				keepbillsdialog.show();
				keepbillsdialog.setClicklistener(new KeepBillsDialog.ClickListenerInterface() {

					@Override
					public void doConfirm() {
						List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
						nameValuePair.add(new BasicNameValuePair("externalLoginKey",
								Localxml.search(mContext, "externalloginkey")));
						nameValuePair.add(new BasicNameValuePair("shoppingListId", mlist.get(position).getBillsID()));
						if (NetworkUtil.isConnected(mContext)) {
							SingleTask singleTask = new SingleTask(mContext, Urls.base + Urls.cancel_keepbills,
									nameValuePair, "1");
							singleTask.execute("");
						} else {
							Toast.makeText(mContext, "没有网络连接，请检查后再试！", Toast.LENGTH_SHORT).show();
						}

						mlist.remove(position);
						notifyDataSetChanged();
						keepbillsdialog.dismiss();
					}

					@Override
					public void doCancel() {
						// TODO Auto-generated method stub
						keepbillsdialog.dismiss();
					}
				});
			}
		});
		holder.btn_keepbills_enter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (PayActivity.productData.size() != 0) {
					final KeepBillsDialog confirmDialog = new KeepBillsDialog(mContext, "当前购物车尚未结算，确定取单么？",
							"确认后将删除当前未结算的购物车数据。", "确认", "取消");
					confirmDialog.show();
					confirmDialog.setClicklistener(new KeepBillsDialog.ClickListenerInterface() {
						@Override
						public void doConfirm() {
							// TODO Auto-generated method stub
							List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
							nameValuePair.add(new BasicNameValuePair("externalLoginKey",
									Localxml.search(mContext, "externalloginkey")));
							nameValuePair
									.add(new BasicNameValuePair("shoppingListId", mlist.get(position).getBillsID()));
							if (NetworkUtil.isConnected(mContext)) {
								SingleTask singleTask = new SingleTask(mContext, Urls.base + Urls.add_billstoCart,
										nameValuePair, "0");
								singleTask.execute("");
							} else {
								Toast.makeText(mContext, "没有网络连接，请检查后再试！", Toast.LENGTH_SHORT).show();
							}

							mlist.remove(position);
							notifyDataSetChanged();
							confirmDialog.dismiss();
							// AppManager.getAppManager().AppExit(mContext);
						}

						@Override
						public void doCancel() {
							// TODO Auto-generated method stub
							confirmDialog.dismiss();
						}
					});
				} else {
					final KeepBillsDialog confirmDialog = new KeepBillsDialog(mContext, "确定取单么？", "", "确认", "取消");
					confirmDialog.show();
					confirmDialog.setClicklistener(new KeepBillsDialog.ClickListenerInterface() {
						@Override
						public void doConfirm() {
							// TODO Auto-generated method stub
							List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
							nameValuePair.add(new BasicNameValuePair("externalLoginKey",
									Localxml.search(mContext, "externalloginkey")));
							nameValuePair
									.add(new BasicNameValuePair("shoppingListId", mlist.get(position).getBillsID()));
							if (NetworkUtil.isConnected(mContext)) {
								SingleTask singleTask = new SingleTask(mContext, Urls.base + Urls.add_billstoCart,
										nameValuePair, "0");
								singleTask.execute("");
							} else {
								Toast.makeText(mContext, "没有网络连接，请检查后再试！", Toast.LENGTH_SHORT).show();
							}

							mlist.remove(position);
							notifyDataSetChanged();
							confirmDialog.dismiss();
							// AppManager.getAppManager().AppExit(mContext);
						}

						@Override
						public void doCancel() {
							// TODO Auto-generated method stub
							confirmDialog.dismiss();
						}
					});
				}

			}
		});

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_billsname, tv_keeptime, tv_bills, tv_billsamount;
		Button btn_keepbills_cancel, btn_keepbills_enter;
		LinearLayout ll_gallery;
	}

	public void updateUI(String which, String result) {
		if (which.equals("1")) {
			Log.e("LOOK", "删除结果==" + result);
			try {
				String code = new JSONObject(result).optString("_IS_SUCCESS_");
				String message = new JSONObject(result).optString("_RETURN_MESSAGE_");

				if (code.equals("Y")) {
					Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (which.equals("0")) {
			Log.e("LOOK", "我要取单==" + result);
			try {
				String promoNo = new JSONObject(result).optString("productPromoCodes");
				String takeResult = new JSONObject(result).optString("_IS_SUCCESS_");
				String takeMessage = new JSONObject(result).optString("_RETURN_MESSAGE_");
				String orderId = new JSONObject(result).optString("orderParyId");
				String cartItem = new JSONObject(result).optString("cartItems");
				Log.e("挂单商品信息", "挂单商品信息==" + cartItem);
				if (takeResult.equals("Y")) {
					Toast.makeText(mContext, takeMessage, Toast.LENGTH_SHORT).show();
					PayActivity.cartItem = result;
					Intent intent = new Intent(mContext,PayActivity.class);
					mContext.startActivity(intent);
				} else {
					Toast.makeText(mContext, "加入购物车失败", Toast.LENGTH_SHORT).show();
				}

				// EventBus.getDefault().post(cartItem);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	class SingleTask extends MyPostTask {
		String which;

		public SingleTask(Context context, String url, List<NameValuePair> nameValuePair, String whichtask) {
			super(context, url, nameValuePair, whichtask);
			which = whichtask;
		}

		@Override
		protected void onPostExecute(String result) {
			if (null == result || ("").equals(result) || result.length() == 0 || result.equals(Constants.not_found)
					|| result.equals(Constants.time_out)) {
				Toast.makeText(mContext, "网络不好，请重试！", Toast.LENGTH_SHORT).show();
			} else {
				updateUI(which, result);
			}
		}
	}

}
