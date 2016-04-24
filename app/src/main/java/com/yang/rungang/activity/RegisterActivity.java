package com.yang.rungang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.rungang.R;
import com.yang.rungang.utils.ConfigUtil;
import com.yang.rungang.utils.GeneralUtil;


import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;


/**
 *注册Activity
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private ImageView backImg;
    private RelativeLayout registerByEmailRelative,registerByMobileRelative;
    private EditText registerEmailEdt;
    private EditText registerMobileEdt;
    private EditText verifyCodeEdt;
    private EditText setPwdEdt;
    private Button getVerifyCodeBtn;
    private Button nextStepBtn;
    private TextView switchText;

    private boolean switchSign=true;//切换标示，true表示用手机注册，false表示用邮箱注册

    private String mobileNumber;
    private String email;
    private String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        initComponent();
        Bmob.initialize(context, ConfigUtil.BMOB_APP_ID);
        setListener();
    }

    /**
     * 初始化组件
     */
    private void initComponent() {

        backImg = (ImageView) findViewById(R.id.img_register_back);
        registerByEmailRelative = (RelativeLayout) findViewById(R.id.relative_register_email);
        registerByMobileRelative = (RelativeLayout) findViewById(R.id.relative_register_mobile);
        registerEmailEdt = (EditText) findViewById(R.id.edt_register_email);
        registerMobileEdt = (EditText) findViewById(R.id.edt_register_mobile);
        verifyCodeEdt = (EditText) findViewById(R.id.edt_register_verify_code);
        setPwdEdt = (EditText) findViewById(R.id.edt_register_set_pwd);
        getVerifyCodeBtn = (Button) findViewById(R.id.btn_get_rerify_code);
        nextStepBtn = (Button) findViewById(R.id.btn_register_next_step);
        switchText = (TextView) findViewById(R.id.text_switch_register);

        backImg.setOnClickListener(this);
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
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_register_back: // 返回

                RegisterActivity.this.finish();

                break;
            case R.id.btn_register_next_step: //下一步

                if(!switchSign && checkInput()){// 邮箱注册，输入检查正确,网络连接

                    BmobQuery<BmobUser> query=new BmobQuery<>();
                    query.findObjects(context, new FindListener<BmobUser>() {
                        @Override
                        public void onSuccess(List<BmobUser> list) {
                            if (list.size() <= 0) { //不存在
                                Intent intent = new Intent(RegisterActivity.this, RegisterUserActivity.class);
                                intent.putExtra("email", email);
                                intent.putExtra("password", password);
                                startActivity(intent);
                            } else { //已存在
                                Toast.makeText(context, "邮箱已存在", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onError(int i, String s) {
                            Toast.makeText(context, "服务器错误，请稍后尝试！！", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else if(switchSign && checkInput()){ //手机号注册，输入检查正确

                }
                break;

            case R.id.btn_get_rerify_code: //获取验证码

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

}
