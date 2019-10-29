package com.ecaray.ecarmobaddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClick(View view) {
        Log.i("ear-ad", "onButtonClick");
        if (view.getId() == R.id.adBannerBt2){
            Intent intent = new Intent(this,AdBannerActivity.class);
            startActivity(intent);
        }
        if (view.getId() == R.id.adBannerBt){
            Intent intent = new Intent(this,AdBannerImagesActivity.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.adInterstitialBt){
            Intent intent = new Intent(this,AdInstlActivity.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.adSplashBt){
            Intent intent = new Intent(this,SplActivity.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.adNativeBt){
            Intent intent = new Intent(this,AdInStremTextActivity.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.adTextNativeBt){
            Intent intent = new Intent(this,AdTextActivity.class);
            startActivity(intent);
        }
    }
}
