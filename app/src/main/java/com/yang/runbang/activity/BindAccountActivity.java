package com.yang.runbang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yang.runbang.R;
import com.yang.runbang.model.bean.User;
import com.yang.runbang.model.biz.ActivityManager;

import cn.bmob.v3.BmobUser;

public class BindAccountActivity extends BaseActivity implements View.OnClickListener {


    private final static int Reqeust_Code_Bind = 0x11;

    private RelativeLayout bindPhone;
    private RelativeLayout bindEmail;
    private TextView bindPhoneState;
    private TextView bindEmailState;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_account);
        ActivityManager.getInstance().pushOneActivity(this);
        initToolBar();
        user = BmobUser.getCurrentUser(context,User.class);
        initComponent();
        setDataToView();
    }
    /**
     * 初始化toolbar
     */
    private void initToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_bind_account);
        toolbar.setTitle("账号绑定");
        toolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initComponent() {
        bindPhone = (RelativeLayout) findViewById(R.id.bind_phone_layout);
        bindPhoneState = (TextView) findViewById(R.id.bind_phone_state);
        bindEmail = (RelativeLayout) findViewById(R.id.bind_email_layout);
        bindEmailState = (TextView) findViewById(R.id.bind_email_state);

        bindPhone.setOnClickListener(this);
        bindEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bind_phone_layout:
                if(TextUtils.isEmpty(user.getMobilePhoneNumber())) {
                    Intent phoneIntent = new Intent(this, BindActivity.class);
                    phoneIntent.putExtra("isPhone", true);
                    startActivityForResult(phoneIntent, Reqeust_Code_Bind);
                }

                break;
            case R.id.bind_email_layout:
                if (TextUtils.isEmpty(user.getEmail())) {
                    Intent emailIntent = new Intent(this, BindActivity.class);
                    emailIntent.putExtra("isPhone", false);
                    startActivityForResult(emailIntent, Reqeust_Code_Bind);
                }
                break;
        }
    }

    private void setDataToView(){
        if (user!= null) {
            if(user.getMobilePhoneNumber()!=null) {
                bindPhoneState.setText("已绑定");
            }else {
                bindPhoneState.setText("未绑定");
            }

            if (user.getEmail()!=null) {
                bindEmailState.setText("已绑定");
            } else {
                bindEmailState.setText("未绑定");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setDataToView();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
