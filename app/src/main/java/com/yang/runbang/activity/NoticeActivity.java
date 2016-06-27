package com.yang.runbang.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.yang.runbang.R;
import com.yang.runbang.model.bean.User;
import com.yang.runbang.model.biz.ActivityManager;

import cn.bmob.v3.BmobUser;

public class NoticeActivity extends BaseActivity {

    private User user;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        ActivityManager.getInstance().pushOneActivity(this);
        user = BmobUser.getCurrentUser(context,User.class);
        initComponent();


    }

    private void initComponent(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_notice);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle("通知");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiprefresh_notice);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_notice);
    }
}
