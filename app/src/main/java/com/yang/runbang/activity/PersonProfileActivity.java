package com.yang.runbang.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.yang.runbang.R;
import com.yang.runbang.adapter.PersonalDynamicAdapter;
import com.yang.runbang.listener.OnRecyclerViewListener;
import com.yang.runbang.model.bean.Dynamic;
import com.yang.runbang.model.bean.Friend;
import com.yang.runbang.model.bean.User;
import com.yang.runbang.utils.GeneralUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

public class PersonProfileActivity extends BaseActivity implements View.OnClickListener {



    private static final int Query_Dynamic_Success = 0x11;

    private Toolbar toolbar;
    private CollapsingToolbarLayout  collapsingToolbarLayout;
    private RecyclerView recyclerView;
    private ImageView avatar;
    private TextView nickName;
    private TextView ageText;
    private TextView signature;
    private TextView fansNumber;
    private TextView followNumber;
    private LinearLayout fansLinear;
    private LinearLayout followLinear;
    private RelativeLayout chat;

    private List<Dynamic> dynamicData = null; //动态数据集合
    private PersonalDynamicAdapter adapter;

    private DisplayImageOptions circleOptions;

    private User user;//当前用户

    private User queryUser;//查询用户

    private String userid;//查询用户id

    private List<User> followList = new ArrayList<>();//当前用户关注对象集合

    private int followCount = 0;//关注人数
    private int fansCount =0;//粉丝人数

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Query_Dynamic_Success:
                    setupRecyclerView(recyclerView);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        user = BmobUser.getCurrentUser(context, User.class);
        userid = getIntent().getStringExtra("userid");
        initComponent();

        queryFollowList();

        if (userid.equals(user.getObjectId())) {//是当前用户
            chat.setVisibility(View.GONE);
            queryUser = user;

            //绑定数据
            bindDataToView();

            queryFansNumber();

            queryFollowNumber();


            queryPersonDynamic();


        } else{
            chat.setVisibility(View.VISIBLE);
            queryUser();
        }

    }

    private void setupRecyclerView(RecyclerView recyclerView) {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PersonalDynamicAdapter(new OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {

                Intent dynamicIntent = new Intent(PersonProfileActivity.this,DynamicDetailsActivity.class);
                dynamicIntent.putExtra("dynamicId",dynamicData.get(position).getObjectId());
                startActivity(dynamicIntent);
                PersonProfileActivity.this.finish();
            }
            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }

            @Override
            public void onChildClick(int position, int childId) {

            }
        });
        adapter.setData(dynamicData);

        recyclerView.setAdapter(adapter);
    }

    private void initComponent(){
        //初始化toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_personal_profile);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle("个人简介");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_personal);
        collapsingToolbarLayout.setTitleEnabled(false);

        //初始化recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_personal_dynamic);

        avatar = (ImageView) findViewById(R.id.image_avatar);
        nickName = (TextView) findViewById(R.id.text_nickname);
        ageText = (TextView) findViewById(R.id.text_age);
        signature = (TextView) findViewById(R.id.text_signature);
        fansNumber = (TextView) findViewById(R.id.text_fans);
        followNumber = (TextView) findViewById(R.id.text_follow);
        fansLinear = (LinearLayout) findViewById(R.id.linear_fans);
        followLinear = (LinearLayout) findViewById(R.id.linear_follow);
        chat = (RelativeLayout) findViewById(R.id.relative_chat);

        circleOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar_blue)
                .showImageOnFail(R.drawable.default_avatar_blue)
                .showImageForEmptyUri(R.drawable.default_avatar_blue)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new CircleBitmapDisplayer())
                .build();

        chat.setOnClickListener(this);
        followLinear.setOnClickListener(this);
        fansLinear.setOnClickListener(this);
    }


    private void bindDataToView(){

        ImageLoader.getInstance().displayImage(queryUser.getHeadImgUrl(),avatar,circleOptions);
        nickName.setText(queryUser.getNickName());
        if (queryUser.getBirthday()!=null) {
            ageText.setText(GeneralUtil.getAgeByBirthday(queryUser.getBirthday().getDate()));
        }
        if (queryUser.getSignature()!=null) {
            signature.setText(queryUser.getSignature());
        }

        fansNumber.setText(fansCount+"");
        followNumber.setText(followCount+"");


    }

    /**
     * 通过objectId查询用户
     */
    private void queryUser() {
        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(context, userid ,new GetListener<User>() {
            @Override
            public void onSuccess(User user) {
                queryUser = user;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        bindDataToView();
                    }
                });

                queryFansNumber();

                queryFollowNumber();

                queryPersonDynamic();

            }

            @Override
            public void onFailure(int i, String s) {

                Log.i("TAG", i + s);
            }
        });
    }
    /**
     * 查询个人动态
     */
    private void queryPersonDynamic() {
        BmobQuery<Dynamic> query = new BmobQuery<>();
        query.addWhereEqualTo("fromUser",queryUser);
        query.order("-createdAt");
        query.findObjects(context, new FindListener<Dynamic>() {
            @Override
            public void onSuccess(List<Dynamic> list) {

                dynamicData = list;
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
     * 查询关注
     */
    private void queryFollowNumber(){
        BmobQuery<Friend> query = new BmobQuery<>();
        query.addWhereEqualTo("fromUser", queryUser);
        query.count(context, Friend.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                followCount = i;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        followNumber.setText(followCount + "");
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

    }

    /**
     * 查询当前用户关注集合
     */
    private void queryFollowList(){
        BmobQuery<Friend> query = new BmobQuery<>();
        query.addWhereEqualTo("fromUser", user);
        query.include("toUser");
        query.findObjects(context, new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                if (list.size() > 0) {
                    for (Friend friend : list) {
                        followList.add(friend.getToUser());
                    }
                }

            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 查询粉丝
     */
    private void queryFansNumber(){
        BmobQuery<Friend> query = new BmobQuery<>();
        query.addWhereEqualTo("toUser",queryUser);
        query.count(context, Friend.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                fansCount = i;
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        fansNumber.setText(fansCount+"");
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

//        getMenuInflater().inflate(R.menu.menu_personal_profile, menu);
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.relative_chat:

                if (!queryUser.equals(user)) {
                    BmobIMUserInfo info = new BmobIMUserInfo(
                            queryUser.getObjectId(),queryUser.getNickName(), queryUser.getHeadImgUrl());
                    //启动一个会话，实际上就是在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
                    BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, null);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("c", c);
                    Intent intent = new Intent(this,ChatActivity.class);
                    intent.putExtra(getPackageName(),bundle);
                    startActivity(intent);
                }

                break;
            case R.id.linear_fans:

                if(fansCount > 0) {
                    Intent fansIntent = new Intent(this,FriendActivity.class);
                    fansIntent.putExtra("userid", queryUser.getObjectId());
                    fansIntent.putExtra("username", queryUser.getNickName());
                    fansIntent.putExtra("sign",true);
                    startActivity(fansIntent);
                }
                break;
            case R.id.linear_follow:
                if (followCount > 0) {
                    Intent followIntent = new Intent(this,FriendActivity.class);
                    followIntent.putExtra("userid", queryUser.getObjectId());
                    followIntent.putExtra("sign",false);
                    followIntent.putExtra("username", queryUser.getNickName());
                    startActivity(followIntent);
                }
                break;
        }
    }
}
