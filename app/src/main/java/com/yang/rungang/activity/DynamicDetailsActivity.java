package com.yang.rungang.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yang.rungang.R;
import com.yang.rungang.model.bean.Dynamic;
import com.yang.rungang.view.NoScrollListView;
import com.yang.rungang.view.RoundImageView;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

public class DynamicDetailsActivity extends BaseActivity {


    private static final int Query_Dynamic_Success = 0x11;

    private static final int Query_Dynamic_Failure = 0x12;
    private ImageView backImg;

    private RoundImageView roundImageView;

    private TextView nameText;

    private TextView timeText;

    private TextView contentText;

    private TextView themeText;

    private NoScrollListView commentListVeiw;


    private String dynamicId = null;

    private Dynamic dynamic = null;


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Query_Dynamic_Success:
                    dynamic = (Dynamic) msg.obj;

                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dynamic_details);

        dynamicId = getIntent().getStringExtra("dynamicId");

        initComponent();
    }

    /**
     * 初始化组件
     */
    private void initComponent() {
        backImg = (ImageView) findViewById(R.id.dynamic_details_back_img);
        roundImageView = (RoundImageView) findViewById(R.id.dynamic_details_head_img);
        nameText = (TextView) findViewById(R.id.dynamic_details_name_text);
        timeText = (TextView) findViewById(R.id.dynamic_details_time_text);

        contentText = (TextView) findViewById(R.id.dynamic_details_content);
        themeText = (TextView) findViewById(R.id.dynamic_details_theme_text);

        commentListVeiw = (NoScrollListView) findViewById(R.id.dynamic_comment_listview);
    }

    private  void queryDynamicById(){

        BmobQuery<Dynamic> query = new BmobQuery<>();
        query.include("fromUser");

        query.getObject(context, dynamicId, new GetListener<Dynamic>() {
            @Override
            public void onSuccess(Dynamic dynamic) {
                Message msg = new Message();
                msg.what = Query_Dynamic_Success;
                msg.obj = dynamic;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    private void setDateToView(){

    }
}
