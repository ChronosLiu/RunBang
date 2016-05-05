package com.yang.rungang.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.yang.rungang.R;
import com.yang.rungang.db.DBManager;
import com.yang.rungang.fragment.DynamicFragment;
import com.yang.rungang.fragment.NewsFragment;
import com.yang.rungang.https.HttpsUtil;
import com.yang.rungang.model.bean.IBmobCallback;
import com.yang.rungang.model.bean.IHttpCallback;
import com.yang.rungang.model.bean.User;
import com.yang.rungang.model.bean.weather.WeatherData;
import com.yang.rungang.model.biz.ActivityManager;
import com.yang.rungang.model.biz.CityList;
import com.yang.rungang.utils.BmobUtil;
import com.yang.rungang.utils.ConfigUtil;
import com.yang.rungang.utils.FileUtil;
import com.yang.rungang.utils.GeneralUtil;
import com.yang.rungang.utils.IdentiferUtil;
import com.yang.rungang.utils.JsonUtil;
import com.yang.rungang.view.RoundImageView;

import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DownloadFileListener;

public class MainActivity extends FragmentActivity implements View.OnClickListener,IBmobCallback {

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

    private ImageView noticeImg;
    private TextView titleText;
    private RelativeLayout homeTitleRelative;
    private TextView dynamicText;
    private TextView newsText;
    private View dynamicLine;
    private View newsLine;
    private ImageView setImg;

    private ViewPager homeViewPager;

    //我的
    private RoundImageView headImg;
    private TextView usernameText;
    private TextView followNumber;
    private TextView fansNubmer;
    private RelativeLayout followRelative;
    private RelativeLayout fansRelative;
    private TextView meDistanceText;
    private TextView meTimeText;
    private RelativeLayout meRunScoreRelative;
    private RelativeLayout meFriendsListRelative;
    private RelativeLayout meScanRelative;

    //跑步

    private Button startBtn;
    private TextView ditanceText;
    private TextView timeText;
    private TextView scoreNumberText;

    private TextView cityText;
    private TextView tempText;
    private TextView airQuality;
    private TextView pm2_5Text;
    private TextView stateText;
    private TextView timeNowText;


    private User user;
    private ArrayList<Fragment> homeFragments;

    private LocationClient client = null;
    private BDLocationListener locationListener = new MyLocationListener();

    private String cityCode; //城市代码
    private String cityName; //城市名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        context=getApplicationContext();
        ActivityManager.getInstance().pushOneActivity(this);
//        //判断是否初次登录，是否有缓存用户
//        judeFirstLogin();
        //初始化工具栏
        initToolbar();
        //初始化组件
        initComponent();
        //初始状态
        initState();

        client = new LocationClient(context);
        client.registerLocationListener(locationListener);
        initLocation();
        client.start();

//        HttpsUtil.getCityList(ConfigUtil.CITY_LIST_API, new IHttpCallback() {
//            @Override
//            public void onSuccess(String response) {
//
//                CityList cityList = JsonUtil.parseCityListJson(response);
//
//                if(cityList.getCities().size()>0) {
//                    Log.i("TAG",cityList.getCities().get(0).getCity());
//                    Log.i("TAG",cityList.getCities().get(cityList.getCities().size()-1).getCity());
//                }
//
//                DBManager.getInstance(context).insertCitys(cityList.getCities());
//
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//
//            }
//        });


    }

    /**
     * 初始状态
     */
    private void initState() {
        homeTitleRelative.setVisibility(View.VISIBLE);
        homeLinear.setVisibility(View.VISIBLE);
        homeImg.setImageResource(R.drawable.tab_home_press_img);
        homeText.setTextColor(getResources().getColor(R.color.colorTheme));
        homeViewPager.setCurrentItem(0);
    }

    /**
     * 初始化工具栏toolbar
     */
    private void initToolbar(){
        noticeImg = (ImageView) findViewById(R.id.toolbar_notice_img);
        titleText = (TextView) findViewById(R.id.toobar_title_text);
        setImg = (ImageView) findViewById(R.id.toolbar_set_img);
        homeTitleRelative = (RelativeLayout) findViewById(R.id.toolbar_home_relative);
        dynamicText = (TextView) findViewById(R.id.toolbar_home_dynamic_text);
        newsText = (TextView) findViewById(R.id.toolbar_home_news_text);
        dynamicLine = findViewById(R.id.toolbar_home_dynamic_line);
        newsLine = findViewById(R.id.toolbar_home_news_line);

        noticeImg.setOnClickListener(this);
        setImg.setOnClickListener(this);
        dynamicText.setOnClickListener(this);
        newsText.setOnClickListener(this);

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

        initMeComponent();
        initHomeComponent();
        initRunComponent();

        homeLayout.setOnClickListener(this);
        runLayout.setOnClickListener(this);
        meLayout.setOnClickListener(this);
    }

    /**
     * 初始化化run组件
     */
    private void initRunComponent(){

        timeNowText = (TextView) findViewById(R.id.main_run_weather_time);
        stateText = (TextView) findViewById(R.id.main_run_weather_sate);
        startBtn = (Button) findViewById(R.id.main_run_start_btn);
        ditanceText = (TextView) findViewById(R.id.main_run_data_distance);
        timeText = (TextView) findViewById(R.id.main_run_data_time);
        scoreNumberText = (TextView) findViewById(R.id.main_run_data_score);
        cityText = (TextView) findViewById(R.id.main_run_weather_city);
        tempText = (TextView) findViewById(R.id.main_run_weather_temp);
        airQuality = (TextView) findViewById(R.id.main_run_weather_airquality);
        pm2_5Text = (TextView) findViewById(R.id.main_run_weather_pm_2_5);

        scoreNumberText.setOnClickListener(this);
        startBtn.setOnClickListener(this);
    }
    /**
     * 初始化me组件
     */
    private void initMeComponent(){
        headImg = (RoundImageView) findViewById(R.id.me_headimg_roundImg);
        usernameText = (TextView) findViewById(R.id.me_username_text);
        followNumber = (TextView) findViewById(R.id.me_follow_number_text);
        followRelative = (RelativeLayout) findViewById(R.id.me_follow_relative);
        fansNubmer = (TextView) findViewById(R.id.me_fans_number_text);
        fansRelative = (RelativeLayout) findViewById(R.id.me_fans_relative);

        meDistanceText = (TextView) findViewById(R.id.me_count_distance);
        meTimeText = (TextView) findViewById(R.id.me_count_time);

        meRunScoreRelative = (RelativeLayout) findViewById(R.id.me_run_score_relative);
        meFriendsListRelative = (RelativeLayout) findViewById(R.id.me_run_list_relative);
        meScanRelative = (RelativeLayout) findViewById(R.id.me_scan_relative);

        headImg.setOnClickListener(this);
        followRelative.setOnClickListener(this);
        fansRelative.setOnClickListener(this);
        meRunScoreRelative.setOnClickListener(this);
        meFriendsListRelative.setOnClickListener(this);
        meScanRelative.setOnClickListener(this);

    }

    /**
     * 初始化Home组件，viewpager设置adapter，页面改变监听
     */
    private void initHomeComponent() {
        homeViewPager = (ViewPager) findViewById(R.id.main_home_mViewPager);

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
                        resetLineState();
                        dynamicLine.setVisibility(View.VISIBLE);
                        dynamicText.setTextColor(getResources().getColor(R.color.title_home_text_press));
                        break;
                    case 1:
                        resetLineState();
                        newsLine.setVisibility(View.VISIBLE);
                        newsText.setTextColor(getResources().getColor(R.color.title_home_text_press));
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 重置Home标题线条状态
     */
    private void resetLineState(){
        newsText.setTextColor(getResources().getColor(R.color.title_home_text_normal));
        dynamicText.setTextColor(getResources().getColor(R.color.title_home_text_normal));
        dynamicLine.setVisibility(View.INVISIBLE);
        newsLine.setVisibility(View.INVISIBLE);
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

    private class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation
                    ||bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {// GPS或网络定位
                cityName = bdLocation.getCity();//获取城市名称
                cityName = cityName.substring(0,cityName.length()-1);
                if(cityName != null) {
                    cityCode = DBManager.getInstance(context).queryIdByName(cityName);
                    queryWeatherfromNet(cityCode);
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

    /**
     * 从网络获取天气信息
     * @param cityCode
     */
    private void queryWeatherfromNet(String cityCode){

        if(cityCode!=null){
            String url = ConfigUtil.WEATHER_API+"?cityid="+cityCode;
            HttpsUtil.httpGetRequest(url, new IHttpCallback() {
                @Override
                public void onSuccess(String response) {
                   WeatherData weatherData = JsonUtil.parseWeatherJson(response.toString());
                    if (weatherData !=null) {
                        Message msg = new Message();
                        msg.what = IdentiferUtil.GET_WEATHER_SUCCESS;
                        msg.obj = weatherData;
                        handler.sendMessage(msg);
                    }
                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_tab_home_layout: //首页
                resetComponentState();
                homeLinear.setVisibility(View.VISIBLE);
                homeImg.setImageResource(R.drawable.tab_home_press_img);
                homeText.setTextColor(getResources().getColor(R.color.colorTheme));

                homeTitleRelative.setVisibility(View.VISIBLE);

                break;
            case R.id.main_tab_run_layout: //跑步
                resetComponentState();
                runLinear.setVisibility(View.VISIBLE);
                runImg.setImageResource(R.drawable.tab_run_press_img);
                runText.setTextColor(getResources().getColor(R.color.colorTheme));

                titleText.setVisibility(View.VISIBLE);
                titleText.setText("跑步");


                break;
            case R.id.main_tab_me_layout:  //我的
                resetComponentState();
                meLinear.setVisibility(View.VISIBLE);
                meImg.setImageResource(R.drawable.tab_me_press_img);
                meText.setTextColor(getResources().getColor(R.color.colorTheme));

                titleText.setVisibility(View.VISIBLE);
                titleText.setText("我的");
                setImg.setVisibility(View.VISIBLE);

                break;

            case R.id.toolbar_home_dynamic_text://动态
                homeViewPager.setCurrentItem(0);
                break;
            case R.id.toolbar_home_news_text://资讯
                homeViewPager.setCurrentItem(1);
                break;

            case R.id.toolbar_set_img:
                Intent intent = new Intent(MainActivity.this,SetActivity.class);
                startActivity(intent);
                break;

            case R.id.main_run_start_btn: // 开始跑步
                Intent runIntent = new Intent(MainActivity.this,RunActivity.class);
                startActivity(runIntent);
                break;
            case R.id.main_run_data_score: //跑步记录

                break;
        }
    }

    private void setHeadImg() {

        if(user == null){
            return;
        }
        String url = user.getHeadImgUrl();
        if (url != null) {
            BmobUtil.downHeadImg(context,url, this);
            usernameText.setText(user.getUsername());
        }


    }
    /**
     * 重置组件状态,初始状态
     */
    private void resetComponentState(){

        //标题栏
        titleText.setVisibility(View.GONE);
        homeTitleRelative.setVisibility(View.GONE);
        setImg.setVisibility(View.GONE);

        //主布局
        homeLinear.setVisibility(View.GONE);
        runLinear.setVisibility(View.GONE);
        meLinear.setVisibility(View.GONE);
        //tab栏
        homeImg.setImageResource(R.drawable.tab_home_normal_img);
        homeText.setTextColor(getResources().getColor(R.color.theme_black));
        runImg.setImageResource(R.drawable.tab_run_normal_img);
        runText.setTextColor(getResources().getColor(R.color.theme_black));
        meImg.setImageResource(R.drawable.tab_me_normal_img);
        meText.setTextColor(getResources().getColor(R.color.theme_black));
    }

    @Override
    public void onFinish(int identifier, Object object) {

        Message msg = new Message();
        msg.what=identifier;
        if(object!=null){
            msg.obj=object;
        }
        handler.handleMessage(msg);
    }

    @Override
    public void onFailure(int identifier) {

        Message msg = new Message();
        msg.what=identifier;
        handler.handleMessage(msg);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IdentiferUtil.DOWN_FILE_SUCCESS://下载文件成功

                    String path = (String) msg.obj;
                    Bitmap bitmap = FileUtil.getBitmapFromFile(path);
                    if (bitmap!=null) {
                        headImg.setImageBitmap(bitmap);
                    }
                    break;
                case IdentiferUtil.DOWN_FILE_FAIL: // 下载文件失败

                    break;

                case IdentiferUtil.GET_WEATHER_SUCCESS : //获取天气成功


                    WeatherData weatherData = (WeatherData) msg.obj;

                    cityText.setText(weatherData.getBasic().getCity());

                    stateText.setText(weatherData.getNow().getCond().getTxt());

                    tempText.setText(weatherData.getNow().getTmp());

                    timeNowText.setText(new SimpleDateFormat("MM-dd").format(new Date()));

                    airQuality.setText(GeneralUtil.valueToAQIState(weatherData.getAqi().getCity().getAqi()));

                    pm2_5Text.setText(weatherData.getAqi().getCity().getPm25());

                    break;
            }
            super.handleMessage(msg);
        }
    };

}
