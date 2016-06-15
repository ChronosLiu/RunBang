package com.yang.runbang.activity;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.app.Fragment;
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
import com.yang.runbang.R;
import com.yang.runbang.db.DBManager;
import com.yang.runbang.fragment.TabHomeFragment;
import com.yang.runbang.fragment.TabMeFragment;
import com.yang.runbang.fragment.TabRunFragment;

import com.yang.runbang.model.bean.User;

import com.yang.runbang.model.biz.ActivityManager;


import org.greenrobot.eventbus.Subscribe;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.newim.listener.ObseverListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.sharesdk.framework.ShareSDK;


public class MainActivity extends FragmentActivity implements View.OnClickListener, ObseverListener {


    private static final int request_code_publish_dynamic = 0x11;
    private static final int request_code_set = 0x12;
    private static final int request_code_run_record = 0x13;
    private static final int request_code_run = 0x14;



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
    private ImageView addUserImg;
    private ImageView publishImg;
    private ImageView tipsImg;


    private Fragment mFragmentContent;
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

        user = BmobUser.getCurrentUser(context,User.class);

        if (user!= null) {
            setIMConnect();
        }

        //自动更新
        setUpdate();
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this);

        //初始化组件
        initComponent();
        //初始状态
        initState();
        //初始化定位
        initLocationClient();
//        Log.i("TAG","开始定位");
//        client.start();
    }


    /**
     * 检查版本更新
     */
    private void setUpdate(){
        BmobUpdateAgent.update(context);
        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {
            @Override
            public void onUpdateReturned(int i, UpdateResponse updateResponse) {
                //根据i来判断更新是否成功
            }
        });
        BmobUpdateAgent.setUpdateCheckConfig(false);
    }
    /**
     * 初始状态
     */
    private void initState() {
        titleText.setText("动态");
        publishImg.setVisibility(View.VISIBLE);
        homeImg.setImageResource(R.drawable.tab_home_press_img);
        homeText.setTextColor(getResources().getColor(R.color.colorTheme));
        setDefaultFragment();
    }

    private void setIMConnect(){
        BmobIM.connect(user.getObjectId(), new ConnectListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Log.i("TAG", "连接成功");
                } else {
                    Log.i("TAG", s + e);
                }
            }
        });

        //监听连接状态，也可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus connectionStatus) {

                if (connectionStatus == ConnectionStatus.DISCONNECT) { //断开连接
                    //重新连接
                    setIMConnect();
                } else if (connectionStatus == ConnectionStatus.NETWORK_UNAVAILABLE) { //网络问题

                }
            }
        });
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
        addUserImg = (ImageView) findViewById(R.id.image_toolbar_add_user);

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

        publishImg = (ImageView) findViewById(R.id.toolbar_publish_img);
        tipsImg = (ImageView) findViewById(R.id.toolbar_tips_img);


        publishImg.setOnClickListener(this);
        noticeImg.setOnClickListener(this);
        addUserImg.setOnClickListener(this);
        homeLayout.setOnClickListener(this);
        runLayout.setOnClickListener(this);
        meLayout.setOnClickListener(this);
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

        mFragmentContent = homeFragment;
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

        switch (v.getId()) {
            case R.id.main_tab_home_layout: //首页
                resetComponentState();
                homeImg.setImageResource(R.drawable.tab_home_press_img);
                homeText.setTextColor(getResources().getColor(R.color.colorTheme));
                titleText.setText("动态");
                publishImg.setVisibility(View.VISIBLE);

                if (homeFragment == null ) {
                    homeFragment = new TabHomeFragment();
                }
                switchFragment(homeFragment);

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

                switchFragment(runFragment);
                break;
            case R.id.main_tab_me_layout:  //我的
                resetComponentState();
                meImg.setImageResource(R.drawable.tab_me_press_img);
                meText.setTextColor(getResources().getColor(R.color.colorTheme));

                titleText.setVisibility(View.VISIBLE);
                titleText.setText("我的");
                addUserImg.setVisibility(View.VISIBLE);

                if(meFragment == null) {
                    meFragment = new TabMeFragment();
                }

                switchFragment(meFragment);
                break;

            case R.id.image_toolbar_add_user:
                Intent intent = new Intent(MainActivity.this,AddFriendActivity.class);
                startActivity(intent);
                break;

            case R.id.toolbar_notice_img:

                Intent notifiIntent = new Intent(MainActivity.this,NewsActivity.class);

                startActivity(notifiIntent);

                break;

            case R.id.toolbar_publish_img:

                Intent publishIntent = new Intent(MainActivity.this,PublishDynamicActivity.class);

                startActivity(publishIntent);
                break;

        }

    }

    /**
     * 重置组件状态,初始状态
     */
    private void resetComponentState(){

        //标题栏
//        titleText.setVisibility(View.VISIBLE);
        addUserImg.setVisibility(View.GONE);
        publishImg.setVisibility(View.GONE);

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
     * Fragment切换，不重新加载
     * @param fragment
     *
     */
    private void switchFragment(Fragment fragment){

        if (mFragmentContent!=fragment) {

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();

            if (!fragment.isAdded()) {
                transaction.hide(mFragmentContent).add(R.id.main_framenlayout,fragment).commit();//隐藏当前，添加下一个
            } else {
                transaction.hide(mFragmentContent).show(fragment).commit();//隐藏当前，显示下一个
            }

            mFragmentContent = fragment;
        }
    }

    /**
     * 保存定位的城市
     */
    private void saveLocationCity(){

        SharedPreferences sharedPreferences = getSharedPreferences("rungang", MODE_PRIVATE);
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

    @Override
    protected void onResume() {
        super.onResume();
        //显示小红点
        checkRedPoint();
        //添加观察者-用于是否显示通知消息
        BmobNotificationManager.getInstance(this).addObserver(this);
        //进入应用后，通知栏应取消
        BmobNotificationManager.getInstance(this).cancelNotification();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //移除观察者
        BmobNotificationManager.getInstance(this).removeObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK(context);
        //清理导致内存泄露的资源
        BmobIM.getInstance().clear();
        //断开服务器连接
        BmobIM.getInstance().disConnect();
        //完全退出应用时需调用clearObserver来清除观察者
        BmobNotificationManager.getInstance(this).clearObserver();
    }

    /**注册消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event){
        checkRedPoint();
    }

    /**注册离线消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event){
        checkRedPoint();
    }

    /**
     * 检查红点
     */
    private void checkRedPoint(){
        if(BmobIM.getInstance().getAllUnReadCount()>0){
            tipsImg.setVisibility(View.VISIBLE);
        }else{
            tipsImg.setVisibility(View.GONE);
        }
    }
}
