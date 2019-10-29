package com.ecaray.ecarmobaddemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ecaray.ecaradsdk.EcarAdBannerImagesManager;
import com.ecaray.ecaradsdk.Interface.EcarAdImagesListener;
import com.ecaray.ecaradsdk.Interface.EcarAdViewListener;


public class AdBannerImagesActivity extends Activity implements EcarAdViewListener,EcarAdImagesListener,
        OnClickListener {

	private EcarAdBannerImagesManager adViewBIDView = null;
	private LinearLayout layout = null;
	private Button banner_back = null;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bannerimage);
		layout = (LinearLayout) findViewById(R.id.banner_layout);
		banner_back = (Button) findViewById(R.id.banner_back);
		banner_back.setOnClickListener(this);

		/**
		 *  多个广告ID   可能返回一张广告
		 *  所以用监听的方式
		 */
        String[] arr=new String[]{"1000220001","1000220002","1000220003"};
		adViewBIDView = new EcarAdBannerImagesManager(AdBannerImagesActivity.this,arr);
        adViewBIDView.setOnEcarListener(this);
		adViewBIDView.setEcarAdImagesListener(this);
	}

	@Override
	public void loadImages(ImageView[]  imageViews) {
		if(imageViews!=null) {
			for (int i = 0; i < imageViews.length; i++) {
				layout.addView(imageViews[i],i);
			}
		}
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

}
