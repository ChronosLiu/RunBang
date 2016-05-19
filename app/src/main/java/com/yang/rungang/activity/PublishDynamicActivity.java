package com.yang.rungang.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.rungang.R;
import com.yang.rungang.adapter.PublishGridAdapter;
import com.yang.rungang.https.HttpsUtil;
import com.yang.rungang.model.bean.Dynamic;
import com.yang.rungang.model.bean.Friend;
import com.yang.rungang.model.bean.IHttpCallback;
import com.yang.rungang.model.bean.Timeline;
import com.yang.rungang.model.bean.User;
import com.yang.rungang.model.biz.ActivityManager;
import com.yang.rungang.utils.ConfigUtil;
import com.yang.rungang.utils.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class PublishDynamicActivity extends BaseActivity implements View.OnClickListener {


    private static final int GET_PHOTO_REQUEST = 0X11;

    private static final int UPLOAD_PICTURE_SUCCESS = 0X12;


    private static final int Push_Dynamic_Success = 0x13;



    private ImageView backImg;

    private TextView publish;

    private EditText contentEdt;

    private GridView mGridView;

    private List<String> mSelectedPicture = new ArrayList<>();

    private PublishGridAdapter adapter;

    private User user; //当前用户

    private Dynamic dynamic = null; //动态

    private String content = null; //内容

    private String theme = null;//主题

    private List<String> pictureList = null; //图片url


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case UPLOAD_PICTURE_SUCCESS: //上传图片文件成功

                    Bundle bundle = msg.getData();
                    //获取图片url集合
                    pictureList = bundle.getStringArrayList("urls");
                    //发布动态
                    publishDynamic();
                    break;
                case Push_Dynamic_Success: //推送动态成功

                    //展示dialog
                    showDialog();

                    break;

            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_publish_dynamic);
        ActivityManager.getInstance().pushOneActivity(this);

        user = BmobUser.getCurrentUser(context,User.class);

        initComponent();

        initGridView();

    }

    /**
     * 初始化组件
     */
    private void initComponent() {
        backImg = (ImageView) findViewById(R.id.publish_back_img);

        publish = (TextView) findViewById(R.id.publish_pulish_text);

        contentEdt = (EditText) findViewById(R.id.publish_content);

        mGridView = (GridView) findViewById(R.id.publish_gridview);

        backImg.setOnClickListener(this);
        publish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publish_back_img:
                this.finish();
                break;
            case R.id.publish_pulish_text: //发布

                if (checkInput()) { //有内容

                    if (mSelectedPicture.size() > 0) { //有图片，先上传图片

                        uploadPicture();

                    } else {//无图片

                        //发布动态
                        publishDynamic();
                    }

                } else { //无内容
                    Toast.makeText(context,"动态不能为空",Toast.LENGTH_SHORT);
                }
                break;



        }
    }


    /**
     * 上传图片
     */
    private void uploadPicture(){

        final  String[] filePaths = new String[mSelectedPicture.size()];

        for (int i =0;i<mSelectedPicture.size();i++) {
            filePaths[i] = mSelectedPicture.get(i);
        }

        BmobFile.uploadBatch(context, filePaths, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> list1) {

                if (list1.size() == filePaths.length) { //全部上传完成


                    Message msg = new Message();
                    msg.what = UPLOAD_PICTURE_SUCCESS;
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("urls", (ArrayList) list1);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onProgress(int i, int i1, int i2, int i3) {

            }

            @Override
            public void onError(int i, String s) {

                Toast.makeText(PublishDynamicActivity.this, "发布动态失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 初始化gridview
     */
    private void initGridView(){

        adapter = new PublishGridAdapter(context,mSelectedPicture);
        mGridView.setAdapter(adapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == mSelectedPicture.size()) { //加

                    Intent intent = new Intent(PublishDynamicActivity.this, AlbumActivity.class);
                    startActivityForResult(intent, GET_PHOTO_REQUEST);
                } else {

                }
            }
        });


    }


    /**
     * 检查输入
     * @return
     */
    private boolean checkInput(){

        if (contentEdt.getText().length()>0 || mSelectedPicture.size()>0) {

            return true;
        } else {
            return false;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case GET_PHOTO_REQUEST: //获取图片返回

                if ( resultCode == AlbumActivity.RESULT_CODE) {

                    ArrayList<String> selectData = data.getStringArrayListExtra("selectData");

                    if (selectData != null && selectData.size()>0) {
                        for (String path : selectData) {
                            mSelectedPicture.add(path);

                            Log.i("TAG",path);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 发布动态
     */
    private void publishDynamic(){

        content = contentEdt.getText().toString();

        dynamic = new Dynamic();

        dynamic.setFromUser(user);

        dynamic.setContent(content);

        dynamic.setTheme(theme);

        dynamic.setImage(pictureList);

        dynamic.setCommentCount(0);
        dynamic.setLikesCount(0);

        //保存到服务器动态表中
        dynamic.save(context, new SaveListener() {
            @Override
            public void onSuccess() {

                Log.i("TAG", "保存到动态表成功");

                //推送动态给粉丝
                pushDynamic();

            }

            @Override
            public void onFailure(int i, String s) {

                Toast.makeText(PublishDynamicActivity.this, "发布动态失败,请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 推送动态
     */
    private void pushDynamic(){

        //获取粉丝对象集合
        BmobQuery<Friend> query = new BmobQuery<>();
        query.addWhereEqualTo("toUser", user);

        query.findObjects(context, new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                if (list.size() > 0) { //有粉丝
                    //遍历粉丝集合
                    for (Friend friend : list) {
                        String fansid = friend.getFromUser().getObjectId();
                        //推送给粉丝
                        pushToFans(fansid);
                    }
                } else { // 粉丝为0
                    Log.i("TAG", "粉丝数为0");
                    Message msg = new Message();
                    msg.what = Push_Dynamic_Success;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.i("TAG", i + s);
            }
        });



    }

    /**
     * 推送给粉丝
     * @param fansid
     */
    public void pushToFans(String fansid) {

        //查询粉丝的时间线
        BmobQuery<Timeline> query = new BmobQuery<>();
        User fans = new User();
        fans.setObjectId(fansid);
        query.addWhereEqualTo("fromUser", fans);
        query.findObjects(context, new FindListener<Timeline>() {
            @Override
            public void onSuccess(List<Timeline> list) {
                if (list.size() == 1) {//获取粉丝时间线成功

                    Timeline timeline = list.get(0);
                    BmobRelation relation = new BmobRelation();
                    relation.add(dynamic);
                    timeline.setAllDynamic(relation);
                    timeline.update(context, new UpdateListener() {
                        @Override
                        public void onSuccess() {

                            Message msg= new Message();
                            msg.what = Push_Dynamic_Success;
                            handler.sendMessage(msg);
                            Log.i("TAG","添加给粉丝时间线成功");
                        }

                        @Override
                        public void onFailure(int i, String s) {

                            Log.i("TAG",s+i);
                        }
                    });
                } else {

                    Log.i("TAG", "推送时间线失败");
                }
            }

            @Override
            public void onError(int i, String s) {

                Log.i("TAG",i+s);
            }
        });
    }

    /**
     * 显示Dialog
     */
    private void showDialog(){
        AlertDialog dialog = new AlertDialog.Builder(PublishDynamicActivity.this)
                .setMessage("发布动态成功")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PublishDynamicActivity.this.finish();
                    }
                }).create();
        dialog.show();
    }
}
