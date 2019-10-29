package com.ecaray.ecaradsdk;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ecaray.ecaradsdk.Interface.EcarAdImagesListener;
import com.ecaray.ecaradsdk.Interface.EcarAdViewListener;
import com.ecaray.ecaradsdk.bean.BaseData;
import com.ecaray.ecaradsdk.bean.ResponseData;
import com.ecaray.ecaradsdk.utils.Constant;
import com.ecaray.ecaradsdk.utils.HTTPUtils;
import com.ecaray.ecaradsdk.view.WebActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EcarAdBannerImagesManager {
    private    final String DownLoad_Apk_Name = "AdBanner.apk";
    Context  mcontext;
    private static final  int  UPDATE_imageView = 0xffff003;
    double widthsizeX;
    SharedPreferences preferences;
    EcarAdViewListener mecarAdViewListener;
    List<String>  imagenames = new ArrayList<String>();
    ImageView[]  imageViews=null;
    BaseData baseData;
    EcarAdImagesListener mecarAdImagesListener;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_imageView:
                      int count = msg.getData().getInt("count");
                      if(count==0&&mecarAdImagesListener!=null){
                          mecarAdImagesListener.loadImages(null);
                      }
                    imageViews = new ImageView[count];
                    for(int i=0 ;i < count;i++){
                        ImageView imageView = new ImageView(mcontext);
                        imageViews[i] = imageView;
                        String  imgname= imagenames.get(i);
                        String  banner_image_size= preferences.getString(Constant.banner_image_size, "705*288");
                        if(banner_image_size.contains("*")){
                            String[]  arr=banner_image_size.split("\\*");
                            double  sp=Integer.parseInt(arr[1])*widthsizeX/Double.parseDouble(arr[0]);
                            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)sp);
                            File adfile = new File(Environment.getExternalStorageDirectory().toString()+File.separator+Constant.adEarFile+File.separator+imgname);
                            if(adfile.exists()){
                                imageView.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString()+File.separator+Constant.adEarFile+File.separator+imgname));
                            }
                            imageView.setLayoutParams(lp1);
                            imageView.setTag(i);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ClickAD(v);
                                }
                            });
                        }
                    }
                    if(mecarAdImagesListener!=null)
                         mecarAdImagesListener.loadImages(imageViews);
                    break;
            }
        }
    };

    /**
     *  多个广告ID   可能返回一张广告
     *  所以用监听的方式
     */
    public EcarAdBannerImagesManager(Context context, String[] arr_adid) {
        mcontext = context;
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) mcontext.getSystemService(Service.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        widthsizeX = dm.widthPixels;

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

    public  void setEcarAdImagesListener(EcarAdImagesListener ecarAdImagesListener ){
        mecarAdImagesListener = ecarAdImagesListener;
    }

    public void ClickAD(View v){
        final  int index = (int) v.getTag();

        new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    String  json =HTTPUtils.clickAd(preferences.getString(baseData.getDatas().get(index).getId(),""));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

        if(baseData.getDatas().get(index).getClick_action().equals(Constant.click_action_1)){
            Intent intent = new Intent(mcontext, WebActivity.class);
            Activity activity=(Activity) mcontext;
            intent.putExtra("url",baseData.getDatas().get(index).getUrl());
            activity.startActivityForResult(intent, EcarAdSplManager.requestCode);
        }
        if(baseData.getDatas().get(index).getClick_action().equals(Constant.click_action_5)){
            HTTPUtils.TheadDownAPK(baseData.getDatas().get(index).getUrl(),mcontext,DownLoad_Apk_Name);
        }
        mecarAdViewListener.onAdClicked();
    }
    public void  setOnEcarListener(EcarAdViewListener ecarAdViewListener){
        this.mecarAdViewListener = ecarAdViewListener;
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
                            JSONArray  jsonArray = new JSONArray();
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
                                    String  imgname = img.replace("/","").replace("\\","") ;
                                    //去掉jpeg  png
                                    imgname = imgname.substring(0,imgname.length()-5);
                                    imagenames.add(imgname);
                                    banner_count = i+1;
                                    if(size.contains("大")||size.contains("中")||size.contains("小")) {
                                        editor.putString(Constant.banner_image_size, size.substring(1, size.length()));
                                    }else {
                                        editor.putString(Constant.banner_image_size , size);
                                    }
                                    editor.commit();
                                    File adfile = new File(Environment.getExternalStorageDirectory().toString()+File.separator+Constant.adEarFile+File.separator+imgname);
                                    //图片不存在就去下载。
                                    if(!adfile.exists()) {
                                        if (img != null && !img.equals("")) {
                                            String    fpath= HTTPUtils.DownIMG(BuildConfig.Url_DOWN + img, imgname);
                                        }
                                    }
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
