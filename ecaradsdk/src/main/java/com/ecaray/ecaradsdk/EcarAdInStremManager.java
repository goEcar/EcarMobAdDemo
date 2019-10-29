package com.ecaray.ecaradsdk;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
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

import java.io.File;

import static android.widget.LinearLayout.VERTICAL;

public class EcarAdInStremManager {
    private    final String DownLoad_Apk_Name = "AdInStrem.apk";
    Context  mcontext;
    TextView  textView;
    ImageView  imageView;
    BaseData baseData;
    double widthsizeX;
    String    imgname;

    double hightsizeX;
    LinearLayout linearLayout;
    LinearLayout.LayoutParams lp1;
    LinearLayout.LayoutParams lp2;
    EcarAdViewListener mecarAdViewListener;
    public static final  int  UPDATE_imageView = 0xffff003;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

                case UPDATE_imageView:
                    //data2.putString("fname", fpath);
                    textView.setText(baseData.getData().getTitle());
                    File adfile = new File(Environment.getExternalStorageDirectory().toString()+File.separator+Constant.adEarFile+File.separator+imgname);
                    int  textViewMaxWidth = (int)widthsizeX/3;
                    if(adfile.exists()){
                       Bitmap bitmap= BitmapFactory.decodeFile(adfile.toString());
                        textViewMaxWidth = (int)widthsizeX - bitmap.getWidth();
                        if(textViewMaxWidth <= 0 ){
                            textViewMaxWidth = (int)widthsizeX/5;
                        }
                       imageView.setImageBitmap(bitmap);
                    }
                    linearLayout.removeAllViews();
                    String layout =baseData.getData().getLayout();
                    if(layout.contains("左图")){
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        linearLayout.addView(imageView, lp2);
                        linearLayout.addView(textView, lp1);

                    }else if(layout.contains("左文")){
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        LinearLayout.LayoutParams   lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        textView.setMaxWidth(textViewMaxWidth);
                        LinearLayout.LayoutParams  lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        linearLayout.addView(textView, lp1);
                        linearLayout.addView(imageView, lp2);
                    }else if(layout.contains("上图")){
                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                        linearLayout.addView(imageView, lp2);
                        linearLayout.addView(textView, lp1);
                    }else {
                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                        linearLayout.addView(textView, lp1);
                        linearLayout.addView(imageView, lp2);
                    }
                    break;
            }
        }
    };



    public void   setTextSize(float textSize){
        if(textView!=null)
            textView.setTextSize(textSize);
    }
    public EcarAdInStremManager(Context context, ViewGroup view, String adId) {
        mcontext = context;
        File file = new File(Environment.getExternalStorageDirectory(),Constant.adEarFile);
        if(!file.exists())
            file.mkdirs();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) mcontext.getSystemService(Service.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        widthsizeX = dm.widthPixels;
        hightsizeX = dm.heightPixels;
        SharedPreferences preferences = mcontext.getSharedPreferences(Constant.adEarInfo,mcontext.MODE_PRIVATE);
        parseJSON(preferences,adId);

        linearLayout = new LinearLayout(context);
        linearLayout.setPadding(20,20,20,20);
        linearLayout.setBackgroundColor(Color.WHITE);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickAD();
            }
        });

        textView = new TextView(mcontext);
        lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setTextColor(Color.BLACK);
        textView.setMarqueeRepeatLimit(Integer.MAX_VALUE);
        textView.setFocusable(true);
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setSingleLine();
        textView.setFocusableInTouchMode(true);
        textView.setHorizontallyScrolling(true);


        imageView = new ImageView(mcontext);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        String viewSize = preferences.getString(Constant.instrem_image_size,"686*200");
        if(!viewSize.contains("*")){
            return;
        }
        String[]  arr=viewSize.split("\\*");
        double  sp=Integer.parseInt(arr[1].trim())*widthsizeX/Double.parseDouble(arr[0].trim());
        lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)sp);
        String  imgname= preferences.getString(Constant.instrem_img, "ecar");
        int textViewMaxWidth = (int)widthsizeX/3;
        File adfile = new File(Environment.getExternalStorageDirectory().toString()+File.separator+Constant.adEarFile+File.separator+imgname);
        if(adfile.exists()){
                Bitmap bitmap= BitmapFactory.decodeFile(adfile.toString());
                 textViewMaxWidth = (int)widthsizeX - bitmap.getWidth();
                 if(textViewMaxWidth <= 0 ){
                     textViewMaxWidth = (int)widthsizeX/5;
                 }
                imageView.setImageBitmap(bitmap);
        }

        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        view.addView(linearLayout,lp3);

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

    private void parseJSON(final SharedPreferences preferences, final  String adId ) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    String   response = HTTPUtils.getJSONFROMInt(adId);
                    baseData =new Gson().fromJson(response,BaseData.class);
                    if(response!=null && !response.equals("")) {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.get("state").toString().equals("1")) {
                            JSONObject data =  (JSONObject) jsonObject.get("data");
                            SharedPreferences.Editor editor = preferences.edit();
                            JSONObject adIdobj =  (JSONObject) data.get(adId);
                            if(adIdobj!=null){
                                baseData.setData(new Gson().fromJson(adIdobj.toString(),ResponseData.class)) ;
                                imgname = baseData.getData().getImg().replace("/","").replace("\\","") ;
                                //去掉jpeg  png
                                imgname = imgname.substring(0,imgname.length()-5);
                                editor.putString(Constant.instrem_image_size,baseData.getData().getSize());
                                File adfile = new File(Environment.getExternalStorageDirectory().toString()+File.separator+Constant.adEarFile+File.separator+imgname);
                                //图片不存在就去下载。
                                if(!adfile.exists()) {
                                    if (baseData.getData().getImg() != null && !baseData.getData().getImg().equals("")) {
                                        HTTPUtils.DownIMG(BuildConfig.Url_DOWN + baseData.getData().getImg(), imgname);
                                    }
                                }
                                editor.putString(Constant.instrem_img, imgname);
                                editor.commit();
                                if(handler!=null) {
                                    Message msg = new Message();
                                    msg.what = UPDATE_imageView;
                                    handler.sendMessage(msg);
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
