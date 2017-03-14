package com.spt.page;

import com.spt.fragment.MtsGoodsFragment;
import com.spt.fragment.MtsSaleFragment;
import com.spt.fragment.MtsStockFragment;
import com.spt.sht.R;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MtsMainActivity extends BaseActivity {

	private RadioGroup rgp_menu;
	private RadioButton rbt_sale, rbt_stock, rbt_goods;

	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;

	private MtsSaleFragment mtsSaleFragment;
	private MtsStockFragment mtsStockFragment;
	private MtsGoodsFragment mtsGoodsFragment;

	private int page;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.activity_mts_main);
		super.onCreate(savedInstanceState);
		
		page = getIntent().getIntExtra("page", 0);

		fragmentManager = getSupportFragmentManager();

		if (page == 0) {
			rgp_menu.check(R.id.rbt_sale);
			setTabSelection(0);
		} else if (page == 1) {
			rgp_menu.check(R.id.rbt_stock);
			setTabSelection(1);
		} else if (page == 2) {
			rgp_menu.check(R.id.rbt_goods);
			setTabSelection(2);
		}

	}

	@Override
	protected void init() {
		rgp_menu = (RadioGroup) findViewById(R.id.rgp_menu);
		rbt_sale = (RadioButton) findViewById(R.id.rbt_sale);
		rbt_stock = (RadioButton) findViewById(R.id.rbt_stock);
		rbt_goods = (RadioButton) findViewById(R.id.rbt_goods);
	}

	@Override
	protected void addClickEvent() {
		rgp_menu.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == rbt_sale.getId()) {
					setTabSelection(0);
					Drawable drawable1 = getResources().getDrawable(R.drawable.sale_tabbar_icon1_hl);
					drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
					rbt_sale.setCompoundDrawables(drawable1, null, null, null);
					rbt_sale.setTextColor(0xff52aee2);

					Drawable drawable2 = getResources().getDrawable(R.drawable.sale_tabbar_icon2);
					drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
					rbt_stock.setCompoundDrawables(drawable2, null, null, null);
					rbt_stock.setTextColor(0xffc8c8c8);

					Drawable drawable5 = getResources().getDrawable(R.drawable.sale_tabbar_icon3);
					drawable5.setBounds(0, 0, drawable5.getMinimumWidth(), drawable5.getMinimumHeight());
					rbt_goods.setCompoundDrawables(drawable5, null, null, null);
					rbt_goods.setTextColor(0xffc8c8c8);

				} else if (checkedId == rbt_stock.getId()) {
					setTabSelection(1);
					Drawable drawable3 = getResources().getDrawable(R.drawable.sale_tabbar_icon2_hl);
					drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());
					rbt_stock.setCompoundDrawables(drawable3, null, null, null);
					rbt_stock.setTextColor(0xff52aee2);

					Drawable drawable4 = getResources().getDrawable(R.drawable.sale_tabbar_icon1);
					drawable4.setBounds(0, 0, drawable4.getMinimumWidth(), drawable4.getMinimumHeight());
					rbt_sale.setCompoundDrawables(drawable4, null, null, null);
					rbt_sale.setTextColor(0xffc8c8c8);

					Drawable drawable6 = getResources().getDrawable(R.drawable.sale_tabbar_icon3);
					drawable6.setBounds(0, 0, drawable6.getMinimumWidth(), drawable6.getMinimumHeight());
					rbt_goods.setCompoundDrawables(drawable6, null, null, null);
					rbt_goods.setTextColor(0xffc8c8c8);
				} else if (checkedId == rbt_goods.getId()) {
					setTabSelection(2);
					Drawable drawable6 = getResources().getDrawable(R.drawable.sale_tabbar_icon3_hl);
					drawable6.setBounds(0, 0, drawable6.getMinimumWidth(), drawable6.getMinimumHeight());
					rbt_goods.setCompoundDrawables(drawable6, null, null, null);
					rbt_goods.setTextColor(0xff52aee2);

					Drawable drawable3 = getResources().getDrawable(R.drawable.sale_tabbar_icon2);
					drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());
					rbt_stock.setCompoundDrawables(drawable3, null, null, null);
					rbt_stock.setTextColor(0xffc8c8c8);

					Drawable drawable4 = getResources().getDrawable(R.drawable.sale_tabbar_icon1);
					drawable4.setBounds(0, 0, drawable4.getMinimumWidth(), drawable4.getMinimumHeight());
					rbt_sale.setCompoundDrawables(drawable4, null, null, null);
					rbt_sale.setTextColor(0xffc8c8c8);
				}
			}
		});

	}

	/**
	 * 切换两个fragment且保存数据的方法
	 * 
	 * @param index
	 */
	private void setTabSelection(int index) {
		fragmentTransaction = fragmentManager.beginTransaction();
		hideFragments(fragmentTransaction);
		switch (index) {
		case 0:
			if (mtsSaleFragment == null) {
				mtsSaleFragment = new MtsSaleFragment();
				fragmentTransaction.add(R.id.fl_frame, mtsSaleFragment, "saleF");
			} else {
				fragmentTransaction.show(mtsSaleFragment);
			}
			break;

		case 1:
			if (mtsStockFragment == null) {
				mtsStockFragment = new MtsStockFragment();
				fragmentTransaction.add(R.id.fl_frame, mtsStockFragment, "stockF");
			} else {
				fragmentTransaction.show(mtsStockFragment);
			}
			break;
		case 2:
			if (mtsGoodsFragment == null) {
				mtsGoodsFragment = new MtsGoodsFragment();
				fragmentTransaction.add(R.id.fl_frame, mtsGoodsFragment, "goodsF");
			} else {
				fragmentTransaction.show(mtsGoodsFragment);
			}

			break;
		}
		fragmentTransaction.commit();
	}

	/**
	 * 将所有的Fragment都置为隐藏状态。
	 * 
	 * @param transaction
	 *            用于对Fragment执行操作的事务
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (mtsSaleFragment != null) {
			transaction.hide(mtsSaleFragment);
		}
		if (mtsStockFragment != null) {
			transaction.hide(mtsStockFragment);
		}
		if (mtsGoodsFragment != null) {
			transaction.hide(mtsGoodsFragment);
		}
	}

}
