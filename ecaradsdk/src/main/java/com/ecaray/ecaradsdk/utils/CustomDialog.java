package com.ecaray.ecaradsdk.utils;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ecaray.ecaradsdk.R;


public class CustomDialog extends Dialog {

    private Context mContext;
    ImageButton imageButton ;
    RelativeLayout lView;
    ImageView  imageView;

    public CustomDialog(Context context) {
        this(context, R.style.CustomProgressDialog);
    }

    public CustomDialog(Context context, int theme) {
        super(context, R.style.CustomProgressDialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pub_dialog_common);
        initView();
    }

    private void initView() {
        lView =(RelativeLayout) findViewById(R.id.custom_layout);
//        setViewsWith(mContext,0.78,lView);
        imageButton =(ImageButton) findViewById(R.id.closead);

        imageView = (ImageView)findViewById(R.id.imageview);
    }
    public  void  setImageViewBackground(Drawable background){
        if(imageView!=null) {
            imageView.setBackground(background);
        }
    }

    public  void  setImageViewClick(View.OnClickListener  clickListener){
        if(lView!=null)
            lView.setOnClickListener(clickListener);
        if(imageButton!=null)
            imageButton.setOnClickListener(clickListener);
        if(imageView!=null)
            imageView.setOnClickListener(clickListener);
    }
    // 设置控件的宽 参数:context 上下文 size;屏幕的倍数 view:需要调整的控件
    public static void setViewsWith(Context context, Double size, View view) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context
                .getSystemService(Service.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        int sizeX = dm.widthPixels;
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = (int) (size * sizeX);
    }

    public void setimageViewSize(String imageViewSize) {
        if(!imageViewSize.contains("*")){
            return;
        }
        String[]  arr=imageViewSize.split("\\*");
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
        imageView.setLayoutParams(lp1);
        FrameLayout.LayoutParams framelayout = new FrameLayout.LayoutParams(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
        lView.setLayoutParams(framelayout);
    }
}
