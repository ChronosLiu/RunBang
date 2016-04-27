package com.yang.rungang.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yang.rungang.R;
import com.yang.rungang.model.biz.ActivityManager;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {


    private ImageView backImg;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reset_password);
        ActivityManager.getInstance().pushOneActivity(this);

        initComponent();
    }

    private void initComponent() {
        backImg= (ImageView) findViewById(R.id.resetPwd_back_img);
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

        backImg.setOnClickListener(this);
        sureBtn.setOnClickListener(this);
        switchText.setOnClickListener(this);
        getCodeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.resetPwd_back_img:
                this.finish();
                break;
            case R.id.resetPwd_get_code_btn :

                break;
            case R.id.resetPws_sure :

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

}
