package com.yang.rungang.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.yang.rungang.R;
import com.yang.rungang.adapter.RunRecordListAdapter;
import com.yang.rungang.db.DBManager;
import com.yang.rungang.model.bean.RunRecord;
import com.yang.rungang.model.bean.User;
import com.yang.rungang.model.biz.ActivityManager;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class RunRecordActivity extends BaseActivity implements View.OnClickListener {


    private static final int Request_Code = 0x11;

    private ImageView backImg;

    private SwipeRefreshLayout swipeRefreshLayout; //下拉刷新组件
    private ListView mListView;

    private LinearLayout syncLayout;
    private List<RunRecord> data;

    private RunRecordListAdapter adapter;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_run_record);
        ActivityManager.getInstance().pushOneActivity(this);

        user = BmobUser.getCurrentUser(context,User.class);
        initComponent();

        getdata();

        setAdapter();

        setListener();

        showSyncLayout();

    }

    /**
     * 初始化组件
     */
    private void initComponent() {

        backImg = (ImageView) findViewById(R.id.run_record_back_img);
        syncLayout = (LinearLayout) findViewById(R.id.run_record_sync_identifer);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.run_record_swiperefreshlayout);
        mListView = (ListView) findViewById(R.id.run_record_listview);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_purple, android.R.color.holo_blue_bright, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        backImg.setOnClickListener(this);

    }

    /**
     * 获取数据
     */
    private void getdata(){
        data = new ArrayList<>();

        //获取runrecord
        data = DBManager.getInstance(context).getRunRecords();

    }

    private boolean hasSync(){
        if(data!= null) {
            for (RunRecord runRecord:data){
                if(!runRecord.isSync()) {

                    return true;
                }
            }
        }
        return false;
    }

    private void showSyncLayout(){
        if(hasSync()) {
            syncLayout.setVisibility(View.VISIBLE);
        } else {
            syncLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {
        if (data != null) {
            adapter = new RunRecordListAdapter(context, data);
            mListView.setAdapter(adapter);
        }
    }

    /**
     * 设置监听
     */
    private void setListener(){

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(RunRecordActivity.this,RunRecordDetailsActivity.class);
                intent.putExtra("position",position);
                startActivityForResult(intent, Request_Code);
            }
        });

        //刷新监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //同步数据
                syncData();
            }
        });


    }

    /**
     * 同步数据
     */
    private void syncData(){

        data = DBManager.getInstance(context).getRunRecords();

        //同步本地数据到服务器端
        if (data!= null) {
            for(final RunRecord record:data){
                if(!record.isSync() && record.getObjectId()== null) { //未同步

                    record.setIsSync(true);

                    record.save(context, new SaveListener() {
                        @Override
                        public void onSuccess() {

                            //修改数据库数据信息
                            DBManager.getInstance(context).updateOneRunRecord(record.getRecordid()
                            ,record.getObjectId(),true);
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });

                }
            }
        }

        swipeRefreshLayout.setRefreshing(false);

    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.run_record_back_img:

                this.finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        switch ( requestCode) {
            case Request_Code:
                getdata();
                adapter.notifyDataSetChanged();
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
}
