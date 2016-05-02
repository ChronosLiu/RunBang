package com.yang.rungang.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yang.rungang.R;
import com.yang.rungang.model.biz.ActivityManager;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

public class SetActivity extends BaseActivity implements View.OnClickListener {

    private ImageView backImg;
    private RelativeLayout modifyInfo;
    private RelativeLayout collection;
    private RelativeLayout bindAccount;
    private RelativeLayout notification;
    private RelativeLayout updateVersion;
    private RelativeLayout feedback;
    private RelativeLayout clearCache;
    private RelativeLayout about;
    private RelativeLayout signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set);
        ActivityManager.getInstance().pushOneActivity(this);

        initComponent();

    }

    /**
     * 初始化组件
     */
    private void initComponent() {
        backImg = (ImageView) findViewById(R.id.set_back_img);
        modifyInfo = (RelativeLayout) findViewById(R.id.set_modify_info_relative);
        collection = (RelativeLayout) findViewById(R.id.set_collection_relative);
        bindAccount = (RelativeLayout) findViewById(R.id.set_bind_account_relative);
        notification = (RelativeLayout) findViewById(R.id.set_notification_relative);
        updateVersion = (RelativeLayout) findViewById(R.id.set_update_version_relative);
        feedback = (RelativeLayout) findViewById(R.id.set_feedback_relative);
        clearCache = (RelativeLayout) findViewById(R.id.set_clear_cache_relative);
        about = (RelativeLayout) findViewById(R.id.set_about_relative);
        signOut = (RelativeLayout) findViewById(R.id.set_sign_out_relative);

        backImg.setOnClickListener(this);
        modifyInfo.setOnClickListener(this);
        collection.setOnClickListener(this);
        bindAccount.setOnClickListener(this);
        notification.setOnClickListener(this);
        updateVersion.setOnClickListener(this);
        feedback.setOnClickListener(this);
        clearCache.setOnClickListener(this);
        about.setOnClickListener(this);
        signOut.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_img:
                this.finish();
                break;
            case R.id.set_modify_info_relative:
                break;
            case R.id.set_collection_relative:
                break;
            case R.id.set_bind_account_relative:
                break;
            case R.id.set_notification_relative:
                break;
            case R.id.set_update_version_relative:
                break;
            case R.id.set_feedback_relative:
                break;
            case R.id.set_clear_cache_relative:
                break;
            case R.id.set_about_relative:
                break;
            case R.id.set_sign_out_relative:
                BmobUser.logOut(context);//清除缓存用户对象
                ActivityManager.getInstance().popAllActivity();//退出应用
                break;
        }
    }
}
