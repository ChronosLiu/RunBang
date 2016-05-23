package com.yang.rungang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yang.rungang.R;
import com.yang.rungang.adapter.FriendListAdapter;
import com.yang.rungang.model.bean.Friend;
import com.yang.rungang.model.bean.User;
import com.yang.rungang.model.biz.ActivityManager;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class FriendActivity extends BaseActivity implements View.OnClickListener {


    private static final int Get_Fans_Success = 0x11;
    private static final int Get_Follow_Success = 0x12;

    private ImageView backImg;
    private TextView titleText;
    private ListView mListView;

    private boolean sign;//true为粉丝，false为关注
    private String userid = null ;
    private String username = null;

    private FriendListAdapter adapter;
    private List<User> data = new ArrayList<>();


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Get_Follow_Success:
                    setAdapter();
                    break;
                case Get_Fans_Success:
                    setAdapter();
                    break;
            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friend);
        ActivityManager.getInstance().pushOneActivity(this);

        userid = getIntent().getStringExtra("userid");
        username = getIntent().getStringExtra("username");
        sign = getIntent().getBooleanExtra("sign", false);
        initComponent();
        setListener();

        if (sign) { //粉丝
            titleText.setText(username+"的粉丝");
            getFans();
        } else {//关注
            titleText.setText(username+"的关注");
            getFollow();

        }


    }

    private void initComponent() {
        backImg = (ImageView) findViewById(R.id.friend_back_img);
        titleText = (TextView) findViewById(R.id.friend_title_text);
        mListView = (ListView) findViewById(R.id.friend_listview);

        backImg.setOnClickListener(this);
    }

    private void setAdapter(){
        if (data!= null) {
            adapter = new FriendListAdapter(this,data);
            mListView.setAdapter(adapter);
        }
    }

    private void setListener(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FriendActivity.this,UserProfileActivity.class);
                intent.putExtra("userid",data.get(position).getObjectId());
                startActivity(intent);
            }
        });
    }
    /**
     * 获取关注用户集合
     */
    private void getFollow(){
        BmobQuery<Friend> query = new BmobQuery<>();
        User user = new User();
        user.setObjectId(userid);
        query.addWhereEqualTo("fromUser", user);
        query.include("toUser");
        query.addQueryKeys("toUser");
        query.findObjects(context, new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                if (list.size()>0) {
                    data.clear();
                    for (Friend friend:list) {
                        data.add(friend.getToUser());
                    }
                    Message msg = new Message();
                    msg.what = Get_Follow_Success;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    /**
     * 获取粉丝用户的集合
     */
    private void getFans(){
        BmobQuery<Friend> query = new BmobQuery<>();
        User user = new User();
        user.setObjectId(userid);
        query.addWhereEqualTo("toUser", user);
        query.include("fromUser");
        query.addQueryKeys("fromUser");
        query.order("-createdAt");
        query.findObjects(context, new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                if (list.size()>0) {
                    data.clear();
                    for (Friend friend:list) {
                        data.add(friend.getFromUser());
                    }
                    Message msg = new Message();
                    msg.what = Get_Fans_Success;
                    handler.sendMessage(msg);

                }
            }

            @Override
            public void onError(int i, String s) {

                Log.i("TAG",s+i);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch ( v.getId()) {
            case R.id.friend_back_img:
                this.finish();
                break;
        }
    }
}
