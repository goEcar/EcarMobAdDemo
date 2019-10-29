package com.ecaray.ecarmobaddemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecaray.ecaradsdk.EcarAdTextManager;
import com.ecaray.ecaradsdk.Interface.EcarAdViewListener;


public class AdTextActivity extends Activity implements EcarAdViewListener,
        OnClickListener {

	private EcarAdTextManager adViewBIDView = null;
	private LinearLayout layout = null;
	private Button banner_back = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text);
		layout = (LinearLayout) findViewById(R.id.banner_layout);
		banner_back = (Button) findViewById(R.id.banner_back);
		banner_back.setOnClickListener(this);

		adViewBIDView = new EcarAdTextManager(AdTextActivity.this,"1000240001");
        adViewBIDView.setOnEcarListener(this);
//		adViewBIDView.setTextSize(25);  // 设置字体大小


        TextView textView=adViewBIDView.getTextView();
        if(textView!=null) {
				layout.addView(textView);
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
