package com.ecaray.ecaradsdk.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.ecaray.ecaradsdk.BuildConfig;
import com.ecaray.ecaradsdk.InitSDKManager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HTTPUtils {

    public  static   String   clickAd(String  id) throws Exception{
        Map<String,String> map = new HashMap<>();
        map.put(Constant.appId_key, InitSDKManager.appId);
        map.put(Constant.module_key,Constant.module_value);
        map.put(Constant.service_key,Constant.service_value);
        map.put(Constant.method_key,Constant.method_value);
        map.put(Constant.timestamp_key,System.currentTimeMillis()+"");
        map.put(Constant.action_key,"api.Ad.clickAd");
        map.put("id",id);
        StringBuilder  stringBuilderURL=new StringBuilder();
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            String val = (String)entry.getValue();
            stringBuilderURL.append(key+"="+val+"&");
        }
        String  url_next = stringBuilderURL.toString();
        URL url = new URL(BuildConfig.Url_Common+url_next.substring(0,url_next.length()-1));
        //得到connection对象。
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //设置请求方式
        connection.setRequestMethod("GET");
        //连接
        connection.connect();
        //得到响应码
        int responseCode = connection.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_OK) {
            //得到响应流
            InputStream inputStream = connection.getInputStream();
            //将响应流转换成字符串
            //连接后，创建一个输入流来读取response
            BufferedReader bufferedReader = new BufferedReader(new
                    InputStreamReader(inputStream, "utf-8"));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            String response = "";
            //每次读取一行，若非空则添加至 stringBuilder
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            //读取所有的数据后，赋值给 response
            response = stringBuilder.toString().trim();
            return response;
        }
        return  null;
    }


    public  static   String   getJSONFROMInt(String  adId) throws Exception{
        Map<String,String> map = new HashMap<>();
        map.put(Constant.appId_key,InitSDKManager.appId);
        map.put(Constant.module_key,Constant.module_value);
        map.put(Constant.service_key,Constant.service_value);
        map.put(Constant.method_key,Constant.method_value);
        map.put(Constant.timestamp_key,System.currentTimeMillis()+"");
        map.put(Constant.action_key,Constant.action_value);
        map.put(Constant.visitor_key,InitSDKManager.userid);
        map.put(Constant.position_code_key,adId);
        StringBuilder  stringBuilderURL=new StringBuilder();
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            String val = (String)entry.getValue();
            stringBuilderURL.append(key+"="+val+"&");
        }
        String  url_next = stringBuilderURL.toString();
        URL url = new URL(BuildConfig.Url_Common+url_next.substring(0,url_next.length()-1));
        //得到connection对象。
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //设置请求方式
        connection.setRequestMethod("GET");
        //连接
        connection.connect();
        //得到响应码
        int responseCode = connection.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_OK) {
            //得到响应流
            InputStream inputStream = connection.getInputStream();
            //将响应流转换成字符串
            //连接后，创建一个输入流来读取response
            BufferedReader bufferedReader = new BufferedReader(new
                    InputStreamReader(inputStream, "utf-8"));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            String response = "";
            //每次读取一行，若非空则添加至 stringBuilder
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            //读取所有的数据后，赋值给 response
            response = stringBuilder.toString().trim();
            return response;
        }
        return  null;
    }

    public static String DownIMG(String  path,String  Fname)throws  Exception{
        long  fileSize = 0;
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn .setRequestProperty("Accept-Encoding", "identity");
        Log.i("tagtag", conn.getResponseCode()+""+path);
        conn.setConnectTimeout(5000);
        fileSize = conn.getContentLength();
        InputStream is = conn.getInputStream();
        File file = new File(Environment.getExternalStorageDirectory().toString()+File.separator+Constant.adEarFile+File.separator+Fname);
        FileOutputStream fos = new FileOutputStream(file);
        BufferedInputStream bis = new BufferedInputStream(is);
        byte[] buffer = new byte[1024];
        int len;
        int total = 0;
        int temp = conn.getContentLength() / 30;
        int tempstep = 0;
        while ((len = bis.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
            total += len;
            // 获取当前下载量
            tempstep += len;
            if (tempstep >= temp) {
                tempstep = 0;
            }
        }
        fos.close();
        bis.close();
        is.close();
        long fileleng = file.length();
        if(fileSize > fileleng){
            return DownIMG(path,Fname);
        }
        return   file.getAbsolutePath();
    }

    public static void TheadDownAPK(final  String  path,final Context context,final  String  DownLoad_Apk_Name) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn .setRequestProperty("Accept-Encoding", "identity");
                    Log.i("tagtag", conn.getResponseCode()+"");
                    conn.setConnectTimeout(5000);
                    InputStream is = conn.getInputStream();
                    File file = new File(Environment.getExternalStorageDirectory().toString()+File.separator+Constant.adEarFile+File.separator+
                            DownLoad_Apk_Name);
                    FileOutputStream fos = new FileOutputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    byte[] buffer = new byte[1024];
                    int len;
                    int total = 0;
                    int temp = conn.getContentLength() / 30;
                    int tempstep = 0;
                    while ((len = bis.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                        total += len;
                        // 获取当前下载量
                        tempstep += len;
                        if (tempstep >= temp) {
                            tempstep = 0;
                        }
                    }
                    fos.close();
                    bis.close();
                    is.close();


                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);
                    String type = "application/vnd.android.package-archive";
                    intent .setDataAndType(Uri.fromFile(file),type);
                    context.startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
