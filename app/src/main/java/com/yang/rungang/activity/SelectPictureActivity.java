package com.yang.rungang.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.yang.rungang.R;
import com.yang.rungang.model.biz.ActivityManager;
import com.yang.rungang.utils.GeneralUtil;

public class SelectPictureActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);
        ActivityManager.getInstance().pushOneActivity(this);
        getImage();
    }

    private void getImage() {
        if(GeneralUtil.isSDCard()){
            Toast.makeText(context,"无外部存储",Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }
}
