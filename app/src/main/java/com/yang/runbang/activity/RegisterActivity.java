package com.yang.runbang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.runbang.R;
import com.yang.runbang.model.bean.User;
import com.yang.runbang.model.biz.ActivityManager;
import com.yang.runbang.utils.ConfigUtil;
import com.yang.runbang.utils.GeneralUtil;


import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;


/**
 *注册Activity
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {


    private static final int GET_CODE_SUCCESS = 0x100;//注册获取验证码成功
    private static final int GET_CODE_FAILURE = 0x101; // 注册获取验证码失败

    private static final int VERIFY_CODE_SUCCESS = 0x200; //验证验证码成功

    private static final int VERIFY_CODE_FAILURE = 0x201; //验证验证码失败

    private static final int Is_Mobile_Have = 0x31; //手机号存在
    private static final int Is_Mobile_unHave = 0x32;//手机号不存在

    private RelativeLayout registerByEmailRelative,registerByMobileRelative;
    private EditText registerEmailEdt;
    private EditText registerMobileEdt;
    private EditText verifyCodeEdt;
    private EditText setPwdEdt;
    private Button getVerifyCodeBtn;
    private Button nextStepBtn;
    private TextView switchText;

    private boolean switchSign=true;//切换标示，true表示用手机注册，false表示用邮箱注册

    private String mobileNumber=null;
    private String email=null;
    private String password=null;
    private String code=null; //验证码

    private MyCountTimer timer;//计时器


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){

                case GET_CODE_SUCCESS:


                    break;
                case VERIFY_CODE_SUCCESS: //手机验证成功

                    Intent intent=new Intent (RegisterActivity.this,RegisterUserActivity.class);
                    intent.putExtra("mobileNumber",mobileNumber);
                    intent.putExtra("password", password);
                    startActivity(intent);
                    break;
                
                case VERIFY_CODE_FAILURE: //手机验证失败

                    Toast.makeText(context,"验证码错误",Toast.LENGTH_SHORT).show();
                    break;

                case Is_Mobile_Have://手机号已注册存在
                    Toast.makeText(context,"手机号已存在,请重新输入",Toast.LENGTH_SHORT).show();
                    break;
                case Is_Mobile_unHave://手机号未注册，为新用户
                    timer=new MyCountTimer(60000,1000);
                    timer.start();
                    getVerifyCodeBtn.setEnabled(false);
                    //获取验证码
                    getVerifyCode();
                    break;

            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActivityManager.getInstance().pushOneActivity(this);
        initComponent();
        Bmob.initialize(context, ConfigUtil.BMOB_APP_ID);
        setListener();
    }

    /**
     * 初始化组件
     */
    private void initComponent() {

        registerByEmailRelative = (RelativeLayout) findViewById(R.id.relative_register_email);
        registerByMobileRelative = (RelativeLayout) findViewById(R.id.relative_register_mobile);
        registerEmailEdt = (EditText) findViewById(R.id.edt_register_email);
        registerMobileEdt = (EditText) findViewById(R.id.edt_register_mobile);
        verifyCodeEdt = (EditText) findViewById(R.id.edt_register_verify_code);
        setPwdEdt = (EditText) findViewById(R.id.edt_register_set_pwd);
        getVerifyCodeBtn = (Button) findViewById(R.id.btn_get_rerify_code);
        nextStepBtn = (Button) findViewById(R.id.btn_register_next_step);
        switchText = (TextView) findViewById(R.id.text_switch_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_register);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle("注册");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getVerifyCodeBtn.setOnClickListener(this);
        switchText.setOnClickListener(this);
        nextStepBtn.setOnClickListener(this);
    }

    /**
     * 设置监听器
     */
    private void setListener(){
        registerEmailEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                email=registerEmailEdt.getText().toString();

            }
        });
        registerMobileEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                mobileNumber=registerMobileEdt.getText().toString();
            }
        });

        setPwdEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                password=setPwdEdt.getText().toString();
            }
        });

        verifyCodeEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                code=verifyCodeEdt.getText().toString();
            }
        });

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_register_next_step: //下一步

                if(!switchSign && checkInput()){// 邮箱注册，输入检查正确,网络连接

                    BmobQuery<User> query=new BmobQuery<>();
                    query.addWhereEqualTo("email",email);
                    query.findObjects(context, new FindListener<User>() {
                        @Override
                        public void onSuccess(List<User> list) {
                            if (list.size() <= 0) { //不存在
                                Intent intent = new Intent(RegisterActivity.this, RegisterUserActivity.class);
                                intent.putExtra("email", email);
                                intent.putExtra("password", password);
                                startActivity(intent);
                            } else { //已存在
                                Toast.makeText(context, "邮箱已存在"+list.size(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onError(int i, String s) {
                            Toast.makeText(context, "服务器错误，请稍后尝试！！", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else if(switchSign && checkInput()){ //手机号注册，输入检查正确

                    if(TextUtils.isEmpty(code)){
                        Toast.makeText(context,"验证码不能为空",Toast.LENGTH_SHORT).show();
                    }else if(GeneralUtil.isPasswordNumber(password)){
                        //验证验证码
                        verifyCode(code);
                    }else{
                        Toast.makeText(context,"密码位数不正确，请重新输入",Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.btn_get_rerify_code: //获取验证码

                if (checkInput()) {
                    //判断手机号是否已注册
                    BmobQuery<User> query = new BmobQuery<>();
                    query.addWhereEqualTo("mobilePhoneNumber", mobileNumber);
                    query.count(context, User.class, new CountListener() {
                        @Override
                        public void onSuccess(int i) {
                            if (i <= 0) { //不存在
                                Message msg = new Message();
                                msg.what = Is_Mobile_unHave;
                                handler.sendMessage(msg);

                            } else { //已存在
                                Message msg = new Message();
                                msg.what = Is_Mobile_Have;
                                handler.sendMessage(msg);
                            }
                        }
                        @Override
                        public void onFailure(int i, String s) {

                            Log.i("TAG",s+i);
                        }
                    });

                }
                break;

            case R.id.text_switch_register: // 切换
                switchView(!switchSign);
                break;
        }
    }

    /**
     * 根据标示，切换注册方式
     * @param sign
     */
    private void switchView( boolean sign) {

        if(sign == true){
            registerByMobileRelative.setVisibility(View.VISIBLE);
            registerByEmailRelative.setVisibility(View.GONE);
            switchText.setText(getResources().getString(R.string.text_switch_to_email));
        }else{
            registerByMobileRelative.setVisibility(View.GONE);
            registerByEmailRelative.setVisibility(View.VISIBLE);
            switchText.setText(getResources().getString(R.string.text_switch_to_mobile));
        }
        this.switchSign=sign;
    }

    /**
     * 检查输入
     * @return
     */
    private boolean checkInput(){
        if (GeneralUtil.isNetworkAvailable(context)) {
            if (switchSign == true) {//手机号注册
                if (TextUtils.isEmpty(mobileNumber)) {
                    Toast.makeText(context,"手机号不能为空",Toast.LENGTH_SHORT).show();
                } else if(GeneralUtil.isMobileNumber(mobileNumber)) {
                    return true;
                } else{
                    Toast.makeText(context,"手机号格式不正确",Toast.LENGTH_SHORT).show();
                }

            } else {//邮箱注册
                if ( GeneralUtil.isEmail(email) && GeneralUtil.isPasswordNumber(password)) {
                    return true;
                } else if ( !GeneralUtil.isEmail(email)) {
                    Toast.makeText(context, "邮箱格式不正确，请重新输入", Toast.LENGTH_SHORT).show();
                } else if ( !GeneralUtil.isPasswordNumber(password)) {
                    Toast.makeText(context, "密码位数错误，请重新输入", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            Toast.makeText(context,"未连接网络！！",Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    /**
     * 请求验证码
     */
    private void getVerifyCode(){
        BmobSMS.requestSMSCode(context, mobileNumber, "注册验证码", new RequestSMSCodeListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e==null) {//验证码发送成功
                    Message msg=new Message();
                    msg.what = GET_CODE_SUCCESS;
                    handler.handleMessage(msg);
                } else {

                    timer.cancel();
                    Toast.makeText(context,"获取验证码失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 验证验证码
     * @param code
     */
    private void verifyCode(String code){
        BmobSMS.verifySmsCode(context, mobileNumber, code, new VerifySMSCodeListener() {
            @Override
            public void done(BmobException e) {
                if (e==null) {//验证成功

                    Message msg=new Message();
                    msg.what = VERIFY_CODE_SUCCESS;
                    handler.handleMessage(msg);
                } else {

                    Message msg=new Message();
                    msg.what = VERIFY_CODE_FAILURE;
                    handler.handleMessage(msg);
                }
            }
        });
    }



    /**
     *计时器
     */
    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            getVerifyCodeBtn.setText((millisUntilFinished / 1000) + "s后重发");
        }
        @Override
        public void onFinish() {
            getVerifyCodeBtn.setText("重新发送");
            getVerifyCodeBtn.setEnabled(true);
        }
    }

}
