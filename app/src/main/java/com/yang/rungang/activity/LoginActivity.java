package com.yang.rungang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.rungang.R;
import com.yang.rungang.model.bean.User;
import com.yang.rungang.model.biz.ActivityManager;
import com.yang.rungang.utils.GeneralUtil;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText usernameEdt;
    private EditText passwordEdt;
    private Button signInBtn;
    private TextView registerText;
    private TextView forgetPwdText;
    private ImageView wechatImg;
    private ImageView weiboImg;
    private ImageView qqImg;

    private String userName;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        ActivityManager.getInstance().pushOneActivity(this);
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

                if ( GeneralUtil.isNetworkAvailable(context) && checkInput()) {

                        BmobUser.loginByAccount(context, userName, password, new LogInListener<User>() {
                            @Override
                            public void done(User user, BmobException e) {
                                if (user!=null) {
                                    Toast.makeText(context,"登录成功",Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    LoginActivity.this.finish();
                                } else {
                                    Toast.makeText(context,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }else if ( !GeneralUtil.isNetworkAvailable(context)) {
                    Toast.makeText(context,"未连接网络！！",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.text_login_register: // 注册
                Intent registerIntent = new Intent( LoginActivity.this, RegisterActivity.class);
                startActivity( registerIntent );
                break;
            case R.id.text_forget_pwd: // 忘记密码

                Intent resetPwdIntent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(resetPwdIntent);

                break;
            case R.id.img_wechat_login: // 微信登录

                break;
            case R.id.img_weibo_login: // 微博登录

                break;
            case R.id.img_qq_login: // qq登录

                break;
        }

    }

    /**
     * 检查输入
     * @return
     */
    private boolean checkInput() {
        userName = usernameEdt.getText().toString();
        password = passwordEdt.getText().toString();

        if (userName.length()>0 && password.length()>0 ) {
            return true;
        }else if ( userName.length()<=0) {
            Toast.makeText(context,"用户名不能为空",Toast.LENGTH_SHORT).show();

        } else if ( password.length()<=0){
            Toast.makeText(context,"密码不能为空",Toast.LENGTH_SHORT).show();
        }
        return  false;
    }
}
