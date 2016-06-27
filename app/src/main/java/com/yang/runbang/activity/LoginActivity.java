package com.yang.runbang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.runbang.R;
import com.yang.runbang.listener.OnLoginListener;
import com.yang.runbang.model.bean.User;
import com.yang.runbang.model.biz.ActivityManager;
import com.yang.runbang.utils.GeneralUtil;

import java.util.HashMap;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

public class LoginActivity extends BaseActivity implements View.OnClickListener, PlatformActionListener {


    private static final int MSG_SMSSDK_CALLBACK = 1;
    private static final int MSG_AUTH_CANCEL = 2;
    private static final int MSG_AUTH_ERROR= 3;
    private static final int MSG_AUTH_COMPLETE = 4;


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
                //微信登录
                //测试时，需要打包签名；sample测试时，用项目里面的demokey.keystore
                //打包签名apk,然后才能产生微信的登录
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                authorize(wechat);

                break;
            case R.id.img_weibo_login: // 微博登录
                //新浪微博
                Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
                authorize(sina);

                break;
            case R.id.img_qq_login: // qq登录
                //新浪微博
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                authorize(qq);
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


    //执行授权，获取用户信息
    private void authorize(Platform plat) {

        if(plat==null) {
            return;
        }

        if (plat.isAuthValid()) {//已授权

            return;
        }

        plat.setPlatformActionListener(this);
        plat.SSOSetting(false);//优先使用客户端，然后使用网页
        plat.showUser(null);//获取用户资料

    }

    //授权完成
    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

        if (i == Platform.ACTION_USER_INFOR) {
            Log.i("TAG",hashMap.toString());
            Message msg = new Message();
            msg.what = MSG_AUTH_COMPLETE;
            msg.obj = new Object[] {platform.getName(), hashMap};
            handler.sendMessage(msg);
        }
    }

    //授权失败
    @Override
    public void onError(Platform platform, int i, Throwable throwable) {

        if (i == Platform.ACTION_USER_INFOR) {
            handler.sendEmptyMessage(MSG_AUTH_ERROR);
        }
        throwable.printStackTrace();
    }

    //授权取消
    @Override
    public void onCancel(Platform platform, int i) {

        if (i == Platform.ACTION_USER_INFOR) {
            handler.sendEmptyMessage(MSG_AUTH_CANCEL);
        }

    }

    private Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUTH_CANCEL:
                    //取消授权
                    Toast.makeText(context,"取消授权", Toast.LENGTH_SHORT).show();
                 break;

                case MSG_AUTH_ERROR:
                    //授权失败
                    Toast.makeText(context,"授权失败",Toast.LENGTH_SHORT).show();
                 break;

                case MSG_AUTH_COMPLETE:
                    //授权成功
                    Toast.makeText(context, "授权成功", Toast.LENGTH_SHORT).show();
                    Object[] objs = (Object[]) msg.obj;
                    String platform = (String) objs[0];

                    HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
                    if (signupListener != null && signupListener.onSignin(platform, res)) {

                        Log.i("TAG",res.toString());
                    }


                 break;
            }
            super.handleMessage(msg);
        }
    };

    private OnLoginListener signupListener = new OnLoginListener() {
        @Override
        public boolean onSignin(String platform, HashMap<String, Object> res) {
            // 在这个方法填写尝试的代码，返回true表示还不能登录，需要注册
            // 此处全部给回需要注册
            return true;
        }

        @Override
        public boolean onSignUp(User user) {
            // 填写处理注册信息的代码，返回true表示数据合法，注册页面可以关闭



            return true;
        }
    };


    private void registerUser() {

    }




}
