package com.yang.runbang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.yang.runbang.R;
import com.yang.runbang.adapter.AddFriendRecyclerAdapter;
import com.yang.runbang.listener.OnRecyclerViewClickListener;
import com.yang.runbang.model.bean.Friend;
import com.yang.runbang.model.bean.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class AddFriendActivity extends BaseActivity implements OnRecyclerViewClickListener {


    /**
     * 查询用户成功
     */
    private static final int Query_User_Success = 0x11;
    /**
     * 关注用户成功
     */
    private static final int Follow_User_Success = 0x12;
    /**
     * 关注用户失败
     */
    private static final int Follow_user_fail = 0x14;

    /**
     * 获取当前用户的关注对象
     */
    private static final int Get_Follow_User = 0x13;

    private EditText nickName;//输入昵称

    private RecyclerView recyclerView;

    private AddFriendRecyclerAdapter adapter;

    private List<User> data = new ArrayList<>();

    private List<User> followData = new ArrayList<>();//已关注的用户

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        user = BmobUser.getCurrentUser(context,User.class);
        initComponent();
        setListener();
        if (user!=null) {
            //获取关注列表
            getFollow();
        }

    }

    /**
     * 初始化组件
     */
    private void initComponent(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_friend);
        toolbar.setTitle("搜索用户");
        toolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nickName = (EditText) findViewById(R.id.edit_nick_name);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_add_friend);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new AddFriendRecyclerAdapter(user,this);
        recyclerView.setAdapter(adapter);

    }

    /**
     * 设置监听
     */
    private void setListener(){
        nickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String name = nickName.getText().toString();
                if (name!=null&&name.length()>0) {
                    queryUserByNickName(name);
                }

            }
        });
    }

    /**
     * 查询用户通过昵称
     * @param name
     */
    private void queryUserByNickName(String name) {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereContains("nickName", name);
        query.findObjects(context, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {

                Log.i("TAG", list.size() + "daxiao");
                if (list != null && list.size() > 0) { //成功且有数据
                    data.clear();
                    data = list;
                    Message msg = new Message();
                    msg.what = Query_User_Success;
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
     * 获取已关注对象列表
     */
    public void getFollow(){
        BmobQuery<Friend> query = new BmobQuery<>();
        query.addWhereEqualTo("fromUser",user);
        query.include("toUser");
        query.addQueryKeys("toUser");
        query.findObjects(context, new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                if (list!=null&&list.size()>0) {
                    Log.i("TAG","大小"+list.size());
                    for(Friend friend:list) {
                        followData.add(friend.getToUser());
                    }
                    Message msg = new Message();
                    msg.what = Get_Follow_User;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.i("TAG",s+i+"失败");
            }
        });
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Query_User_Success:
                    if (data!=null&&data.size()>0) {

                        adapter.setData(data);
                        adapter.notifyDataSetChanged();
                    }

                    break;
                case Follow_User_Success:

                    new AlertDialog.Builder(AddFriendActivity.this)
                            .setMessage("关注成功")
                            .setPositiveButton("完成", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create().show();
                    break;

                case Follow_user_fail:
                    new AlertDialog.Builder(AddFriendActivity.this)
                            .setMessage("关注失败，请稍后重试")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create().show();
                    break;

                case Get_Follow_User:

                    adapter.setFollowData(followData);
                    adapter.notifyDataSetChanged();

                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemClick(int position) {

        Intent intent = new Intent(AddFriendActivity.this,PersonProfileActivity.class);
        intent.putExtra("userid",data.get(position).getObjectId());
        startActivity(intent);


    }

    @Override
    public boolean onItemLongClick(int position) {
        return false;
    }

    @Override
    public void onChildClick(int position, int childId) {
        switch (childId){
            case R.id.linear_item_add_friend_follow://关注

                User aimUser = data.get(position);
                if (aimUser!=user && !followData.contains(aimUser)) {
                    Friend friend = new Friend();
                    friend.setFromUser(user);
                    friend.setToUser(data.get(position));
                    friend.save(context, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Message msg = new Message();
                            msg.what = Follow_User_Success;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onFailure(int i, String s) {

                            handler.sendEmptyMessage(Follow_user_fail);
                        }
                    });
                }

                break;
        }
    }

}
