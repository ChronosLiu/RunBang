package com.yang.rungang.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yang.rungang.R;
import com.yang.rungang.model.bean.User;
import com.yang.rungang.model.biz.ActivityManager;

import cn.bmob.v3.BmobUser;

public class BindAccountActivity extends BaseActivity implements View.OnClickListener {


    private final static int Reqeust_Code_Bind = 0x11;

    private ImageView backImg;
    private RelativeLayout bindPhone;
    private RelativeLayout bindEmail;
    private TextView bindPhoneState;
    private TextView bindEmailState;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bind_account);
        ActivityManager.getInstance().pushOneActivity(this);
        user = BmobUser.getCurrentUser(context,User.class);
        initComponent();
        setDataToView();
    }

    private void initComponent() {
        backImg = (ImageView) findViewById(R.id.bind_back_img);
        bindPhone = (RelativeLayout) findViewById(R.id.bind_phone_layout);
        bindPhoneState = (TextView) findViewById(R.id.bind_phone_state);
        bindEmail = (RelativeLayout) findViewById(R.id.bind_email_layout);
        bindEmailState = (TextView) findViewById(R.id.bind_email_state);

        backImg.setOnClickListener(this);
        bindPhone.setOnClickListener(this);
        bindEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bind_back_img:
                this.finish();
                break;
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
