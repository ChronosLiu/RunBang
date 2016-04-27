package com.yang.rungang.activity;

import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.yang.rungang.R;
import com.yang.rungang.model.bean.User;
import com.yang.rungang.model.biz.ActivityManager;

import cn.bmob.v3.BmobUser;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ActivityManager.getInstance().pushOneActivity(this);

        User user= BmobUser.getCurrentUser(context,User.class);

        Toast.makeText(context,user.getUsername(),Toast.LENGTH_SHORT).show();

    }
}
