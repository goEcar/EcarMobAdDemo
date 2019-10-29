package com.ecaray.ecaradsdk;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ecaray.ecaradsdk.Interface.EcarAdImagesListener;
import com.ecaray.ecaradsdk.Interface.EcarAdViewListener;
import com.ecaray.ecaradsdk.Interface.OnBannerListener;
import com.ecaray.ecaradsdk.bean.BaseData;
import com.ecaray.ecaradsdk.bean.ResponseData;
import com.ecaray.ecaradsdk.loader.GlideImageLoader;
import com.ecaray.ecaradsdk.utils.Constant;
import com.ecaray.ecaradsdk.utils.HTTPUtils;
import com.ecaray.ecaradsdk.view.WebActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youth.banner.Banner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EcarAdBannerManager    {
    private    final String DownLoad_Apk_Name = "AdBanner.apk";
    Context  mcontext;
    private static final  int  UPDATE_imageView = 0xffff003;
    SharedPreferences preferences;
    public   List<String> images=new ArrayList<String>();
    EcarAdViewListener mecarAdViewListener;
    BaseData baseData;
    Banner  banner;
    public static   int  HightAutoLaytou = 10000;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_imageView:
                    int count = msg.getData().getInt("count");
                    //简单使用
                    banner.setImages(images)
                            .setImageLoader(new GlideImageLoader())
                            .setOnBannerListener(new com.youth.banner.listener.OnBannerListener() {
                                @Override
                                public void OnBannerClick(int position) {
                                        ClickAD(position);
                                }
                            })
                            .start();

                    break;
            }
        }
    };

    public EcarAdBannerManager(Context context, String[] arr_adid) {
        banner = new Banner(context);
        mcontext = context;
        File file = new File(Environment.getExternalStorageDirectory(),Constant.adEarFile);
        if(!file.exists())
            file.mkdirs();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<arr_adid.length;i++){
            stringBuilder.append(arr_adid[i]+",");
        }
        preferences = mcontext.getSharedPreferences(Constant.adEarInfo,mcontext.MODE_PRIVATE);
        String  adId=stringBuilder.toString().substring(0,stringBuilder.toString().length()-1);
        parseJSON(preferences,adId,arr_adid);
    }

    public Banner getBanner(ViewGroup.LayoutParams    params) {
        if(params.height==HightAutoLaytou) {
            String banner_image_size = preferences.getString(Constant.banner_image_size, "705*288");
            if (banner_image_size.contains("*")) {
                DisplayMetrics dm = new DisplayMetrics();
                WindowManager wm = (WindowManager) mcontext.getSystemService(Service.WINDOW_SERVICE);
                wm.getDefaultDisplay().getMetrics(dm);
                int widthsizeX = dm.widthPixels;
                String[] arr = banner_image_size.split("\\*");
                double sp = Integer.parseInt(arr[1]) * widthsizeX / Double.parseDouble(arr[0]);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) sp);
                banner.setLayoutParams(lp1);
            } else {
                banner.setLayoutParams(params);
            }
        }else{
            banner.setLayoutParams(params);
        }
        return banner;
    }

    public void ClickAD(final int  position){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    String  json = HTTPUtils.clickAd(baseData.getDatas().get(position).getId() );
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

        if(baseData.getDatas().get(position).getClick_action().equals(Constant.click_action_1)){
            Intent intent = new Intent(mcontext, WebActivity.class);
            Activity activity=(Activity) mcontext;
            intent.putExtra("url",baseData.getDatas().get(position).getUrl());
            activity.startActivityForResult(intent, EcarAdSplManager.requestCode);
        }
        if(baseData.getDatas().get(position).getClick_action().equals(Constant.click_action_5)){
            HTTPUtils.TheadDownAPK(baseData.getDatas().get(position).getUrl(),mcontext,DownLoad_Apk_Name);
        }
        mecarAdViewListener.onAdClicked();
    }
    public void  setOnEcarListener(EcarAdViewListener ecarAdViewListener){
        this.mecarAdViewListener = ecarAdViewListener;
    }
    //开始轮播
    public  void   bannerStarAutoPlay(){
        if(banner!=null){
            banner.startAutoPlay();
        }
    }
    //结束轮播
    public  void   bannerStopAutoPlay(){
        if(banner!=null){
            banner.stopAutoPlay();
        }
    }



    private void parseJSON(final SharedPreferences preferences, final  String adId, final String[] arr_adid) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String   response = HTTPUtils.getJSONFROMInt(adId);
                    if(response!=null && !response.equals("")) {
                        baseData =new Gson().fromJson(response,BaseData.class);
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.get("state").toString().equals("1")) {
                            JSONObject data =  (JSONObject) jsonObject.get("data");
                            SharedPreferences.Editor editor = preferences.edit();
                            int banner_count =0;
                            JSONArray jsonArray = new JSONArray();
                            for(int i=0;i<arr_adid.length;i++){
                                JSONObject adIdobj=null;
                                if(data.has(arr_adid[i]))
                                   adIdobj =  (JSONObject) data.get(arr_adid[i]);
                                if(adIdobj!=null){
                                    jsonArray.put(adIdobj);
                                    String  img="";
                                    if(adIdobj.has("img"))
                                       img = (String) adIdobj.get("img");
                                    String  size="";
                                    if(adIdobj.has("size"))
                                       size = (String) adIdobj.get("size");
                                    banner_count = i+1;
                                    images.add(BuildConfig.Url_DOWN + img);
                                    if(size.contains("大")||size.contains("中")||size.contains("小")) {
                                        editor.putString(Constant.banner_image_size , size.substring(1, size.length()));
                                    }else {
                                        editor.putString(Constant.banner_image_size , size);
                                    }
                                    editor.commit();
                                }
                            }
                            baseData.setDatas((List<ResponseData>) new Gson().fromJson(jsonArray.toString(), new TypeToken<List<ResponseData>>(){}.getType()));
                            if(handler!=null) {
                                Message msg = new Message();
                                msg.what = UPDATE_imageView;
                                Bundle data2 = new Bundle();
//                                data2.putString("fname", fpath);
                                data2.putInt("count",banner_count);
                                msg.setData(data2);
                                handler.sendMessage(msg);
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
