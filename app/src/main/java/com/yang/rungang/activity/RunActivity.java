package com.yang.rungang.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.yang.rungang.R;
import com.yang.rungang.model.bean.RunRecord;
import com.yang.rungang.model.bean.User;
import com.yang.rungang.utils.GeneralUtil;
import com.yang.rungang.utils.IdentiferUtil;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

public class RunActivity extends BaseActivity implements View.OnClickListener {

    private ImageView backImg;
    private MapView mapView;
    private TextView timeText;
    private TextView distanceText;

    private ImageView startOrPauseImg;
    private ImageView continueImg;
    private ImageView stopImg;
    private RelativeLayout startRelative;
    private LinearLayout pauseLinear;
    private TextView stateText;

    private TextView dialogMessage;
    private TextView dialogContinue;
    private TextView dialogEnd;

    private Dialog dialog; // 弹窗提示

    private User user;

    private boolean isStart = false; // 标示 是否开始运动，默认false，未开始

    private double distance = 0.0; // 跑步总距离
    private int time = 0; //跑步用时，单位秒
    private Timer timer = null;
    private TimerTask timerTask = null;

    private BaiduMap baiduMap;
    public LocationClient mLocationClient = null;
    private BDLocationListener locationListener = new MyLocationListener();

    private List<LatLng> pointList = new ArrayList<>(); //坐标点集合

    private List<Float> speedList = new ArrayList<>(); // 速度集合



    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case IdentiferUtil.TIME_TASK:

                    timeText.setText(GeneralUtil.secondsToString(msg.arg1));
                    break;
            }

            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(context);
        setContentView(R.layout.activity_run);

        user = BmobUser.getCurrentUser(context,User.class);

        initComponent();

        baiduMap = mapView.getMap();
        mLocationClient = new LocationClient(context);
        mLocationClient.registerLocationListener(locationListener);
        initLocation();

    }

    /**
     * 初始化组件
     */
    private void initComponent() {

        backImg = (ImageView) findViewById(R.id.run_back_img);

        mapView = (MapView) findViewById(R.id.run_mapview);

        timeText = (TextView) findViewById(R.id.run_time_text);
        distanceText = (TextView) findViewById(R.id.run_distance_text);

        stateText = (TextView) findViewById(R.id.run_state_text);
        startRelative = (RelativeLayout) findViewById(R.id.run_init_run_relative);
        pauseLinear = (LinearLayout) findViewById(R.id.run_pause_run_linear);

        continueImg = (ImageView) findViewById(R.id.run_continue_img);
        startOrPauseImg = (ImageView) findViewById(R.id.run_start_or_pause_img);
        stopImg = (ImageView) findViewById(R.id.run_stop_img);

        backImg.setOnClickListener(this);
        continueImg.setOnClickListener(this);
        startOrPauseImg.setOnClickListener(this);
        stopImg.setOnClickListener(this);
        mapView.setOnClickListener(this);



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

        mLocationClient.setLocOption(option);

    }

    /**
     * 定位监听器
     */
    private class MyLocationListener implements  BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {


            if (bdLocation != null && baiduMap != null) {

                double latitude = bdLocation.getLatitude(); //纬度
                double longitude = bdLocation.getLongitude(); // 经度
                double radius = bdLocation.getRadius(); //精度

                LatLng latLng = new LatLng(latitude,longitude); //坐标点

                if(bdLocation.hasSpeed()) {
                    Log.i("TAG", "速度" + bdLocation.getSpeed());
                }else {
                    Log.i("TAG","无速度");
                }

                if (bdLocation.getLocType() == BDLocation.TypeGpsLocation ){ //gps定位

                } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation ) { // 网络定位

                } else if(bdLocation.getLocType() == BDLocation.TypeServerError ) { //服务器错误
                    Toast.makeText(context,"服务器错误，请稍后重试",Toast.LENGTH_SHORT).show();
                } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException ) {
                    Toast.makeText(context,"网络错误，请连接网络",Toast.LENGTH_SHORT).show();
                } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException ) {
                    Toast.makeText(context,"定位错误，请设置手机模式",Toast.LENGTH_SHORT).show();
                }


                if ( pointList.size()<=0) { // 第一次定位
                    pointList.add(latLng); //放入坐标集合
                } else {

                    LatLng lastPoint = pointList.get(pointList.size()-1);
                    double rang = DistanceUtil.getDistance(lastPoint,latLng); // 两次定位的距离
                    Log.i("TAG","距离"+rang);
                    if(rang >= 10 ) {
                        pointList.add(latLng);
                        distance += rang;
                    }
                }

            }

            setStartLocationMark();
            drawTrack();
            distanceText.setText(GeneralUtil.doubleToString(distance));
        }



    }

    /**
     * 设置初始位置(定位)覆盖物
     */
    private void setStartLocationMark(){

        LatLng startLocation = pointList.get(0);

        baiduMap.setMyLocationEnabled(true); //定位图层

        MyLocationData data = new MyLocationData.Builder()
                .direction(0)
                .latitude(startLocation.latitude)
                .longitude(startLocation.longitude)
                .build();

        baiduMap.setMyLocationData(data);

        BitmapDescriptor bitmapDescriptor= BitmapDescriptorFactory.fromResource(R.drawable.startpoint); //覆盖物icon
        MyLocationConfiguration configuration =new
                MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING,true,bitmapDescriptor); //跟随模式
        baiduMap.setMyLocationConfigeration(configuration);

        // 设置比例尺 ,17 表示 比例尺100米
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(startLocation, 17);
        baiduMap.animateMapStatus(mapStatusUpdate);


    }

    /**
     * 绘制轨迹,折线
     */
    private void drawTrack(){

        if (pointList.size()>=2) {
            baiduMap.clear(); //清除地图覆盖物

            OverlayOptions polyline = new PolylineOptions()
                    .width(5)
                    .color(R.color.colorPrimary)
                    .points(pointList);
            baiduMap.addOverlay(polyline);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 开始计时
     */
    private void startTimer(){
        if(timer == null) {
            timer = new Timer();
        }
        if(timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {

                    time++;
                    Message msg = new Message();
                    msg.what = IdentiferUtil.TIME_TASK;
                    msg.arg1 = time;
                    handler.sendMessage(msg);
                }
            };
        }

        if(timer != null && timerTask != null) {
            timer.schedule(timerTask,1000,1000);
        }

    }

    /**
     * 结束计时
     */
    private void stopTimer(){

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.run_back_img: //返回

                this.finish();

                break;
            case R.id.run_mapview:

                break;

            case R.id.run_start_or_pause_img:
                if (isStart) {//已开始，暂停按钮

                    stopTimer(); //停止计时

                    startRelative.setVisibility(View.GONE);
                    pauseLinear.setVisibility(View.VISIBLE);

                    mLocationClient.stop();

                } else { //未开始，开始按钮

                    startTimer(); //开始计时
                    isStart = true;
                    startOrPauseImg.setImageResource(R.drawable.run_stop);
                    stateText.setText("暂停");

                    mLocationClient.start();

                }
                break;
            case R.id.run_continue_img:// 继续

                startTimer(); // 开始计时

                startRelative.setVisibility(View.VISIBLE);
                pauseLinear.setVisibility(View.GONE);
                break;
            case R.id.run_stop_img: //停止

                stopTimer(); // 停止计时
                showDialog();
                break;

            case R.id.dialog_continue_run :
                dialog.dismiss();
                break;
            case R.id.dialog_end_run :
                RunRecord runRecord = new RunRecord();

                runRecord.setUserId(user.getObjectId());
                runRecord.setDistance(distance);
                runRecord.setTime(time);
                runRecord.setPoints(pointList);
                runRecord.setSpeeds(speedList);

                runRecord.save(context, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        Log.i("TAG","成功");
                    }

                    @Override
                    public void onFailure(int i, String s) {

                        Log.i("TAG",s+i);
                    }
                });

                break;
        }
    }


    /**
     * 展示dialog
     */
    private void showDialog(){

        View view = getLayoutInflater().inflate(R.layout.dialog_run_finish_layout, null);

        dialog = new AlertDialog.Builder(RunActivity.this).create();

        dialog.show();
        dialog.setContentView(view);

        dialogMessage = (TextView) view.findViewById(R.id.dialog_message_text);
        dialogContinue = (TextView) view.findViewById(R.id.dialog_continue_run);
        dialogEnd = (TextView) view.findViewById( R.id.dialog_end_run);

        dialogContinue.setOnClickListener(this);
        dialogEnd.setOnClickListener(this);

    }

}
