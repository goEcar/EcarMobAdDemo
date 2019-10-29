package com.ecaray.ecaradsdk;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ecaray.ecaradsdk.Interface.EcarSpreadListener;
import com.ecaray.ecaradsdk.utils.Constant;
import com.ecaray.ecaradsdk.utils.HTTPUtils;
import com.ecaray.ecaradsdk.view.WebActivity;

import org.json.JSONObject;

import java.io.File;

public class AdViewBannerManager {
    private    final String DownLoad_Apk_Name = "tableAPP.apk";
    Context  mcontext;
    public static final  int  UPDATE_imageView = 0xffff003;

    EcarSpreadListener mecarSpreadListener;
    ImageView[]  imageViews=null;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_imageView:
                      int index = msg.getData().getInt("index");
                      if(imageViews.length > index){
                          imageViews[index].setImageBitmap(BitmapFactory.decodeFile(msg.getData().getString("fname")));
                      }
                    break;
            }
        }
    };


    public AdViewBannerManager(Context context,  String[] arr_adid) {
        mcontext = context;
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<arr_adid.length;i++){
            stringBuilder.append(arr_adid[i]+",");
        }
        final  SharedPreferences preferences = mcontext.getSharedPreferences(Constant.adEarInfo,mcontext.MODE_PRIVATE);
        String  adId=stringBuilder.toString().substring(0,stringBuilder.toString().length()-1);
        parseJSON(preferences,adId,arr_adid);
    }
    public ImageView[]  getBannerImageViews(){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) mcontext.getSystemService(Service.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        double widthsizeX = dm.widthPixels;
        final  SharedPreferences preferences = mcontext.getSharedPreferences(Constant.adEarInfo,mcontext.MODE_PRIVATE);
        int  bannercount = preferences.getInt(Constant.banner_count, -1);
        if(bannercount == -1)
            return  null;
        imageViews = new ImageView[bannercount];
        for(int i=0 ;i < bannercount;i++){
            ImageView  imageView =new ImageView(mcontext);
            imageViews[i] = imageView;
            String  imgname= preferences.getString(Constant.banner_img+i, "ecar");
            String  banner_image_size= preferences.getString(Constant.banner_image_size+i, "705*288");
            //图片不存在就去下载。
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
        return   imageViews;
    }
    public void ClickAD(   View v){
        final  int index = (int) v.getTag();
        final  SharedPreferences preferences = mcontext.getSharedPreferences(Constant.adEarInfo,mcontext.MODE_PRIVATE);
        new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    String  json =HTTPUtils.clickAd(preferences.getString(Constant.banner_id+index,""));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

        if(preferences.getString(Constant.banner_click_action+index,"").equals(Constant.click_action_1)){
            Intent intent = new Intent(mcontext, WebActivity.class);
            Activity activity=(Activity) mcontext;
            intent.putExtra("url",preferences.getString(Constant.banner_URL+index,""));
            activity.startActivityForResult(intent,EcarSpreadManager.requestCode);
        }
        if(preferences.getString(Constant.banner_click_action+index,"").equals(Constant.click_action_5)){
            HTTPUtils.TheadDownAPK(preferences.getString(Constant.banner_URL+index,""),mcontext,DownLoad_Apk_Name);
        }
        mecarSpreadListener.onAdClicked();
    }
    public void  setOnEcarListener(EcarSpreadListener ecarSpreadListener){
        this.mecarSpreadListener = ecarSpreadListener;
    }

    private void parseJSON(final SharedPreferences preferences, final  String adId, final String[] arr_adid) {
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
                            for(int i=0;i<arr_adid.length;i++){
                                JSONObject adIdobj =  (JSONObject) data.get(arr_adid[i]);
                                if(adIdobj!=null){
                                    String  click_action = (String) adIdobj.get("click_action");
                                    String  id = (String) adIdobj.get("id");
                                    String  img = (String) adIdobj.get("img");
                                    String  size = (String) adIdobj.get("size");
                                    String  click_url = (String) adIdobj.get("url");
                                    String  imgname = img.replace("/","").replace("\\","") ;
                                    //去掉jpeg  png
                                    imgname = imgname.substring(0,imgname.length()-5);
                                    // banner 有多个， banner_count存放banner数据
                                    editor.putString(Constant.banner_click_action+i, click_action);
                                    editor.putString(Constant.banner_id+i, id);
                                    editor.putInt(Constant.banner_count, i+1);
                                    editor.putString(Constant.banner_URL+i, click_url);
                                    editor.putString(Constant.banner_image_size+i, size.substring(1,size.length()));

                                    File adfile = new File(Environment.getExternalStorageDirectory().toString()+File.separator+Constant.adEarFile+File.separator+imgname);
                                    //图片不存在就去下载。
                                    if(!adfile.exists()) {
                                        if (img != null && !img.equals("")) {
                                            String    fpath= HTTPUtils.DownIMG(BuildConfig.Url_DOWN + img, imgname);
                                            if(handler!=null) {
                                                Message msg = new Message();
                                                msg.what = EcarSpreadManager.UPDATE_imageView;
                                                Bundle data2 = new Bundle();
                                                data2.putString("fname", fpath);
                                                data2.putInt("index",i);
                                                msg.setData(data2);
                                                handler.sendMessage(msg);
                                            }
                                        }
                                    }
                                    editor.putString(Constant.banner_img+i, imgname);
                                    editor.commit();
                                }
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
