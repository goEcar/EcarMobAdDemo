package com.ecaray.ecaradsdk;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecaray.ecaradsdk.Interface.EcarSpreadListener;
import com.ecaray.ecaradsdk.utils.Constant;
import com.ecaray.ecaradsdk.utils.HTTPUtils;
import com.ecaray.ecaradsdk.view.WebActivity;

import org.json.JSONObject;

import java.io.File;

public class AdViewTextManager {
    private    final String DownLoad_Apk_Name = "tableAPP.apk";
    Context  mcontext;
    TextView  textView;
    EcarSpreadListener mecarSpreadListener;
    public static final  int  UPDATE_imageView = 0xffff003;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

                case UPDATE_imageView:
                    SharedPreferences preferences = mcontext.getSharedPreferences(Constant.adEarInfo,mcontext.MODE_PRIVATE);
                    textView.setText(preferences.getString(Constant.text_content, ""));
                    break;
            }
        }
    };


    public AdViewTextManager(Context context, String adId) {
        mcontext = context;
        SharedPreferences preferences = mcontext.getSharedPreferences(Constant.adEarInfo,mcontext.MODE_PRIVATE);
        parseJSON(preferences,adId);
        textView = new TextView(mcontext);
    }
    public TextView  getBannerImageViews(){

        SharedPreferences preferences = mcontext.getSharedPreferences(Constant.adEarInfo,mcontext.MODE_PRIVATE);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(lp1);
        textView.setTextColor(Color.parseColor(preferences.getString(Constant.text_color, "#F0F0F0")));
        textView.setText(preferences.getString(Constant.text_content, ""));
        textView.setMarqueeRepeatLimit(Integer.MAX_VALUE);
        textView.setFocusable(true);
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setSingleLine();
        textView.setFocusableInTouchMode(true);
        textView.setHorizontallyScrolling(true);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickAD();
            }
        });
        return   textView;
    }
    public void ClickAD(  ){

        final  SharedPreferences preferences = mcontext.getSharedPreferences(Constant.adEarInfo,mcontext.MODE_PRIVATE);
        new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    String  json =HTTPUtils.clickAd(preferences.getString(Constant.text_id,""));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

        if(preferences.getString(Constant.text_click_action,"").equals(Constant.click_action_1)){
            Intent intent = new Intent(mcontext, WebActivity.class);
            Activity activity=(Activity) mcontext;
            intent.putExtra("url",preferences.getString(Constant.text_URL,""));
            activity.startActivityForResult(intent,EcarSpreadManager.requestCode);
        }
        if(preferences.getString(Constant.text_click_action,"").equals(Constant.click_action_5)){
            HTTPUtils.TheadDownAPK(preferences.getString(Constant.text_URL,""),mcontext,DownLoad_Apk_Name);
        }
        mecarSpreadListener.onAdClicked();
    }
    public void  setOnEcarListener(EcarSpreadListener ecarSpreadListener){
        this.mecarSpreadListener = ecarSpreadListener;
    }

    private void parseJSON(final SharedPreferences preferences, final  String adId ) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String   response = HTTPUtils.getJSONFROMInt(adId);
                    if(response!=null && !response.equals("")) {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.get("state").toString().equals("1")) {
                            JSONObject data =  (JSONObject) jsonObject.get("data");
                            SharedPreferences.Editor editor = preferences.edit();
                            JSONObject adIdobj =  (JSONObject) data.get(adId);
                            if(adIdobj!=null){
                                String  click_action = (String) adIdobj.get("click_action");
                                String  id = (String) adIdobj.get("id");
                                String  content = (String) adIdobj.get("content");
                                String  color = (String) adIdobj.get("color");
                                String  click_url = (String) adIdobj.get("url");
                                // banner 有多个， banner_count存放banner数据
                                editor.putString(Constant.text_click_action, click_action);
                                editor.putString(Constant.text_id, id);
                                editor.putString(Constant.text_URL, click_url);
                                editor.putString(Constant.text_color, color);
                                editor.putString(Constant.text_content, content);
                                editor.commit();
                                handler.sendEmptyMessage(UPDATE_imageView);
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
