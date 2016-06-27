package com.yang.runbang.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.runbang.R;
import com.yang.runbang.model.biz.ActivityManager;
import com.yang.runbang.utils.GeneralUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;
import cn.bmob.v3.listener.ResetPasswordByEmailListener;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {


    private static final int CHECK_MOBILE_EXIST = 0X11; // 检测手机号码是否已注册

    private static final int VERIFY_CODE_SUCCESS = 0X12; // 验证码验证成功

    private static final int VERIFY_CODE_FAILURE = 0X13; //验证码验证失败

    private static final int RESET_PASSWORD_SUCCESS = 0x21; //修改密码成功

    private Button sureBtn;
    private EditText mobileEdt;
    private EditText codeEdt;
    private EditText setPwdEdt;
    private EditText againPwdEdt;
    private EditText emailEdt;
    private Button getCodeBtn;
    private TextView switchText;

    private RelativeLayout resetByMobileRelative;
    private RelativeLayout resetByEmailRelative;


    private boolean isSwitch=true; //标示，true为手机修改，false为邮箱修改


    private String mobile;
    private String code;
    private String setPwd;
    private String againPwd;
    private String email;

    private MyCountTimer timer;

    Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case CHECK_MOBILE_EXIST://手机号已注册
                    //获取验证码
                    timer=new MyCountTimer(60000,1000);
                    timer.start();
                    getVerifyCode();
                    break;

                case VERIFY_CODE_SUCCESS:

                    break;

                case RESET_PASSWORD_SUCCESS://重置密码成功

                    resultSuccessDialog();//弹窗提示

                    break;

            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reset_password);
        ActivityManager.getInstance().pushOneActivity(this);

        initComponent();
    }

    /**
     * 初始化化组件
     */
    private void initComponent() {
        Toolbar  toolbar = (Toolbar) findViewById(R.id.toolbar_reset_password);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle("找回密码");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sureBtn= (Button) findViewById(R.id.resetPws_sure);
        mobileEdt = (EditText) findViewById(R.id.resetPwd_mobile_edt);
        codeEdt = (EditText) findViewById(R.id.resetPwd_code_edt);
        setPwdEdt = (EditText) findViewById(R.id.resetPwd_set_password);
        againPwdEdt = (EditText) findViewById(R.id.resetPwd_again_password);
        emailEdt = (EditText) findViewById(R.id.resetPwd_email_edt);
        getCodeBtn = (Button) findViewById(R.id.resetPwd_get_code_btn);
        switchText = (TextView) findViewById(R.id.resetPwd_switch_reset_form);

        resetByEmailRelative = (RelativeLayout) findViewById(R.id.resetPwd_email_relative);

        resetByMobileRelative = (RelativeLayout) findViewById(R.id.resetPwd_mobile_relative);

        sureBtn.setOnClickListener(this);
        switchText.setOnClickListener(this);
        getCodeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.resetPwd_get_code_btn :
                //检验手机号是否已注册
                checkMobileExist();

                break;
            case R.id.resetPws_sure :
                if(GeneralUtil.isNetworkAvailable(context)){
                    if(isSwitch){//通过手机修改密码
                        resetPasswordByMobile();
                    }else{//通过邮箱修改密码
                        resetPasswordByEmail();
                    }
                } else {
                    Toast.makeText(context,"未检测到网络",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.resetPwd_switch_reset_form:
                switchView(!isSwitch);
                break;

        }
    }


    /**
     * 根据标示，切换注册方式
     * @param b
     */
    private void switchView( boolean b) {

        if(b == true){
            resetByMobileRelative.setVisibility(View.VISIBLE);
            resetByEmailRelative.setVisibility(View.GONE);
            switchText.setText(getResources().getString(R.string.text_switch_to_email_reset));
        }else{
            resetByMobileRelative.setVisibility(View.GONE);
            resetByEmailRelative.setVisibility(View.VISIBLE);
            switchText.setText(getResources().getString(R.string.text_switch_to_mobile_reset));
        }
        this.isSwitch=b;
    }

    /**
     * 通过邮箱修改密码
     */
    private void resetPasswordByEmail(){

        email=emailEdt.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(context,"邮箱不能为空，请输入邮箱",Toast.LENGTH_SHORT).show();
        } else if (GeneralUtil.isEmail(email)) {
            BmobUser.resetPasswordByEmail(context, email, new ResetPasswordByEmailListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(context,"发送邮件成功，请去邮箱修改密码",Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure(int i, String s) {
                    Log.i("TAG",s+i);
                    Toast.makeText(context,"发送邮件失败，请稍后重试",Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(context,"邮箱格式不正确，请重新输入",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 通过手机修改密码
     */
    private void resetPasswordByMobile(){

        code=codeEdt.getText().toString();
        setPwd=setPwdEdt.getText().toString();
        againPwd=againPwdEdt.getText().toString();

        if(TextUtils.isEmpty(setPwd)||TextUtils.isEmpty(againPwd)){
            Toast.makeText(context,"密码不能为空",Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(code)){
            Toast.makeText(context,"验证码不能为空",Toast.LENGTH_SHORT).show();
        } if(setPwd.equals(againPwd)) {
            BmobUser.resetPasswordBySMSCode(context, code, setPwd, new ResetPasswordByCodeListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Message msg = new Message();
                        msg.what = RESET_PASSWORD_SUCCESS;
                        handler.handleMessage(msg);
                    } else {
                        Log.i("TAG", "重置失败：code ="+e.getErrorCode()+",msg = "+e.getLocalizedMessage());
                        Toast.makeText(context, "密码重置失败，请稍后尝试！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(context,"两次输入的密码不一致",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 检测手机号码是否合法，已注册
     */
    private void checkMobileExist(){
        if(GeneralUtil.isNetworkAvailable(context)) {
            mobile = mobileEdt.getText().toString();
            if (TextUtils.isEmpty(mobile)) {
                Toast.makeText(context,"手机号不能为空，请输入",Toast.LENGTH_SHORT).show();
            } else if(!GeneralUtil.isNumber(mobile)){
                Toast.makeText(context,"手机号格式不正确",Toast.LENGTH_SHORT).show();
            } else {

                BmobQuery<BmobUser> query=new BmobQuery<>();
                query.addWhereEqualTo("mobilePhoneNumber",mobile);
                query.findObjects(context, new FindListener<BmobUser>() {
                    @Override
                    public void onSuccess(List<BmobUser> list) {

                        if (list.size()<=0) {//手机号未注册
                            Toast.makeText(context,"该手机号码未注册或绑定账户",Toast.LENGTH_SHORT).show();
                        } else {
                            Message msg=new Message();
                            msg.what=CHECK_MOBILE_EXIST;
                            handler.handleMessage(msg);
                        }
                    }
                    @Override
                    public void onError(int i, String s) {

                        Log.i("TAG",s+i);
                        Toast.makeText(context,"服务器错误，请稍后重试！！",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }else{
            Toast.makeText(context,"未检测到网络！！",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取验证码
     */
    private void getVerifyCode() {
        BmobSMS.requestSMSCode(context, mobile, "注册验证码", new RequestSMSCodeListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {//验证码发送成功

                } else {
                    timer.cancel();
                    Toast.makeText(context, "获取验证码失败，请稍后重试", Toast.LENGTH_SHORT).show();
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
            getCodeBtn.setText((millisUntilFinished / 1000) + "s后重发");
        }
        @Override
        public void onFinish() {

            getCodeBtn.setText("重新发送");
        }
    }

    /**
     * 修改密码成功弹窗提示
     */
    private void resultSuccessDialog(){
        new AlertDialog.Builder(ResetPasswordActivity.this)
                .setTitle("提示")
                .setMessage("修改密码成功！请重新登录")
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(ResetPasswordActivity.this,LoginActivity.class);
                        startActivity(intent);
                        ResetPasswordActivity.this.finish();
                    }
                }).create().show();
    }



}
