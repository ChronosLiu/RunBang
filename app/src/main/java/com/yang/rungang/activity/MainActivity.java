package com.yang.rungang.activity;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import android.view.Window;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.yang.rungang.R;
import com.yang.rungang.cache.ACache;
import com.yang.rungang.db.DBManager;
import com.yang.rungang.fragment.TabHomeFragment;
import com.yang.rungang.fragment.TabMeFragment;
import com.yang.rungang.fragment.TabRunFragment;

import com.yang.rungang.https.HttpsUtil;
import com.yang.rungang.model.bean.IHttpCallback;
import com.yang.rungang.model.bean.User;

import com.yang.rungang.model.bean.weather.WeatherData;
import com.yang.rungang.model.biz.ActivityManager;
import com.yang.rungang.utils.ConfigUtil;
import com.yang.rungang.utils.GeneralUtil;
import com.yang.rungang.utils.IdentiferUtil;
import com.yang.rungang.utils.JsonUtil;


import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobUser;


public class MainActivity extends FragmentActivity implements View.OnClickListener{

    private Context context;

    private FrameLayout frameLayout;

    private RelativeLayout homeLayout;
    private RelativeLayout meLayout;
    private RelativeLayout runLayout;
    private ImageView homeImg;
    private ImageView meImg;
    private ImageView runImg;
    private TextView homeText;
    private TextView meText;
    private TextView runText;

    private ImageView noticeImg;
    private TextView titleText;
    private ImageView setImg;


    private TabHomeFragment homeFragment;
    private TabMeFragment meFragment;
    private TabRunFragment runFragment;

    private User user; // 用户

    //定位客户端
    private LocationClient client = null;
    //定位监听器
    private BDLocationListener locationListener = new MyLocationListener();

    private String cityCode; //城市代码
    private String cityName; //城市名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        context=getApplicationContext();
        ActivityManager.getInstance().pushOneActivity(this);

       //判断是否初次登录，是否有缓存用户
        judeFirstLogin();
        //初始化组件
        initComponent();
        //初始状态
        initState();
        //初始化定位
        initLocationClient();

        Log.i("TAG","开始定位");
        client.start();
    }

    /**
     * 初始状态
     */
    private void initState() {
        titleText.setText("动态");
        homeImg.setImageResource(R.drawable.tab_home_press_img);
        homeText.setTextColor(getResources().getColor(R.color.colorTheme));
        setDefaultFragment();
    }

    /**
     * 初始化定位
     */
    private void initLocationClient() {
        client = new LocationClient(context);
        client.registerLocationListener(locationListener);
        initLocation();
    }


    /**
     * 初始化组件
     */
    private void initComponent() {

        noticeImg = (ImageView) findViewById(R.id.toolbar_notice_img);
        titleText = (TextView) findViewById(R.id.toobar_title_text);
        setImg = (ImageView) findViewById(R.id.toolbar_set_img);

        frameLayout = (FrameLayout) findViewById(R.id.main_framenlayout);

        homeLayout = (RelativeLayout) findViewById(R.id.main_tab_home_layout);
        homeImg = (ImageView) findViewById(R.id.main_tab_home_img);
        homeText = (TextView) findViewById(R.id.main_tab_home_text);

        runLayout = (RelativeLayout) findViewById(R.id.main_tab_run_layout);
        runImg = (ImageView) findViewById(R.id.main_tab_run_img);
        runText = (TextView) findViewById(R.id.main_tab_run_text);

        meLayout = (RelativeLayout) findViewById(R.id.main_tab_me_layout);
        meImg = (ImageView) findViewById(R.id.main_tab_me_img);
        meText = (TextView) findViewById(R.id.main_tab_me_text);

        noticeImg.setOnClickListener(this);
        setImg.setOnClickListener(this);
        homeLayout.setOnClickListener(this);
        runLayout.setOnClickListener(this);
        meLayout.setOnClickListener(this);
    }

    /**
     * 判断是否初次登录
     */
    private  void  judeFirstLogin(){
        user = BmobUser.getCurrentUser(context,User.class);
        if(user ==null){ //无缓存的用户信息，初次登录，
            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
    }


    /**
     * 设置默认fragment
     */
    private void setDefaultFragment(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction =fm.beginTransaction();
        homeFragment = new TabHomeFragment();
        transaction.replace(R.id.main_framenlayout, homeFragment);
        transaction.commit();
    }


    private class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation
                    ||bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {// GPS或网络定位
                cityName = bdLocation.getCity();//获取城市名称
                cityName = cityName.substring(0, cityName.length() - 1);
                Log.i("TAG","城市"+cityName);
                if(cityName != null) {
                    cityCode = DBManager.getInstance(context).queryIdByName(cityName);
                    //保存
                    saveLocationCity();
                }
                client.stop();
            } else {
                client.stop();
            }
        }
    }
    /**
     * 初始化定位，设置定位参数
     */
    private void initLocation(){
        //用来设置定位sdk的定位方式
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=2000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要

        client.setLocOption(option);

    }

    @Override
    public void onClick(View v) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        switch (v.getId()) {
            case R.id.main_tab_home_layout: //首页
                resetComponentState();
                homeImg.setImageResource(R.drawable.tab_home_press_img);
                homeText.setTextColor(getResources().getColor(R.color.colorTheme));
                titleText.setText("动态");

                if (homeFragment == null ) {
                    homeFragment = new TabHomeFragment();
                }
                transaction.replace(R.id.main_framenlayout,homeFragment);

                break;
            case R.id.main_tab_run_layout: //跑步
                resetComponentState();

                runImg.setImageResource(R.drawable.tab_run_press_img);
                runText.setTextColor(getResources().getColor(R.color.colorTheme));
                titleText.setVisibility(View.VISIBLE);
                titleText.setText("跑步");


                if (runFragment == null) {
                    runFragment = TabRunFragment.newInstance(cityName,cityCode);
                }

                transaction.replace(R.id.main_framenlayout,runFragment);
                break;
            case R.id.main_tab_me_layout:  //我的
                resetComponentState();
                meImg.setImageResource(R.drawable.tab_me_press_img);
                meText.setTextColor(getResources().getColor(R.color.colorTheme));

                titleText.setVisibility(View.VISIBLE);
                titleText.setText("我的");
                setImg.setVisibility(View.VISIBLE);

                if(meFragment == null) {
                    meFragment = new TabMeFragment();
                }

                transaction.replace(R.id.main_framenlayout,meFragment);

                break;

            case R.id.toolbar_set_img:
                Intent intent = new Intent(MainActivity.this,SetActivity.class);
                startActivity(intent);
                break;

            case R.id.toolbar_notice_img:

                Intent notifiIntent = new Intent(MainActivity.this,NotificationActivity.class);

                startActivity(notifiIntent);

                break;

        }

        transaction.commit();
    }

    /**
     * 重置组件状态,初始状态
     */
    private void resetComponentState(){

        //标题栏
//        titleText.setVisibility(View.VISIBLE);
        setImg.setVisibility(View.GONE);

        //主布局

        //tab栏
        homeImg.setImageResource(R.drawable.tab_home_normal_img);
        homeText.setTextColor(getResources().getColor(R.color.theme_black));
        runImg.setImageResource(R.drawable.tab_run_normal_img);
        runText.setTextColor(getResources().getColor(R.color.theme_black));
        meImg.setImageResource(R.drawable.tab_me_normal_img);
        meText.setTextColor(getResources().getColor(R.color.theme_black));
    }

    /**
     * 保存定位的城市
     */
    private void saveLocationCity(){

        SharedPreferences sharedPreferences = getSharedPreferences("rungang",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cityname", cityName);
        editor.putString("citycode", cityCode);
        editor.commit();
    }

    /**
     * 获取保存的城市
     */
    private void getLocationCity(){
        SharedPreferences sharedPreferences = getSharedPreferences("rungang", MODE_PRIVATE);
        cityName = sharedPreferences.getString("cityname",null);
        cityCode = sharedPreferences.getString("citycode",null);
    }


}
