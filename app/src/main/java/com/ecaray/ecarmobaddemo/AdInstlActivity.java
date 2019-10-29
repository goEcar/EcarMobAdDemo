package com.ecaray.ecarmobaddemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ecaray.ecaradsdk.EcarAdInstlManager;
import com.ecaray.ecaradsdk.Interface.EcarAdViewListener;

/**
 * 插屏广告
 *
 */
public class AdInstlActivity extends Activity implements EcarAdViewListener,OnClickListener {

	private Button nextInstl = null;
	private Button insert_back = null;
	private EcarAdInstlManager adInstlBIDView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insert);
		nextInstl = (Button) findViewById(R.id.instl_change);
		insert_back = (Button) findViewById(R.id.insert_back);
		nextInstl.setOnClickListener(this);
		insert_back.setOnClickListener(this);
		adInstlBIDView = new EcarAdInstlManager(this,  "1000230001");//有关闭按钮：true，无关闭按钮：false
		adInstlBIDView.setOnEcarListener(this);


	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.instl_change:

				adInstlBIDView.showInstl();
				break;
		case R.id.insert_back:
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
