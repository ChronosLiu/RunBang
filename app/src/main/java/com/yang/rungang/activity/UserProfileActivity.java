package com.yang.rungang.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.yang.rungang.R;
import com.yang.rungang.adapter.DynamicListAdapter;
import com.yang.rungang.adapter.UserDynamicListAdapter;
import com.yang.rungang.model.bean.Dynamic;
import com.yang.rungang.model.bean.Friend;
import com.yang.rungang.model.bean.User;
import com.yang.rungang.model.biz.ActivityManager;
import com.yang.rungang.utils.GeneralUtil;
import com.yang.rungang.view.NoScrollListView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;

public class UserProfileActivity extends BaseActivity implements View.OnClickListener {



    private static final int Query_Fans_Count_Success = 0x11;

    private static final int Query_Follow_Count_Success = 0x12;

    private static final int Query_User_Success = 0x13;

    private static final int Query_Dynamic_Success = 0x14;

    private static final int Query_Is_Follow_Success = 0x15;

    private static final int Add_Follow_Success = 0x16;
    private static final int Add_Follow_Failure = 0x17;

    private ImageView backImg;
    private LinearLayout followLayout;
    private TextView alreadyFollowText;
    private ImageView headImg;
    private ImageView userBgImg;
    private TextView nameText;
    private TextView ageText;
    private TextView fansText;
    private TextView followText;
    private RelativeLayout chatLayout;
    private NoScrollListView dynamicListView;
    private SwipeRefreshLayout refreshLayout;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private DisplayImageOptions circleOptions;

    private User loginUser;//登录用户

    private User nowUser;//当前用户

    private String userId = null; //当前用户id

    private int fansCount = 0;

    private int followCount = 0;

    private List<Dynamic> dynamics = null;

    private UserDynamicListAdapter adapter = null;

    private boolean isFollow = false; //是否已关注



    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Query_User_Success:
                    setUserInfoToView();

                    queryFansCount();
                    queryFollowCount();
                    queryPersonDynamic();
                    queryIsFollow();
                    break;
                case Query_Fans_Count_Success:
                    fansText.setText(fansCount+"");
                    break;
                case Query_Follow_Count_Success:
                    followText.setText(followCount+"");
                    break;
                case Query_Dynamic_Success:

                    if (dynamics!= null && dynamics.size()>0) {
                        Log.i("TAG",dynamics.size()+"大小");
                        setAdapter();
                    } else {
                        Toast.makeText(UserProfileActivity.this,"没有动态",Toast.LENGTH_SHORT).show();
                    }
                    break;

                case Query_Is_Follow_Success:
                   setFollowToView();
                    break;

                case Add_Follow_Success: //关注成功
                    isFollow = true;
                    Toast.makeText(context,"关注成功",Toast.LENGTH_SHORT).show();
                    setFollowToView();

                    break;
                case Add_Follow_Failure: //关注失败
                    Toast.makeText(context,"抱歉，关注失败",Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_profile);
        ActivityManager.getInstance().pushOneActivity(this);
        initComponent();
        loginUser = BmobUser.getCurrentUser(context,User.class);
        userId = getIntent().getStringExtra("userid");
        imageLoader = ImageLoader.getInstance();
        initOptions();
        if (userId != null && !userId.equals(loginUser.getObjectId())) {
            chatLayout.setVisibility(View.VISIBLE);
            queryNowUser();


        } else if (userId != null&&userId.equals(loginUser.getObjectId())) { //当前登录用户

            chatLayout.setVisibility(View.GONE);
            followLayout.setVisibility(View.GONE);

        }

        setAdapter();

    }

    private void initOptions(){
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.upload_head_pic)
                .showImageOnFail(R.drawable.upload_head_pic)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        circleOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.upload_head_pic)
                .showImageOnFail(R.drawable.upload_head_pic)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new CircleBitmapDisplayer())
                .build();
    }

    private void setAdapter(){
        if (dynamics!=null&&dynamics.size()>0) {
            adapter = new UserDynamicListAdapter(UserProfileActivity.this,dynamics);
            dynamicListView.setAdapter(adapter);
        }
    }
    /**
     * 初始化组件
     */
    private void initComponent() {
        backImg = (ImageView) findViewById(R.id.user_back_img);
        followLayout = (LinearLayout) findViewById(R.id.user_follow_layout);
        alreadyFollowText = (TextView) findViewById(R.id.user_already_concern_text);
        userBgImg = (ImageView) findViewById(R.id.user_album_bg_img);
        headImg = (ImageView) findViewById(R.id.user_head_img);
        nameText = (TextView) findViewById(R.id.user_name_text);
        ageText = (TextView) findViewById(R.id.user_age_text);
        fansText = (TextView) findViewById(R.id.user_fans_count_text);
        followText = (TextView) findViewById(R.id.user_follow_count_text);
        chatLayout = (RelativeLayout) findViewById(R.id.user_chat_layout);
        dynamicListView = (NoScrollListView) findViewById(R.id.user_dynamic_listview);

        backImg.setOnClickListener(this);
        followLayout.setOnClickListener(this);
        fansText.setOnClickListener(this);
        followText.setOnClickListener(this);
        chatLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_back_img: //返回
                this.finish();
                break;
            case R.id.user_follow_layout: //关注

                addFollow(); //添加关注
                break;

            case R.id.user_follow_count_text: //关注人数

                if (followCount > 0) {

                    Intent followIntent = new Intent(this,FriendActivity.class);
                    followIntent.putExtra("userid",nowUser.getObjectId());
                    followIntent.putExtra("sign",false);
                    followIntent.putExtra("username",nowUser.getNickName());
                    startActivity(followIntent);
                }

                break;
            case R.id.user_fans_count_text: //粉丝数量
                if(fansCount > 0) {
                    Intent fansIntent = new Intent(this,FriendActivity.class);
                    fansIntent.putExtra("userid",nowUser.getObjectId());
                    fansIntent.putExtra("username",nowUser.getNickName());
                    fansIntent.putExtra("sign",true);
                    startActivity(fansIntent);
                }

                break;
            case R.id.user_chat_layout: //私聊


                break;
        }
    }

    private void setUserInfoToView(){
        if (nowUser != null) {
            imageLoader.displayImage(nowUser.getHeadImgUrl(), headImg, circleOptions);
            nameText.setText(nowUser.getNickName());
            ageText.setText(GeneralUtil.getAgeByBirthday(nowUser.getBirthday().getDate()));
        }
    }

    private void setFollowToView(){
        if (isFollow) {
            followLayout.setVisibility(View.GONE);
            alreadyFollowText.setVisibility(View.VISIBLE);
        } else {
            followLayout.setVisibility(View.VISIBLE);
            alreadyFollowText.setVisibility(View.GONE);
        }
    }

    /**
     * 通过objectId查询用户
     */
    private void queryNowUser() {
        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(context, userId, new GetListener<User>() {
            @Override
            public void onSuccess(User user) {
                nowUser = user;
                Message msg = new Message();
                msg.what = Query_User_Success;
                handler.sendMessage(msg);

            }

            @Override
            public void onFailure(int i, String s) {

                Log.i("TAG", i + s);
            }
        });
    }

    /**
     * 查询粉丝数量
     */
    private void queryFansCount() {


        BmobQuery<Friend> query = new BmobQuery<>();
        query.addWhereEqualTo("toUser", nowUser);
        query.count(context, Friend.class, new CountListener() {
            @Override
            public void onSuccess(int i) {

                fansCount = i;
                Message msg = new Message();
                msg.what = Query_Fans_Count_Success;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(int i, String s) {

                Log.i("TAG", s + i);
            }
        });

    }

    /**
     * 查询关注数量
     */
    private void queryFollowCount() {

        BmobQuery<Friend> query = new BmobQuery<>();
        query.addWhereEqualTo("fromUser", nowUser);
        query.count(context, Friend.class, new CountListener() {
            @Override
            public void onSuccess(int i) {

                followCount = i;
                Message msg = new Message();
                msg.what = Query_Follow_Count_Success;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

    }

    /**
     * 查询个人动态
     */
    private void queryPersonDynamic() {


        BmobQuery<Dynamic> query = new BmobQuery<>();
        query.addWhereEqualTo("fromUser", nowUser);
        query.order("-createdAt");
        query.findObjects(context, new FindListener<Dynamic>() {
            @Override
            public void onSuccess(List<Dynamic> list) {

                dynamics = list;
                Message msg = new Message();
                msg.what = Query_Dynamic_Success;
                handler.sendMessage(msg);
            }

            @Override
            public void onError(int i, String s) {

                Log.i("TAG", s + i);
            }
        });


    }


    /**
     * 判断是否已关注当前用户
     * @return
     */
    private void queryIsFollow(){

        BmobQuery<Friend> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("fromUser", loginUser);
        BmobQuery<Friend> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("toUser", nowUser);

        List<BmobQuery<Friend>> andQuerys = new ArrayList<>();
        andQuerys.add(query1);
        andQuerys.add(query2);

        BmobQuery<Friend> query = new BmobQuery<>();
        query.and(andQuerys);
        query.count(context, Friend.class, new CountListener() {
            @Override
            public void onSuccess(int i) {

                if (i == 1) {
                    isFollow = true;
                } else {
                    isFollow = false;
                }

                Message msg = new Message();
                msg.what = Query_Is_Follow_Success;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(int i, String s) {
                Log.i("TAG", s + i);
            }
        });
    }


    /**
     * 添加关注
     */
    private void addFollow(){
        Friend friend = new Friend();
        friend.setFromUser(loginUser);
        friend.setToUser(nowUser);
        friend.save(context, new SaveListener() {
            @Override
            public void onSuccess() {
                Message msg = new Message();
                msg.what = Add_Follow_Success;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(int i, String s) {

                Message msg = new Message();
                msg.what = Add_Follow_Failure;
                handler.sendMessage(msg);
            }
        });
    }

}
