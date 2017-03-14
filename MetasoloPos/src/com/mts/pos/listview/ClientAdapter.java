package com.mts.pos.listview;

import java.util.HashMap;
import java.util.List;

import com.mts.pos.R;
import com.mts.pos.Activity.ChangeNumberActivity;
import com.mts.pos.Activity.ChangeWeatherActivity;
import com.mts.pos.Activity.ClientActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ClientAdapter extends BaseAdapter {

	Context mContext;
	List<ClientInfo> mlist;
	private List<String> weatherList;
	private HashMap<String, String> weatherMap;
	private String wea = "", weather = "", weatherNo = "";
	private int postion;
	private ArrayAdapter<String> weather_adapter;

	public ClientAdapter(Context mContext, List<ClientInfo> mlist) {
		super();
		this.mContext = mContext;
		this.mlist = mlist;
	}

	@Override
	public int getCount() {
		return mlist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return (ClientInfo) mlist.get(position);
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
			convertView = ln.inflate(R.layout.item_client, parent, false);
			holder = new ViewHolder();
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_people = (TextView) convertView.findViewById(R.id.tv_people);
			holder.tv_remark = (TextView) convertView.findViewById(R.id.tv_remark);
			holder.tv_weather = (TextView) convertView.findViewById(R.id.tv_weather);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_time.setText(mlist.get(position).getTimeSlot());
		holder.tv_weather.setText(mlist.get(position).getWeather());
		holder.tv_people.setText("" + mlist.get(position).getPeople());
		holder.tv_remark.setText(mlist.get(position).getRemark());

		holder.tv_people.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ChangeNumberActivity.class);
				intent.putExtra("position", String.valueOf(position));
				intent.putExtra("type", "clientNum");
				intent.putExtra("time", holder.tv_time.getText().toString());
				intent.putExtra("weather", mlist.get(position).getWeatherNo());
				intent.putExtra("people", holder.tv_people.getText().toString());
				intent.putExtra("remark", holder.tv_remark.getText().toString());
				
				((ClientActivity) mContext).startActivity(intent);
			}
		});

		holder.tv_remark.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ChangeNumberActivity.class);
				intent.putExtra("position", String.valueOf(position));
				intent.putExtra("type", "clientMark");
				intent.putExtra("time", holder.tv_time.getText().toString());
				intent.putExtra("weather", mlist.get(position).getWeatherNo());
				intent.putExtra("people", holder.tv_people.getText().toString());
				intent.putExtra("remark", holder.tv_remark.getText().toString());
				
				((ClientActivity) mContext).startActivity(intent);
			}
		});

		holder.tv_weather.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ChangeWeatherActivity.class);
				intent.putExtra("position", String.valueOf(position));
				intent.putExtra("time", holder.tv_time.getText().toString());
				intent.putExtra("weather", holder.tv_weather.getText().toString());
				intent.putExtra("people", holder.tv_people.getText().toString());
				intent.putExtra("remark", holder.tv_remark.getText().toString());
				((ClientActivity) mContext).startActivity(intent);
			}
		});

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_time, tv_weather, tv_people, tv_remark;
		// EditText et_people, et_remark;
		// Spinner sp_weather;
	}
}
