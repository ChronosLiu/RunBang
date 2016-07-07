package com.yang.runbang.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.yang.runbang.R;
import com.yang.runbang.model.bean.User;
import com.yang.runbang.model.biz.ActivityManager;
import com.yang.runbang.utils.FileUtil;
import com.yang.runbang.utils.GeneralUtil;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class ModifyInformatonActivity extends BaseActivity implements View.OnClickListener {


    private static final int Request_Code_Camera = 0x11;
    private static final int Request_Code_Album = 0x12;
    private static final int Upload_Avatar_Success = 0x13;
    private static final int Upload_Acatar_Failure = 0x14;
    private static final int Upload_user_Success = 0x15;
    private static final int Upload_user_Failure = 0x16;



    private LinearLayout modifyLayout;
    private ImageView avatarImg;
    private EditText nickNameEdt;
    private TextView sexText;
    private TextView birthdayText;
    private EditText signatureEdt;

    private ProgressDialog progressDialog;
    private User user;
    private DisplayImageOptions circleOptions;

    private String avatarPath = null; //头像路径
    private String avatarUrl = null; //头像Url
    private String oldAvatarUrl = null; //原先的头像url
    private PopupWindow avatarPopup;
    private PopupWindow sexPopup;
    private DatePickerDialog datePickerDialog;
    private String birthdayStr = null; //选择的生日拼接字符串
    private Date birthdayDate = null; //生日
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_informaton);
        ActivityManager.getInstance().pushOneActivity(this);
        user = BmobUser.getCurrentUser(context,User.class);
        initComponent();
        initToolbar();
        setDataToView();

        initAvatarPopupWindow();
        initSexPopupWindow();
        initBirthdayDataPicker();

         circleOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar_blue)
                .showImageOnFail(R.drawable.default_avatar_blue)
                .showImageForEmptyUri(R.drawable.upload_head_pic)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new CircleBitmapDisplayer())
                .build();
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_modify_information);
        toolbar.setTitle("修改个人资料");
        toolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_finish:
                        showProgressDialog(ModifyInformatonActivity.this,"正在修改...");
                        if (avatarPath!=null ){
                            uploadImage(); //上传头像
                        } else {
                            updateUserInfo(); //更新用户信息
                        }
                        break;
                }
                return true;
            }
        });

    }
    /**
     * 初始化组件
     */
    private void initComponent() {
        modifyLayout = (LinearLayout) findViewById(R.id.modify_info_layout);
        avatarImg = (ImageView) findViewById(R.id.modify_info_avatar_img);
        nickNameEdt = (EditText) findViewById(R.id.modify_info_nickname_edt);
        sexText = (TextView) findViewById(R.id.modify_info_sex_text);
        birthdayText = (TextView) findViewById(R.id.modify_info_birthday_text);
        signatureEdt = (EditText) findViewById(R.id.modify_info_signature_edt);

        avatarImg.setOnClickListener(this);
        sexText.setOnClickListener(this);
        birthdayText.setOnClickListener(this);
    }

    /**
     * 初始化填充数据
     */
    private void setDataToView(){
        if (user!= null) {
            nickNameEdt.setText(user.getNickName());
            if (user.getSex()!=null && user.getSex()) {
                sexText.setText("男");
            } else if (user.getSex()!=null){
                sexText.setText("女");
            } else {
                sexText.setText("未知");
            }
            ImageLoader.getInstance().displayImage(user.getHeadImgUrl(), avatarImg, circleOptions);

            if (user.getBirthday()!=null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date date = null;
                try {
                    date = sdf.parse(user.getBirthday().getDate().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                birthdayText.setText(new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
            if (user.getSignature()!= null) {
                signatureEdt.setText(user.getSignature());
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_modify_information, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId()) {
            case R.id.modify_info_avatar_img://头像
                avatarPopup.showAtLocation(modifyLayout, Gravity.BOTTOM,0,0);
                break;
            case R.id.modify_info_sex_text://性别
                sexPopup.showAtLocation(modifyLayout, Gravity.BOTTOM,0,0);
                break;
            case R.id.modify_info_birthday_text: //生日
                datePickerDialog.show();
                break;
            case R.id.popup_camera: //拍照
                if(GeneralUtil.isSDCard()){
                    Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(cameraIntent.resolveActivity(getPackageManager())!=null){
                        //判断系统是否有能处理cameraIntent的activity
                        startActivityForResult(cameraIntent, Request_Code_Camera);
                        avatarPopup.dismiss();
                    }
                }else{
                    Toast.makeText(context, "没有检测到SD卡", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.popup_photo: //相册
                Intent albumIntent = new Intent(this,AlbumActivity.class);
                startActivityForResult(albumIntent,Request_Code_Album);
                avatarPopup.dismiss();
                break;
            case R.id.popup_sex_man:
                sexText.setText("男");
                sexPopup.dismiss();
                break;
            case R.id.popup_sex_woman:
                sexText.setText("女");
                sexPopup.dismiss();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case Request_Code_Camera: //拍照返回

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
                    //获取拍照图片路径
                    this.avatarPath= FileUtil.saveBitmapToFile(bitmap, "avatar");
                    String url = ImageDownloader.Scheme.FILE.wrap(avatarPath);
                    ImageLoader.getInstance().displayImage(url,avatarImg,circleOptions);

                }
                break;
            case Request_Code_Album: //从相册返回

                if (resultCode == AlbumActivity.RESULT_CODE) {
                    ArrayList<String> selectData = data.getStringArrayListExtra("selectData");
                    this.avatarPath = selectData.get(0);
                    String url = ImageDownloader.Scheme.FILE.wrap(avatarPath);
                    ImageLoader.getInstance().displayImage(url,avatarImg,circleOptions);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 初始化头像弹窗
     */
    private void initAvatarPopupWindow(){
        View view = getLayoutInflater().inflate(R.layout.popup_get_headimg_layout,null);
        avatarPopup = new PopupWindow();
        avatarPopup.setContentView(view);
        avatarPopup.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        avatarPopup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        avatarPopup.setFocusable(true);
        avatarPopup.setOutsideTouchable(true);
        avatarPopup.setTouchable(true);

        RelativeLayout avatarlayout = (RelativeLayout) view.findViewById(R.id.popup_relative);
        TextView photoText = (TextView) view.findViewById(R.id.popup_photo);
        TextView cameraText = (TextView) view.findViewById(R.id.popup_camera);
        TextView cancelText= (TextView) view.findViewById(R.id.popup_cancel);
        avatarlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatarPopup.dismiss();
            }
        });
        photoText.setOnClickListener(this);
        cameraText.setOnClickListener(this);
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatarPopup.dismiss();
            }
        });
    }

    /**
     * 初始化性别弹窗
     */
    private void initSexPopupWindow(){
        View view = getLayoutInflater().inflate(R.layout.popup_sex_layout,null);
        sexPopup = new PopupWindow();
        sexPopup.setContentView(view);
        sexPopup.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        sexPopup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        sexPopup.setFocusable(true);
        sexPopup.setOutsideTouchable(true);
        sexPopup.setTouchable(true);

        final RelativeLayout sexlayout = (RelativeLayout) view.findViewById(R.id.popup_sex_layout);
        TextView manText = (TextView) view.findViewById(R.id.popup_sex_man);
        TextView womanText = (TextView) view.findViewById(R.id.popup_sex_woman);
        TextView cancleText = (TextView) view.findViewById(R.id.popup_sex_cancel);

        sexlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sexPopup.dismiss();
            }
        });
        manText.setOnClickListener(this);
        womanText.setOnClickListener(this);
        cancleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sexPopup.dismiss();
            }
        });
    }

    /**
     * 初始化日期popup
     */
    private void initBirthdayDataPicker(){

        Calendar c=Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                birthdayStr=year+"-"+monthOfYear+"-"+dayOfMonth;
                birthdayText.setText(birthdayStr);
                //字符串转化为日期Date
                birthdayDate=GeneralUtil.stringToDate(birthdayStr);
            }
        },c.get(Calendar.YEAR),c.get(Calendar.MONTH),Calendar.DAY_OF_MONTH);

    }


    /**
     * 更新用户信息
     */
    private void updateUserInfo(){
        User newUser = new User();
        if(avatarUrl!=null) {
            newUser.setHeadImgUrl(avatarUrl);
        }
        if (!nickNameEdt.getText().toString().equals(user.getNickName())) {
            newUser.setNickName(nickNameEdt.getText().toString());
        }

        if (sexText.getText().toString().equals("男")) {
            newUser.setSex(true);
        } else {
            newUser.setSex(false);
        }

        if(birthdayDate!= null) {
            newUser.setBirthday(new BmobDate(birthdayDate));
        }
        if (!TextUtils.isEmpty(signatureEdt.getText().toString())) {
            newUser.setSignature(signatureEdt.getText().toString());
        }

        newUser.update(context, user.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                Message msg = new Message();
                msg.what= Upload_user_Success;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(int i, String s) {
                Message msg = new Message();
                msg.what= Upload_user_Failure;
                handler.sendMessage(msg);
            }
        });




    }
    /**
     * 上传图片
     */
    private void uploadImage(){

        final BmobFile bmobFile=new BmobFile(new File(avatarPath));
        bmobFile.uploadblock(context, new UploadFileListener() {
            @Override
            public void onSuccess() {
                //获取图片URL
                avatarUrl = bmobFile.getFileUrl(context);
                oldAvatarUrl = user.getHeadImgUrl();
                Message message = new Message();
                message.what = Upload_Avatar_Success;
                handler.handleMessage(message);

            }

            @Override
            public void onFailure(int i, String s) {

                Log.i("TAG", s + i);
                Message message = new Message();
                message.what = Upload_Acatar_Failure;
                handler.handleMessage(message);
            }
        });
    }

    /**
     * 删除旧头像文件
     */
    private void deleteOldFile(){
        if (!TextUtils.isEmpty(oldAvatarUrl)) {
            BmobFile file = new BmobFile();
            file.setUrl(oldAvatarUrl);
            file.delete(context, new DeleteListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(int i, String s) {

                    Log.i("TAG", s + i);
                }
            });
        }
    }

    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Upload_Avatar_Success:
                    updateUserInfo();
                    break;
                case Upload_Acatar_Failure:
                    closeProgressDialog();
                    Toast.makeText(ModifyInformatonActivity.this,"上传头像失败",Toast.LENGTH_SHORT).show();
                    break;

                case Upload_user_Success:
                    deleteOldFile();
                    closeProgressDialog();
                    Toast.makeText(ModifyInformatonActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                    ModifyInformatonActivity.this.finish();
                    break;
                case Upload_user_Failure:
                    closeProgressDialog();
                    Toast.makeText(ModifyInformatonActivity.this,"修改个人资料失败",Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };


}
