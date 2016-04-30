package com.yang.rungang.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yang.rungang.R;
import com.yang.rungang.fragment.DynamicFragment;
import com.yang.rungang.fragment.NewsFragment;
import com.yang.rungang.model.bean.User;
import com.yang.rungang.model.biz.ActivityManager;

import java.io.File;
import java.util.ArrayList;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DownloadFileListener;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private Context context;

    private LinearLayout homeLinear;
    private LinearLayout meLinear;
    private LinearLayout runLinear;

    private RelativeLayout homeLayout;
    private RelativeLayout meLayout;
    private RelativeLayout runLayout;
    private ImageView homeImg;
    private ImageView meImg;
    private ImageView runImg;
    private TextView homeText;
    private TextView meText;
    private TextView runText;

    private TextView dynamicText;
    private TextView newsText;
    private View dynamicLine;
    private View newsLine;
    private ViewPager homeViewPager;



    private User user;
    private ArrayList<Fragment> homeFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        context=getApplicationContext();
        ActivityManager.getInstance().pushOneActivity(this);
        judeFirstLogin();
        initComponent();
        initHomeComponent();
        init();
        user=BmobUser.getCurrentUser(context,User.class);

    }

    private void init() {
        homeLinear.setVisibility(View.VISIBLE);
        homeImg.setImageResource(R.drawable.tab_home_press_img);
        homeText.setTextColor(getResources().getColor(R.color.colorTheme));
    }

    /**
     * 初始化组件
     */
    private void initComponent() {
        homeLinear= (LinearLayout) findViewById(R.id.main_home_layout);
        homeLayout = (RelativeLayout) findViewById(R.id.main_tab_home_layout);
        homeImg = (ImageView) findViewById(R.id.main_tab_home_img);
        homeText = (TextView) findViewById(R.id.main_tab_home_text);
        runLinear = (LinearLayout) findViewById(R.id.main_run_layout);
        runLayout = (RelativeLayout) findViewById(R.id.main_tab_run_layout);
        runImg = (ImageView) findViewById(R.id.main_tab_run_img);
        runText = (TextView) findViewById(R.id.main_tab_run_text);
        meLinear = (LinearLayout) findViewById(R.id.main_me_layout);
        meLayout = (RelativeLayout) findViewById(R.id.main_tab_me_layout);
        meImg = (ImageView) findViewById(R.id.main_tab_me_img);
        meText = (TextView) findViewById(R.id.main_tab_me_text);

        homeLayout.setOnClickListener(this);
        runLayout.setOnClickListener(this);
        meLayout.setOnClickListener(this);
    }


    /**
     * 初始化Home组件
     */
    private void initHomeComponent() {
        dynamicText = (TextView) findViewById(R.id.main_home_title_dynamic);
        newsText = (TextView) findViewById(R.id.main_home_title_news);
        dynamicLine = findViewById(R.id.main_home_title_dynamic_line);
        newsLine = findViewById(R.id.main_home_title_news_line);
        homeViewPager = (ViewPager) findViewById(R.id.main_home_mViewPager);

        dynamicText.setOnClickListener(this);
        newsText.setOnClickListener(this);

        initHomeViewpager();
    }

    /**
     * 重置Home标题状态
     */
    private void resetLine(){
        newsText.setTextColor(getResources().getColor(R.color.title_home_text_normal));
        dynamicText.setTextColor(getResources().getColor(R.color.title_home_text_normal));
        dynamicLine.setVisibility(View.INVISIBLE);
        newsLine.setVisibility(View.INVISIBLE);
    }

    /**
     * 初始化homeViewpager，设置监听
     */
    private void initHomeViewpager(){
        homeFragments = new ArrayList<>();
        final DynamicFragment dynamicFragment=new DynamicFragment();
        NewsFragment newsFragment = new NewsFragment();

        homeFragments.add(dynamicFragment);
        homeFragments.add(newsFragment);

        homeViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return homeFragments.get(position);
            }

            @Override
            public int getCount() {
                return homeFragments.size();
            }
        });

        homeViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        resetLine();
                        dynamicLine.setVisibility(View.VISIBLE);
                        dynamicText.setTextColor(getResources().getColor(R.color.title_home_text_press));
                        break;
                    case 1:
                        resetLine();
                        newsLine.setVisibility(View.VISIBLE);
                        newsText.setTextColor(getResources().getColor(R.color.title_home_text_press));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        homeViewPager.setCurrentItem(0);


    }
    /**
     * 判断是否初次登录
     */
    private  void  judeFirstLogin(){
        User user=BmobUser.getCurrentUser(context,User.class);
        if(user ==null){ //无缓存的用户信息，初次登录，
            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_tab_home_layout:
                resetTabItem();
                homeLinear.setVisibility(View.VISIBLE);
                homeImg.setImageResource(R.drawable.tab_home_press_img);
                homeText.setTextColor(getResources().getColor(R.color.colorTheme));

                break;
            case R.id.main_tab_run_layout:
                resetTabItem();
                runLinear.setVisibility(View.VISIBLE);
                runImg.setImageResource(R.drawable.tab_run_press_img);
                runText.setTextColor(getResources().getColor(R.color.colorTheme));
                break;
            case R.id.main_tab_me_layout:
                resetTabItem();
                meLinear.setVisibility(View.VISIBLE);
                meImg.setImageResource(R.drawable.tab_me_press_img);
                meText.setTextColor(getResources().getColor(R.color.colorTheme));
                break;

            case R.id.main_home_title_dynamic://动态
                homeViewPager.setCurrentItem(0);
                break;
            case R.id.main_home_title_news://资讯
                homeViewPager.setCurrentItem(1);
                break;
        }
    }

    /**
     * 重置tab元素
     */
    private void resetTabItem(){
        homeLinear.setVisibility(View.GONE);
        runLinear.setVisibility(View.GONE);
        meLinear.setVisibility(View.GONE);
        homeImg.setImageResource(R.drawable.tab_home_normal_img);
        homeText.setTextColor(getResources().getColor(R.color.theme_black));
        runImg.setImageResource(R.drawable.tab_run_normal_img);
        runText.setTextColor(getResources().getColor(R.color.theme_black));
        meImg.setImageResource(R.drawable.tab_me_normal_img);
        meText.setTextColor(getResources().getColor(R.color.theme_black));
    }

    /**
     * 下载头像
     */
    private void downheadImg(String url){
        BmobFile bmobFile=new BmobFile("headimg.png","",url);
        File saveFile = new File(Environment.getExternalStorageDirectory(),bmobFile.getFilename());
        bmobFile.download(context, saveFile, new DownloadFileListener() {
            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }
}
