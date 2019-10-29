package com.ecaray.ecaradsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecaray.ecaradsdk.Interface.EcarAdViewListener;
import com.ecaray.ecaradsdk.bean.BaseData;
import com.ecaray.ecaradsdk.bean.ResponseData;
import com.ecaray.ecaradsdk.utils.Constant;
import com.ecaray.ecaradsdk.utils.HTTPUtils;
import com.ecaray.ecaradsdk.view.WebActivity;
import com.google.gson.Gson;

import org.json.JSONObject;

public class EcarAdTextManager {
    private    final String DownLoad_Apk_Name = "tableAPP.apk";
    Context  mcontext;
    TextView  textView;
    BaseData  baseData;
    EcarAdViewListener mecarAdViewListener;
    public static final  int  UPDATE_imageView = 0xffff003;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

                case UPDATE_imageView:

                    textView.setTextColor(Color.parseColor(baseData.getData().getColor()));
                    textView.setText(baseData.getData().getContent() );

                    break;
            }
        }
    };


    public EcarAdTextManager(Context context, String adId) {
        mcontext = context;
        parseJSON(adId);
        textView = new TextView(mcontext);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(lp1);
        textView.setMarqueeRepeatLimit(Integer.MAX_VALUE);
        textView.setPadding(0,10,0,10);
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
    }

    public void   setTextSize(float textSize){
        if(textView!=null)
          textView.setTextSize(textSize);
    }
    public TextView  getTextView(){
        return   textView;
    }
    public void ClickAD(  ){

        new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    String  json =HTTPUtils.clickAd(baseData.getData().getId());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

        if(baseData.getData().getClick_action().equals(Constant.click_action_1)){
            Intent intent = new Intent(mcontext, WebActivity.class);
            Activity activity=(Activity) mcontext;
            intent.putExtra("url",baseData.getData().getUrl());
            activity.startActivityForResult(intent, EcarAdSplManager.requestCode);
        }
        if(baseData.getData().getClick_action().equals(Constant.click_action_5)){
            HTTPUtils.TheadDownAPK(baseData.getData().getUrl(),mcontext,DownLoad_Apk_Name);
        }
        mecarAdViewListener.onAdClicked();
    }
    public void  setOnEcarListener(EcarAdViewListener ecarAdViewListener){
        this.mecarAdViewListener = ecarAdViewListener;
    }

    private void parseJSON( final  String adId ) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String   response = HTTPUtils.getJSONFROMInt(adId);
                    if(response!=null && !response.equals("")) {
                        baseData = new Gson().fromJson(response,BaseData.class);
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.get("state").toString().equals("1")) {
                            JSONObject data =  (JSONObject) jsonObject.get("data");
                            JSONObject adIdobj =  (JSONObject) data.get(adId);
                            if(adIdobj!=null){
                                baseData.setData(new Gson().fromJson(adIdobj.toString(),ResponseData.class)) ;
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
