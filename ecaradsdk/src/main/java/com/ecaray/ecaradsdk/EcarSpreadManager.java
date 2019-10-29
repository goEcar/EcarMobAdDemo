package com.ecaray.ecaradsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ecaray.ecaradsdk.Interface.EcarSpreadListener;
import com.ecaray.ecaradsdk.utils.Constant;
import com.ecaray.ecaradsdk.utils.HTTPUtils;
import com.ecaray.ecaradsdk.view.WebActivity;


import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EcarSpreadManager extends InitSDKManager {
    EcarSpreadListener mecarSpreadListener;
    RelativeLayout relativeLayout;
    ImageView imageView ;
    public static  int  requestCode=3;
    private    final String DownLoad_Apk_Name = "openAPP.apk";
    Button btn1;
    private final  int  COUNTDOWN_MSG = 0xffff001;
    private final  int  Colse_MSG = 0xffff002;
    public static final  int  UPDATE_imageView = 0xffff003;
    private    int  countdown = 5;
    private Handler  handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
             switch (msg.what){
                 case COUNTDOWN_MSG:
                     if(btn1!=null){
                         countdown--;
                         btn1.setText(countdown+"s | 跳过");
                         if(countdown>=1) {
                             handler.sendEmptyMessageDelayed(COUNTDOWN_MSG, 1000);
                         }else{
                             if(null!=mecarSpreadListener){
                                 //跳到下一个Activity
                                 mecarSpreadListener.onAdClosed();
                                 handler.removeMessages(COUNTDOWN_MSG);
                                 btn1 = null;
                             }
                         }
                     }
                     break;
                 case Colse_MSG:
                     if(null!=mecarSpreadListener){
                         //跳到下一个Activity
                         mecarSpreadListener.onAdClosed();
                         handler.removeMessages(COUNTDOWN_MSG);
                         btn1 = null;
                     }
                     break;
                 case UPDATE_imageView:
                     if(imageView!=null){
                         imageView.setBackground(Drawable.createFromPath(msg.getData().getString("fname")));
                     }
                     break;
             }
        }
    };
    public EcarSpreadManager(final Context context, ViewGroup view, final String adId) {
        final SharedPreferences  preferences = context.getSharedPreferences(Constant.adEarInfo,context.MODE_PRIVATE);
        File file = new File(Environment.getExternalStorageDirectory(),Constant.adEarFile);
        if(!file.exists())
            file.mkdirs();

        handler.sendEmptyMessageDelayed(COUNTDOWN_MSG,1000);
        relativeLayout = new RelativeLayout(context);

        RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp4.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        imageView = new ImageView(context);
//        imageViewBottom.setLogo(R.mipmap.logo);
        relativeLayout.addView(imageView , lp4 );


        btn1 = new Button(context);
        btn1.setText("");
        btn1.setText(countdown+"s | 跳过");
        btn1.setBackgroundResource(R.drawable.buttonshape);
        btn1.setTextSize(20);
        btn1.requestFocus();
        btn1.setTextColor(Color.WHITE);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mecarSpreadListener){
                    mecarSpreadListener.onAdClosedByUser();
                    handler.removeMessages(COUNTDOWN_MSG);
                    btn1 = null;
                }
            }
        });
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mecarSpreadListener){
                    mecarSpreadListener.onAdClicked();
                    handler.removeMessages(COUNTDOWN_MSG);
                    countdown = 1;
                    btn1.setText("1s | 跳过");
                }
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try{
                            String  json =HTTPUtils.clickAd(preferences.getString(Constant.open_screen_id,""));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }.start();
                if(preferences.getString(Constant.open_screen_click_action,"").equals(Constant.click_action_1)){
                    Intent intent = new Intent(context, WebActivity.class);
                    Activity  activity=(Activity) context;
                    intent.putExtra("url",preferences.getString(Constant.open_screen_URL,""));
                    activity.startActivityForResult(intent,EcarSpreadManager.requestCode);
                }
                if(preferences.getString(Constant.open_screen_click_action,"").equals(Constant.click_action_5)){
                    HTTPUtils.TheadDownAPK(preferences.getString(Constant.open_screen_URL,""),context,DownLoad_Apk_Name);
                }

            }
        });
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp1.topMargin = 20 ;
        lp1.rightMargin = 20 ;
        lp1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        // btn1 位于父 View 的顶部，在父 View 中水平居中
        relativeLayout.addView(btn1, lp1 );

        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp2.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        view.addView(relativeLayout,lp2);

        //判断广告是否存在   不存在就跳过开屏
        File adfile = new File(Environment.getExternalStorageDirectory().toString()+File.separator+Constant.adEarFile+File.separator+preferences.getString(Constant.open_screen_img,"ecar"));
        if(adfile.exists()){
            imageView.setBackground(Drawable.createFromPath(adfile.toString()));
        }else{
            btn1.setVisibility(View.INVISIBLE);
            handler.removeMessages(COUNTDOWN_MSG);
            handler.sendEmptyMessageDelayed(Colse_MSG,2000);
        }
        parseJSON(preferences,adId);
    }

    private void parseJSON(final SharedPreferences  preferences, final String adId) {
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
                            JSONObject adIdobj =  (JSONObject) data.get(adId);
                            String  click_action = (String) adIdobj.get("click_action");
                            String  id = (String) adIdobj.get("id");
                            String  img = (String) adIdobj.get("img");
                            String  click_url = (String) adIdobj.get("url");
                            String  imgname = img.replace("/","").replace("\\","") ;
                            //去掉jpeg  png
                            imgname = imgname.substring(0,imgname.length()-5);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(Constant.open_screen_click_action, click_action);
                            editor.putString(Constant.open_screen_id, id);
                            editor.putString(Constant.open_screen_URL, click_url);

                            File adfile = new File(Environment.getExternalStorageDirectory().toString()+File.separator+Constant.adEarFile+File.separator+imgname);
                            //图片不存在就去下载。
                            if(!adfile.exists()) {
                                if (img != null && !img.equals("")) {
                                    String    fpath=HTTPUtils.DownIMG(BuildConfig.Url_DOWN + img, imgname);
                                    if(handler!=null) {
                                        Message msg = new Message();
                                        msg.what = EcarSpreadManager.UPDATE_imageView;
                                        Bundle data2 = new Bundle();
                                        data2.putString("fname", fpath);
                                        msg.setData(data2);
                                        handler.sendMessage(msg);
                                    }
                                }
                            }
                            editor.putString(Constant.open_screen_img, imgname);
                            editor.commit();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void  setOnEcarListener(EcarSpreadListener ecarSpreadListener){
        this.mecarSpreadListener = ecarSpreadListener;
    }

    public void setBackgroundColor(int backgroundColor) {
        relativeLayout.setBackgroundColor(backgroundColor);
    }



//    public void setLogo(int logo) {
//        imageViewBottom.setBackgroundResource(logo);
//    }

}
