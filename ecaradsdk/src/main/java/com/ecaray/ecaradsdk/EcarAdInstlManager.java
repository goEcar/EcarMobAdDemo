package com.ecaray.ecaradsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.ecaray.ecaradsdk.Interface.EcarAdViewListener;
import com.ecaray.ecaradsdk.bean.BaseData;
import com.ecaray.ecaradsdk.bean.ResponseData;
import com.ecaray.ecaradsdk.utils.Constant;
import com.ecaray.ecaradsdk.utils.CustomDialog;
import com.ecaray.ecaradsdk.utils.HTTPUtils;
import com.ecaray.ecaradsdk.view.WebActivity;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;

public class EcarAdInstlManager implements   View.OnClickListener{
    CustomDialog mCustomDialog;
    EcarAdViewListener monEcarListener;
    Context mcontext;
    String  imgname;
    private    final String DownLoad_Apk_Name = "AdInstl.apk";
    BaseData baseData;
    public static final  int  UPDATE_imageView = 0xffff003;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

                case UPDATE_imageView:
                    if (mCustomDialog != null) {
                        if(imgname!=null){
                            mCustomDialog.setImageViewBackground(Drawable.createFromPath(Environment.getExternalStorageDirectory().toString()+File.separator+Constant.adEarFile+File.separator+imgname));
                        }
                    }
                    break;
            }
        }
    };


    public EcarAdInstlManager(Context context, String adid) {
        mcontext = context;
        File file = new File(Environment.getExternalStorageDirectory(),Constant.adEarFile);
        if(!file.exists())
            file.mkdirs();
        SharedPreferences preferences = context.getSharedPreferences(Constant.adEarInfo,context.MODE_PRIVATE);
        if (mCustomDialog == null) {
                mCustomDialog = new CustomDialog(context);
        }
        parseJSON(preferences,adid);

    }

    public void showInstl() {
        SharedPreferences preferences = mcontext.getSharedPreferences(Constant.adEarInfo,mcontext.MODE_PRIVATE);
        if (!mCustomDialog.isShowing()) {
                 mCustomDialog.show();
           }
        mCustomDialog.setImageViewClick(this);
        mCustomDialog.setimageViewSize(preferences.getString(Constant.table_screen_image_size,"624*744"));
        mCustomDialog.setCanceledOnTouchOutside(false);
        mCustomDialog.setCancelable(false);
        //判断广告是否存在   不存在就跳过开屏
        File adfile = new File(Environment.getExternalStorageDirectory().toString()+File.separator+Constant.adEarFile+File.separator+imgname);
        if(adfile.exists()) {
            mCustomDialog.setImageViewBackground(Drawable.createFromPath(adfile.toString()));
        }
    }

    public void setOnEcarListener(EcarAdViewListener onEcarListener) {
        this.monEcarListener = onEcarListener;
    }


    private void parseJSON(final SharedPreferences preferences, final String adId) {
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
                            JSONObject adIdobj =  (JSONObject) data.get(adId);
                            baseData.setData(new Gson().fromJson(adIdobj.toString(),ResponseData.class)) ;
                            String  img = (String) adIdobj.get("img");
                            String  size = (String) adIdobj.get("size");
                            imgname = img.replace("/","").replace("\\","") ;
                            //去掉jpeg  png
                            imgname = imgname.substring(0,imgname.length()-5);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(Constant.table_screen_image_size, size);

                            File adfile = new File(Environment.getExternalStorageDirectory().toString()+File.separator+Constant.adEarFile+File.separator+imgname);
                            //图片不存在就去下载。
                            if(!adfile.exists()) {
                                if (img != null && !img.equals("")) {
                                      HTTPUtils.DownIMG(BuildConfig.Url_DOWN + img, imgname);
                                }
                            }
                            editor.commit();
                        }

                        if(handler!=null) {
                            handler.sendEmptyMessage(UPDATE_imageView);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
            if(v.getId() == R.id.closead){
                if (mCustomDialog != null ) {
                    mCustomDialog.dismiss();
                }
                monEcarListener.onAdClosedByUser();
            }else    if(v.getId() == R.id.custom_layout){
                  ClickAD();
           }else if(v.getId()==R.id.imageview){
                  ClickAD();
            }
    }

    private void ClickAD() {
        final  SharedPreferences preferences = mcontext.getSharedPreferences(Constant.adEarInfo,mcontext.MODE_PRIVATE);
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
        if (mCustomDialog != null ) {
            mCustomDialog.dismiss();
        }
        if(preferences.getString(Constant.open_screen_click_action,"").equals(Constant.click_action_1)){
            Intent intent = new Intent(mcontext, WebActivity.class);
            Activity activity=(Activity) mcontext;
            intent.putExtra("url",preferences.getString(Constant.open_screen_URL,""));
            activity.startActivityForResult(intent, EcarAdSplManager.requestCode);
        }
        if(preferences.getString(Constant.open_screen_click_action,"").equals(Constant.click_action_5)){
            HTTPUtils.TheadDownAPK(preferences.getString(Constant.open_screen_URL,""),mcontext,DownLoad_Apk_Name);
        }
        monEcarListener.onAdClicked();
    }
}
