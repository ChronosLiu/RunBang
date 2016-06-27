package com.yang.runbang.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yang.runbang.R;
import com.yang.runbang.model.bean.User;
import com.yang.runbang.model.biz.ActivityManager;
import com.yang.runbang.utils.GeneralUtil;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.EmailVerifyListener;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

public class BindActivity extends BaseActivity implements View.OnClickListener {

    private static final int GET_CODE_SUCCESS = 0x100;//获取验证码成功
    private static final int GET_CODE_FAILURE = 0x101; // 获取验证码失败

    private static final int VERIFY_CODE_SUCCESS = 0x200; //验证验证码成功

    private static final int VERIFY_CODE_FAILURE = 0x201; //验证验证码失败

    private static final int Bind_Phone_Success = 0x11; //绑定手机成功
    private static final int Bind_Email_Success = 0x12; //绑定邮箱成功
    private static final int Phone_Single = 0x13; //手机号唯一
    private static final int Email_Not_Single = 0x14; //邮箱不唯一,已存在
    private static final int Phone_Not_Single= 0x15; //手机号不唯一，已存在

    private User user;

    private RelativeLayout phoneLayout;
    private RelativeLayout emailLayout;
    private EditText emailEdt;
    private EditText phoneEdt;
    private EditText codeEdt;
    private Button sendCodeBtn;
    private EditText passwordEdt;
    private Button finishBtn;

    private String phone = null;
    private String email = null;
    private String code = null;
    private String password = null;
    private String title = null;


    private boolean isPhone = true; //标识，true为绑定phone,false为绑定邮箱
    private MyCountTimer timer;//计时器

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_CODE_SUCCESS:

                    break;
                case VERIFY_CODE_SUCCESS:
                    bindPhone();
                    break;
                case VERIFY_CODE_FAILURE:
                    Toast.makeText(context,"验证码错误",Toast.LENGTH_SHORT).show();
                    timer.cancel();
                    sendCodeBtn.setText("重新发送");
                    break;

                case Bind_Email_Success:
                    Toast.makeText(context,"绑定邮箱成功,请去邮箱验证",Toast.LENGTH_SHORT).show();
                    break;
                case Bind_Phone_Success:
                    Toast.makeText(context,"绑定手机号成功",Toast.LENGTH_SHORT).show();
                    break;

                case Email_Not_Single:
                    Toast.makeText(context,"邮箱已存在，请重新输入",Toast.LENGTH_SHORT).show();
                    break;
                case Phone_Not_Single:
                    Toast.makeText(context,"手机号码已存在，请重新输入",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bind);
        ActivityManager.getInstance().pushOneActivity(this);
        user = BmobUser.getCurrentUser(context, User.class);
        isPhone = getIntent().getBooleanExtra("isPhone",true);
        initComponent();
        if (isPhone) {
            title="绑定手机号";
            emailLayout.setVisibility(View.GONE);
            phoneLayout.setVisibility(View.VISIBLE);
        } else {
            title="绑定邮箱";
            emailLayout.setVisibility(View.VISIBLE);
            phoneLayout.setVisibility(View.GONE);
        }
        initToolbar();

    }

    /**
     * 初始化Toolbar
     */
    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_bind);
        toolbar.setTitle(title);
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
        phoneLayout = (RelativeLayout) findViewById(R.id.bind_phone_relative);
        emailLayout = (RelativeLayout) findViewById(R.id.bind_email_relative);
        emailEdt = (EditText) findViewById(R.id.bind_email_account);
        phoneEdt = (EditText) findViewById(R.id.bind_phone_account);
        codeEdt = (EditText) findViewById(R.id.bind_phone_code_edt);
        sendCodeBtn = (Button) findViewById(R.id.bind_send_code_btn);
//        passwordEdt = (EditText) findViewById(R.id.bind_password_edt);
        finishBtn = (Button) findViewById(R.id.bind_finish_btn);

        sendCodeBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bind_send_code_btn: //发送验证码
                phone = phoneEdt.getText().toString();

                if (checkInput()) {
                    timer=new MyCountTimer(60000,1000);
                    timer.start();
                    //检查手机号是否唯一
                    checkPhone();
                }
                break;
            case R.id.bind_finish_btn://完成
//                password = passwordEdt.getText().toString();
                code = codeEdt.getText().toString();
                email = emailEdt.getText().toString();
                if (isPhone&& checkInput()) { //绑定手机号
                    if (TextUtils.isEmpty(code)) {
                        Toast.makeText(context,"验证码不能为空",Toast.LENGTH_SHORT).show();
                    } else {
                        checkCode(code);
                    }
                } else if (!isPhone && checkInput()){ //绑定邮箱

                    checkEmail();
                }
                break;
        }
    }


    /**
     * 检查输入
     * @return
     */
    private boolean checkInput(){

        if (GeneralUtil.isNetworkAvailable(context)) {
            if (isPhone) {//手机号注册
                if (!TextUtils.isEmpty(phone)&&GeneralUtil.isMobileNumber(phone)) {
                    return true;
                } else if(TextUtils.isEmpty(phone)){
                    Toast.makeText(context,"手机号不能为空",Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(context,"手机号格式不正确",Toast.LENGTH_SHORT).show();
                }
            } else {//邮箱注册
                if ( !TextUtils.isEmpty(email) && GeneralUtil.isEmail(email)) {
                    return true;
                } else if ( TextUtils.isEmpty(email)) {
                    Toast.makeText(context, "邮箱不能为空", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(context, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
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
    private void getCode(){
        BmobSMS.requestSMSCode(context,phone, "注册验证码", new RequestSMSCodeListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {//验证码发送成功
                    Message msg = new Message();
                    msg.what = GET_CODE_SUCCESS;
                    handler.handleMessage(msg);
                } else {
                    timer.cancel();
                    Toast.makeText(context, "获取验证码失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 验证验证码
     * @param code
     */
    private void checkCode(String code){
        BmobSMS.verifySmsCode(context,phone,code, new VerifySMSCodeListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {//验证成功

                    Message msg = new Message();
                    msg.what = VERIFY_CODE_SUCCESS;
                    handler.handleMessage(msg);
                } else {

                    Message msg = new Message();
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
            sendCodeBtn.setText((millisUntilFinished / 1000) + "s后重发");
        }
        @Override
        public void onFinish() {

            sendCodeBtn.setText("重新发送");
        }
    }


    /**
     * 绑定手机号
     */
    private void bindPhone(){
        User newUser = new User();
        newUser.setMobilePhoneNumber(phone);
        newUser.setMobilePhoneNumberVerified(true);

        newUser.update(context, user.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {

                Message msg = new Message();
                msg.what = Bind_Phone_Success;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

    }

    /**
     * 绑定邮箱
     */
    private void bindEmail(){
        sendVerifyEmail();

        User newUser = new User();
        newUser.setEmail(email);
        newUser.update(context, user.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                Message msg = new Message();
                msg.what = Bind_Email_Success;
                handler.sendMessage(msg);

            }
            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    /**
     * 检查手机号是否存在
     */
    private void checkPhone(){

        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("mobilePhoneNumber", phone);
        query.count(context, User.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                if (i <= 0) { //不存在
                    //获取验证码
                    getCode();
                } else { //存在
                    Message msg = new Message();
                    msg.what = Phone_Not_Single;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int i, String s) {

                Log.i("TAG", s + i);
            }
        });
    }

    /**
     * 检查邮箱是否已存在
     */
    private void checkEmail(){
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("email",email);
        query.count(context, User.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                if ( i <=0) { //不存在
                    bindEmail();
                } else { //存在
                    Message msg = new Message();
                    msg.what = Email_Not_Single;
                    handler.sendMessage(msg);
                }
            }
            @Override
            public void onFailure(int i, String s) {
                Log.i("TAG",s+i);
            }
        });
    }

    /**
     * 发送验证邮件
     */
    private void sendVerifyEmail(){
        BmobUser.requestEmailVerify(context, email, new EmailVerifyListener() {
            @Override
            public void onSuccess() {
            }
            @Override
            public void onFailure(int code, String e) {
            }
        });
    }
}
