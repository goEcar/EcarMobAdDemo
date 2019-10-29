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
import android.view.View;
import android.view.ViewGroup;

import com.ecaray.ecaradsdk.Interface.EcarSpreadListener;
import com.ecaray.ecaradsdk.utils.Constant;
import com.ecaray.ecaradsdk.utils.CustomDialog;
import com.ecaray.ecaradsdk.utils.HTTPUtils;
import com.ecaray.ecaradsdk.view.WebActivity;

import org.json.JSONObject;

import java.io.File;

public class AdViewInstlManager  implements   View.OnClickListener{
    CustomDialog mCustomDialog;
    EcarSpreadListener monEcarListener;
    Context mcontext;
    private    final String DownLoad_Apk_Name = "tableAPP.apk";

    public static final  int  UPDATE_imageView = 0xffff003;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

                case UPDATE_imageView:
                    if (mCustomDialog == null) {
                        mCustomDialog.setImageViewBackground(Drawable.createFromPath(msg.getData().getString("fname")));
                    }
                    break;
            }
        }
    };


    public AdViewInstlManager(Context context,   String adid) {
        mcontext = context;
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
        File adfile = new File(Environment.getExternalStorageDirectory().toString()+File.separator+Constant.adEarFile+File.separator+preferences.getString(Constant.table_screen_img,"ecar"));
        if(adfile.exists()) {
            mCustomDialog.setImageViewBackground(Drawable.createFromPath(adfile.toString()));
        }
    }

    public void setOnEcarListener(EcarSpreadListener onEcarListener) {
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
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.get("state").toString().equals("1")) {
                            JSONObject data =  (JSONObject) jsonObject.get("data");
                            JSONObject adIdobj =  (JSONObject) data.get(adId);
                            String  click_action = (String) adIdobj.get("click_action");
                            String  id = (String) adIdobj.get("id");
                            String  img = (String) adIdobj.get("img");
                            String  size = (String) adIdobj.get("size");
                            String  click_url = (String) adIdobj.get("url");
                            String  imgname = img.replace("/","").replace("\\","") ;
                            //去掉jpeg  png
                            imgname = imgname.substring(0,imgname.length()-5);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(Constant.table_screen_click_action, click_action);
                            editor.putString(Constant.table_screen_id, id);
                            editor.putString(Constant.table_screen_URL, click_url);
                            editor.putString(Constant.table_screen_image_size, size);

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
                                        msg.setData(data2);
                                        handler.sendMessage(msg);
                                    }
                                }
                            }
                            editor.putString(Constant.table_screen_img, imgname);
                            editor.commit();
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
                    String  json =HTTPUtils.clickAd(preferences.getString(Constant.table_screen_id,""));
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
            activity.startActivityForResult(intent,EcarSpreadManager.requestCode);
        }
        if(preferences.getString(Constant.open_screen_click_action,"").equals(Constant.click_action_5)){
            HTTPUtils.TheadDownAPK(preferences.getString(Constant.open_screen_URL,""),mcontext,DownLoad_Apk_Name);
        }
        monEcarListener.onAdClicked();
    }
}
