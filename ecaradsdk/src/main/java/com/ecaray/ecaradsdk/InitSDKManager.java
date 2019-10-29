package com.ecaray.ecaradsdk;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.ecaray.ecaradsdk.utils.Constant;

import java.io.File;

public class InitSDKManager {
    public static String userid;
    public static String appId;


    private static InitSDKManager initSDKManager;


    public InitSDKManager() {
    }
    public static  InitSDKManager getInstance() {
        if (initSDKManager == null) {
            initSDKManager = new InitSDKManager();
        }
        return initSDKManager;
    }


    /**
     *
     * @param context
     * @param appId
     * @param appSecret
     * @param userid  用户标识 ，比如 蜜蜂APP 用户id
     */
    public void init(Context context, String appId, String appSecret,String userid) {
        InitSDKManager.userid = userid;
        InitSDKManager.appId = appId;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED)){
            Toast.makeText(context, "没有sdCard",Toast.LENGTH_LONG).show();
        }
        File file = new File(Environment.getExternalStorageDirectory(),Constant.adEarFile);
        if(!file.exists())
            file.mkdirs();
    }
}
