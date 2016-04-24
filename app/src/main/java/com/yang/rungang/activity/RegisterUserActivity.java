package com.yang.rungang.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.rungang.R;
import com.yang.rungang.model.bean.User;
import com.yang.rungang.view.RoundImageView;

import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class RegisterUserActivity extends BaseActivity implements View.OnClickListener {

    private ImageView backImg;
    private RoundImageView headImg;
    private TextView uploadText;
    private EditText nickEdt;
    private EditText birthdayEdt;
    private RadioGroup sexRGroup;
    private RadioButton maleRadioBtn,femaleRadioBtn;
    private Button registerBtn;

    private String nickName=null;
    private Date birthday=null;
    private boolean sex=true;

    private String mobileNumber;
    private String email;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register_user);

        mobileNumber = getIntent().getStringExtra("mobileNumber");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        initComponent();
        setListener();
    }

    /**
     * 初始化化组件
     */
    private void initComponent() {
        backImg = (ImageView) findViewById(R.id.img_register_user_back);
        headImg = (RoundImageView) findViewById(R.id.register_user_headImg);
        uploadText = (TextView) findViewById(R.id.text_register_upload);
        nickEdt = (EditText) findViewById(R.id.edt_register_nick);
        birthdayEdt = (EditText) findViewById(R.id.edt_register_birthday);
        sexRGroup = (RadioGroup) findViewById(R.id.radiogroup_sex);
        maleRadioBtn = (RadioButton) findViewById(R.id.radio_sex_male);
        femaleRadioBtn = (RadioButton) findViewById(R.id.radio_sex_female);
        registerBtn = (Button) findViewById(R.id.btn_register);

        maleRadioBtn.setChecked(true);

        backImg.setOnClickListener(this);
        headImg.setOnClickListener(this);
        birthdayEdt.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

    }

    /**
     * 设置监听器
     */
    private void setListener(){

        sexRGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_sex_male:
                        sex=true;
                        break;
                    case R.id.radio_sex_female:
                        sex=false;
                        break;
                    default:
                        sex=true;
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.img_register_user_back:
                RegisterUserActivity.this.finish();
                break;
            case R.id.register_user_headImg:

                break;
            case R.id.edt_register_birthday:

                break;
            case R.id.btn_register:

                register();

                break;
        }
    }

    /**
     * 注册用户
     */
    private void register(){
        nickName=nickEdt.getText().toString();

        User user=new User();

        user.setNickName(nickName);
        user.setSex(sex);
        user.setEmail(email);
        user.setHeadImg(null);
        user.setPassword(password);
        user.setMobilePhoneNumber(mobileNumber);
        user.setAge(null);
        user.setBirthday(null);
        user.signUp(context, new SaveListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(context,"注册成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(context,"注册失败",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
