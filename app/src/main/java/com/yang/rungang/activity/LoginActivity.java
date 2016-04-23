package com.yang.rungang.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yang.rungang.R;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText usernameEdt;
    private EditText passwordEdt;
    private Button signInBtn;
    private TextView registerText;
    private TextView forgetPwdText;
    private ImageView wechatImg;
    private ImageView weiboImg;
    private ImageView qqImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        initComponent();
    }

    /**
     * 初始化组件
     */
    private void initComponent() {
        usernameEdt = (EditText) findViewById(R.id.edt_login_username);
        passwordEdt = (EditText) findViewById(R.id.edt_login_password);
        signInBtn = (Button) findViewById(R.id.btn_login);
        registerText = (TextView) findViewById(R.id.text_login_register);
        forgetPwdText = (TextView) findViewById(R.id.text_forget_pwd);
        wechatImg = (ImageView) findViewById(R.id.img_wechat_login);
        weiboImg = (ImageView) findViewById(R.id.img_weibo_login);
        qqImg = (ImageView) findViewById(R.id.img_qq_login);

        signInBtn.setOnClickListener(this);
        registerText.setOnClickListener(this);
        forgetPwdText.setOnClickListener(this);
        wechatImg.setOnClickListener(this);
        weiboImg.setOnClickListener(this);
        qqImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_login: // 登录

                break;
            case R.id.text_login_register: // 注册

                break;
            case R.id.text_forget_pwd: // 忘记密码

                break;
            case R.id.img_wechat_login: // 微信登录

                break;
            case R.id.img_weibo_login: // 微博登录

                break;
            case R.id.img_qq_login: // qq登录

                break;
        }
    }
}
