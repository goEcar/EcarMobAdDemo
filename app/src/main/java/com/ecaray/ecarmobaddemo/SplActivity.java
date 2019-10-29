package com.ecaray.ecarmobaddemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ecaray.ecaradsdk.EcarAdSplManager;
import com.ecaray.ecaradsdk.InitSDKManager;
import com.ecaray.ecaradsdk.Interface.EcarAdViewListener;

import java.util.ArrayList;
import java.util.List;

public class SplActivity extends Activity  implements EcarAdViewListener {
    private String appId="20190917162648";
    private String appSecret="pogxGlWpuOQuyrky";
    public String[] permissions = null;
    EcarAdSplManager ecarSpreadManager;
    private PermissionListener mlistener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spl);

        InitSDKManager.getInstance().init(this,appId, appSecret,"1");
        permissions=new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= 23) {
            mlistener = new PermissionListener() {

                @Override
                public void onGranted(List<String> grantedPermission) {
                    this.onGranted();
                }

                @Override
                public void onGranted() {
                    requestSpreadAd();
                }

                @Override
                public void onDenied(List<String> deniedPermission) {
                    Toast.makeText(SplActivity.this, deniedPermission.get(0) + "权限被拒绝了", Toast.LENGTH_SHORT).show();
                }
            };
            requestRunTimePermission(permissions, mlistener);
        }else{
            requestSpreadAd();
        }

    }
    private void requestSpreadAd(){
        ecarSpreadManager = new EcarAdSplManager(this, (RelativeLayout) findViewById(R.id.spreadlayout),"1000210001");
        ecarSpreadManager.setBackgroundColor(Color.WHITE);
        ecarSpreadManager.setOnEcarListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == EcarAdSplManager.requestCode){
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return false;
        return super.onKeyDown(keyCode, event);
    }
    /**
     * 权限申请结果
     * @param requestCode
     *            请求码
     * @param permissions
     *            所有的权限集合
     * @param grantResults
     *            授权结果集合
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    // 被用户拒绝的权限集合
                    List<String> deniedPermissions = new ArrayList<>();
                    // 用户通过的权限集合
                    List<String> grantedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        // 获取授权结果，这是一个int类型的值
                        int grantResult = grantResults[i];

                        if (grantResult != PackageManager.PERMISSION_GRANTED) { // 用户拒绝授权的权限
                            String permission = permissions[i];
                            deniedPermissions.add(permission);
                        } else { // 用户同意的权限
                            String permission = permissions[i];
                            grantedPermissions.add(permission);
                        }
                    }

                    if (deniedPermissions.isEmpty()) { // 用户拒绝权限为空
                        mlistener.onGranted();
                    } else { // 不为空
                        Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
                        // 回调授权成功的接口
                        mlistener.onDenied(deniedPermissions);
                        // 回调授权失败的接口
                        mlistener.onGranted(grantedPermissions);
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                }
                break;
            default:
                break;
        }
    }
    /**
     * 权限申请
     * @param permissions
     *            待申请的权限集合
     * @param listener
     *            申请结果监听事件
     */
    protected void requestRunTimePermission(String[] permissions,
                                            PermissionListener listener) {
        // 用于存放为授权的权限
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            // 判断是否已经授权，未授权，则加入待授权的权限集合中
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        // 判断集合
        if (!permissionList.isEmpty()) { // 如果集合不为空，则需要去授权
            ActivityCompat.requestPermissions(this,
                    permissionList.toArray(new String[permissionList.size()]),
                    1);
        } else { // 为空，则已经全部授权
            listener.onGranted();
        }
    }

    @Override
    public void onAdClicked() {

    }

    @Override
    public void onAdClosed() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onAdClosedByUser() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void noAd() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }


}
