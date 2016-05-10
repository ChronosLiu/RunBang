package com.yang.rungang.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.DialogPreference;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.rungang.R;
import com.yang.rungang.model.bean.User;
import com.yang.rungang.model.biz.ActivityManager;
import com.yang.rungang.utils.FileUtil;
import com.yang.rungang.utils.GeneralUtil;
import com.yang.rungang.view.RoundImageView;


import java.io.File;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class RegisterUserActivity extends BaseActivity implements View.OnClickListener {



    private final static int REQUEST_CODE_CAMERA=0X001; //拍照的requestcode

    private final static int REQUEST_CODE_ALBUM = 0x002; // 从相册中选择图片的requestCode

    private final static int UPLOAD_SUCCESS = 0x11; //上传图片成功

    private final static int UPLOAD_FAILURE = 0x12; //上传图片失败

    private final static int REGISTER_SUCCESS = 0x21; // 注册成功

    private final static int REGISTER_FAILURE = 0X22; // 注册失败

    private ImageView backImg;
    private RoundImageView headImg;
    private TextView uploadText;
    private EditText usernameEdt;
    private EditText nicknameEdt;
    private TextView birthdayText;
    private RadioGroup sexRGroup;
    private RadioButton maleRadioBtn,femaleRadioBtn;
    private Button registerBtn;
    private RelativeLayout popupRelative;
    private TextView photoText,cameraText,cancelText;
    private PopupWindow popupWindow;
    private DatePickerDialog birthdayDialog;

    private String birthdayStr = null; //选择的生日拼接字符串

    private String userName=null; //用户名
    private String nickName=null; //昵称
    private Date birthday=null; //生日
    private Integer age=null; //年龄
    private boolean sex=true; //性别

    private String mobileNumber=null;
    private String email=null;
    private String password=null;

    private Bitmap headBitmap = null; // 头像
    private String picPath = null; //头像路径
    private String headImgUrl = null; //头像存储在Bmob上的url




    Handler handler=new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){

                case UPLOAD_SUCCESS:
                    //注册
                    registerUser();
                    break;
                case UPLOAD_FAILURE:
                    Toast.makeText(context,"上传头像失败，注册失败",Toast.LENGTH_SHORT).show();
                    break;
                case REGISTER_SUCCESS:
                    resultSuccessDialog();
                    break;
                case REGISTER_FAILURE:
                    Toast.makeText(context,"注册失败",Toast.LENGTH_SHORT).show();
                    break;

            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register_user);
        ActivityManager.getInstance().pushOneActivity(this);

        mobileNumber = getIntent().getStringExtra("mobileNumber");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        initComponent();
        initPopupWindow();
        initBirthdayDataPicker();
        setListener();
    }

    /**
     * 初始化化组件
     */
    private void initComponent() {

        backImg = (ImageView) findViewById(R.id.img_register_user_back);
        headImg = (RoundImageView) findViewById(R.id.register_user_headImg);
        uploadText = (TextView) findViewById(R.id.text_register_upload);
        usernameEdt = (EditText) findViewById(R.id.edt_register_name);
        nicknameEdt = (EditText) findViewById(R.id.edt_register_nickname);
        birthdayText = (TextView) findViewById(R.id.text_register_birthday);
        sexRGroup = (RadioGroup) findViewById(R.id.radiogroup_sex);
        maleRadioBtn = (RadioButton) findViewById(R.id.radio_sex_male);
        femaleRadioBtn = (RadioButton) findViewById(R.id.radio_sex_female);
        registerBtn = (Button) findViewById(R.id.btn_register);

        maleRadioBtn.setChecked(true);

        backImg.setOnClickListener(this);
        headImg.setOnClickListener(this);
        birthdayText.setOnClickListener(this);
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

        usernameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                userName = usernameEdt.getText().toString();
            }
        });

        nicknameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                nickName = nicknameEdt.getText().toString();
            }
        });

    }

    /**
     * 初始化日期popup
     */
    private void initBirthdayDataPicker(){

        Calendar c=Calendar.getInstance();

        birthdayDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                birthdayStr=year+"-"+monthOfYear+"-"+dayOfMonth;
                birthdayText.setText(birthdayStr);
                //字符串转化为日期Date
                birthday=GeneralUtil.stringToDate(birthdayStr);

        }
        },c.get(Calendar.YEAR),c.get(Calendar.MONTH),Calendar.DAY_OF_MONTH);

    }

    /**
     * 初始化popup
     */
    private void initPopupWindow() {
        View view=getLayoutInflater().inflate(R.layout.popup_get_headimg_layout,null);
        popupWindow=new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        popupRelative = (RelativeLayout) view.findViewById(R.id.popup_relative);
        photoText = (TextView) view.findViewById(R.id.popup_photo);
        cameraText = (TextView) view.findViewById(R.id.popup_camera);
        cancelText = (TextView) view.findViewById(R.id.popup_cancel);

        popupRelative.setOnClickListener(this);
        photoText.setOnClickListener(this);
        cameraText.setOnClickListener(this);
        cancelText.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.img_register_user_back:
                RegisterUserActivity.this.finish();
                break;
            case R.id.register_user_headImg:
                popupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.activity_register_user,null), Gravity.BOTTOM, 0, 0);
                break;
            case R.id.text_register_birthday:
                birthdayDialog.show();
                break;
            case R.id.btn_register:
                if(TextUtils.isEmpty(userName)){
                    Toast.makeText(context,"用户名不能为空！",Toast.LENGTH_SHORT).show();
                }else if(picPath!=null){
                    //上传头像，注册用户
                    uploadImage();
                }else{
                    //无头像，注册用户
                    registerUser();
                }
            case R.id.popup_camera:
                if(GeneralUtil.isSDCard()){
                    Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(cameraIntent.resolveActivity(getPackageManager())!=null){
                        //判断系统是否有能处理cameraIntent的activity
                        startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
                        popupWindow.dismiss();
                    }
                }else{
                    Toast.makeText(context,"没有检测到SD卡",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.popup_photo:

                if(GeneralUtil.isSDCard()){
                    Intent intent=new Intent(this,SelectPictureActivity.class);
                    startActivityForResult(intent,REQUEST_CODE_ALBUM);

                }else{
                    Toast.makeText(context,"没有检测到SD卡",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.popup_cancel:
                popupWindow.dismiss();
                break;
            case R.id.popup_relative:
                popupWindow.dismiss();
                break;

        }
    }

    /**
     * 注册用户
     */
    private void registerUser(){

        User user=new User();
        user.setUsername(userName);
        user.setNickName(nickName);
        user.setSex(sex);
        user.setEmail(email);
        user.setHeadImgPath(picPath);
        user.setHeadImgUrl(headImgUrl);
        user.setPassword(password);
        user.setMobilePhoneNumber(mobileNumber);
        user.setAge(age);
        user.setBirthday(new BmobDate(birthday));
        user.signUp(context, new SaveListener() {
            @Override
            public void onSuccess() {

                Message msg=new Message();
                msg.what=REGISTER_SUCCESS;
                handler.handleMessage(msg);
            }

            @Override
            public void onFailure(int i, String s) {
                Log.i("TAG",s+i);
                Message msg=new Message();
                msg.what=REGISTER_FAILURE;
                handler.handleMessage(msg);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA://拍照返回
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap=null;
                    Uri uri=data.getData();
                    if (uri!=null) {
                        bitmap= BitmapFactory.decodeFile(uri.getPath());
                    }
                    if (bitmap==null){
                        Bundle bundle=data.getExtras();
                        if (bundle!=null) {
                            bitmap = (Bitmap) bundle.get("data");//缩略图
                        } else {
                            Toast.makeText(context,"拍照失败！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    destroyBitmap();
                    this.headBitmap=bitmap;
                    headImg.setImageBitmap(headBitmap);
                    //获取拍照图片路径
                    this.picPath= FileUtil.saveBitmapToFile(headBitmap,"headImg");
                }
                break;
            case REQUEST_CODE_ALBUM://从相册选取图片返回

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 销毁图片headBitmap
     */
    private void destroyBitmap(){
        if(this.headBitmap!=null && !this.headBitmap.isRecycled()){
            headBitmap.recycle();
            headBitmap=null;
        }
    }

    /**
     * 上传图片
     */
    private void uploadImage(){
        final BmobFile bmobFile=new BmobFile(new File(picPath));
        bmobFile.uploadblock(context, new UploadFileListener() {
            @Override
            public void onSuccess() {
                //获取图片URL
                headImgUrl=bmobFile.getFileUrl(context);

                Message message=new Message();
                message.what=UPLOAD_SUCCESS;
                handler.handleMessage(message);

            }
            @Override
            public void onFailure(int i, String s) {

                Log.i("TAG",s+i);
                Message message=new Message();
                message.what=UPLOAD_FAILURE;
                handler.handleMessage(message);
            }
        });
    }

    /**
     * 注册成功弹窗提示
     */
    private void resultSuccessDialog(){
        new AlertDialog.Builder(RegisterUserActivity.this)
                .setMessage("注册成功！请登录..")
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(RegisterUserActivity.this,LoginActivity.class);
                        startActivity(intent);
                        RegisterUserActivity.this.finish();

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }


}
