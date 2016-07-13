package com.yang.runbang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.yang.runbang.R;
import com.yang.runbang.adapter.RunRecordListAdapter;
import com.yang.runbang.db.DBManager;
import com.yang.runbang.model.bean.RunRecord;
import com.yang.runbang.model.bean.User;
import com.yang.runbang.model.biz.ActivityManager;
import com.yang.runbang.utils.GeneralUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class RunRecordActivity extends BaseActivity{


    private static final int Request_Code = 0x11;


    private SwipeRefreshLayout swipeRefreshLayout; //下拉刷新组件

    private ListView mListView;

    private LinearLayout syncLayout;

    private List<RunRecord> data = new ArrayList<>(); //本地数据
    private List<RunRecord> serverData = new ArrayList<>();//服务器端数据

    private RunRecordListAdapter adapter;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_run_record);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle("跑步记录");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        syncLayout = (LinearLayout) findViewById(R.id.run_record_sync_identifer);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.run_record_swiperefreshlayout);
        mListView = (ListView) findViewById(R.id.run_record_listview);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_purple, android.R.color.holo_blue_bright, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    /**
     * 获取数据
     */
    private void getdata(){

        //获取本地数据
        data = DBManager.getInstance(context).getRunRecords();
        //获取服务器数据
        getDataFromServer();


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

                if (GeneralUtil.isNetworkAvailable(context)) {
                    //同步数据
                    syncData();
                }else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(context,"网络状态异常，请稍后重试",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    /**
     * 同步数据
     */
    private void syncData(){

        //同步网络数据到本地

        if(data.size()<=0&&serverData.size()>0) {//本地数据为空

            data = serverData;
            //保存到数据库中
            for (RunRecord runRecord :serverData) {
                DBManager.getInstance(context).insertRunRecord(runRecord);
            }

        } else if(data.size()>0&&serverData.size()<=0) {//网络无数据

            for(final RunRecord record:data){

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

            serverData = data;

        } else if (data.size()>0&&serverData.size()>0) {//都有数据

            for(final RunRecord record:data){

                if(!serverData.contains(record)) { //网络数据中不包含这条记录

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
            for(RunRecord runRecord:serverData) {

                if (!data.contains(runRecord)) {//本地数据没有
                    data.add(runRecord);
                    DBManager.getInstance(context).insertRunRecord(runRecord);
                }
            }
        } else {

        }
        swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();

    }
    /**
     * 从服务器端获取数据
     */
    private void getDataFromServer(){
        BmobQuery<RunRecord> query = new BmobQuery<>();
        query.addWhereEqualTo("userId",user.getObjectId());
        query.findObjects(context, new FindListener<RunRecord>() {
            @Override
            public void onSuccess(List<RunRecord> list) {
                serverData = list;
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            @Override
            public void onError(int i, String s) {

            }
        });
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

}
