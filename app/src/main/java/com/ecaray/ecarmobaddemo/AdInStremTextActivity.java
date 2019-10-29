package com.ecaray.ecarmobaddemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ecaray.ecaradsdk.EcarAdInStremManager;
import com.ecaray.ecaradsdk.Interface.EcarAdViewListener;


public class AdInStremTextActivity extends Activity implements EcarAdViewListener,
        OnClickListener {

	private EcarAdInStremManager adViewBIDView = null;
	private LinearLayout layout = null;
	private Button banner_back = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instremtext);
		layout = (LinearLayout) findViewById(R.id.banner_layout);
		banner_back = (Button) findViewById(R.id.banner_back);
		banner_back.setOnClickListener(this);

		adViewBIDView = new EcarAdInStremManager(AdInStremTextActivity.this,layout,"1000250001");
        adViewBIDView.setOnEcarListener(this);


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
