package com.ecaray.ecarmobaddemo;

import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ecaray.ecaradsdk.EcarAdBannerImagesManager;
import com.ecaray.ecaradsdk.EcarAdBannerManager;
import com.ecaray.ecaradsdk.Interface.EcarAdViewListener;
import com.youth.banner.Banner;


public class AdBannerActivity extends Activity implements EcarAdViewListener,
        OnClickListener {

	private EcarAdBannerManager adViewBIDView = null;
	private LinearLayout banner_layout = null;
	private Button banner_back = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) getSystemService(Service.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		int widthsizeX = dm.widthPixels;


		setContentView(R.layout.activity_banner);
		banner_layout = (LinearLayout) findViewById(R.id.banner_layout);
		banner_back = (Button) findViewById(R.id.banner_back);
		banner_back.setOnClickListener(this);


        String[] arr=new String[]{"1000220001","1000220002","1000220003"};
		adViewBIDView = new EcarAdBannerManager(AdBannerActivity.this,arr);
        adViewBIDView.setOnEcarListener(this);
//		Banner banner=adViewBIDView.getBanner(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,widthsizeX / 4)); //   listview addHeaderView 使用 AbsListView.LayoutParams
//		Banner banner=adViewBIDView.getBanner(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,widthsizeX / 4));
		Banner banner=adViewBIDView.getBanner(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,EcarAdBannerManager.HightAutoLaytou));
		banner_layout.addView(banner);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.banner_back:
			this.finish();
			break;
		}

	}


	@Override
	public void onAdClicked() {

	}

	@Override
	public void onAdClosed() {

	}

	@Override
	public void onAdClosedByUser() {

	}

	@Override
	public void noAd() {

	}


	@Override
	protected void onStart() {
		super.onStart();
		//开始轮播
		adViewBIDView.bannerStarAutoPlay();
	}

	@Override
	protected void onStop() {
		super.onStop();
		//结束轮播
		adViewBIDView.bannerStopAutoPlay();
	}
}
