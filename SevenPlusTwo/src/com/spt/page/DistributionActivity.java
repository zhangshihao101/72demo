package com.spt.page;

import com.spt.fragment.DirectoryFragment;
import com.spt.fragment.IndentFragment;
import com.spt.fragment.ShopCartFragment;
import com.spt.sht.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * 分销首页
 * 
 * @author lihongxuan
 *
 */
@SuppressLint("CommitTransaction")
public class DistributionActivity extends BaseActivity {

	private RadioGroup rg_distribution;
	private FragmentManager fm;
	private FragmentTransaction ft;
	private DirectoryFragment dirFragment;
	private ShopCartFragment cartFragment;
	private IndentFragment indFragment;
	private ImageView iv_dis_bg;
	private int page;
	private int tag;
	private boolean flag;
	private String order_sn, status, add_time_from, add_time_to;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.activity_distribution);
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		page = intent.getIntExtra("page", 1);
		tag = intent.getIntExtra("tag", 0);
		flag = intent.getBooleanExtra("flag", flag);
		order_sn = intent.getStringExtra("order_sn");
		status = intent.getStringExtra("status");
		add_time_from = intent.getStringExtra("add_time_from");
		add_time_to = intent.getStringExtra("add_time_to");

		initFragment();

	}

	/**
	 * 初始化fragment
	 */
	private void initFragment() {

		fm = getSupportFragmentManager();

		if (page == 2) {
			rg_distribution.check(R.id.rb_dis_shopcart);
			showFragment(2);
			rg_distribution.setVisibility(View.GONE);
			iv_dis_bg.setVisibility(View.GONE);

		} else if (page == 3) {
			rg_distribution.check(R.id.rb_dis_indent);
			showFragment(3);
			rg_distribution.setVisibility(View.GONE);
			iv_dis_bg.setVisibility(View.GONE);
		} else if (page == 0) {
			rg_distribution.check(R.id.rb_dis_indent);
			showFragment(3);
			rg_distribution.setVisibility(View.VISIBLE);
			iv_dis_bg.setVisibility(View.VISIBLE);
		} else {
			// 首次进入默认选中第一个
			rg_distribution.check(R.id.rb_dis_directory);
			showFragment(1);
		}

	}

	private void showFragment(int index) {
		ft = fm.beginTransaction();
		// 想要显示一个fragment先要隐藏其它fragment
		hintFragment(ft);
		switch (index) {
		case 1:
			// 如果fragment存在则显示出来
			if (dirFragment != null) {
				ft.show(dirFragment);
				// 否则第一次切换则添加fragment，注意添加后是会显示出来的，replace方法也是
			} else {
				dirFragment = new DirectoryFragment();
				ft.add(R.id.fl_dis_fragment, dirFragment);
			}
			break;
		case 2:
			if (cartFragment != null) {
				ft.show(cartFragment);
			} else {
				cartFragment = new ShopCartFragment();
				rg_distribution.check(R.id.rb_dis_shopcart);
				ft.add(R.id.fl_dis_fragment, cartFragment);
				if (page == 2) {
					Bundle bundle = new Bundle();
					bundle.putBoolean("flag", flag);
					cartFragment.setArguments(bundle);
				} else {
					Bundle bundle = new Bundle();
					bundle.putBoolean("flag", false);
					cartFragment.setArguments(bundle);
				}
			}
			break;
		case 3:
			if (indFragment != null) {
				ft.show(indFragment);
			} else {
				indFragment = new IndentFragment();
				rg_distribution.check(R.id.rb_dis_indent);
				ft.add(R.id.fl_dis_fragment, indFragment);
				if (page == 3) {
					Bundle bundle = new Bundle();
					bundle.putInt("tag", tag);
					bundle.putString("order_sn", order_sn);
					bundle.putString("status", status);
					bundle.putString("add_time_from", add_time_from);
					bundle.putString("add_time_to", add_time_to);
					indFragment.setArguments(bundle);
				} else if (page == 0) {
					Bundle bundle = new Bundle();
					bundle.putInt("tag", tag);
					indFragment.setArguments(bundle);
				} else {
					Bundle bundle = new Bundle();
					bundle.putInt("tag", 3);
					indFragment.setArguments(bundle);
				}
			}
			break;
		default:
			break;
		}
		ft.commit();
	}

	private void hintFragment(FragmentTransaction ft) {
		if (dirFragment != null) {
			ft.hide(dirFragment);
		}
		if (cartFragment != null) {
			ft.hide(cartFragment);
		}
		if (indFragment != null) {
			ft.hide(indFragment);
		}
	}

	@Override
	protected void init() {
		rg_distribution = (RadioGroup) findViewById(R.id.rg_distribution);
		iv_dis_bg = (ImageView) findViewById(R.id.iv_dis_bg);
	}

	@Override
	protected void addClickEvent() {
		rg_distribution.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_dis_directory:
					showFragment(1);
					break;
				case R.id.rb_dis_shopcart:
					showFragment(2);
					break;
				case R.id.rb_dis_indent:
					showFragment(3);
					break;
				default:
					break;
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			DistributionActivity.this.finish();
		}

		return super.onKeyDown(keyCode, event);
	}

}
