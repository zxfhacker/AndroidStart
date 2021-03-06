package com.alex.develop.androidstart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * App的启动画面，持续2.5s，可用于<br>
 * 1、展示App品牌LOGO<br>
 * 2、加载程序所需数据<br>
 * 3、介绍软件新特性
 * 4、广告展示
 * 
 * @author Created by alex 2014/11/07
 */
public class Splash extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		initialize();
		
//		initYoumiAd();// Youmi ads
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				startActivity(isFirstLaunch());
			}
		}, getResources().getInteger(R.integer.splash_duration));
	}
	
	/**
	 * 使用SharedPreferences写入标志位的方式判断App的每次运行是否为安装后第一次运行
	 * 若是，则需要跳转到新特性介绍画面；若不是，则直接进入主界面
	 * @return true，App安装后第一次运行；false，不是第一次运行
	 */
	private boolean isFirstLaunch() {
		
		String preferFiles = getPackageName();
		SharedPreferences prefer = getSharedPreferences(preferFiles, Context.MODE_PRIVATE);
		
		boolean firstLaunch = prefer.getBoolean(getString(R.string.key_first_launch), true);
		
		if(firstLaunch) {
			SharedPreferences.Editor editor = prefer.edit();
			editor.putBoolean(getString(R.string.key_first_launch), false);
			editor.commit();
		}
		
		return firstLaunch;
	}
	
	private void startActivity(boolean isFirst) {
		if (isFirst) {
			
			/**
			 *  启动新特性介绍
			 */
			final ImageView splash = (ImageView) findViewById(R.id.splash);
			Animation splashAnim = AnimationUtils.loadAnimation(Splash.this, R.anim.out_from_left);
			splashAnim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					Animation featureAnim = AnimationUtils.loadAnimation(Splash.this, R.anim.in_from_right);
					feature.startAnimation(featureAnim);
					feature.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {}

				@Override
				public void onAnimationEnd(Animation animation) {
					splash.setVisibility(View.GONE);
				}
			});
			
			splash.startAnimation(splashAnim);
			
		} else {
			
			// 程序主界面
			intent = new Intent(Splash.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
	}

	@SuppressLint("InflateParams")
	private void initialize() {
		LayoutInflater inflater = LayoutInflater.from(this);
		views = new View[]{
				inflater.inflate(R.layout.feature_1, null),
				inflater.inflate(R.layout.feature_2, null),
				inflater.inflate(R.layout.feature_3, null)
		};
		
		FeatureAdapter featureAdapter = new FeatureAdapter(views);

		feature = (ViewPager) findViewById(R.id.feature);
		feature.setAdapter(featureAdapter);
		feature.setOnPageChangeListener(featureAdapter);
	}

	private Intent intent;// 启动Activity
	private View[] views;// 存储ViewPager中的页面
	private ViewPager feature;// 新特性介绍
	private class FeatureAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

		public FeatureAdapter(View[] views) {
			this.views = views;
		}

		@Override
		public int getCount() {
			return views.length;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

			// 判断用户是否在拖拽画面
			isScolling = ViewPager.SCROLL_STATE_DRAGGING == arg0;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

			// 到达ViewPager的最后一页，才可执行后面的操作
			if(views.length != (arg0+1)) {
				return ;
			}

			// 在ViewPager的最后一页，向左滑动，进入主界面
			if(0.0f == arg1 && 0 == arg2 && isScolling) {
				if(!lastPageWasScolledLeft) {

					// 进入程序主界面
					startActivity(false);

					lastPageWasScolledLeft = true;
				}
			}
		}

		@Override
		public void onPageSelected(int arg0) {

			// 设置当前页面对应的指示器为激活状态
			ImageView indicator = (ImageView) views[arg0].findViewById(getIndicator(arg0));
			indicator.setImageResource(R.drawable.circle_dot_activited);

			// 设置其余的指示器为正常状态
			for(int i=0; i<views.length; ++i) {
				if(i!=arg0) {
					indicator = (ImageView) views[arg0].findViewById(getIndicator(i));
					indicator.setImageResource(R.drawable.circle_dot_normal);
				}
			}
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(views[position]);
			return views[position];
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(views[position]);
		}

		private int getIndicator(int index) {
			int resId = -1;
			switch (index) {
				case 0:
					resId = R.id.indicator_1;
					break;
				case 1:
					resId = R.id.indicator_2;
					break;
				case 2:
					resId = R.id.indicator_3;
					break;
			}
			return resId;
		}

		private View[] views;// 每个特性界面的布局
		private boolean lastPageWasScolledLeft;// 在ViewPager最后一页是否向左滑动
		private boolean isScolling;// 是否正在滑动
	}
}
